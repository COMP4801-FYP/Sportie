const functions = require("firebase-functions");
const { getFirestore} = require('firebase-admin/firestore');
const admin = require("firebase-admin"); // The Firebase Admin SDK to access Firestore.
const {ImageAnnotatorClient} = require('@google-cloud/vision').v1;

admin.initializeApp();

const runtimeOpts = {
  timeoutSeconds: 540
}

const numOfImages = 50;   // number of images per batch per court

// Function to:
// 1. Call Google Vision API on each of the 50 images of the court every 10 minutes
// 2.
// This function
exports.countPlayers = functions.runWith(runtimeOpts).pubsub.schedule('every 10 minutes').onRun(async(data, context)=>{
 var inputImageUriArray = new Array();          // array containing Images' URI
 var courtId = data.text                        // courtId which will be used to navigate the Firebase Storage
 var countArray = [];                           // array containing player counts for the photos
 var countDict = {};                            // dictionary with key as the photo number and value as the player count of the photo
 const db = getFirestore();

 const client = new ImageAnnotatorClient();     // initialize Image Annotator Client of Google Vision API

 // Build the array with Images' URI on Firebase Storage
 for(var i = 1; i<numOfImages+1; i++){
   inputImageUriArray.push('gs://sportie-a3ce0.appspot.com/'+courtId + '/' + i.toString() + '.jpg');
 }

 console.log("Start analyzing...")
 for (var i = 0; i < inputImageUriArray.length; i++){
   const [result] = await client.objectLocalization(inputImageUriArray[i]);     // call the Google Vision API on the Image
   const objects = result.localizedObjectAnnotations;                           // get the object annotations
   var count = 0;
   objects.forEach(object => {
     // count the number of objects analyzed that are "Person"(s) and with confidence score >= 0.8
     if (object.name == 'Person' && object.score >= 0.8){
       count += 1;
     }
   });
   countArray.push(count)
   countDict[i+1] = count
 }
 console.log("Finish analyzing!")
 console.log("Result for " + courtId + ":")
 console.log(countDict)


 // Find max number of people based on the numOfImages photos
 maxValue = 0
 countArray.forEach(count =>{
   if (count > maxValue){
       maxValue = count
   }
 })

 // Update the specific side of the court's player count in Firebase Firestore
 const docRef = db.collection("AllCourt").doc("Central & Western").collection("SportCentre").doc("dummytesting").collection("Court").doc("dummy_No_1");
 docRef.update({
     playercount_a: maxValue,
 })
 .then(() => {
     console.log("Document successfully updated!");
 })
 .catch((error) => {
     console.error("Error updating document: ", error);
 });

 return;
})
