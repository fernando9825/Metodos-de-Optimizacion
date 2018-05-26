/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import java.util.ArrayList;
import java.util.Arrays;
import static metodosimplex.MetodoSimplex2.setfObjetivo;

/**
 *
 * @author striker
 */
public class pruebas {

    //Nuevos campos de clase
    static ArrayList<Integer> vHolguraIndice = new ArrayList<>();
    static ArrayList<Integer> vArtificialIndice = new ArrayList<>();

    //Antigua campos de clase
    static int MAXMIN;

    public static void main(String[] args) {
        //Adaptando código

        MAXMIN = 1; //MIN
        //Restricciones
        double[] fObjetivo = {5, 8, 7};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
        setfObjetivo(fObjetivo);
        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
            {3, 3, 3, 30},
            {0, 2, 5, 30},
            {4, 4, 0, 24}};

        /*
           CONDICIÓN DE LAS RESTRICCIONES:
        
            0 ---->  (<=)
            1 ---->  (=)
            2 ---->  (>=)
         */
        int[] condicion = new int[]{
            0,
            0,
            0,};

        double[][] igualdades = paso1(array, fObjetivo, condicion, MAXMIN, verificarCondiciones(condicion));
        System.out.println("");
        System.out.println(Arrays.deepToString(igualdades).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
    }

    static boolean verificarCondiciones(int[] condicion) {
        boolean menorOIgual = false;

        for (int x : condicion) {
            if (x == 0) {

                menorOIgual = true;
            } else {
                menorOIgual = false;
                break;
            }
        }

        if (menorOIgual) {
            System.out.println("Todas las restricciones son <=");
        }else{
            System.out.println("tenemos una combinación de diferentes tipos de restricción");
        }
        return menorOIgual;
    }

    static double[][] paso1(double[][] array, double[] fObjetivo, int[] condicion, int MAXMIN, boolean menorOIgualQue) {
        /*
            Si menorOIgualque = true, no se ejecuta la penalización, 
            si es false, se utiliza la Tecnica M y se penaliza la función objetivo
         */

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

        int indice = (array[0].length - 1);//Indice de la ultima variable de decision

        //Determinando la nueva dimension para la matriz
        int columnas = array.length, filas = array[0].length;

        for (int x = 0; x < condicion.length; x++) {
            switch (condicion[x]) {
                case 0:
                    filas++;
                    break;
                case 1:
                    filas++;
                    break;
                default:
                    filas += 2;
                    break;
            }
        }

        //Construyendo la nueva matriz
        double[][] aux = new double[columnas][filas];
        for (int i = 0; i < array.length; i++) {

            for (int j = 0; j < array[i].length; j++) {
                if (j == (array[i].length - 1)) {
                    aux[i][(aux[i].length - 1)] = array[i][j];
                } else {
                    aux[i][j] = array[i][j];
                }
            }
        }

        //Agregar variable de holgura y artificiales;
        for (int i = 0; i < aux.length; i++) {

            switch (condicion[i]) {
                case 0:
                    aux[i][indice] = 1;
                    vHolguraIndice.add(indice);
                    indice++;

                    break;
                case 1:
                    aux[i][indice] = 1;
                    vArtificialIndice.add(indice);
                    indice++;

                    break;
                default:
                    aux[i][indice] = -1;
                    vHolguraIndice.add(indice);
                    indice++;
                    aux[i][indice] = 1;
                    vArtificialIndice.add(indice);
                    indice++;

                    break;
            }
        }

//        System.out.println(Arrays.deepToString(aux).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
//        System.out.println("");

        if (!vHolguraIndice.isEmpty()) {
            System.out.println("Variables de holgura");
            vHolguraIndice.forEach((Integer a) -> {
                System.out.println("X" + a);
            });
        }

        if (!vArtificialIndice.isEmpty()) {
            System.out.println("\nVariables artificiales");
            vArtificialIndice.forEach((Integer b) -> {
                System.out.println("X" + b);
            });
        }

        //Ejecutar la penalizacion en la función objetivo
//        System.out.println("\n");

        /*
            Para mi caso, he dividido la función objetivo en dos para poder
            aplicar la "técnica M"-
        
            el vectorM, contendra todos los terminos de M
            el vectorZ contendrá todos los terminos independientes de M
         */
        double[] vectorM = new double[filas];
        double[] vectorZ = new double[filas];

        //Aplicando penalización en vectorZ
        for (int x = 0; x < vArtificialIndice.size(); x++) {
            switch (MAXMIN) {
                case 1:
                    //MAXIMIZAR
                    vectorM[vArtificialIndice.get(x)] = -1;

                    break;

                case 2:
                    //MINIMIZAR
                    vectorM[vArtificialIndice.get(x)] = 1;
            }
        }

        //Estableciendo valor de vectorZ
        System.arraycopy(fObjetivo, 0, vectorZ, 0, fObjetivo.length);

//        System.out.println(Arrays.toString(vectorM));
//        System.out.println(Arrays.toString(vectorZ));
//        System.out.println("\n\n");
        //Igualar vectorM y vector Z a cero; VectorM = vectorZ = 0
        for (int x = 0; x < filas; x++) {
            vectorM[x] *= -1;
            vectorZ[x] *= -1;
        }

        //Limpiar vectores del valor -0.0
        for (int x = 0; x < filas; x++) {
            if (vectorM[x] == -0.0) {
                vectorM[x] = 0;
            }

            if (vectorZ[x] == -0.0) {
                vectorZ[x] = 0;
            }
        }

//        System.out.println(Arrays.toString(vectorM));
//        System.out.println(Arrays.toString(vectorZ));
//        System.out.println("\n\n");
        //Agregar la matriz aux, vectorM y vectorZ a la matriz de igualdades aux2
        double[][] aux2 = new double[(aux.length + 2)][filas];

        for (int i = 0; i < aux.length; i++) {
            System.arraycopy(aux[i], 0, aux2[i], 0, aux[i].length);
        }

        for (int i = aux.length; i < aux2.length; i++) {
            for (int j = 0; j < aux2[i].length; j++) {
                if (i == aux.length) {
                    aux2[i][j] = vectorM[j];
                } else {
                    aux2[i][j] = vectorZ[j];
                }
            }
        }

//        System.out.println(Arrays.deepToString(aux2).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

        /*  System.out.println(Arrays.deepToString(aux2).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
            Si se da el caso de que todas las restricciones sean <=
            declaramos una nueva matriz aux3, con una sola fila z
         */
        double[][] aux3 = new double[(aux.length + 1)][filas];

        for (int i = 0; i < aux.length; i++) {
            System.arraycopy(aux[i], 0, aux3[i], 0, aux[i].length);
        }

        double[] fObjetivoAux = new double[fObjetivo.length];
        for (int x = 0; x < fObjetivoAux.length; x++) {
            fObjetivoAux[x] = fObjetivo[x] * -1;
        }

        //Limpiar la función objetivo auxiliar
        for (int x = 0; x < fObjetivoAux.length; x++) {
            if (fObjetivoAux[x] == -0.0) {
                fObjetivo[x] = 0;
            }
        }

        //Insertar fObjetivo Aux en Aux3
        System.arraycopy(fObjetivoAux, 0, aux3[columnas], 0, fObjetivoAux.length);

//        System.out.println("\n");
//        System.out.println(Arrays.deepToString(aux3).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        if (menorOIgualQue) {
            return aux3;
        } else {
            return aux2;
        }

    }

}
