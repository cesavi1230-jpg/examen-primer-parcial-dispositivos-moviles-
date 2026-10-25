# Contact Management App Walkthrough (UCENM Exam)

I have successfully implemented the complete Contact Management Android application in Java according to all your technical and design specifications.

## What Was Accomplished

### 1. Model & Database Layer
- **[Contacto.java](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/java/com/ucenm/examen1/Contacto.java)**: Model class containing attributes (`id`, `pais`, `nombre`, `telefono`, `nota`, `imagenUri`), constructors, getters, and setters.
- **[DatabaseHelper.java](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/java/com/ucenm/examen1/DatabaseHelper.java)**: SQLiteOpenHelper managing table creation and full CRUD operations (`insertContacto`, `getAllContactos`, `updateContacto`, `deleteContacto`).

### 2. Screen 1 - Contact Registration (`MainActivity.java`)
- **[activity_main.xml](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/res/layout/activity_main.xml)**: Form layout with Country Spinner (Honduras +504, Guatemala +502, El Salvador +503, Costa Rica +506, etc.), ImageView with photo picker (Camera via FileProvider or Gallery via ACTION_OPEN_DOCUMENT), EditText fields for Name, Phone, and Note.
- **[MainActivity.java](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/java/com/ucenm/examen1/MainActivity.java)**: Strict Regex validation for names (letters and spaces) and phones (digits only) with `AlertDialog` warnings and errors, saving to SQLite, and navigating to saved contacts.

### 3. Screen 2 - Contact List & CRUD (`ContactListActivity.java` & `ContactAdapter.java`)
- **[activity_contact_list.xml](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/res/layout/activity_contact_list.xml)** & **[item_contact.xml](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/res/layout/item_contact.xml)**: RecyclerView with SearchView filtering in real-time.
- **Actions**:
  - **Call**: Shows confirmation `AlertDialog` ("¿Desea llamar a [Nombre]?") -> `ACTION_CALL` intent with country code + number and runtime `CALL_PHONE` permission request.
  - **Share**: `ACTION_SEND` intent sharing name, phone, and note.
  - **Edit/Update**: Opens [EditContactActivity.java](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/java/com/ucenm/examen1/EditContactActivity.java) to modify and save changes.
  - **Delete**: Deletes from SQLite database and refreshes list.
  - **View Image**: Previews stored contact photo in an `AlertDialog`.

### 4. Manifest & Permissions
- **[AndroidManifest.xml](file:///C:/Users/giose/AndroidStudioProjects/examen1/app/src/main/AndroidManifest.xml)**: Declared permissions (`CALL_PHONE`, `CAMERA`, `READ_EXTERNAL_STORAGE`, `READ_MEDIA_IMAGES`), features, activities, and FileProvider configuration (`file_paths.xml`).

## Verification Results
- **Gradle Build**: Successfully built (`app:assembleDebug`) with zero errors.
