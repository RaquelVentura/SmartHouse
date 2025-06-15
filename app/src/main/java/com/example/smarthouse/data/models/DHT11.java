package com.example.smarthouse.data.models;

public class DHT11 {
    private String id;
    private String ubicacion;
    private String tipo;
    private String temperatura;
    private String humedad;

    public DHT11() {}

    public DHT11(String id, String ubicacion, String tipo, String temperatura, String humedad) {
        this.id = id;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.temperatura = temperatura;
        this.humedad = humedad;
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
}
