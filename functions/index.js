// const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
const functions = require("firebase-functions");

// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.addMessage = functions.https.onCall((data, context) => {
  return data.text;
});



// Imports the Google Cloud Video Intelligence library + Node's fs library

// Creates a client


const Video = require('@google-cloud/video-intelligence').v1;



exports.countPlayers = functions.https.onCall(async(data,context)=>{
  const gcsUri = 'gs://sportie-a3ce0.appspot.com/WhatsApp Video 2022-10-29 at 9.23.07 PM.mp4';
  const video = new Video.VideoIntelligenceServiceClient();
  console.log("TESTTTTTTTTTTT---------------------------------")

  const detectPersonGCS = async function() {
    const request = {
      inputUri: gcsUri,
      features: ['PERSON_DETECTION'],
      videoContext: {
        personDetectionConfig: {
          // Must set includeBoundingBoxes to true to get poses and attributes.
          includeBoundingBoxes: true,
          // includePoseLandmarks: true,
          // includeAttributes: true,
        },
      },
    };
    // Detects faces in a video
    // We get the first result because we only process 1 video
    const [operation] = await video.annotateVideo(request);
    const results = await operation.promise();
    console.log('Waiting for operation to complete...');
  
    // Gets annotations for video
    const personAnnotations =
      results[0].annotationResults[0].personDetectionAnnotations;

    console.log('Number of people: ' + personAnnotations.length.toString())

    return personAnnotations.length.toString()
  }
  return await detectPersonGCS();
})
