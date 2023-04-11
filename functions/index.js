const functions = require("firebase-functions");
const { initializeApp, applicationDefault, cert } = require('firebase-admin/app');      //the duplicate initializeApp might cause some problem
const { getFirestore, Timestamp, FieldValue } = require('firebase-admin/firestore');
// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

const {ImageAnnotatorClient} = require('@google-cloud/vision').v1;

const runtimeOpts = {
  timeoutSeconds: 540,
}

exports.analyzeImages = functions.runWith(runtimeOpts).https.onCall(async(data,context)=>{

  var inputImageUriArray = new Array();
  var category = data.text

  for(var i = 1; i<6; i++){
    inputImageUriArray.push('gs://sportie-a3ce0.appspot.com/'+category + '/' + i.toString() + '.jpg');
  }

  var countArray = [];
  const client = new ImageAnnotatorClient();
  var countDict = {};

  console.log("start analyzing")
  for (var i = 0; i < inputImageUriArray.length; i++){
    const [result] = await client.objectLocalization(inputImageUriArray[i]);
    const objects = result.localizedObjectAnnotations;

    var count = 0;
    objects.forEach(object => {
//      console.log(object)
      if (object.name == 'Person' && object.score >= 0.8){
        count += 1;
      }
    });
    countArray.push(count)
    countDict[i+1] = count
  }
  console.log("finish analyzing")
  console.log("result for " + category)
  console.log(countDict)
  return countArray;
})

exports.countPlayersImagesPresentation = functions.runWith(runtimeOpts).pubsub.schedule('every 1 minutes').onRun(async(context)=>{

  var inputImageUriArray = new Array();
  var category = "presentation"

  for(var i = 1; i<6; i++){
    inputImageUriArray.push('gs://sportie-a3ce0.appspot.com/'+category + '/' + i.toString() + '.jpg');
  }


  var countArray = [];
  const client = new ImageAnnotatorClient();
  var countDict = {};

  console.log("start analyzing")
  for (var i = 0; i < inputImageUriArray.length; i++){
    const [result] = await client.objectLocalization(inputImageUriArray[i]);
    const objects = result.localizedObjectAnnotations;

    var count = 0;
    objects.forEach(object => {
//      console.log(object)
      if (object.name == 'Person' && object.score >= 0.8){
        count += 1;
      }
    });
    countArray.push(count)
    countDict[i+1] = count
  }
  console.log("finish analyzing")
  console.log("result for " + category)
  console.log(countDict)

  const db = getFirestore();
  const docRef = db.collection("AllCourt").doc("Central & Western").collection("SportCentre").doc("dummytesting").collection("Court").doc("dummy_No_1");

  // Update the "name" attribute of the document
  // find max of array
  maxValue = 0
  countArray.forEach(count =>{
    if (count > maxValue){
        maxValue = count
    }
  })

  docRef.update({
      playercount_a: maxValue,
      isUpdated: true,
      photo_1: countDict['1'],
      photo_2: countDict['2'],
      photo_3: countDict['3'],
      photo_4: countDict['4'],
      photo_5: countDict['5'],
  })
  .then(() => {
      console.log("Document successfully updated!");
  })
  .catch((error) => {
      console.error("Error updating document: ", error);
  });

  return;
})

exports.countPlayersImages = functions.runWith(runtimeOpts).https.onCall(async(data,context)=>{

  var inputImageUriArray = new Array();
  var category = data.text

  for(var i = 1; i<51; i++){
    inputImageUriArray.push('gs://sportie-a3ce0.appspot.com/'+category + '/' + i.toString() + '.jpg');
  }


  var countArray = [];
  const client = new ImageAnnotatorClient();
  var countDict = {};

  console.log("start analyzing")
  for (var i = 0; i < inputImageUriArray.length; i++){
    const [result] = await client.objectLocalization(inputImageUriArray[i]);
    const objects = result.localizedObjectAnnotations;

    var count = 0;
    objects.forEach(object => {
//      console.log(object)
      if (object.name == 'Person' && object.score >= 0.8){
        count += 1;
      }
    });
    countArray.push(count)
    countDict[i+1] = count
  }
  console.log("finish analyzing")
  console.log("result for " + category)
  console.log(countDict)
  return;
})