# Contact Management App Implementation Plan (UCENM Exam)

Complete implementation of a Contact Management Android application in Java covering Model, SQLite database helper, Registration Screen, Contact List Screen with CRUD, filtering, image preview, sharing, updating, deleting, and Calling via `ACTION_CALL` with runtime permissions.

## User Review Required

> [!IMPORTANT]
> - **Permissions**: The app requests `CAMERA`, `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` (or file access for image selection), and `CALL_PHONE` in `AndroidManifest.xml` and handles runtime permission requests.
> - **Image Storage**: Images captured from camera or gallery will be stored as file URIs (or string paths) in SQLite, allowing easy retrieval and preview in `ImageView` and dialogs.
> - **UI Components**: Uses Material 3 components, RecyclerView, CardView, Spinner, SearchView, and AlertDialogs as requested.

## Proposed Changes

### Data & Database Layer
- [NEW] `app/src/main/java/com/ucenm/examen1/Contacto.java`: Model class with `id`, `pais`, `nombre`, `telefono`, `nota`, `imagenUri`.
- [NEW] `app/src/main/java/com/ucenm/examen1/DatabaseHelper.java`: SQLiteOpenHelper handling table creation and CRUD operations (`insertContacto`, `getAllContactos`, `updateContacto`, `deleteContacto`).

### Adapters
- [NEW] `app/src/main/java/com/ucenm/examen1/ContactAdapter.java`: RecyclerView adapter for displaying contacts with action buttons (Call, Edit/Update, Delete, Share, View Image).

### Activities & UI Screens
- [MODIFY] `app/src/main/java/com/ucenm/examen1/MainActivity.java`: Registration screen with country Spinner (Honduras +504, Guatemala +502, El Salvador +503, Costa Rica +506, etc.), Camera/Gallery image picker, EditText validations with Regex, save logic, and navigation to Contact List.
- [NEW] `app/src/main/java/com/ucenm/examen1/ContactListActivity.java`: Contact list screen with RecyclerView, SearchView filtering, click-to-call confirmation dialog (`ACTION_CALL`), and CRUD actions.
- [NEW] `app/src/main/java/com/ucenm/examen1/EditContactActivity.java` (or dialog handling in list/main): Activity or dialog to update contact details.

### Layouts & Resources
- [MODIFY] `app/src/main/res/layout/activity_main.xml`: Form layout for registration.
- [NEW] `app/src/main/res/layout/activity_contact_list.xml`: List layout with SearchView and Back button.
- [NEW] `app/src/main/res/layout/item_contact.xml`: Card item layout for RecyclerView with contact details and action buttons.
- [MODIFY] `app/src/main/AndroidManifest.xml`: Add permissions (`CALL_PHONE`, `CAMERA`, `READ_EXTERNAL_STORAGE`/`READ_MEDIA_IMAGES`) and register `ContactListActivity` and `EditContactActivity`.

## Verification Plan

### Automated Tests
- Build project using Gradle (`app:assembleDebug`) to verify compilation without errors.

### Manual Verification
- Deploy app to emulator/device, test registration with validation, image picker, saving to SQLite, listing in RecyclerView, search filtering, calling with permission prompt, updating, sharing, and deleting.
