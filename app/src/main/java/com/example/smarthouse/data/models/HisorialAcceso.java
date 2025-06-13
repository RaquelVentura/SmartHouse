package com.example.smarthouse.data.models;

public class HisorialAcceso {
    private String id;
    private String fecha;
    private String hora;
    private Boolean resultado;

    public HisorialAcceso() {}
    public HisorialAcceso(String id, String fecha, String hora, Boolean resultado) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.resultado = resultado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Boolean getResultado() {
        return resultado;
    }

    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }
}
