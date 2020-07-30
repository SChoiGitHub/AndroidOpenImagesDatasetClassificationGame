A small Android application that uses images from the [Open Images Dataset (Version 6)](https://storage.googleapis.com/openimages/web/index.html).

Specifically, the images are pulled from a small sample of the testing subset of the dataset.

How the dataset in this application was made is described in "AggregateOpenImageDataset.ipynb", which was used in Google Colab. The raw data itself is in ".csv" files in "app/src/main/res/raw".

About the application, its a small game where the player is given a section of a random picture from the dataset and must choose the correct classification amongst four of them. An issue is that some sections can be multiple classifications. For example, a picture of a Woman's Face can be classified as "Human Hair" or "Face" but the app only supposes one of them is correct.

Additionally, the app was only tested on an emulated "Pixel 2 API 26". The interface may be broken on other devices.