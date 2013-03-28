Introduction
============

We recommend you to download the latest version of the Android SDK Manager. You need to install the Tools, Android 2.3.3 Gingerbread (API 10) and Android 4.0.3 Ice Cream Sandwich (API 15).

Read [Android Tools](http://developer.android.com/tools/index.html) for more information on how to procede.

Set up Android SDK
==================

```bash
root@debian-vm:/# cd /opt/
root@debian-vm:/# wget http://dl.google.com/android/android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# tar -xf android-sdk_r20.0.1-linux.tgz
root@debian-vm:/# export ANDROID_HOME=/opt/android-sdk-linux/
```

Set up an Android Virtual Device
================================

```bash
consistec@debian-vm:~$ /opt/android-sdk-linux/tools/android create avd -n TestDevice -t android-10 -c 128M
```

Check out the source code 
=========================

```bash
$ git clone gitosis@bigmama1.ads.consistec.de:syncframework.git
$ cd syncframework/
```

Build the framework
===================

```bash
$ mvn install
```

OPTIONAL: without Database setup
```bash
$ mvn install -DskipTests
```

OPTIONAL: with Android tests
```bash
$ mvn install -PandroidTest
```

Install the application on the Virtual Device
=============================================

```bash
$ /opt/android-sdk-linux/platform-tools/adb install AndroidSyncClient.apk
```

If you want to redeploy the application:
```bash
$ /opt/android-sdk-linux/platform-tools/adb install -r AndroidSyncClient.apk
```
