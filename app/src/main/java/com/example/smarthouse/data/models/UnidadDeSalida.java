package com.example.smarthouse.data.models;

public class UnidadDeSalida {

    private String id;
    private String ubicacion;
    private Boolean estado;
    private String tipo;

    public UnidadDeSalida() {}
    public UnidadDeSalida(String id, String ubicacion, Boolean estado, String tipo) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
