package com.example.smarthouse.data.models;

public class Seguridad {
    private String fecha;
    private String hora;
    private String resultado;

    public Seguridad() {
    }

    public Seguridad(String fecha, String hora, String resultado) {
        this.fecha = fecha;
        this.hora = hora;
        this.resultado = resultado;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
