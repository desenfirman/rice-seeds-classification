"""
Implementation of Gaussian Naive Bayes Classification.
The code is written from scratch and does NOT use existing functions or packages which can provide the Naive Bayes
Classifier class or fit/predict function (e.g. sklearn).

I use Bank note authentication dataset, which can be downloaded from
http://archive.ics.uci.edu/ml/datasets/banknote+authentication. Complete description of the
dataset can be also found on that web-page.

The Implementation and some of it's functions are taken from this website (https://machinelearningmastery.com/naive-bayes-classifier-scratch-python/)

Author: Shaishav Jogani (https://www.linkedin.com/in/shaishavjogani/)
"""

import numpy as np
from random import randrange
import csv
import math
from PIL import Image
from PIL.ExifTags import TAGS


def load_csv_dataset(filename):
    """Load the CSV file"""
    lines = csv.reader(open(filename), delimiter=",")
    next(lines, None)

    data = []
    for line in lines:
        if not len(line) < 1:
            data.append(line)

    dataset = list(data)
    class_list = np.unique([x[len(x) - 1] for x in dataset]).tolist()
    for i in range(len(dataset)):
        # dataset[i][len(dataset[i]) - 1] = class_list.index(dataset[i][len(dataset[i]) - 1])
        # Convert String to Float numbers
        dataset[i] = [float(x) if type(
            x) is not str else x for x in dataset[i]]

    # print(dataset)
    return dataset


def mean(numbers):
    """Returns the mean of numbers"""
    # numbers = np.array(numbers).astype(np.float)
    return np.mean(numbers)


def stdev(numbers):
    """Returns the std_deviation of numbers"""
    # numbers = np.array(numbers).astype(np.float)
    return np.std(numbers)


def variance(numbers):
    return np.var(numbers)


def sigmoid(z):
    """Returns the sigmoid number"""
    return 1.0 / (1.0 + math.exp(-z))


def cross_validation_split(dataset, n_folds):
    """Split dataset into the k folds. Returns the list of k folds"""
    dataset_split = list()
    dataset_copy = list(dataset)
    fold_size = int(len(dataset) / n_folds)
    for i in range(n_folds):
        fold = list()
        while len(fold) < fold_size:
            index = randrange(len(dataset_copy))
            popped = dataset_copy.pop(index)
            fold.append(popped)
        dataset_split.append(fold)
    return dataset_split


def accuracy_metric(actual, predicted):
    """Calculate accuracy percentage"""
    correct = 0
    for i in range(len(actual)):
        if actual[i] == predicted[i]:
            correct += 1
    return correct / float(len(actual)) * 100.0


def evaluate_algorithm(dataset, algorithm, n_folds, ):
    import copy
    """Evaluate an algorithm using a cross validation split"""
    folds = cross_validation_split(dataset, n_folds)
    # print(folds, "\n")
    scores = list()
    for fold in folds:
        train_set = list(copy.deepcopy(folds))
        # print(fold)
        # print("\n", train_set )
        train_set.remove(fold)
        # print("\n", train_set )
        # print("\n", train_set )
        test_set = list()
        # print(fold)
        for row in fold:
            row_copy = list(row)
            test_set.append(row_copy)
            row_copy[-1] = None
        train_set = sum(train_set, [])
        # print(f"Train set:\n{train_set}\nTest set: {test_set}")
        predicted = algorithm(train_set, test_set, )
        actual = [row[-1] for row in fold]
        accuracy = accuracy_metric(actual, predicted)
        # print(accuracy)
        scores.append(accuracy)
        train_set = None
    return scores


#############################
#############################
######## Naive Bayes  #######
#############################
#############################


def separate_by_class(dataset):
    """Split training set by class value"""
    separated = {}
    for i in range(len(dataset)):
        row = dataset[i]
        if row[-1] not in separated:
            separated[row[-1]] = []
        separated[row[-1]].append(row)
    return separated


def model(dataset):
    """Find the mean and standard deviation of each feature in dataset"""
    for x in range(len(dataset)):
        dataset[x].pop()
    # print(dataset)
    models = [
        {
            "mean": mean([float(i) for i in attribute]),
            "variance": variance([float(i) for i in attribute]),
            "n_data": len(attribute),
            "feature_id": idx
        } for idx, attribute in enumerate(zip(*dataset))]
    return models


def model_by_class(dataset):
    """find the mean and standard deviation of each feature in dataset by their class"""
    separated = separate_by_class(dataset)
    # print(separated)
    class_models = {}
    for (classValue, instances) in separated.items():
        # print(instances, ", ", classValue)
        # class_models.append({
        #     "class_id": classValue,
        #     "features": model(instances)
        # })
        class_models[classValue] = model(instances)
    # print(class_models)
    return class_models


def calculate_pdf(x, mean, stdev):
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


def calculate_class_probabilities(models, input):
    """Calculate the class probability for input sample. Combine probability of each feature"""
    probabilities = {}
    for classValue, classModels in models.items():
        # print(classValue, " ", classModels)
        probabilities[classValue] = 1
        for i in range(len(classModels)):
            (mean, variance) = (classModels[i]['mean'], classModels[i]['variance'])
            stdev = np.power(variance, 2)
            x = input[i]
            # print(f"{x}, {mean}, {stdev}")
            probabilities[classValue] *= calculate_pdf(x, mean, stdev)
    return probabilities


def predict(models, inputVector):
    """Compare probability for each class. Return the class label which has max probability."""
    probabilities = calculate_class_probabilities(models, inputVector)
    (bestLabel, bestProb) = (None, -1)
    for (classValue, probability) in probabilities. items():
        if bestLabel is None or probability > bestProb:
            bestProb = probability
            bestLabel = classValue
    return bestLabel


def getPredictions(models, testSet):
    """Get class label for each value in test set."""
    predictions = []
    for i in range(len(testSet)):
        result = predict(models, testSet[i])
        predictions.append(result)
    return predictions


def naive_bayes(train, test, ):
    """Create a naive bayes model. Then test the model and returns the testing result."""
    summaries = model_by_class(train)
    predictions = getPredictions(summaries, test)
    return predictions


def main_naive_bayes(n_folds=10):
    # load and prepare data
    filename = 'data/data.csv'
    dataset = load_csv_dataset(filename)

    # std_dataset = standardize(dataset)

    print("------------- Step 2:  Gaussian Naive Bayes ---------------")
    accuracy_naive = evaluate_algorithm(dataset, naive_bayes, n_folds)
    print("Processing Naive Bayes Classification")
    print('Accuracy in each fold: %s' % accuracy_naive)
    print('K-Fold Accuracy: %f percent' % (sum(accuracy_naive) / len(accuracy_naive)))
    # test_set = [60.508402768012516,1000.033355903686, None]
    # result = predict(model_by_class(dataset), test_set)
    # print(f"Class: {result}")
    # import json

    # model_builder = {}
    # model_builder["number_of_features"] = len(dataset[0]) - 1
    # model_builder["data"] = model_by_class(dataset)

    # print(model_by_class(dataset))
    # print(class_list.iloc[0, 0][0])
    # class_list.to_json("model.json")
    # print(predict)
    return model_by_class(dataset)
