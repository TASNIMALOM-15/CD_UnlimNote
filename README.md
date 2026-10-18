# UniNotes 📝

UniNotes is an advanced, offline-first Notebook, Checklist, and Attachment Manager app built with modern Android Jetpack Compose and Material Design 3.

---

## 📱 How to Build & Download APK using GitHub on your Android Phone (মোবাইলে APK ডাউনলোড করার নিয়ম)

### Step 1: Upload this Project to GitHub (GitHub-এ আপলোড করুন)
1. Download this project as a ZIP file from Google AI Studio (or push directly via Git).
2. Go to [github.com](https://github.com) on your phone or computer and create a new repository (e.g. `UniNotes`).
3. Upload all files from this project to your GitHub repository and commit.

### Step 2: Automatic Build with GitHub Actions (অটোমেটিক APK তৈরি)
1. As soon as you commit or push, go to the **Actions** tab in your GitHub repository.
2. You will see the **Build Android Debug APK** workflow running.
3. The build takes about 2 to 3 minutes to complete.

### Step 3: Download the APK on your Phone (ফোনে APK ডাউনলোড করুন)
1. Once the workflow run shows a green checkmark (`✔`), tap on it.
2. Scroll down to the **Artifacts** section at the bottom of the page.
3. Tap on **UniNotes-Debug-APK** to download the ZIP file.
4. Extract the ZIP on your Android phone and install `app-debug.apk` directly!

---

## ✨ Features Included

- **Notebook & Text Editor**: Rich text formatting (Bold, Italic, Underline, Strikethrough, Code, Lists, Color swatches), character/word count, reading time, undo/redo.
- **Checklist & To-Do Mode**: Interactive checklists, completion opacity, quick task bar, reordering.
- **Visual Design matching Reference**:
  - Colorful card grid view & list view.
  - Multi-tab Color Picker: Material swatches, Custom HSV spectrum wheel, RGB/Hex values, and Clear.
  - Category manager with custom color dots and "Apply color to cards" toggle.
  - Bottom navigation dock with Search, View switcher, Quick edit, To-do list, and elevated FAB.
- **Security & Privacy**:
  - Biometric sign-in & auto-trigger.
  - PIN code protection.
  - Locked notes & folders.
  - AES-256-GCM local end-to-end encryption.
- **Backup & Restore**:
  - Full ZIP / JSON / Markdown export.
  - Offline local storage with Jetpack Room.
  - 100% private, no mandatory cloud, works completely offline.
