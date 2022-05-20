## ActivityResultPermission - Android Runtime permissions & receiving results w/ ActivityResult API
### [Sample app (apk)](https://github.com/khoben/activityresult/releases/latest/download/sample.apk)
### Usage

0. Prepare AndroidManifest (configure app permissions)

    Declare all required permission requests in [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml):
    ```xml
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ```
1. Create and register request launcher in Activity or Fragment

    ```kotlin
   val permission = PermissionRequest()
   permission.register(this)
   
   val imagePicker = GetContentUriLauncher()
   imagePicker.register(this)
    ```
2. Run

    ```kotlin
    permissions.launch(
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
        onDenied = { denied, isCancelled -> showToast("onDenied $denied $isCancelled") },
        onExplained = { explained -> showToast("onExplained $explained") },
        onGranted = { showToast("Granted") }
    )
   
   imagePicker.launch("image/*") {
        success = { imageUri -> setImageAsBackground(imageUri) }
        failed = { cause -> showToast("Image picker failed: $cause") }
   }
    ```
3. How to create custom result request

    3.1 Create your own contract: [example](arresult/src/main/java/io/github/khoben/arresult/contract/TakeVideoUriContract.kt)
    
    3.2 Create launcher: extend [`BaseLauncher`](arresult/src/main/java/io/github/khoben/arresult/launcher/BaseLauncher.kt), [example](arresult/src/main/java/io/github/khoben/arresult/launcher/GetContentUriLauncher.kt)
### Installation
Latest version is⠀[![](https://jitpack.io/v/khoben/activityresult.svg)](https://jitpack.io/#khoben/activityresult)
```bash
# Top-level build.gradle
repositories {
    ...
    maven { url 'https://jitpack.io' } # <-- Add this
}
```
```bash
# Permission request
implementation 'com.github.khoben.activityresult:permission:<latest_version>'
# Result request
implementation 'com.github.khoben.activityresult:result:<latest_version>'
```
