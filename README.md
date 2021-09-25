## ActivityResultPermission - Android Runtime permissions w/ ActivityResult API
### Usage

0. Prepare AndroidManifest

    Declare all required permission requests in [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml):
    ```xml
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ```
1. Create and register permission request launcher in Activity or Fragment

    ```kotlin
   val permission = PermissionRequest()
   permission.register(this)
    ```
2. Run something with permissions

    ```kotlin
    permission.launch(
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
        onDenied = { permissions, isCancelled ->
            Toast.makeText(this, "onDenied $permissions $isCancelled", Toast.LENGTH_SHORT).show()
        },
        onExplained = { permissions ->
            Toast.makeText(this, "onExplained $permissions", Toast.LENGTH_SHORT).show()
        }
    ) { // All requested permission granted
        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
    }
    ```
### Installation
Latest version is⠀[![](https://jitpack.io/v/khoben/arpermission.svg)](https://jitpack.io/#khoben/arpermission)
```bash
# Top-level build.gradle
repositories {
    ...
    maven { url 'https://jitpack.io' } # <-- Add this
}
```
```bash
implementation 'com.github.khoben.arpermission:<latest_version>'
```

### [Sample app](app/src/main/java/io/github/khoben/sample/MainActivity.kt)
