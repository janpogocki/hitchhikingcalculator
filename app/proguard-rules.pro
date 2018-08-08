# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Jan\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-keep class android.** { *; }
-keep interface android.** { *; }
-keep public class android.** { *; }

-keep class com.google.** { *; }
-keep interface com.google.** { *; }
-keep public class com.google.** { *; }

-keep class com.crashlytics.** { *; }
-keep interface com.crashlytics.** { *; }
-keep public class com.crashlytics.** { *; }

-keep class io.fabric.** { *; }
-keep interface io.fabric.** { *; }
-keep public class io.fabric.** { *; }