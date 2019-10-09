import cv2
import numpy as np
from scipy.spatial import distance as dist
import imutils
from imutils import contours, perspective


def midpoint(ptA, ptB):
    return ((ptA[0] + ptB[0]) * 0.5, (ptA[1] + ptB[1]) * 0.5)


class ImageRecognition():
    """
    """

    def __init__(self, path):
        self.image = cv2.imread(path)
        self.process()
        pass

    def process(
        self,
        gray_param=cv2.COLOR_BGR2GRAY,
        gaussian_param=((7, 7), 0),
        canny_param=(30, 50),
        dilate_param=(None, 20),
            erode_param=(None, 15)):
            """
            """
            self.image_crop = self.image[300:3800, 1700:2300]
            self.image = cv2.cvtColor(self.image_crop, gray_param)
            self.image = cv2.GaussianBlur(self.image, gaussian_param[0],
                                          gaussian_param[1])
            self.image = cv2.Canny(self.image, canny_param[0], canny_param[1])
            self.image = cv2.dilate(self.image, dilate_param[0],
                                    iterations=dilate_param[1])
            self.image = cv2.erode(self.image, erode_param[0],
                                   iterations=erode_param[1])
            pass

    def getTextureData(self):
        width, height, _ = self.image_crop.shape
        flat_image = self.image_crop.flatten()
        return (height, width, flat_image)

    def getWidthHeight(self):
        cnts = cv2.findContours(self.image.copy(), cv2.RETR_EXTERNAL,
                                cv2.CHAIN_APPROX_SIMPLE)
        cnts = imutils.grab_contours(cnts)

        # sort the contours from left-to-right and initialize the
        # 'pixels per metric' calibration variable
        (cnts, _) = contours.sort_contours(cnts)

        extracted = False
        orig = None
        # loop over the contours individually
        for c in cnts:
            # if the contour is not sufficiently large, ignore it

            if cv2.contourArea(c) < 20000:
                continue

            if extracted:
                print("ERROR!, already extracted!")
                continue

            extracted = True

            # compute the rotated bounding box of the contour
            orig = self.image_crop.copy()
            box = cv2.minAreaRect(c)
            box = cv2.cv.BoxPoints(box) if imutils.is_cv2() else cv2.boxPoints(box)
            box = np.array(box, dtype="int")

            # order the points in the contour such that they appear
            # in top-left, top-right, bottom-right, and bottom-left
            # order, then draw the outline of the rotated bounding
            # box
            box = perspective.order_points(box)
            cv2.drawContours(orig, [box.astype("int")], -1, (0, 255, 0), 2)

            # loop over the original points and draw them
            for (x, y) in box:
                cv2.circle(orig, (int(x), int(y)), 5, (0, 0, 255), -1)
            # unpack the ordered bounding box, then compute the midpoint
            # between the top-left and top-right coordinates, followed by
            # the midpoint between bottom-left and bottom-right coordinates
            (tl, tr, br, bl) = box
            (tltrX, tltrY) = midpoint(tl, tr)
            (blbrX, blbrY) = midpoint(bl, br)

            # compute the midpoint between the top-left and top-right points,
            # followed by the midpoint between the top-righ and bottom-right
            (tlblX, tlblY) = midpoint(tl, bl)
            (trbrX, trbrY) = midpoint(tr, br)

            # draw the midpoints on the image
            cv2.circle(orig, (int(tltrX), int(tltrY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(blbrX), int(blbrY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(tlblX), int(tlblY)), 5, (255, 0, 0), -1)
            cv2.circle(orig, (int(trbrX), int(trbrY)), 5, (255, 0, 0), -1)

            # draw lines between the midpoints
            cv2.line(orig, (int(tltrX), int(tltrY)), (int(blbrX), int(blbrY)),
                     (255, 0, 255), 2)
            cv2.line(orig, (int(tlblX), int(tlblY)), (int(trbrX), int(trbrY)),
                     (255, 0, 255), 2)

            # compute the Euclidean distance between the midpoints
            height = dist.euclidean((tltrX, tltrY), (blbrX, blbrY))
            width = dist.euclidean((tlblX, tlblY), (trbrX, trbrY))



            # compute the size of the object
            # px_width = len(self.image_ori)
            # px_height = len(self.image[0])

            real_width = width
            real_height = height

            # real_height = height / 1.2 
            # real_width = width / 1.2

            real_width, real_height = (real_width, real_height) if (real_width < real_height) else (real_height, real_width) 
        return (real_width, real_height)
        pass
