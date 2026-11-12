package com.mycompany.rutaexpress.ui;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Utilidades comunes para la interacción visual de la aplicación.
 */
public final class Dialogos {

    private Dialogos() {
    }

    /**
     * Método para mostrar un menu de opciones al usuario
     * @param titulo: El título del menú
     * @param opciones: El arreglo de opciones
     * @return La opción seleccionada por el usuario
     */
    public static String menu(String titulo, String[] opciones) {
        return (String) JOptionPane.showInputDialog(null, "Seleccione una opción:",
                titulo, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
    }

    /**
     * Método para pedir datos al usuario mediante un cuadro de diálogo.
     * @param mensaje: El mensaje a mostrar
     * @return El valor ingresado por el usuario
     */
    public static String pedir(String mensaje) {
        return JOptionPane.showInputDialog(null, mensaje);
    }

    /**
     * Método para pedir datos obligatorios al usuario mediante un cuadro de diálogo.
     * @param mensaje: El mensaje a mostrar
     * @return El valor ingresado por el usuario
     */
    public static String pedirObligatorio(String mensaje) {
        String valor = pedir(mensaje);
        if (valor == null) {
            return null;
        }
        if (valor.trim().isEmpty()) {
            aviso("El dato es obligatorio.");
            return pedirObligatorio(mensaje);
        }
        return valor.trim();
    }

    /**
     * Método para pedir un número decimal al usuario mediante un cuadro de diálogo.
     * @param mensaje: El mensaje a mostrar
     * @return El valor ingresado por el usuario
     */
    public static Double pedirDouble(String mensaje) {
        String valor = pedir(mensaje);
        if (valor == null) {
            return null;
        }
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            aviso("Valor numérico inválido.");
            return null;
        }
    }

    /**
     * Método para mostrar un mensaje de aviso al usuario mediante un cuadro de diálogo.
     * @param mensaje: El mensaje a mostrar
     */
    public static void aviso(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    /**
     * metodo para mostrar un mensaje largo en un cuadro de diálogo con scroll
     * @param titulo: El título del cuadro de diálogo
     * @param texto: El texto a mostrar
     */
    public static void mostrar(String titulo, String texto) {
        if (texto == null) {
            texto = "(sin información)";
        }
        JTextArea area = new JTextArea(texto, 22, 90);
        area.setEditable(false);
        area.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(area);
        JOptionPane.showMessageDialog(null, scroll, titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
