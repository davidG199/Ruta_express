package com.mycompany.rutaexpress.files;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Properties;

public class GestorNotificaciones {
    private String rutaConfig;
    private String correoEmisor;
    private String claveApp;
    private String correoCoordinador;
    private GestorArchivos archivos;
    private String servidorSmtp;
    private int puertoSmtp;
    private boolean autenticacionSmtp;
    private boolean startTls;
    private boolean configuracionCargada;

    public GestorNotificaciones() {
        this(new GestorArchivos());
    }

    public GestorNotificaciones(GestorArchivos archivos) {
        this.archivos = archivos == null ? new GestorArchivos() : archivos;
        this.rutaConfig = "config.txt";
        this.correoEmisor = "";
        this.claveApp = "";
        this.correoCoordinador = "";
        this.servidorSmtp = "smtp.gmail.com";
        this.puertoSmtp = 587;
        this.autenticacionSmtp = true;
        this.startTls = true;
        this.configuracionCargada = false;
    }

    public void cargarConfiguracion() {
        String[] lineas = archivos.leerLineas(rutaConfig);
        for (String linea : lineas) {
            String[] partes = linea.split("=", 2);
            if (partes.length != 2) {
                continue;
            }
            String llave = partes[0].trim().toLowerCase();
            String valor = partes[1].trim();
            switch (llave) {
                case "correoemisor" -> correoEmisor = valor;
                case "claveaplicacion" -> claveApp = valor.replace(" ", "");
                case "correocoordinador" -> correoCoordinador = valor;
                case "smtphost", "servidorsmtp" -> servidorSmtp = valor;
                case "puertosmtp" -> puertoSmtp = Integer.parseInt(valor);
                case "autenticacionsmtp" -> autenticacionSmtp = Boolean.parseBoolean(valor);
                case "starttls" -> startTls = Boolean.parseBoolean(valor);
                default -> {
                }
            }
        }
        configuracionCargada = true;
    }

    public boolean enviarCorreo(String destino, String asunto, String cuerpo) {
        if (!configuracionCargada) {
            cargarConfiguracion();
        }
        if (correoEmisor.isEmpty() || claveApp.isEmpty()) {
            registrarFallo(destino, "Cuenta de correo no configurada en " + rutaConfig);
            return false;
        }
        if (!esCorreoValido(destino)) {
            registrarFallo(destino, "Correo destino inválido");
            return false;
        }

        // Configuración de las propiedades del servidor SMTP
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", servidorSmtp);
        propiedades.put("mail.smtp.port", String.valueOf(puertoSmtp));
        propiedades.put("mail.smtp.auth", String.valueOf(autenticacionSmtp));
        propiedades.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        propiedades.put("mail.smtp.starttls.required", String.valueOf(startTls));

        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(correoEmisor, claveApp);
            }
        });

        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(correoEmisor));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            mensaje.setSubject(asunto == null ? "" : asunto, "UTF-8");
            mensaje.setText(cuerpo == null ? "" : cuerpo, "UTF-8");
            Transport.send(mensaje);
            return true;
        } catch (MessagingException e) {
            registrarFallo(destino, "No se pudo enviar el correo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida si un correo electrónico es válido.
     *
     * @param correo El correo electrónico a validar.
     * @return true si el correo es válido, false en caso contrario.
     */
    private boolean esCorreoValido(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        try {
            // Se utiliza InternetAddress para validar el formato del correo.
            // internetAddress.validate() lanzará una AddressException si el correo no es válido.
            InternetAddress direccion = new InternetAddress(correo.trim());
            direccion.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    /**
     * Envía una notificación a una sede específica.
     *
     * @param destino El correo del destinatario.
     * @param asunto  El asunto del mensaje.
     * @param cuerpo  El cuerpo del mensaje.
     */
    public void notificarSede(String destino, String asunto, String cuerpo) {
        if (destino == null || destino.trim().isEmpty()) {
            destino = correoCoordinador;
        }
        if (!enviarCorreo(destino, asunto, cuerpo)) {
            registrarFallo(destino, "No se pudo enviar la notificación: " + asunto);
        }
    }

    public void registrarFallo(String destino, String error) {
        archivos.agregarLinea("fallos.log", LocalDateTime.now()
                + " | Destino: " + (destino == null ? "(sin destino)" : destino)
                + " | Error: " + error);
    }

    public String getCorreoEmisor() {
        return correoEmisor;
    }

    public String getCorreoCoordinador() {
        return correoCoordinador;
    }

    public String getRutaConfig() {
        return rutaConfig;
    }

    public void setRutaConfig(String rutaConfig) {
        this.rutaConfig = rutaConfig;
        this.configuracionCargada = false;
    }
}
