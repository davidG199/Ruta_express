package com.mycompany.rutaexpress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GestorArchivos {
    private File f;
    private FileReader fr;
    private FileWriter fw;
    private BufferedReader br;
    private BufferedWriter bw;
    private int cargados;

    public int getCargados() {
        return cargados;
    }

    public File resolverArchivo(String nombre) {
        File local = new File(nombre);
        if (local.exists()) {
            return local;
        }
        try {
            File base = new File(GestorArchivos.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File dir = base.isFile() ? base.getParentFile() : base;
            for (int i = 0; i < 6 && dir != null; i++) {
                File candidato = new File(dir, nombre);
                if (candidato.exists()) {
                    return candidato;
                }
                dir = dir.getParentFile();
            }
        } catch (Exception e) {
            // Se continúa con la ruta local.
        }
        return local;
    }

    public String[] leerLineas(String ruta) {
        String[] datos = new String[16];
        int n = 0;
        cargados = 0;

        f = resolverArchivo(ruta);
        try {
            fr = new FileReader(f, StandardCharsets.UTF_8);
            br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                if (n == datos.length) {
                    String[] nuevo = new String[datos.length * 2];
                    System.arraycopy(datos, 0, nuevo, 0, datos.length);
                    datos = nuevo;
                }
                datos[n] = linea;
                n++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] resultado = new String[n];
        System.arraycopy(datos, 0, resultado, 0, n);
        cargados = n;
        return resultado;
    }

    public void escribirLineas(String ruta, String[] lineas) {
        f = new File(ruta);
        try {
            fw = new FileWriter(f, StandardCharsets.UTF_8);
            bw = new BufferedWriter(fw);
            if (lineas != null) {
                for (String linea : lineas) {
                    bw.write(linea == null ? "" : linea);
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void escribirTexto(String ruta, String contenido) {
        f = new File(ruta);
        try {
            fw = new FileWriter(f, StandardCharsets.UTF_8);
            bw = new BufferedWriter(fw);
            bw.write(contenido == null ? "" : contenido);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void agregarLinea(String ruta, String linea) {
        f = new File(ruta);
        try {
            fw = new FileWriter(f, StandardCharsets.UTF_8, true);
            bw = new BufferedWriter(fw);
            bw.write(linea == null ? "" : linea);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
