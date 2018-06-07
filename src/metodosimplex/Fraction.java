package metodosimplex;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author c3rberuss
 *
 */
public class Fraction {

    public boolean devolverFraccion;
    //String directorio = new File("src/metodosimplex/fraction.py").getAbsolutePath();
    //ClassLoader classLoader = getClass().getClassLoader();
    //File file = new File(classLoader.getResource("metodosimplex/fraction.py").getFile());

    File home = new File(System.getProperty("user.home") + "/fraction.py");
    String directorio = "";

    public Fraction(boolean devolverFraccion) throws IOException {
        this.devolverFraccion = devolverFraccion;
        //System.out.println(directorio);
        System.out.println(home);
        PrintWriter writer = new PrintWriter(home, "UTF-8");
        writer.println("#!/usr/bin/python");
        writer.println("#-*- coding: utf-8 -*-");
        writer.println("");
        writer.println("import sys");
        writer.println("from fractions import Fraction");
        writer.println("");
        writer.println("val = str(sys.argv[1])");
        writer.println("");
        writer.println("print(str(Fraction(val).limit_denominator(1000)))");
        writer.close();
        //Files.copy(file.toPath(), home.toPath(), StandardCopyOption.REPLACE_EXISTING);
        directorio = home.getAbsolutePath();
    }

    public void setFraccionActivarDesactivar(boolean devolverFraccion) {
        this.devolverFraccion = devolverFraccion;
    }

    public String fraction(double decimalDouble) throws IOException {
        //Ejecutar un script de Python, para convertir en fracciones
        String[] cmd = {"python", directorio, Double.toString(decimalDouble)};
        Process pb = Runtime.getRuntime().exec(cmd);

        //System.out.println("Decimal a convertir: " + Double.toString(decimalDouble));
        String line = "";
        String response = "";

        if (Double.toString(decimalDouble).contains("E") || Double.toString(decimalDouble).contains("e")) {

            if (decimalDouble < Math.pow(1, -20)) {
                response = Double.toString(Math.round(decimalDouble));
            } else {
                response = Double.toString(decimalDouble);
            }

        } else {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
                while ((line = input.readLine()) != null) {
                    response = line;
                }

            }
        }
        if (devolverFraccion) {
            return response;
        } else {
            if (decimalDouble < Math.pow(1, -15)) {
                return Double.toString(Math.round(decimalDouble));
            } else {
                return Double.toString(decimalDouble);
            }

        }

    }

}
