# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ─── Keep data classes (model & responses) ────────────────────────────────────
# These are serialized/deserialized by Gson/Moshi and must not be renamed.

-keep class com.orikino.fatty.domain.model.** { *; }
-keep class com.orikino.fatty.domain.responses.** { *; }

# Keep field names intact for JSON serialization (Gson/Moshi)
-keepclassmembers class com.orikino.fatty.domain.model.** {
    <fields>;
    <init>(...);
}
-keepclassmembers class com.orikino.fatty.domain.responses.** {
    <fields>;
    <init>(...);
}

# ─── Retrofit & OkHttp ────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# ─── Gson ─────────────────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ─── Moshi ────────────────────────────────────────────────────────────────────
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonQualifier interface *

# ─── Kotlin serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep class kotlinx.serialization.** { *; }

# ─── Hilt / Dagger ────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# ─── Kotlin Parcelize ─────────────────────────────────────────────────────────
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ─── Firebase ─────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ─── Pushy ────────────────────────────────────────────────────────────────────
-keep class me.pushy.** { *; }
-dontwarn me.pushy.**

# ─── PaperDB ──────────────────────────────────────────────────────────────────
-keep class io.paperdb.** { *; }

# ─── KBZ SDK (local AAR) ──────────────────────────────────────────────────────
-keep class com.kbzbank.** { *; }
-dontwarn com.kbzbank.**

# ─── Coil / Glide / Picasso ───────────────────────────────────────────────────
-dontwarn com.bumptech.glide.**
-dontwarn com.squareup.picasso.**

# ─── Arrow-kt ─────────────────────────────────────────────────────────────────
-keep class arrow.** { *; }
-dontwarn arrow.**

# ─── Fragments ────────────────────────────────────────────────────────────────
# Fragment.instantiate() uses Class.forName() + getConstructor() to restore
# fragments after process death. Keep all Fragment subclasses and their
# public no-arg constructors, or the app will crash with NoSuchMethodException.
-keep public class * extends androidx.fragment.app.Fragment {
    public <init>();
}
-keep public class * extends androidx.fragment.app.DialogFragment {
    public <init>();
}
-keep public class * extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {
    public <init>();
}
