package metodosimplex;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//aquí tu paquete xd
//package modelos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author c3rberuss
 *
 */
public class Fraction {

    public String fraction(double decimalDouble) throws IOException {

        String decimal = Double.toString(decimalDouble);
        //reemplaza por la ruta en la que esté el archivo
        String[] cmd = {"python", "/home/striker/NetBeansProjects/Metodos-de-Optimizacion/src/metodosimplex/fraction.py", decimal};
        Process pb = Runtime.getRuntime().exec(cmd);

        String line = "";
        String response = "";

        try (BufferedReader input = new BufferedReader(new InputStreamReader(pb.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                response = line;
            }

        }

        //aqui imprimes la linea de retorno del script
        //System.out.println("Respuesta: " + response);

        return response;
    }

}
