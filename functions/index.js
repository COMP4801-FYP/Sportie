// const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
const functions = require("firebase-functions");
const { initializeApp, applicationDefault, cert } = require('firebase-admin/app');      //the duplicate initializeApp might cause some problem
const { getFirestore, Timestamp, FieldValue } = require('firebase-admin/firestore');
// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });



//exports.addMessage = functions.https.onCall((data, context) => {
//  return data.text;
//});



//const Video = require('@google-cloud/video-intelligence').v1;
//
//exports.countPlayers = functions.https.onCall(async(data,context)=>{
////  const gcsUri = 'gs://sportie-a3ce0.appspot.com/WhatsApp Video 2022-10-29 at 9.23.07 PM.mp4';
//  console.log("TESTTTTTTTTTTT---------------------------------1")
//
//  const gcsUri = 'gs://sportie-a3ce0.appspot.com/WhatsApp Video 2022-11-15 at 4.49.51 PM.mp4'
//  const video = new Video.VideoIntelligenceServiceClient();
//  console.log("TESTTTTTTTTTTT---------------------------------")
//
//  const detectPersonGCS = async function() {
//    const request = {
//      inputUri: gcsUri,
//      features: ['PERSON_DETECTION'],
//      videoContext: {
//        personDetectionConfig: {
////           Must set includeBoundingBoxes to true to get poses and attributes.
//           includeBoundingBoxes: true,
////           includePoseLandmarks: true,
//           includeAttributes: true,
//        },
//      },
//    };
//    // Detects faces in a video
//    // We get the first result because we only process 1 video
//    const [operation] = await video.annotateVideo(request);
//    const results = await operation.promise();
//    // Gets annotations for video
//    const personAnnotations =
//      results[0].annotationResults[0].personDetectionAnnotations;
//
//    console.log('testtt------------')
//    for (const {tracks} of personAnnotations) {
//        console.log('Person detected:');
//
//        for (const {segment, timestampedObjects} of tracks) {
//          console.log(
//            `\tStart: ${segment.startTimeOffset.seconds}` +
//              `.${(segment.startTimeOffset.nanos / 1e6).toFixed(0)}s`
//          );
//          console.log(
//            `\tEnd: ${segment.endTimeOffset.seconds}.` +
//              `${(segment.endTimeOffset.nanos / 1e6).toFixed(0)}s`
//          );
//
//          // Each segment includes timestamped objects that
//          // include characteristic--e.g. clothes, posture
//          // of the person detected.
//          const [firstTimestampedObject] = timestampedObjects;
//
//          // Attributes include unique pieces of clothing, poses (i.e., body
//          // landmarks) of the person detected.
//          for (const {name, value} of firstTimestampedObject.attributes) {
//            console.log(`\tAttribute: ${name}; Value: ${value}`);
//          }
//
//          // Landmarks in person detection include body parts.
////          for (const {name, point} of firstTimestampedObject.landmarks) {
////            console.log(`\tLandmark: ${name}; Vertex: ${point.x}, ${point.y}`);
////          }
//        }
//      }
//      console.log('test end-----------')
//
//
//
//    console.log('Number of people: ' + personAnnotations.length.toString())
//
//    return personAnnotations.length.toString()
//  }
//  return await detectPersonGCS();
//})


//const {ImageAnnotatorClient} = require('@google-cloud/vision').v1;

// Instantiates a client


//exports.countPlayersImages = functions.https.onCall(async(data,context)=>{
// const inputImageUri = 'gs://sportie-a3ce0.appspot.com/Screenshot 2023-01-12 at 4.59.01 PM.png';
//// const outputUri = 'gs://YOUR_BUCKET_ID/path/to/save/results/';
//  const client = new ImageAnnotatorClient();
//  async function asyncBatchAnnotateImages() {
//    // Set the type of annotation you want to perform on the image
//    // https://cloud.google.com/vision/docs/reference/rpc/google.cloud.vision.v1#google.cloud.vision.v1.Feature.Type
//    const features = [{type: 'LABEL_DETECTION'}];
//
//    // Build the image request object for that one image. Note: for additional images you have to create
//    // additional image request objects and store them in a list to be used below.
//    const imageRequest = {
//      image: {
//        source: {
//          imageUri: inputImageUri,
//        },
//      },
//      features: features,
//    };
//
//    // Set where to store the results for the images that will be annotated.
//    const outputConfig = {
//      gcsDestination: {
//        uri: outputUri,
//      },
//      batchSize: 2, // The max number of responses to output in each JSON file
//    };
//
//    // Add each image request object to the batch request and add the output config.
//    const request = {
//      requests: [
//        imageRequest, // add additional request objects here
//      ],
//      outputConfig,
//    };
//
//    // Make the asynchronous batch request.
//    const [operation] = await client.asyncBatchAnnotateImages(request);
//
//    // Wait for the operation to complete
//    const [filesResponse] = await operation.promise();
//
//    // The output is written to GCS with the provided output_uri as prefix
//    const destinationUri = filesResponse.outputConfig.gcsDestination.uri;
//    console.log(`Output written to GCS with prefix: ${destinationUri}`);
//  }
//
//  return await asyncBatchAnnotateImages();
//
//})

const {ImageAnnotatorClient} = require('@google-cloud/vision').v1;

const inputImageUriArray = ['gs://sportie-a3ce0.appspot.com/1.png',
  'gs://sportie-a3ce0.appspot.com/2.png',
  'gs://sportie-a3ce0.appspot.com/3.png',
  'gs://sportie-a3ce0.appspot.com/4.png',
  'gs://sportie-a3ce0.appspot.com/5.png']

exports.countPlayersImages = functions.https.onCall(async(data,context)=>{

  var countArray = [];
  const client = new ImageAnnotatorClient();

  for (var i = 0; i < inputImageUriArray.length; i++){
    const [result] = await client.objectLocalization(inputImageUriArray[i]);
    const objects = result.localizedObjectAnnotations;

    var count = 0;
    objects.forEach(object => {
      console.log(object)
      if (object.name == 'Person' && object.score >= 0.8){
        count += 1;
      }
    });
    countArray.push(count)
  }
  console.log('countArray: ');
  console.log(countArray);
  countAvg = Math.ceil(countArray.reduce((a,b) => a + b, 0) / countArray.length);
  console.log('countAvg: ');
  console.log(countAvg);
  const db = getFirestore();
  await db.collection('testVenueCollection').doc('testVenue').update({player_count: countAvg});
  console.log('finisheedddd!!');
  return countAvg.toString();
})