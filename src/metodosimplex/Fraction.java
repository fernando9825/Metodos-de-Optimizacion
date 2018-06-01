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

/**
 *
 * @author c3rberuss
 *
 */
public class Fraction {

    String directorio = new File("src/metodosimplex/fraction.py").getAbsolutePath();
    public String fraction(double decimalDouble) throws IOException {
        //Ejecutar un script de Python, para convertir en fracciones
        String[] cmd = {"python", directorio, Double.toString(decimalDouble)};
        Process pb = Runtime.getRuntime().exec(cmd);

        String line = "";
        String response = "";

        try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                response = line;
            }

        }
        return response;
    }

}
