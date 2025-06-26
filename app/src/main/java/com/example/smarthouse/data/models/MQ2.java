package com.example.smarthouse.data.models;

public class MQ2 {
    private String id;
    private String nombre;
    private String ubicacion;
    private Boolean estado;

    public MQ2() {
    }

    public MQ2(String id, String nombre, String ubicacion, Boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
