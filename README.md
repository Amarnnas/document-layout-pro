 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a/README.md b/README.md
index efbc596f29e94c0d8a9dc1b0da50426b376dd1bb..ed574fe23361de8e44b2d1e9b4430c166cd5ae68 100644
--- a/README.md
+++ b/README.md
@@ -1 +1,71 @@
-# document-layout-pro
\ No newline at end of file
+# Offline Accounting Pro (Android)
+
+تطبيق محاسبة يعمل **أوفلاين بالكامل** على أندرويد، مبني بـ Kotlin + Jetpack Compose + Room.
+
+## الميزات المتوفرة الآن
+- إدارة الأصناف (اسم، SKU، سعر، مخزون).
+- إدارة العملاء.
+- إنشاء فواتير وحساب الإجمالي + الخصم + الضريبة.
+- حفظ بيانات التطبيق محليًا (بدون إنترنت).
+- تخصيص بيانات المتجر (الاسم، الهاتف، العنوان، عنوان الفاتورة).
+- تصدير الفاتورة كملف **PDF** وصورة **PNG**.
+
+## ملاحظات مهمة
+- الشعار وتخصيص أعمدة الفاتورة والألوان موجودة كأساس في نموذج البيانات ويمكن إكمال ربطها في الواجهة بسهولة.
+- التطبيق مصمم كبداية احترافية قابلة للتطوير (تقارير، صلاحيات مستخدمين، نسخ احتياطي، باركود).
+
+## التشغيل
+1. افتح المشروع في Android Studio (Hedgehog أو أحدث).
+2. انتظر Sync.
+3. شغّل التطبيق على جهاز أندرويد أو محاكي.
+
+## تحويل المشروع إلى APK (خطوة بخطوة)
+
+### الطريقة الأسهل (من Android Studio)
+1. من القائمة اختر: **Build**
+2. ثم اختر: **Build Bundle(s) / APK(s)**
+3. ثم: **Build APK(s)**
+4. بعد انتهاء البناء اضغط **locate** لفتح مسار الملف.
+
+المسار غالبًا يكون:
+`app/build/outputs/apk/debug/app-debug.apk`
+
+### نسخة جاهزة للنشر (Signed APK)
+1. من القائمة: **Build > Generate Signed Bundle / APK**.
+2. اختر **APK**.
+3. أنشئ أو اختر ملف التوقيع `keystore.jks`.
+4. اختر `release` ثم Finish.
+
+المسار غالبًا يكون:
+`app/build/outputs/apk/release/app-release.apk`
+
+> مهم: نسخة `release` تحتاج توقيع حتى تعمل كتطبيق توزيع رسمي.
+
+### البناء من الطرفية (Terminal)
+```bash
+gradle assembleDebug
+```
+
+ولنسخة النشر:
+```bash
+gradle assembleRelease
+```
+
+### إذا ظهر خطأ Java / Gradle
+- هذا المشروع مضبوط على **Java 17**.
+- داخل Android Studio اختر JDK 17 من:
+  `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`.
+- ثم أعد Sync وجرّب البناء مرة ثانية.
+
+## البنية
+- `app/src/main/java/com/example/offlineaccounting/data`: قواعد البيانات والكيانات.
+- `app/src/main/java/com/example/offlineaccounting/domain`: المستودع.
+- `app/src/main/java/com/example/offlineaccounting/ui`: الواجهات و ViewModel.
+- `app/src/main/java/com/example/offlineaccounting/utils`: إنشاء PDF/صور.
+
+## الخطوة التالية (لو أردت نسخة تجارية كاملة)
+- إضافة نظام سندات قبض/صرف.
+- ضريبة قيمة مضافة متقدمة.
+- طباعة حرارية (Bluetooth / USB).
+- تعدد مستخدمين وصلاحيات.
+- مزامنة اختيارية مع سحابة (مع بقاء الأوفلاين أساسي).
 
EOF
)
