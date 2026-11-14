package com.mycompany.rutaexpress.domain.pqr;

import java.time.LocalDateTime;

public class Seguimiento {
    private LocalDateTime fechaHora;
    private String funcionario;
    private String anotacion;

    public Seguimiento(LocalDateTime fechaHora, String funcionario, String anotacion) {
        this.fechaHora = fechaHora;
        this.funcionario = funcionario;
        this.anotacion = anotacion;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getFuncionario() { return funcionario; }
    public void setFuncionario(String funcionario) { this.funcionario = funcionario; }
    public String getAnotacion() { return anotacion; }
    public void setAnotacion(String anotacion) { this.anotacion = anotacion; }

    @Override
    public String toString() {
        return fechaHora + " | " + funcionario + " | " + anotacion;
    }
}
