# UniNotes: Advanced Notebook & Checklist App

UniNotes is a high-performance, offline-first personal notebook, checklist, and attachment manager for Android. Inspired by the provided screenshots, it delivers a modern colorful card interface, rich text formatting toolbar, customizable color picker with Material/custom color tabs, category tagging, PIN & biometric locking, E2EE AES-256 backup/restore, and automated GitHub Actions APK generation.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during clarification and establish the foundation of the app:

- **Confirmed Decision 1 (Editor Style)**: The default note creation mode opens directly into the **Rich Text Editor with formatting toolbar**, with an instant toggle to switch to checklist / to-do mode whenever needed.
- **Confirmed Decision 2 (Security Configuration)**: Security (App Lock, PIN, and Biometrics) is kept **optional** and configurable from the Settings screen with a seamless setup wizard.
- **Confirmed Decision 3 (Theme & Palette)**: **Foxy Orange** is configured as the default accent theme, paired with colorful card backgrounds and full user customization (Sky Blue, Sakura Red, Night Town Purple, Forest Green, AMOLED Black, and Material You).
- **Offline-First & Local Storage**: All notes, checklists, folders, and attachments are persisted locally using **Room Database** and internal device storage for zero latency, zero internet requirement, and total user privacy.
- **E2EE AES-256 Backup & Export**: Encrypted local backup files (ZIP / JSON) using password-derived keys (PBKDF2 + AES-256-GCM), with export options for TXT, Markdown, and HTML.
- **GitHub Actions Automation**: A production-ready `.github/workflows/build-apk.yml` file is included in the project to automatically build a debug APK and provide a downloadable artifact directly from GitHub.

---

## 1. Overview & Core Concept

- **What It Does**: A comprehensive digital notebook and task organizer that lets users create rich formatted notes, organize checklists, attach files and photos, group into nested categories and folders, lock sensitive content behind biometric/PIN security, and customize every visual aspect from card colors to font styling.
- **Target Audience / Persona**: Students, professionals, creative writers, and privacy-conscious individuals who want a clean, lightning-fast note-taking app that doesn't hold data hostage behind mandatory cloud subscriptions or surveillance.
- **Key Value**: Full feature richness without cloud lock-in. Complete visual elegance matching modern Material 3 aesthetics with exact fidelity to the user's reference designs.

---

## 2. User Experience & Visual Design

The UI directly incorporates the layout, spacing, and styling demonstrated in the user's screenshots:

### Key Screens & User Flows

1. **All Notes Screen (Home Screen)**:
   - **Top App Bar**: Hamburger menu button to slide out the sidebar drawer, screen title ("All Notes", "Starred", "Archive", or current Category), search icon, sort/filter button, and user profile/account avatar.
   - **Notes Container**: Adaptive 2-column Grid View (or 1-column List View per settings toggle) with colorful note cards displaying title, content preview (configurable line count), date stamp (e.g. "18 Oct 26"), pin pin badge, and star indicators.
   - **Bottom Action Dock**: Floating docked bottom bar featuring quick search, layout toggle (grid/list), quick edit, to-do list mode shortcut, and an elevated tonal Material 3 Floating Action Button (`+`) to create notes.
2. **Note Editor Screen**:
   - **Top Navigation Bar**: Back navigation arrow, editable note title (defaults to "My Note"), attachment paperclip icon, bookmark/favorite toggle, and overflow menu (Share, Lock, Change Color, Move Category, Export, Delete).
   - **Note Body**: Distraction-free editing area with auto-save indicator, word/character counter, and dynamic background tint.
   - **Rich Text Toolbar**: Docked bottom formatting bar with iconic buttons: Undo (`↺`), Redo (`↻`), Bold (`B`), Italic (`I`), Underline (`U`), Strikethrough, Code, Bullet List, Numbered List, Checklist item, Text Color, Background Highlight, Indent, and Insert Date/Time.
3. **Advanced Color Selector Modal (BottomSheet / Dialog)**:
   - Exactly matches the reference screenshots with three tabs: **Material**, **Color**, and **Clear**.
   - **Material Tab**: Vertical rainbow palette column with horizontal shade swatches for rapid selection.
   - **Color Tab**: Interactive HSV 2D gradient spectrum canvas, hue rainbow slider, opacity/alpha slider, numeric RGB readouts, hex input field, and preview swatch with "Ok" confirmation.
4. **Categories & Folder Management Dialog**:
   - Matches reference screenshot: category list with vertical ribbon icon in category color, delete cross icon, input field to "Create a new category", horizontal row of 8 selectable color dots, and "Apply color to note cards" toggle switch with a styled "Save Category" button.
5. **Sidebar Drawer Menu**:
   - Quick navigation items: All Notes, All Files & Attachments, Starred Notes, Pinned Notes, Archived Notes, Categories & Folders, Trash / Recycle Bin, Calendar View, Note Templates, and Settings.
6. **Settings Screen**:
   - Clean dark-mode optimized grouped list (General, Security, Notes, Category & Folder, Theme & Colors).
   - Switch toggles with descriptive subtitle captions (e.g., "Biometric sign in - Protect notes with your biometrics", "Readonly Mode", "Scrollbar in Notes Overview").

```
┌─────────────────────────────────────────────────────────────┐
│ ≡   All Notes                           🔍  ⇅  👤           │
├──────────────────────────────┬──────────────────────────────┤
│  Ideas 💡                    │  Passwords & Auth            │
│  YouTube automation          │  AI of google accounts and   │
│  Create a personal web app   │  proton accounts             │
│                              │                              │
│  18 Oct 26                   │  18 Oct 26                   │
├──────────────────────────────┼──────────────────────────────┤
│  Account information         │  My Note                     │
│                              │  A                           │
│  18 Oct 26                   │  18 Oct 26                   │
├──────────────────────────────┴──────────────────────────────┤
│  [ 🔍 ]   [ ▦ ]   [ ✎ ]   [ ☑ ]               [ ( + ) ]    │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Key Product Decisions & Trade-Offs

- **Room Database with SQLite**:
  - *Chosen Approach*: Android Jetpack Room with Kotlin Coroutines Flow for reactive note and category streaming.
  - *Why*: Guaranteed offline performance, crash resilience, fast search queries with SQLite `LIKE` and indexing, and compatibility with low-end 32-bit ARM (armeabi-v7a) devices.
- **Client-Side Cryptography (AES-256-GCM + PBKDF2)**:
  - *Chosen Approach*: Android `Cipher` using `AES/GCM/NoPadding` with keys derived from user passwords using PBKDF2 with SHA-256 and secure random salt.
  - *Why*: True Zero-Knowledge end-to-end encryption for locked notes and exported backups. Cloud providers or intermediate storage cannot decrypt the user's data.
- **Compose Multiplatform Rich-Text Engine**:
  - *Chosen Approach*: `AnnotatedString` and markdown serialization engine with live visual formatting spans and interactive task items.
  - *Why*: Native Jetpack Compose performance without webview overhead or external heavy dependencies.
- **GitHub Actions Workflow File**:
  - *Chosen Approach*: Standard Ubuntu container running Gradle `assembleDebug` with `actions/upload-artifact@v4`.
  - *Why*: Allows anyone who clones or pushes the repository to GitHub to get a compiled, downloadable `.apk` within 3 minutes without requiring Android Studio on their PC.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                            │
│                                                                        │
│  ┌───────────────────────┐  ┌──────────────────┐  ┌─────────────────┐  │
│  │ NotesOverviewScreen   │  │ NoteEditorScreen │  │ ToDoListScreen  │  │
│  └──────────┬────────────┘  └────────┬─────────┘  └────────┬────────┘  │
│             │                        │                     │           │
│  ┌──────────▼────────────┐  ┌────────▼─────────┐  ┌────────▼────────┐  │
│  │ CategoryDialog & Nav  │  │ ColorPickerDialog│  │ SettingsScreen  │  │
│  └──────────┬────────────┘  └────────┬─────────┘  └────────┬────────┘  │
│             └────────────────────────┼─────────────────────┘           │
│                                      ▼                                 │
│                             MainViewModel                              │
└──────────────────────────────────────┬─────────────────────────────────┘
                                       │ StateFlow / Events
┌──────────────────────────────────────▼─────────────────────────────────┐
│                           REPOSITORY LAYER                             │
│                                                                        │
│   ┌───────────────────────────┐      ┌──────────────────────────────┐  │
│   │   NoteRepository          │      │   Security & Crypto Engine   │  │
│   │   - Notes, Categories     │      │   - AES-256-GCM / PBKDF2     │  │
│   │   - Attachments, Trash    │      │   - Biometric Prompt Bridge  │  │
│   └─────────────┬─────────────┘      └──────────────┬───────────────┘  │
└─────────────────┼───────────────────────────────────┼──────────────────┘
                  ▼                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        LOCAL DATA PERSISTENCE                          │
│                                                                        │
│   ┌───────────────────────────┐      ┌──────────────────────────────┐  │
│   │  Room Database (SQLite)   │      │   Device Filesystem Storage  │  │
│   │  - NoteEntity             │      │   - Image / Audio / PDF files│  │
│   │  - CategoryEntity         │      │   - Encrypted Backup ZIPs    │  │
│   │  - AttachmentEntity       │      │   - Exported Markdown/TXT    │  │
│   └───────────────────────────┘      └──────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

### Core Entities & Models

- `NoteEntity`: `id`, `title`, `content`, `categoryName`, `colorHex`, `isPinned`, `isStarred`, `isArchived`, `isLocked`, `isDeleted`, `deletedTimestamp`, `isTodo`, `todoItemsJson`, `createdAt`, `modifiedAt`, `fontFamily`, `fontSizeMultiplier`.
- `CategoryEntity`: `id`, `name`, `colorHex`, `iconName`, `applyColorToCards`.
- `AttachmentEntity`: `id`, `noteId`, `filePath`, `fileName`, `mimeType`, `fileSize`, `createdAt`.
- `AppSettings`: Theme name, font preference, biometric enable flags, trash auto-delete period, layout mode (grid/list), auto-save mode.

### Implementation Phases

1. **Project Dependencies & Assets**: Ensure Material 3, Room, Biometric, and Compose icons extended are properly configured in `app/build.gradle.kts` and `libs.versions.toml`.
2. **Database & Crypto Layer**: Implement `AppDatabase`, `NoteDao`, `CategoryDao`, `AttachmentDao`, and `CryptoManager` (AES-256-GCM).
3. **Core ViewModel & Repository**: Implement reactive state streams (`allNotes`, `starredNotes`, `categories`, `activeFilter`, `searchQuery`).
4. **UI Components Matching Screenshots**:
   - `ColorPickerDialog`: Dual-mode (Material grid + HSV custom wheel/sliders).
   - `CategoryDialog`: Create, pick color dot, card color toggle.
   - `NoteCard`: Distinctive rounded cards with color backgrounds, readable typography, and quick long-press context menu.
   - `EditorToolbar`: Complete icon-based rich text and markdown formatting actions.
   - `BottomDock`: Search, View mode, Edit, Checklist, and Floating Action Button.
5. **Security & Settings**: Biometric prompt integration, PIN verification modal, Settings screen with categorized switch controls.
6. **Export & Backup**: JSON/Markdown/ZIP export and import with AES-256 encryption option.
7. **CI/CD Workflow**: `.github/workflows/build-apk.yml` with clear instructions for GitHub upload and direct APK download on Android phones.
