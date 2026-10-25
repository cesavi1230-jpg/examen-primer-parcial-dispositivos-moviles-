package com.ucenm.examen1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

public class EditContactActivity extends AppCompatActivity {

    private Spinner spinnerEditPais;
    private ImageView ivEditPhoto;
    private Button btnEditSelectImage, btnUpdateContacto, btnEditCancel;
    private EditText etEditNombre, etEditTelefono, etEditNota;

    private DatabaseHelper dbHelper;
    private Contacto contacto;
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
        setContentView(R.layout.activity_edit_contact);

        dbHelper = new DatabaseHelper(this);

        spinnerEditPais = findViewById(R.id.spinnerEditPais);
        ivEditPhoto = findViewById(R.id.ivEditPhoto);
        btnEditSelectImage = findViewById(R.id.btnEditSelectImage);
        btnUpdateContacto = findViewById(R.id.btnUpdateContacto);
        btnEditCancel = findViewById(R.id.btnEditCancel);
        etEditNombre = findViewById(R.id.etEditNombre);
        etEditTelefono = findViewById(R.id.etEditTelefono);
        etEditNota = findViewById(R.id.etEditNota);

        setupSpinner();
        setupImageLaunchers();

        if (getIntent() != null && getIntent().hasExtra("contacto")) {
            contacto = (Contacto) getIntent().getSerializableExtra("contacto");
            if (contacto != null) {
                populateData();
            }
        }

        btnEditSelectImage.setOnClickListener(v -> showImageSourceDialog());
        ivEditPhoto.setOnClickListener(v -> showImageSourceDialog());

        btnUpdateContacto.setOnClickListener(v -> validarYActualizar());
        btnEditCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, PAISES_CON_CODIGO);
        spinnerEditPais.setAdapter(adapter);
    }

    private void populateData() {
        etEditNombre.setText(contacto.getNombre());
        etEditTelefono.setText(contacto.getTelefono());
        etEditNota.setText(contacto.getNota());
        currentImageUri = contacto.getImagenUri();

        if (currentImageUri != null && !currentImageUri.isEmpty()) {
            try {
                ivEditPhoto.setImageURI(Uri.parse(currentImageUri));
            } catch (Exception e) {
                ivEditPhoto.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        for (int i = 0; i < PAISES_CON_CODIGO.length; i++) {
            if (PAISES_CON_CODIGO[i].equals(contacto.getPais())) {
                spinnerEditPais.setSelection(i);
                break;
            }
        }
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
                            ivEditPhoto.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        currentImageUri = cameraImageUri.toString();
                        ivEditPhoto.setImageURI(cameraImageUri);
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
        builder.setTitle("Actualizar Imagen");
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

    private void validarYActualizar() {
        String nombre = etEditNombre.getText().toString().trim();
        String telefono = etEditTelefono.getText().toString().trim();
        String nota = etEditNota.getText().toString().trim();
        String paisSeleccionado = spinnerEditPais.getSelectedItem().toString();

        Pattern namePattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
        Pattern phonePattern = Pattern.compile("^[0-9]{5,15}$");

        if (nombre.isEmpty() || !namePattern.matcher(nombre).matches()) {
            etEditNombre.setError("Nombre inválido (solo letras y espacios)");
            etEditNombre.requestFocus();
            return;
        }

        if (telefono.isEmpty() || !phonePattern.matcher(telefono).matches()) {
            etEditTelefono.setError("Teléfono inválido (solo dígitos)");
            etEditTelefono.requestFocus();
            return;
        }

        contacto.setPais(paisSeleccionado);
        contacto.setNombre(nombre);
        contacto.setTelefono(telefono);
        contacto.setNota(nota);
        contacto.setImagenUri(currentImageUri);

        dbHelper.updateContacto(contacto);
        Toast.makeText(this, "Contacto actualizado con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }
}
