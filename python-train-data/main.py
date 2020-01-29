import object_detection as obj_d
import naive_bayes as nb
import json

print("============================================================")
print("===== Rice Seeds Classification: Data Training Program =====")
print("============================================================")
print()
g_blur_kernel_size = int(input(
    "Masukkan besar kernel gaussian blur (default 11): ") or 11)
canny_threshold_1 = int(input(
    "Masukkan besar canny threshold 1 (default 15): ") or 15)
canny_threshold_2 = int(input(
    "Masukkan besar canny threshold 2 (default 20): ") or 20)
dilate_size = int(input("Masukkan besar dari dilasi (default 20): ") or 20)
erode_size = int(input("Masukkan besar dari erosi (default 15): ") or 15)

print()
obj_detection_config = obj_d.build_dataset(g_blur_kernel_size,
                                           canny_threshold_1,
                                           canny_threshold_2,
                                           dilate_size, erode_size)
model_naive_bayes = nb.main_naive_bayes(n_folds=5)

model_builder = {
    "imgproc_config": obj_detection_config,
    "model": model_naive_bayes
}


with open('model.json', 'w') as file_object:
    json.dump(model_builder, file_object)
    print("Model saved on ./model.json")
