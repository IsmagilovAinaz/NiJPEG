#from picamera import PiCamera
import wiringpi
import datetime
import time
import os
from wiringpi import GPIO
import cv2 as cv
import requests

cap=cv.VideoCapture(1)
if not cap.isOpened():
    print("Cannonopcam")
    exit()
def setup_GPIO():
    
    # пин Trig
    TRIGGER = 6
    # пин Echo
    ECHO = 9
    # установим режим работы пина TRIGGER на Выход
    wiringpi.wiringPiSetup()
    wiringpi.pinMode(TRIGGER, GPIO.OUTPUT)
    # установим режим работы пина ECHO на Вход со стягивающим резистором
    wiringpi.pinMode(ECHO, GPIO.INPUT)
    wiringpi.pullUpDnControl(ECHO,1)
    return TRIGGER, ECHO

# пауза после объявления пинов
# без паузы датчик работает некорректно
def pause(value):
    for i in range(value):
        #print('Запуск через', value - i, 'сек.')
        time.sleep(1)
    #print('Сигнализация включена.\n')
    with open("log.txt", "a") as logfile:
        cur_time = datetime.datetime.now()
        stime = cur_time.strftime("%Y-%m-%d-%H-%M-%S")
        logfile.write(stime + " System start. \n")


def ultrasonic_detection(TRIGGER, ECHO):
    # подадим импульс, т . е. установим состояние пина на HIGH
    wiringpi.digitalWrite(TRIGGER, GPIO.HIGH)
    # длительность импульса 0.00001 сек
    time.sleep(0.00001)
    # установим состояние пина TRIGGER на LOW
    wiringpi.digitalWrite(TRIGGER, GPIO.LOW)
    # считываем состояние пина ECHO
    # пока ничего не происходит, фиксируем текущее время start
    while wiringpi.digitalRead(ECHO) == 0:
        start = time.time()
    # если обнаружено движение, зафиксируем время end
    while wiringpi.digitalRead(ECHO) == 1:
        end = time.time()
    # рассчитаем длительность сигнала
    signal_duration = end - start
    # рассчитаем расстояние до объекта
    distance = round(signal_duration * 17150, 2)
    
    # если объект обнаружен на расстоянии от 3 до 150 см
    if 3 < distance < 150:
        #print("Замечено движение на расстоянии", distance, "см. от датчика.")
        with open("log.txt", "a") as logfile:
            cur_time = datetime.datetime.now()
            stime = cur_time.strftime("%Y-%m-%d-%H-%M-%S")
            logfile.write(stime + " Move detected. Distance, sm: " + str(distance) + "\n")
        time.sleep(0.4)
        result, image = cap.read()
        down_width=800
        down_height=600
        down_points=(down_width,down_height)
        image=cv.resize(image,down_points,interpolation=cv.INTER_LINEAR)
        if result:
            #cv.imshow("asdad",image)
            cv.imwrite("asdas.jpeg",image)
        #cap.release()
        #exit(0)

def SendImage():
    with open("asdas.jpeg", "rb") as image:
     r = image.read()
     ba = bytearray(r)
    with open("address.txt", "r") as address:
        url = address.readline()
    headers = {'DeviceType': 'Observer'}
    response = requests.post(url, data = ba, headers = headers)
    
        
       


def main():
    
    TRIGGER, ECHO = setup_GPIO()
    pause(3)
    
    while True:
        try:
            ultrasonic_detection(TRIGGER, ECHO)
            SendImage()
            time.sleep(5)
        except KeyboardInterrupt:
            cap.release()
            exit(0)


if __name__ == "__main__":
    main()
