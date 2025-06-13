package com.example.smarthouse.data.models;

public class CambioProgramado {
    private String id;
    private String fecha;
    private String hora;
    private Boolean estado;
    private String idUnidadSalida;
    public CambioProgramado() {}

    public CambioProgramado(String id, String fecha, String hora, Boolean estado, String idUnidadSalida) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.idUnidadSalida = idUnidadSalida;
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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getIdUnidadSalida() {
        return idUnidadSalida;
    }

    public void setIdUnidadSalida(String idUnidadSalida) {
        this.idUnidadSalida = idUnidadSalida;
    }
}
