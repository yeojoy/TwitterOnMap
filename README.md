# Twitter on Map
This app is gathering tweets near you with specific radius, and then display tweets on the google map.

It displays maximum 100 tweets.


## How to run
1. This app needs Twttier Consumer Key and Consumer Secret to login and to get tweets.
- location : app/src/main/res/values/twitters.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- for Twitter -->
    <string name="twitter_consumer_key">YOUR TWITTER CONSUMER KEY</string>
    <string name="twitterPconsumer_secret">YOUR TWITTER CONSUMER SECRET</string>
</resources>
```

2. Also it needs Google map api key
- location : app/src/release/res/values/google_maps_api.xml
- There is debug signing key in ${PROJECT_PATH}/signingkey for google map api key

```xml
<resources>
    <!--
    TODO: Before you run your application, you need a Google Maps API key.

    To get one, follow this link, follow the directions and press "Create" at the end:

    https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=96:29:CC:A4:ED:6C:DB:60:38:3D:02:9D:98:CC:97:17:AB:A0:45:53%3Bme.yeojoy.twitteronmap

    You can also add your credentials to an existing key, using these values:

    Package name:
    96:29:CC:A4:ED:6C:DB:60:38:3D:02:9D:98:CC:97:17:AB:A0:45:53

    SHA-1 certificate fingerprint:
    96:29:CC:A4:ED:6C:DB:60:38:3D:02:9D:98:CC:97:17:AB:A0:45:53

    Alternatively, follow the directions here:
    https://developers.google.com/maps/documentation/android/start#get-key

    Once you have your key (it starts with "AIza"), replace the "google_maps_key"
    string in this file.
    -->
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">AIzaSyBaWyAPgrAYDg3hlsybNQ8O0Tum1hipzrA</string>
</resources>
```

## TO DO
1. Display tweets on the google map
2. (if needed) Setup random coordinates for tweet without geo information
3. add a function to modify radius
4. When any each marker clicks, display tweet's brief information like text, name, timestamp
5. When swipe up bottom sheet or it clicks, move to another detail activity, and then display tweet's detail information
6. Add two functions, RETWEET and LIKE.
7. Add search function and an activity to display results
8. Display photos and play videos
9. translate default string resources to French

## Problems
Most of tweets have no geo information. Maybe there are 1 or 3 in 100 tweets.

