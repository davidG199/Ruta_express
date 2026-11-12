package com.mycompany.rutaexpress;

import java.time.LocalDateTime;

public class NodoSeguimiento {
    private LocalDateTime fechaHora;
    private String funcionario;
    private String anotacion;
    private NodoSeguimiento sig;

    public NodoSeguimiento(LocalDateTime fechaHora, String funcionario, String anotacion) {
        this.fechaHora = fechaHora;
        this.funcionario = funcionario;
        this.anotacion = anotacion;
        this.sig = null;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(String anotacion) {
        this.anotacion = anotacion;
    }

    public NodoSeguimiento getSig() {
        return sig;
    }

    public void setSig(NodoSeguimiento sig) {
        this.sig = sig;
    }

    @Override
    public String toString() {
        return fechaHora + " | " + funcionario + " | " + anotacion;
    }
}
