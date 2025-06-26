package com.example.smarthouse.data.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Alarma {
    private String id;
    private String fecha;
    private String hora;
    private String tipoEvento;
    private String usuarioEmail;
    private String resultado;
    private String ubicacion;

    public Alarma() {}

    public Alarma(String id, String fecha, String hora, String tipoEvento, String usuarioEmail, String resultado, String ubicacion) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.tipoEvento = tipoEvento;
        this.usuarioEmail = usuarioEmail;
        this.resultado = resultado;
        this.ubicacion = ubicacion;
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

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("fecha", fecha);
        map.put("hora", hora);
        map.put("tipoEvento", tipoEvento);
        map.put("usuarioEmail", usuarioEmail);
        map.put("resultado", resultado);
        map.put("ubicacion", ubicacion);
        return map;
    }
    public static Alarma fromDataSnapshot(DataSnapshot snapshot) {
        Alarma acceso = snapshot.getValue(Alarma.class);
        if (acceso != null) {
            acceso.setId(snapshot.getKey());
        }
        return acceso;
    }
}