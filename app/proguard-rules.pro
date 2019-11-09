-optimizationpasses 3
-overloadaggressively
#-keep public class * extends android.view.View {
#    public <init>(android.content.Context);
#}
-keep class ru.ostrovskal.ostrovlib.** { *; }

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static *** checkReturnedValueIsNotNull(...);
    public static *** checkParameterIsNotNull(...);
    public static *** checkExpressionValueIsNotNull(...);
    public static *** checkExpressionValuesIsNotNull(...);
    public static *** throwNpe(...);
}
-repackageclasses ''
-allowaccessmodification
#-keep class ru.ostrov.droid.form.views.Form*
-dontobfuscate
#-dontshrink
#-keepattributes *Annotation*,SourceFile,LineNumberTable
