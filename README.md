# Pear

## Build Instructions

0. Install Android Studio
1. Clone this repository
2. Go to pinned messages in `#pear-android` and download `keys.properties` and `google-services.json`. Place `keys.properties` in `pear-android`, and `google-services.json` in `pear-android/app`
3. Open the project in Android Studio
4. Get your computer's SHA-1 by running the `signingReport` task [through Gradle](https://stackoverflow.com/questions/67600259/how-to-get-sha1-and-sha256-in-android-studio-4-2-1). Add that SHA-1 to the Pear Firebase project so you can sign in through Google during development

## Libraries and External Services

- [Google Sign In](https://developers.google.com/identity/sign-in/android): user sign-in
- [Google Firebase](https://firebase.google.com/docs/android/setup): analytics and Google services
- [OkHttp](https://github.com/square/okhttp): HTTP client for networking
- [Moshi](https://github.com/square/moshi): JSON library for converting between JSON and Kotlin classes
- [Android Image Cropper](https://github.com/ArthurHub/Android-Image-Cropper): image cropping
- [Compressor](https://github.com/zetbaitsu/Compressor): image compression
- [Glide](https://github.com/bumptech/glide): image loading

## Directory Structure

```
coffee_chats_android
+---adapters
+---fragments
    +---messaging
+---models
+---networking
+---utils
```

- Activities are located directly under `coffee_chats_android`
- Fragments and adapters are located in their corresponding folders
- `models` contain the data models for communicating with the backend; we use Moshi to convert between JSON and these data classes
- `networking` contains helper functions for communicating with the backend. To add a new type of networking request, add the appropriate endpoint to `UserEndpoints.kt`, and add the function for the network call in `NetworkingUtils.kt`. To make that networking request, call your helper function in `NetworkingUtils.kt` from inside a `CoroutineScope`
- `utils` contains helper files and methods that don't fall into any of the previous categories

## Contributions

If you want to help contribute or improve Pear, feel free to submit any issues or pull requests.

## Made by Cornell AppDev

Cornell AppDev is an engineering project team at Cornell University dedicated to designing and developing mobile applications. We were founded in 2014 and have since released apps for Cornell and beyond, like [Eatery](https://play.google.com/store/apps/details?id=com.cornellappdev.android.eatery&gl=US). Our goal is to produce apps that benefit the Cornell community and the local Ithaca area as well as promote open-source development with the community. We have a diverse team of software engineers and product designers that collaborate to create apps from an idea to a reality. Cornell AppDev also aims to foster innovation and learning through training courses, campus initiatives, and collaborative research and development. For more information, visit our [website](http://www.cornellappdev.com/).
