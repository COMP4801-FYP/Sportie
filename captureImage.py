import aiy.toneplayer
from aiy.vision.inference import CameraInference
from aiy.vision.models import face_detection
from signal import pause
from gpiozero import Button
from gpiozero import LED
from aiy.pins import LED_1
from aiy.pins import PIN_D
from aiy.pins import BUTTON_GPIO_PIN
from picamera import PiCamera
import time
import RPi.GPIO as GPIO 
GPIO.setwarnings(False) 
# GPIO.setmode(GPIO.BOARD) 
GPIO.setup(10, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
import os
import pyrebase

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
def takeAPicture():
    with PiCamera() as camera:
        # Configure camera
        camera.resolution = (1640, 922)  # Full Frame, 16:9 (Camera v2)
        camera.start_preview()
        name = "image_"+str(n)+".jpg"
        camera.capture("image_"+str(n)+".jpg")
        storage.child(name).put(name)
        print("Image sent to db")
        os.remove(name)



# def onButtonPressed(led):
#     led.on()
#     takeAPicture()


led = LED(LED_1)

button = Button(BUTTON_GPIO_PIN)


while True:
    n+=1
    led.on()
    takeAPicture()
    print("Hello World")
    time.sleep(5)


# while True:
#     led.on()
#     takeAPicture()
#     time.sleep(2)
#     if button.is_pressed:
#         led.off()
#         break
    

# # When the button is pressed, call the led.on() function (turn the led on)
# button.when_pressed = onButtonPressed
# # When the button is released, call the led.off() function (turn the led off)
# button.when_released = led.off