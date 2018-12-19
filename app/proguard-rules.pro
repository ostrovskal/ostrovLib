-optimizationpasses 3
-overloadaggressively
#-keep public class * extends android.view.View {
#    public <init>(android.content.Context);
#}
-keep public class ru.ostrovskal.ostrovlib.ExampleSurface {
    public <init>(android.content.Context);
}

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
