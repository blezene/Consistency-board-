# Consistency Board (Android)

Your consistency board packaged as an Android app. It shows `app/src/main/assets/index.html`
inside a WebView, and saves everything on the phone (localStorage).

## Option A: build with GitHub (no Android Studio needed)
1. Create a free GitHub account and a new repository.
2. Upload every file in this folder. The hidden `.github/workflows/build-apk.yml` file must be included.
   If the web uploader skips it, use "Add file > Create new file", name it `.github/workflows/build-apk.yml`
   and paste the contents of that file.
3. Open the repository's **Actions** tab, choose **Build APK**, tap **Run workflow**.
4. When it finishes (a few minutes), open the run and download the **consistency-board-apk** artifact.
   Unzip it to get `app-debug.apk`.
5. Copy the APK to your phone and open it. Allow "Install unknown apps" for your file manager or
   browser when Android asks.

## Option B: Android Studio
1. Install Android Studio and choose File > Open, then select this folder.
2. Let Gradle sync, then Build > Build Bundle(s) / APK(s) > Build APK(s).
3. The file is at `app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- Requires Android 8.0 (API 26) or newer.
- The APK is signed with a debug key. That is fine for installing on your own phone.
- Export backup opens the Android share sheet (save to Drive, Files, WhatsApp, etc.).
  Import backup lets you pick that saved file.
- Browser notifications are not available inside an Android WebView. Reminders show inside the app while it is open.
- To change the app later, edit `app/src/main/assets/index.html` and rebuild.
