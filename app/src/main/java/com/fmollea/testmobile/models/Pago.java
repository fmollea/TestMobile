package com.fmollea.testmobile.models;

public class Pago {

    public double monto;
    public String idTarjeta;
    public String nombreTarjeta;
    public String urlImagenTarjeta;
    public int idBanco;
    public String nombreBanco;
    public String urlImagenBanco;
    public String mensajeCuotas;
    public boolean datosOk;

    public boolean isDatosOk() {
        return datosOk;
    }

    public void setDatosOk(boolean datosOk) {
        this.datosOk = datosOk;
    }

    @Override
    public String toString() {

        return "Pago{" +
                "monto=" + monto +
                ", idTarjeta='" + idTarjeta + '\'' +
                ", nombreTarjeta='" + nombreTarjeta + '\'' +
                ", urlImagenTarjeta='" + urlImagenTarjeta + '\'' +
                ", idBanco=" + idBanco +
                ", nombreBanco='" + nombreBanco + '\'' +
                ", urlImagenBanco='" + urlImagenBanco + '\'' +
                ", mensajeCuotas='" + mensajeCuotas + '\'' +
                '}';
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(String idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getUrlImagenTarjeta() {
        return urlImagenTarjeta;
    }

    public void setUrlImagenTarjeta(String urlImagenTarjeta) {
        this.urlImagenTarjeta = urlImagenTarjeta;
    }

    public Integer getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getUrlImagenBanco() {
        return urlImagenBanco;
    }

    public void setUrlImagenBanco(String urlImagenBanco) {
        this.urlImagenBanco = urlImagenBanco;
    }

    public String getMensajeCuotas() {
        return mensajeCuotas;
    }

    public void setMensajeCuotas(String mensajeCuotas) {
        this.mensajeCuotas = mensajeCuotas;
    }

    //empty constructor
    public Pago (){
        datosOk = false;
        idBanco = 0;
    }
}
