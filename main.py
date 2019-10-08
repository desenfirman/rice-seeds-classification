import kivy
import json
import math
import os
from kivy.app import App
from kivy.factory import Factory
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.gridlayout import GridLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.properties import StringProperty, ObjectProperty, ListProperty
from kivy.uix.popup import Popup


kivy.require("1.10.0")


class LoadDialog(FloatLayout):
    load = ObjectProperty(None)
    cancel = ObjectProperty(None)
    default_path = StringProperty()
    pass


class SeedClassification(GridLayout):
    """

    """
    classification_model = ObjectProperty()
    loaded_file = ObjectProperty()
    input_vector = ListProperty()
    status = StringProperty('Status: No model selected!')

    def dismiss_popup(self):
        self._popup.dismiss()

    def show_load_dialog(self):
        content = LoadDialog(load=self.load_file, cancel=self.dismiss_popup, default_path=os.getcwd())
        self._popup = Popup(title="Load file", content=content,
                            size_hint=(0.9, 0.9))
        self._popup.open()
        pass

    def load_file(self, path, filename):
        with open(os.path.join(path, filename[0]), 'r') as file_object:
            config_file = json.load(file_object)
            self.classification_model = config_file['data']
            self.status = "data/model.json model loaded. You can use to classify image now"
            print(self.classification_model)
            self.dismiss_popup()
        pass


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
                (mean, stdev) = (classModels[i]['mean'], classModels[i]['stdev'])
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
        exponent = math.exp(-(math.pow(x_minus_mean, 2) / (2 * math.pow(float(stdev), 2))))
        return 1 / (math.sqrt(2 * math.pi) * stdev) * exponent

    def predict(self, inputVector):
        """
        Compare probability for each class.
        Return the class label which has max probability.
        """
        if self.classification_model == None:
            self.status = "Please import your classification model before classify the image."
            return
        self.input_vector = inputVector
        probabilities = self.calculate_class_probabilities()
        (bestLabel, bestProb) = (None, -1)
        for (classValue, probability) in probabilities. items():
            if bestLabel is None or probability > bestProb:
                bestProb = probability
                bestLabel = classValue
        self.status = f"Image classified as {bestLabel} with probability = {bestProb}"

    pass


class SeedClassificationApp(App):
    def build(self):
        return SeedClassification()
    pass


Factory.register('SeedClassification', cls=SeedClassification)
Factory.register('LoadDialog', cls=LoadDialog)

if __name__ in ('__main__', '__android__'):
    SeedClassificationApp().run()
