package com.example.smarthouse.data.models;

public class Usuario {

    private String id;

    private String nombreCompleto;

    private String rol; // Admin (agrega los demas usuarios) | Usuario normal (solo tiene acceso a modificar el estado de los componentes)

    private String correo;

    public Usuario() {
    }

    public Usuario(String nombreCompleto, String rol, String correo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.correo = correo;
    }

    public Usuario(String id, String nombreCompleto, String rol, String correo) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.correo = correo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
