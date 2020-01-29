import numpy as np
from scipy.spatial import distance as dist
from imutils import perspective
from imutils import contours
import argparse
import imutils
import cv2
import csv
import os
import ntpath
from PIL import Image
from PIL.ExifTags import TAGS

MAX_DIMEN = 1500

foto_path_base = "data"


def getDirectoryListing(dir_name):
    file_list = []
    for file in os.listdir(dir_name):
        file_name = os.fsdecode(file)
        path_file = dir_name + "/" + file_name
        if os.path.isfile(path_file):
            continue
        file_list.append(path_file)
    return file_list


def getFileListing(dir_name):
    file_list = []
    for file in os.listdir(dir_name):
        file_name = os.fsdecode(file)
        path_file = dir_name + "/" + file_name
        if os.path.isdir(path_file):
            continue
        file_list.append(path_file)
    return file_list


def get_focal_length_image(path):  # get focal length of image in mm
    exif = (Image.open(path))._getexif()
    for (k, v) in exif.items():
        if TAGS.get(k) == "FocalLength":
            return v[0] / v[1]


def midpoint(ptA, ptB):
    return ((ptA[0] + ptB[0]) * 0.5, (ptA[1] + ptB[1]) * 0.5)


def path_leaf(path):
    head, tail = ntpath.split(path)
    return tail or ntpath.basename(head)


def build_dataset(g_blur_kernel_size=7, canny_threshold_1=30,
                  canny_threshold_2=50, dilate_size=20, erode_size=15):

    target_dataset = "data/data.csv"
    writer = csv.writer(open(target_dataset, 'w'))
    writer.writerow(["width", "height", "class"])

    print(f"------------- Step 1: Build Dataset w/ Object Detection -------------  \
            \nStarting building dataset to {target_dataset}")

    list_class_height = [0.9, 0.6, 0.8, 1.2]

    list_class = getDirectoryListing(foto_path_base)
    print(f"Detected class in directory: {list_class}\n")
    for cls_name in list_class:
        list_file = getFileListing(cls_name)

        print(f"Processing file {list_file}")

        for path in list_file:
            image = cv2.imread(path)
            # dim = (int(MAX_WIDTH), int( * float(image.shape[1] / 100)))
            # image = cv2.resize(image, dim, interpolation=cv2.INTER_AREA)

            dim, other_dim = (MAX_DIMEN, int(
                image.shape[1] * float(MAX_DIMEN / image.shape[0])
            )) if image.shape[0] > image.shape[1] else (int(
                image.shape[0] * float(MAX_DIMEN / image.shape[1])
            ), MAX_DIMEN)

            image = cv2.resize(image, (other_dim, dim), interpolation=cv2.INTER_AREA)
            image_crop = image[75:950, 425:575]

            # image_crop = cv2.resize(image_crop, (other_dim, dim), interpolation=cv2.INTER_AREA)

            if not os.path.isdir(cls_name + "/1. crop"):
                os.makedirs(cls_name + "/1. crop")
            cv2.imwrite(cls_name + "/1. crop/" + path_leaf(path), image_crop)

            image_gray=cv2.cvtColor(image_crop, cv2.COLOR_BGR2GRAY)
            if not os.path.isdir(cls_name + "/2. gray"):
                os.makedirs(cls_name + "/2. gray")
            cv2.imwrite(cls_name + "/2. gray/" + path_leaf(path), image_gray)
            # cv2.imwrite("%s/3. gray/data (%d).JPG" %(foto_path, i), image_gray)

            image_blurred=cv2.GaussianBlur(
                image_gray, (g_blur_kernel_size, g_blur_kernel_size), 0)
            if not os.path.isdir(cls_name + "/3. blur"):
                os.makedirs(cls_name + "/3. blur")
            cv2.imwrite(cls_name + "/3. blur/" +
                        path_leaf(path), image_blurred)
            # cv2.imwrite("%s/4. blur/data (%d).JPG" %(foto_path, i), image_blurred)

            image_canny=cv2.Canny(
                image_blurred, canny_threshold_1, canny_threshold_2)
            if not os.path.isdir(cls_name + "/4. canny"):
                os.makedirs(cls_name + "/4. canny")
            cv2.imwrite(cls_name + "/4. canny/" + path_leaf(path), image_canny)
            # cv2.imwrite("%s/4. canny/data (%d).JPG" %(foto_path, i), image_canny)

            image_dilate=cv2.dilate(
                image_canny, None, iterations=dilate_size)
            if not os.path.isdir(cls_name + "/5. dilate"):
                os.makedirs(cls_name + "/5. dilate")
            cv2.imwrite(cls_name + "/5. dilate/" +
                        path_leaf(path), image_dilate)
            # cv2.imwrite("%s/5. dilate/data (%d).JPG" %(foto_path, i), image_dilate)

            image_erode=cv2.erode(image_dilate, None, iterations=erode_size)
            if not os.path.isdir(cls_name + "/6. erode"):
                os.makedirs(cls_name + "/6. erode")
            cv2.imwrite(cls_name + "/6. erode/" + path_leaf(path), image_erode)
            # cv2.imwrite("%s/6. erode/data (%d).JPG" %(foto_path, i), image_erode)

            # find contours in the edge map
            cnts=cv2.findContours(image_erode.copy(), cv2.RETR_EXTERNAL,
                                    cv2.CHAIN_APPROX_SIMPLE)
            cnts=imutils.grab_contours(cnts)

            # sort the contours from left-to-right and initialize the
            # 'pixels per metric' calibration variable
            (cnts, _)=contours.sort_contours(cnts)

            extracted=False
            orig=None
            # loop over the contours individually
            for c in cnts:
                # if the contour is not sufficiently large, ignore it

                if cv2.contourArea(c) < 5000:
                    continue

                if extracted:
                    print("ERROR!, already extracted!")
                    continue

                extracted=True

                # compute the rotated bounding box of the contour
                orig=image_crop.copy()
                box=cv2.minAreaRect(c)
                box=cv2.cv.BoxPoints(
                    box) if imutils.is_cv2() else cv2.boxPoints(box)
                box=np.array(box, dtype="int")

                # order the points in the contour such that they appear
                # in top-left, top-right, bottom-right, and bottom-left
                # order, then draw the outline of the rotated bounding
                # box
                box=perspective.order_points(box)
                cv2.drawContours(orig, [box.astype("int")], -1, (0, 255, 0), 2)

                # loop over the original points and draw them
                for (x, y) in box:
                    cv2.circle(orig, (int(x), int(y)), 5, (0, 0, 255), -1)
                # unpack the ordered bounding box, then compute the midpoint
                # between the top-left and top-right coordinates, followed by
                # the midpoint between bottom-left and bottom-right coordinates
                (tl, tr, br, bl)=box
                (tltrX, tltrY)=midpoint(tl, tr)
                (blbrX, blbrY)=midpoint(bl, br)

                # compute the midpoint between the top-left and top-right points,
                # followed by the midpoint between the top-righ and bottom-right
                (tlblX, tlblY)=midpoint(tl, bl)
                (trbrX, trbrY)=midpoint(tr, br)

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
                height=dist.euclidean((tltrX, tltrY), (blbrX, blbrY))
                width=dist.euclidean((tlblX, tlblY), (trbrX, trbrY))

                # compute the size of the object
                focal_length=get_focal_length_image(path)
                px_width=len(image)
                px_height=len(image[0])

                real_width=width
                real_height=height

                # real_height = height / 1.2
                # real_width = width / 1.2

                real_width, real_height=(real_width, real_height) if (
                    real_width > real_height) else (real_height, real_width)

                # draw the object sizes on the image in cm
                cv2.putText(orig, "{:.4f}mm".format(real_width),
                            (int(tltrX - 15), int(tltrY - 10)
                             ), cv2.FONT_HERSHEY_SIMPLEX,
                            0.65, (255, 255, 255), 2)
                cv2.putText(orig, "{:.4f}mm ".format(real_height),
                            (int(trbrX + 10), int(trbrY)
                             ), cv2.FONT_HERSHEY_SIMPLEX,
                            0.65, (255, 255, 255), 2)

                # show the output image

                if not os.path.isdir(cls_name + "/7. result"):
                    os.makedirs(cls_name + "/7. result")
                cv2.imwrite(cls_name + "/7. result/" + path_leaf(path), orig)
                if cls_name == "4. Legowo":
                    print(path_leaf(path))

                row=[real_width, real_height, path_leaf(cls_name)]
                writer.writerow(row)
                image=None

    return {
        "gblur_kernel_size": g_blur_kernel_size,
        "canny_threshold_1": canny_threshold_1,
        "canny_threshold_2": canny_threshold_2,
        "dilate_size": dilate_size,
        "erode_size": erode_size
    }
