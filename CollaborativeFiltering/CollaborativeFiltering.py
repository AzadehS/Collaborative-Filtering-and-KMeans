# -*- coding: utf-8 -*-
"""
@author: AzadehSamadian
"""

import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import pairwise_distances
from sklearn.metrics import mean_squared_error, mean_absolute_error
from math import sqrt


def predict(ratings, similarity):
    mean_user_rating = ratings.mean(axis=1)
    # np.newaxis : mean_user_rating has same format as ratings
    ratings_diff = (ratings - mean_user_rating[:, np.newaxis])
    pred = mean_user_rating[:, np.newaxis] + similarity.dot(ratings_diff) / np.array([np.abs(similarity).sum(axis=1)]).T
    return pred


def rmse(prediction, ground_truth):
    prediction = prediction[ground_truth.nonzero()].flatten()
    ground_truth = ground_truth[ground_truth.nonzero()].flatten()
    return sqrt(mean_squared_error(prediction, ground_truth))


def mae(prediction, ground_truth):
    prediction = prediction[ground_truth.nonzero()].flatten()
    ground_truth = ground_truth[ground_truth.nonzero()].flatten()
    return mean_absolute_error(prediction, ground_truth)


header = ['movie_id', 'user_id', 'rating']

train_data = pd.read_csv('./netflix/TrainingRatings.txt', sep=',', names=header)
test_data = pd.read_csv('./netflix/TestingRatings.txt', sep=',', names=header)

users = train_data.user_id.unique()

usersDic = {}
count = 0
for user in users:
    usersDic[user] = count
    count += 1

items = train_data.movie_id.unique()
itemDic = {}
count = 0
for item in items:
    itemDic[item] = count
    count += 1

# print (usersDic)
# print (itemDic)


n_users = len(usersDic)
n_items = len(itemDic)
#print 'Number of users = ' + str(n_users) + ' | Number of movies = ' + str(n_items)
print ("Number of users: "+str(n_users)+ " | Number of movies: "+ str(n_items))

# Create two user-item matrices, one for training and another for testing
train_data_matrix = np.zeros((n_users, n_items))
for line in train_data.itertuples():
    train_data_matrix[usersDic[line[2]], itemDic[line[1]]] = line[3]

test_data_matrix = np.zeros((n_users, n_items))
for line in test_data.itertuples():
    test_data_matrix[usersDic[line[2]], itemDic[line[1]]] = line[3]

user_similarity = pairwise_distances(train_data_matrix, metric='euclidean')

user_prediction = predict(train_data_matrix, user_similarity)
#print 'Memory-based RMSE: ' + str(rmse(user_prediction, test_data_matrix))
print ("Memory-based RMSE: " + str(rmse(user_prediction, test_data_matrix)))


#print 'Memory-based MAE: ' + str(mae(user_prediction, test_data_matrix))
print ("Memory-based MAE: " + str(mae(user_prediction, test_data_matrix)))

