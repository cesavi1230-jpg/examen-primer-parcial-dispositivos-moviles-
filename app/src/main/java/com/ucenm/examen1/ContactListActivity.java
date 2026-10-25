package com.ucenm.examen1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity implements ContactAdapter.OnContactActionListener {

    private RecyclerView recyclerViewContacts;
    private SearchView searchView;
    private Button btnBack;

    private DatabaseHelper dbHelper;
    private List<Contacto> contactoList;
    private ContactAdapter contactAdapter;

    private String pendingCallNumber = "";
    private ActivityResultLauncher<String> requestCallPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        dbHelper = new DatabaseHelper(this);

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        searchView = findViewById(R.id.searchView);
        btnBack = findViewById(R.id.btnBack);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        setupCallPermissionLauncher();
        loadContacts();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContacts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        contactoList = dbHelper.getAllContactos();
        contactAdapter = new ContactAdapter(this, contactoList, this);
        recyclerViewContacts.setAdapter(contactAdapter);
    }

    private void filterContacts(String query) {
        List<Contacto> filteredList = new ArrayList<>();
        for (Contacto c : contactoList) {
            if (c.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                    c.getTelefono().contains(query) ||
                    c.getPais().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(c);
            }
        }
        contactAdapter.updateList(filteredList);
    }

    private void setupCallPermissionLauncher() {
        requestCallPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (!pendingCallNumber.isEmpty()) {
                            executeCall(pendingCallNumber);
                        }
                    } else {
                        Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onCallClick(Contacto contacto) {
        // Extract phone number with country code
        String phoneStr = contacto.getTelefono().replaceAll("[^0-9]", "");
        String codeStr = "";
        if (contacto.getPais().contains("+")) {
            int start = contacto.getPais().indexOf("+");
            int end = contacto.getPais().indexOf(")", start);
            if (end != -1) {
                codeStr = contacto.getPais().substring(start, end);
            } else {
                codeStr = "+504";
            }
        } else {
            codeStr = "+504";
        }

        final String fullNumber = "tel:" + codeStr + phoneStr;

        new AlertDialog.Builder(this)
                .setTitle("Llamar Contacto")
                .setMessage("¿Desea llamar a " + contacto.getNombre() + " (" + codeStr + " " + contacto.getTelefono() + ")?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    pendingCallNumber = fullNumber;
                    checkCallPermissionAndExecute();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void checkCallPermissionAndExecute() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            executeCall(pendingCallNumber);
        } else {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void executeCall(String numberUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(numberUri));
            startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permiso CALL_PHONE requerido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onShareClick(Contacto contacto) {
        String shareText = "Contacto:\nNombre: " + contacto.getNombre() +
                "\nTeléfono: " + contacto.getPais() + " " + contacto.getTelefono() +
                "\nNota: " + (contacto.getNota() != null ? contacto.getNota() : "");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Compartir contacto vía"));
    }

    @Override
    public void onEditClick(Contacto contacto) {
        Intent intent = new Intent(ContactListActivity.this, EditContactActivity.class);
        intent.putExtra("contacto", contacto);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Contacto contacto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Contacto")
                .setMessage("¿Está seguro de eliminar a " + contacto.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.deleteContacto(contacto.getId());
                    loadContacts();
                    Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onImageClick(Contacto contacto) {
        if (contacto.getImagenUri() != null && !contacto.getImagenUri().isEmpty()) {
            ImageView imageView = new ImageView(this);
            try {
                imageView.setImageURI(Uri.parse(contacto.getImagenUri()));
            } catch (Exception e) {
                imageView.setImageResource(android.R.drawable.ic_menu_camera);
            }
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(20, 20, 20, 20);

            new AlertDialog.Builder(this)
                    .setTitle("Foto de " + contacto.getNombre())
                    .setView(imageView)
                    .setPositiveButton("Cerrar", null)
                    .show();
        } else {
            Toast.makeText(this, "Este contacto no tiene imagen guardada", Toast.LENGTH_SHORT).show();
        }
    }
}
