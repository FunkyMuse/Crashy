# Crashy
### A small Android library written entirely in Kotlin to collect crash reports and save them to storage.

[![](https://jitpack.io/v/CraZyLegenD/Crashy.svg)](https://jitpack.io/#CraZyLegenD/Crashy)
 [![Kotlin](https://img.shields.io/badge/Kotlin-1.3.72-blue.svg)](https://kotlinlang.org) [![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/guide/) 
![API](https://img.shields.io/badge/Min%20API-21-green)
![API](https://img.shields.io/badge/Compiled%20API-30-green)


## Usage
1. Add JitPack to your project build.gradle

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
   }
}
```

2. Add the dependency in the application build.gradle

```gradle
dependencies {
    // androidX startup for auto-init
    implementation "androidx.startup:startup-runtime:1.0.0-alpha01"

    //crashy
    implementation 'com.github.CraZyLegenD:Crashy:$version'
  }
```

3. In your application build.gradle add

```gradle
   compileOptions {
        sourceCompatibility = 1.8
        targetCompatibility = 1.8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
```
4. Inside your AndroidManifest.xml file
```xml
 <provider
   android:name="androidx.startup.InitializationProvider"
   android:authorities="${applicationId}.androidx-startup"
   android:exported="false"
   tools:node="merge">
       <meta-data
          android:name="com.crazylegend.crashyreporter.initializer.CrashyInitializer"
          android:value="androidx.startup" />
</provider>
```

## Screens of how the stack trace info looks like
<img src="https://raw.githubusercontent.com/CraZyLegenD/Crashy/master/screens/screen_1.png" width="33%"></img>
<img src="https://raw.githubusercontent.com/CraZyLegenD/Crashy/master/screens/screen_2.png" width="33%"></img>
<img src="https://raw.githubusercontent.com/CraZyLegenD/Crashy/master/screens/screen_3.png" width="33%"></img>

5. How to use?

Get logs
```kotlin
//as a list of strings
CrashyReporter.getLogsAsStrings()

//as a list of files
CrashyReporter.getLogFiles()
```
Get all logs and purge them afterwards
```kotlin
//as a list of strings
CrashyReporter.getLogsAsStringsAndPurge()

//as a list of files
CrashyReporter.getLogFilesAndPurge()
```
Manually log an exception
```kotlin
CrashyReporter.logException(thread: Thread, throwable: Throwable)

CrashyReporter.logException(exception: Throwable)
```
Purge logs
```kotlin
CrashyReporter.purgeLogs()
```
Get dump folder
```kotlin
val folder: File = CrashyReporter.dumpFolder
```


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
