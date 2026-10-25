package com.ucenm.examen1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactos.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTOS = "contactos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAIS = "pais";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_TELEFONO = "telefono";
    public static final String COLUMN_NOTA = "nota";
    public static final String COLUMN_IMAGEN = "imagen_uri";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PAIS + " TEXT, " +
                    COLUMN_NOMBRE + " TEXT, " +
                    COLUMN_TELEFONO + " TEXT, " +
                    COLUMN_NOTA + " TEXT, " +
                    COLUMN_IMAGEN + " TEXT" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        onCreate(db);
    }

    public long insertContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagenUri());

        long id = db.insert(TABLE_CONTACTOS, null, values);
        db.close();
        return id;
    }

    public List<Contacto> getAllContactos() {
        List<Contacto> lista = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTOS + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String pais = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIS));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO));
                String nota = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTA));
                String imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN));

                Contacto contacto = new Contacto(id, pais, nombre, telefono, nota, imagenUri);
                lista.add(contacto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int updateContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAIS, contacto.getPais());
        values.put(COLUMN_NOMBRE, contacto.getNombre());
        values.put(COLUMN_TELEFONO, contacto.getTelefono());
        values.put(COLUMN_NOTA, contacto.getNota());
        values.put(COLUMN_IMAGEN, contacto.getImagenUri());

        int rows = db.update(TABLE_CONTACTOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(contacto.getId())});
        db.close();
        return rows;
    }

    public void deleteContacto(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTOS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
