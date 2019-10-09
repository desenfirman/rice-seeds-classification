import kivy
import json
import math
import os
import cv2
from module.image_detection import ImageRecognition
from kivy.app import App
from kivy.factory import Factory
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.properties import StringProperty, ObjectProperty, ListProperty
from kivy.uix.popup import Popup
from kivy.uix.image import Image
from kivy.graphics.texture import Texture

kivy.require("1.10.0")


class LoadDialog(FloatLayout):
    load = ObjectProperty(None)
    cancel = ObjectProperty(None)
    filters = ListProperty()
    default_path = StringProperty()
    pass


class SeedClassification(GridLayout):
    """

    """
    classification_model = ObjectProperty()
    loaded_image = ObjectProperty()
    input_vector = ListProperty()
    status = StringProperty('Status: No model selected!')

    def dismiss_popup(self):
        self._popup.dismiss()

    def show_load_model(self):
        content = LoadDialog(load=self.load_model,
                             cancel=self.dismiss_popup,
                             default_path=os.getcwd(),
                             filters=["*.json"]
                             )
        self._popup = Popup(title="Load model file", content=content,
                            size_hint=(0.9, 0.9))
        self._popup.open()

    def show_load_image(self):
        if self.classification_model is None:
            self.status = "Please import your classification model before \
                classify the image."
            return

        content = LoadDialog(load=self.load_image,
                             cancel=self.dismiss_popup,
                             default_path=os.getcwd(),
                             filters=["*.jpg", "*.jpeg", "*.bmp", "*.png"]
                             )
        self._popup = Popup(title="Load image file", content=content,
                            size_hint=(0.9, 0.9))
        self._popup.open()

    def load_model(self, path, filename):
        with open(os.path.join(path, filename[0]), 'r') as file_object:
            config_file = json.load(file_object)
            self.classification_model = config_file['data']
            self.status = f"model file {os.path.split(filename[0])[1]}" + \
                " loaded.\nYou can use to classify image now."
            print(self.classification_model)
            self.dismiss_popup()

    def load_image(self, path, filename):
        image = ImageRecognition(os.path.join(path, filename[0]))
        width, height, texture_data = image.getTextureData()
        texture = Texture.create(size=(width, height))
        texture.blit_buffer(texture_data, colorfmt='bgr', bufferfmt='ubyte')
        self.loaded_image = texture
        width, height = image.getWidthHeight()
        self.predict([width, height, None])
        self.dismiss_popup()

    def calculate_class_probabilities(self):
        """
        Calculate the class probability for input sample.
        Combine probability of each feature
        """
        probabilities = {}
        for class_model in self.classification_model:
            classValue = class_model['class_id']
            classModels = class_model['features']
            print(classValue, " ", classModels)
            probabilities[classValue] = 1
            for i in range(len(classModels)):
                (mean, stdev) = (classModels[i]['mean'],
                                 classModels[i]['stdev'])
                x = self.input_vector[i]
                # print(f"{x}, {mean}, {stdev}")
                probabilities[classValue] *= self.calculate_pdf(x, mean, stdev)
        return probabilities

    def calculate_pdf(self, x, mean, stdev):
        x_minus_mean = float(x) - float(mean)
        """Calculate probability using gaussian density function"""
        if stdev == 0.0:
            if x == mean:
                return 1.0
            else:
                return 0.0
        exponent = math.exp(-(math.pow(x_minus_mean, 2) /
                            (2 * math.pow(float(stdev), 2))))
        return 1 / (math.sqrt(2 * math.pi) * stdev) * exponent

    def predict(self, inputVector):
        """
        Compare probability for each class.
        Return the class label which has max probability.
        """

        self.input_vector = inputVector
        probabilities = self.calculate_class_probabilities()
        (bestLabel, bestProb) = (None, -1)
        for (classValue, probability) in probabilities. items():
            if bestLabel is None or probability > bestProb:
                bestProb = probability
                bestLabel = classValue
        self.status = f"Object width: {inputVector[1]}" + \
            f", Object height: {inputVector[0]} \n" + \
            f"Image classified as {bestLabel} " + \
            f"with probability = {bestProb}"

    pass


class SeedClassificationApp(App):
    def build(self):
        return SeedClassification()
    pass


Factory.register('SeedClassification', cls=SeedClassification)
Factory.register('LoadDialog', cls=LoadDialog)

if __name__ in ('__main__', '__android__'):
    SeedClassificationApp().run()
