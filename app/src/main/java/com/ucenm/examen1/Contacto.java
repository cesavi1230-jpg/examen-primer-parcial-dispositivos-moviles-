package com.ucenm.examen1;

import java.io.Serializable;

public class Contacto implements Serializable {
    private long id;
    private String pais;
    private String nombre;
    private String telefono;
    private String nota;
    private String imagenUri;

    public Contacto() {
    }

    public Contacto(long id, String pais, String nombre, String telefono, String nota, String imagenUri) {
        this.id = id;
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.imagenUri = imagenUri;
    }

    public Contacto(String pais, String nombre, String telefono, String nota, String imagenUri) {
        this.pais = pais;
        this.nombre = nombre;
        this.telefono = telefono;
        this.nota = nota;
        this.imagenUri = imagenUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }
}
