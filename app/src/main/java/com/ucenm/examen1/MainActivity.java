package com.ucenm.examen1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerPais;
    private ImageView ivContactPhoto;
    private Button btnSelectImage, btnSalvar, btnVerContactos;
    private EditText etNombre, etTelefono, etNota;

    private DatabaseHelper dbHelper;
    private String currentImageUri = "";
    private Uri cameraImageUri;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private static final String[] PAISES_CON_CODIGO = {
            "Honduras (+504)",
            "Guatemala (+502)",
            "El Salvador (+503)",
            "Costa Rica (+506)",
            "Nicaragua (+505)",
            "Panamá (+507)",
            "México (+52)",
            "Estados Unidos (+1)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        spinnerPais = findViewById(R.id.spinnerPais);
        ivContactPhoto = findViewById(R.id.ivContactPhoto);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVerContactos = findViewById(R.id.btnVerContactos);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etNota = findViewById(R.id.etNota);

        setupSpinner();
        setupImageLaunchers();

        btnSelectImage.setOnClickListener(v -> showImageSourceDialog());
        ivContactPhoto.setOnClickListener(v -> showImageSourceDialog());

        btnSalvar.setOnClickListener(v -> validarYSalvarContacto());

        btnVerContactos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
            startActivity(intent);
        });
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, PAISES_CON_CODIGO);
        spinnerPais.setAdapter(adapter);
    }

    private void setupImageLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (Exception ignored) {}
                            currentImageUri = selectedImageUri.toString();
                            ivContactPhoto.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        currentImageUri = cameraImageUri.toString();
                        ivContactPhoto.setImageURI(cameraImageUri);
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showImageSourceDialog() {
        CharSequence[] options = {"Tomar foto con cámara", "Seleccionar de galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen de Contacto");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpen();
            } else if (which == 1) {
                openGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void validarYSalvarContacto() {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nota = etNota.getText().toString().trim();
        String paisSeleccionado = spinnerPais.getSelectedItem().toString();

        // Regex validations
        // Nombre: only letters, spaces and common accented characters (min 2 chars)
        Pattern namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
        // Telefono: only digits, 5 to 15 digits
        Pattern phonePattern = Pattern.compile("^[0-9]{5,15}$");

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es requerido");
            etNombre.requestFocus();
            showAlertDialog("Error de Validación", "El campo Nombre no puede estar vacío.");
            return;
        }

        if (!namePattern.matcher(nombre).matches()) {
            etNombre.setError("Nombre inválido (solo letras y espacios)");
            etNombre.requestFocus();
            showAlertDialog("Error de Validación", "El nombre solo debe contener letras y espacios (2 a 50 caracteres).");
            return;
        }

        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es requerido");
            etTelefono.requestFocus();
            showAlertDialog("Error de Validación", "El campo Teléfono no puede estar vacío.");
            return;
        }

        if (!phonePattern.matcher(telefono).matches()) {
            etTelefono.setError("Teléfono inválido (solo dígitos)");
            etTelefono.requestFocus();
            showAlertDialog("Error de Validación", "El teléfono solo debe contener dígitos numéricos (5 a 15 dígitos).");
            return;
        }

        // Insert into database
        Contacto contacto = new Contacto(paisSeleccionado, nombre, telefono, nota, currentImageUri);
        long id = dbHelper.insertContacto(contacto);

        if (id != -1) {
            showSuccessDialog("¡Éxito!", "Contacto guardado correctamente en la base de datos.");
            limpiarCampos();
        } else {
            showAlertDialog("Error", "No se pudo guardar el contacto en la base de datos.");
        }
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ver Lista", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Registrar Otro", null)
                .show();
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etTelefono.setText("");
        etNota.setText("");
        spinnerPais.setSelection(0);
        ivContactPhoto.setImageResource(android.R.drawable.ic_menu_camera);
        currentImageUri = "";
        etNombre.requestFocus();
    }
}
