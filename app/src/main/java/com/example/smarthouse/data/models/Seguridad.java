package com.example.smarthouse.data.models;

public class Seguridad {
    private String Fecha;
    private String Hora;
    private String Resultado;

    public Seguridad() {
    }

    public Seguridad(String fecha, String hora, String resultado) {
        Fecha = fecha;
        Hora = hora;
        Resultado = resultado;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getResultado() {
        return Resultado;
    }

    public void setResultado(String resultado) {
        Resultado = resultado;
    }
}
