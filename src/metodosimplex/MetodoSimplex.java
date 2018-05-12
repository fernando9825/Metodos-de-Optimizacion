/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author striker
 */
public class MetodoSimplex {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /*

            Resolver mediante el método simplex:
        
            MAX Z = 5X1 + 8X2 +7X3

            S.A.R
                3X1 + 3X2 + 3X3 <= 30
                    + 2X2 + 5X3 <= 30
                4X1 + 4X2       <= 24    


            //Creando un el arreglo de las restricciones
            double[][] array = new double[][]{
                {3, 3, 3, 1, 0, 0, 30},
                {0, 2, 5, 0, 1, 0, 30},
                {4, 4, 0, 0, 0, 1, 24},
                {-5, -8, -7, 0, 0, 0, 0} //Función objetivo
        };
        
         */
        //Restricciones
        double[] fObjetivo = {5, 8, 7};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
            {3, 3, 3, 30},
            {0, 2, 5, 30},
            {4, 4, 0, 24}};
        /*  
            Paso 1:
                Convertir las desigualdades en igualdades
         */

 /*
            debido a que este problema es con <=... para determinar cuantas variables de holguras vamos a tener...
            solo hay que ver multiplicar la cantidad de restricciones que tenemos, esto es:
            array.lenght * 2, y a eso hay que sumarle 1, para considerar a la función objetivo.
         */
        //CONVIRTIENDO LAS RESTRICCIONES EN IGUALDADES, AGREGANDO UNA VARIABLE DE HOLGURA (PROBLEMA CON <=)
        double[][] igualdades = new double[(array.length + 1)][2 * (array.length) + 1];

        for (int i = 0; i < array.length; i++) { //Se recorre la matriz original (con restricciones)

            for (int j = 0; j < array[i].length; j++) {

                if (j == (array[i].length - 1)) { //La columna de bi, se convierte en la última columna de la matriz igualdades
                    igualdades[i][(igualdades[i].length - 1)] = array[i][j];
                } else {

                    igualdades[i][j] = array[i][j]; //Se agregan las variables de decisión a la nueva matriz igualdades

                    if (j == array[i].length) {

                        for (int x = i; x < (igualdades.length - 1); x++, j++) {
                            igualdades[i][(j + 1)] = 1;

                        }
                    }
                }
            }
        }

        //En estos for anidados, se recorre la matriz igualdades para agregar las variables de holgura.
        for (int i = 0, k = (array[i].length - 1); i < igualdades.length - 1; i++, k++) {
            System.out.println("i:" + i);
            System.out.println("(i, k) = (" + i + ", " + k + ")");
            igualdades[i][k] = 1; //Se establecen las variables de holgura

            System.out.println("");
        }

        //Se utilizan ArrayList para guardar los valores de I y J (es decir (X,Y), donde hay variables base(valor de 1).
        ArrayList<Integer> variablesBasicasPosI = new ArrayList<>();
        ArrayList<Integer> variablesBasicasPosJ = new ArrayList<>();
        for (int i = 0; i < igualdades.length; i++) {
            for (int j = 0; j < igualdades[i].length; j++) {

                if (igualdades[i][j] == 1.0) { //Se evalua, a donde hay variables en la base
                    variablesBasicasPosI.add(i);
                    variablesBasicasPosJ.add(j);
                }
            }
        }

        /*
            Por último se procede a hacer Z=0, en la función objetivo.
            para lo que creamos un nuevo arreglo llamado fObjetivoCero
            y multiplicamos cada elemento por -1, de esta manera,
            tendremos Z de la forma Z = 0, como el método exige.
         */
        double[] fObjetivoCero = new double[igualdades[0].length];

        for (int i = 0; i < fObjetivo.length; i++) {
            fObjetivoCero[i] = fObjetivo[i] * -1;
        }

        //Agregamos la función objetivo a la última fila de igualdades.  
        System.arraycopy(fObjetivoCero, 0, igualdades[(igualdades.length - 1)], 0, igualdades[0].length);

        /*
            Esta parte de encarga de mostrar en consola, el paso 1.
            Luego de haber convertido las desigualdades en igualdades.
         */
        //Problema planteado inicialmente.
        System.out.println("MAX Z = 5X1 + 8X2 +7X3\n"
                + "        \n"
                + "        S.A.R\n"
                + "            3X1 + 3X2 + 3X3 <= 30\n"
                + "                + 2X2 + 5X3 <= 30\n"
                + "            4X1 + 4X2       <= 24\n\n");

        /*
            Se definen las columnas del simplex, mediante el siguiente bucle, el
            el cual funciona obteniendo el número de filas que tiene la matriz
            "igualdades".
         */
        //System.out.println("VB     X1        X2        X3        X4        X5        X6        Bi");
        System.out.print("VB     ");
        for (int i = 1; i < igualdades[0].length; i++) {
            System.out.print("X" + i + "        ");
        }
        System.out.print("Bi\n");

        //Se muestra la matriz con igualdades luego de haber convertido las desigualdades
        for (int i = 0; i < igualdades.length; i++) {
            for (int j = 0; j < igualdades[i].length; j++) {

                if (i != igualdades.length - 1) {
                    if ((i == variablesBasicasPosI.get(i)) && (j == variablesBasicasPosJ.get(i))) {
                        //Si se entra aqui, quiere decir que esa variable esta en la base.

                        String filaVariableBase = "";
                        for (int x = 0; x < igualdades[i].length; x++) {
                            if (igualdades[i][x] < 0) {
                                filaVariableBase += igualdades[i][x] + "      "; //Si el valor es negativo se coloca un espacio menos
                            } else {

                                filaVariableBase += igualdades[i][x] + "       ";
                            }
                        }

                        //Se muestra la fila que contiene variable en la Base.
                        System.out.print("X" + (j + 1));
                        System.out.print("     " + filaVariableBase);
                        break;//Terminamos el bucle anterior, porque ya fue recorrido.

                    }

                } else {
                    String filaZ = ""; //Se crea una nueva cadena para poder almacenar la Fila Z
                    for (int k = 0; k < igualdades[i].length; k++) { //Aqui se lee la fila Z y se concatena cada valor
                        
                        if (igualdades[i][j] < 0) {
                            filaZ += igualdades[i][k] + "      "; //Si el valor es negativo se coloca un espacio menos
                        } else {
                            filaZ += igualdades[i][k] + "       ";

                        }
                    }
                    //Se muestra la fila Z
                    System.out.print("Z");
                    System.out.print("      " + filaZ);
                    break; //Terminamos el bucle anterior, porque ya fue recorrido.
                }

            }//Fin del segundo bucle
            System.out.println("");//Dar el salto de linea para la siguiente fila
        }//Fin del primer bucle

    }
}
