# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/liaoheng/Programs/sdk/android/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-dontwarn com.flyco.systembar.**
-dontwarn okio.**
#-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn org.joda.time.**
#-dontwarn butterknife.**
-dontwarn com.squareup.**
#-dontwarn org.apache.commons.io.**
#-dontwarn org.greenrobot.eventbus.**
#-dontwarn com.bumptech.glide.**
#-dontwarn jp.wasabeef.glide.transformations.**
-dontwarn com.fasterxml.jackson.**
#-dontwarn net.yslibrary.licenseadapter.**
#-dontwarn com.orhanobut.logger.**
#-dontwarn com.mugen.**
-dontwarn com.jakewharton.**
-dontwarn rx.**
#-dontwarn com.trello.rxlifecycle.**
#-dontwarn com.r0adkll.slidr.**
#-dontwarn com.h6ah4i.android.tablayouthelper.**