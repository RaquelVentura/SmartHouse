package com.example.smarthouse.data.models;

import java.util.HashMap;
import java.util.Map;

public class CambioDispositivo {
    private String id;
    private String tipoCambio;
    private String fecha;
    private String hora;
    private boolean estado;
    private String idUnidadSalida;
    private String tipoDispositivo;
    private String nombreDispositivo;
    private String usuarioId;
    private String usuarioNombre;
    private long timestamp;
    private boolean ejecutado;

    public CambioDispositivo(String id, String tipoCambio, String fecha, String hora, boolean estado,
                             String idUnidadSalida, String tipoDispositivo, String nombreDispositivo,
                             String usuarioId, String usuarioNombre, long timestamp, boolean ejecutado) {
        this.id = id;
        this.tipoCambio = tipoCambio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.idUnidadSalida = idUnidadSalida;
        this.tipoDispositivo = tipoDispositivo;
        this.nombreDispositivo = nombreDispositivo;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.timestamp = timestamp;
        this.ejecutado = ejecutado;
    }

    public CambioDispositivo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getIdUnidadSalida() {
        return idUnidadSalida;
    }

    public void setIdUnidadSalida(String idUnidadSalida) {
        this.idUnidadSalida = idUnidadSalida;
    }

    public String getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEjecutado() {
        return ejecutado;
    }

    public void setEjecutado(boolean ejecutado) {
        this.ejecutado = ejecutado;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("tipoCambio", tipoCambio);
        map.put("fecha", fecha);
        map.put("hora", hora);
        map.put("estado", estado);
        map.put("idUnidadSalida", idUnidadSalida);
        map.put("tipoDispositivo", tipoDispositivo);
        map.put("nombreDispositivo", nombreDispositivo);
        map.put("usuarioId", usuarioId);
        map.put("usuarioNombre", usuarioNombre);
        map.put("timestamp", timestamp);
        map.put("ejecutado", ejecutado);
        return map;
    }
}