import cv2
import numpy as np
from collections import Counter

frame = cv2.imread('./file.jpeg', cv2.IMREAD_COLOR)

classes = [
    "background",
    "person",
    "bicycle",
    "car",
    "motorcycle",
    "airplane",
    "bus",
    "train",
    "truck",
    "boat",
    "traffic light",
    "fire hydrant",
    "unknown",
    "stop sign",
    "parking meter",
    "bench",
    "bird",
    "cat",
    "dog",
    "horse",
    "sheep",
    "cow",
    "elephant",
    "bear",
    "zebra",
    "giraffe",
    "unknown",
    "backpack",
    "umbrella",
    "unknown",
    "unknown",
    "handbag",
    "tie",
    "suitcase",
    "frisbee",
    "skis",
    "snowboard",
    "sports ball",
    "kite",
    "baseball bat",
    "baseball glove",
    "skateboard",
    "surfboard",
    "tennis racket",
    "bottle",
    "unknown",
    "wine glass",
    "cup",
    "fork",
    "knife",
    "spoon",
    "bowl",
    "banana",
    "apple",
    "sandwich",
    "orange",
    "broccoli",
    "carrot",
    "hot dog",
    "pizza",
    "donut",
    "cake",
    "chair",
    "couch",
    "potted plant",
    "bed",
    "unknown",
    "dining table",
    "unknown",
    "unknown",
    "toilet",
    "unknown",
    "tv",
    "laptop",
    "mouse",
    "remote",
    "keyboard",
    "cell phone",
    "microwave",
    "oven",
    "toaster",
    "sink",
    "refrigerator",
    "unknown",
    "book",
    "clock",
    "vase",
    "scissors",
    "teddy bear",
    "hair drier",
    "toothbrush"]

pb = './frozen_inference_graph.pb'
pbtxt = './ssd_inception_v2_coco_2017_11_17.pbtxt'

cvNet = cv2.dnn.readNetFromTensorflow(pb, pbtxt)

rows = frame.shape[0]
colums = frame.shape[1]
cvNet.setInput(
    cv2.dnn.blobFromImage(
        frame,
        size=(
            300,
            300),
        swapRB=True,
        crop=False))
cvOut = cvNet.forward()
l = []
for det in cvOut[0, 0, :, :]:
    score = float(det[2])
    if score > 0.5:
        idx = int(det[1])
        print(classes[idx])
        predcvOut = cvOut
        left = det[3] * colums
        top = det[4] * rows
        right = det[5] * colums
        bottom = det[6] * rows
        cv2.rectangle(frame, (int(left), int(top)), (int(
            right), int(bottom)), (0, 0, 0), thickness=2)
        cv2.putText(frame, classes[idx], (int(left), int(
            top)), cv2.FONT_HERSHEY_COMPLEX, 2, (0, 0, 255), 2)
        l.append(classes[idx])
c_l = Counter(l)
f = open('listOfObjects.txt', 'w')
for key, val in c_l.items():
    text = ':'.join((key, str(val)))
    f.write(text)
    f.write('\n')
f.close()
cv2.imwrite('cam.jpeg', frame)
# capture.release()
