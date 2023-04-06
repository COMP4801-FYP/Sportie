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

exports.countPlayersImagesTESTTT = functions.runWith(runtimeOpts).https.onCall(async(data,context)=>{

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