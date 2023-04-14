# Firebase Function for Player Tracking

This Firebase function provides a player count tracking feature for Sportie. The function is designed to capture photos of the court using a camera and use Google Vision API to detect number of people in the court. 

Currently, the function is set to run every 10 minutes and analyze photos stored in Firebase Storage for each court.

## Deployment Steps

To deploy the Firebase function, follow these steps:

1. Run the command `npm i` to install the required dependencies.

2. Run the command `sudo firebase deploy` to deploy the function to Firebase.

Note: The `sudo` command may be required depending on your system settings.

## Contributors

- Michael Wang
- Timothee Wirasaputra Kurniawan
- Vincent