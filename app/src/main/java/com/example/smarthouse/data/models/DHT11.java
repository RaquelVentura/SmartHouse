package com.example.smarthouse.data.models;

public class DHT11 {
    private String id;
    private String ubicacion;
    private String tipo;
    private String modo;
    private String temperatura;
    private String humedad;
    private Boolean estado;

    public DHT11() {}

    public DHT11(String id, String ubicacion, String tipo, String modo, String temperatura, String humedad, Boolean estado) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.modo = modo;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.estado = estado;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getHumedad() {
        return humedad;
    }

    public void setHumedad(String humedad) {
        this.humedad = humedad;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
