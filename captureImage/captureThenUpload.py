from picamera import PiCamera
import time
import os
import pyrebase
from datetime import datetime

firebaseConfig = {
    'apiKey': "AIzaSyA2FnF7XvCKqw3aeKzTGIH5ncdGMNf-6YA",
    'authDomain': "sportie-a3ce0.firebaseapp.com",
    'databaseURL': "https://sportie-a3ce0-default-rtdb.asia-southeast1.firebasedatabase.app",
    'projectId': "sportie-a3ce0",
    'storageBucket': "sportie-a3ce0.appspot.com",
    'messagingSenderId': "1092062534112",
    'appId': "1:1092062534112:web:3924fbcdf4bbc401c07385",
    'measurementId': "G-FXKCRHP03N"

}

firebase = pyrebase.initialize_app(firebaseConfig)
storage = firebase.storage()

n=0
image_list = []

def takeAPicture():
    with PiCamera() as camera:
        # Configure camera
        camera.resolution = (1640, 922)  # Full Frame, 16:9 (Camera v2)
        camera.start_preview()
        name = str(n+1)+".jpg"
        camera.capture(str(n+1)+".jpg")
        image_list.append(name)

while True:
    #capture and upload 50 images every 10 minutes
    if n == 50:
        for name in image_list:
            storage.child("presentation/"+name).put(name)
            print("Image sent to db")
            os.remove(name)
        image_list.clear()
        #sleep for 10 minutes
        time.sleep(600)
        n=0
    print("before taking pic")
    takeAPicture()
    n+=1
    print("after taking pic")

