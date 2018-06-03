/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import UI2.inter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class MetodoSimplex2 {

    //Campos de clase
    public static Fraction fraccion = new Fraction(true);//Instancia de la clase Fraction para convertir decimales a quebrados
    public static int columnaPivote, filaPivote, MAXMIN;
    public static double[][] matriz;
    public static double[] fObjetivo;
    public static int[] condicion;
    public static boolean condicionZ = false, tecnicaM = false;
    public static String procedimiento = "";
    //Nuevos campos de clase
    public static ArrayList<Integer> vHolguraIndice = new ArrayList<>();
    public static ArrayList<Integer> vArtificialIndice = new ArrayList<>();

    //Filas
    public static ArrayList<Integer> vArtificialFilas = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        new inter().setVisible(true);
//        MAXMIN = 1; //MAX
//        //MAXMIN = 2; //MIN
//        //Restricciones
//
//        //Problema de amaya
//        double[] fObjetivo = {4, 6, 7, 5, 9};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//        setfObjetivo(fObjetivo);
//        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//            {1, 3, 4, 5, 7, 20},
//            {0, 6, 7, 8, 0, 15},
//            {7, 8, 0, 7, 9, 30},
//            {7, 2, 1, 0, 8, 20}};
//
////        //PROBLEMA CON TODO TIPO DE DESIGUALDADES
////        double[] fObjetivo = {7, 8, 13, 6};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
////        setfObjetivo(fObjetivo);
////        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
////            {3, 2, 6, 1, 28},
////            {2, 3, 5, 2, 35},
////            {4, 1, 5, 1, 27}};
////        
////        int[] condicion = new int[]{
////            2,
////            1,
////            0};
////        double[] fObjetivo = {20, 40};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
////        setfObjetivo(fObjetivo);
////        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
////            {1, 3, 9},
////            {2, 1, 8},};
////        double[] fObjetivo = {-3, 8};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
////        setfObjetivo(fObjetivo);
////        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
////            {4, 1, 13},
////            {2, 3, 6},};
////
//        /*
//           CONDICIÓN DE LAS RESTRICCIONES:
//        
//            0 ---->  (<=)
//            1 ---->  (=)
//            2 ---->  (>=)
//         */
////        Problema de amaya
//        int[] condicion = new int[]{
//            2,
//            0,
//            1,
//            0};
//        int[] condicion = new int[]{
//            0,
//            0,
//            2,
//            0};
//        tecnicaM = verificarCondiciones(condicion);//Si es false, todas son <=, sino, hay que usar la Tecnica M
//        matriz = paso1(array, fObjetivo, condicion, MAXMIN, tecnicaM);
//
//        //Paso2 -->Mostrar matriz
//        System.out.println("");
//        procedimiento += "\n";
//        System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
//
//        //Agregando la Matriz al procedimiento
//        procedimiento += Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");
//
//        if (!tecnicaM) {
//            System.out.println("\nSe eliminan las M, para obtener la solución básica inicial...");
//            procedimiento += "\nSe eliminan las M, para obtener la solución básica inicial...";
//            System.out.println("");
//            procedimiento += "\n";
//            System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
//            procedimiento += Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");
//        }
//
//        //Paso3 --> Solucion basica inial
//        solBasicaInicial(matriz, tecnicaM);
//
//        //Paso4 -->Determinar si la funcion es optima
//        System.out.println("Determinando si la funcion es optima...");
//        procedimiento += "Determinando si la funcion es optima...";
//        condicionZ = comprobarFactibilidadZ(matriz, MAXMIN, tecnicaM);
//
//        int iteracion = 1;
//        while (condicionZ) {
//            System.out.println("Iteración: " + iteracion);
//            procedimiento += "\nIteración: " + iteracion;
//            //paso5 --> Determinar variable de entrada
//            filaPivote = varEntrada(matriz, MAXMIN, tecnicaM);
//
//            //paso6 --> Determinar variable de salida
//            columnaPivote = varSalida(matriz, tecnicaM);
//
//            /////////ELEMEMTO PIVOTE//////////////////////////////
//            System.out.println("Variable de entrada con posición: " + (filaPivote + 1));
//            System.out.println("Variable de salida con posición: " + (columnaPivote + 1));
//            System.out.println("A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote]);
//            procedimiento += "\nVariable de entrada con posición: " + (filaPivote + 1)
//                    + "\n" + "Variable de salida con posición: " + (columnaPivote + 1)
//                    + "\n" + "A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote];
//            matriz = ConvertirVariableEnBase(matriz, columnaPivote, filaPivote);
//            mostrarMatriz(matriz);
//            condicionZ = comprobarFactibilidadZ(matriz, MAXMIN, tecnicaM);
//
//            iteracion++;
//        }
//
//        comprobarZ(matriz, fObjetivo, tecnicaM);

    }

    public static double[][] paso1(double[][] array, double[] fObjetivo, int[] condicion, int MAXMIN, boolean menorOIgualQue) {
        /*
            Si menorOIgualque = true, no se ejecuta la penalización, 
            si es false, se utiliza la Tecnica M y se penaliza la función objetivo
         */

 /*
            Esta parte de encarga de mostrar en consola, el paso 1.
            Luego de haber convertido las desigualdades en igualdades.
         */
        //Problema planteado inicialmente.
//        System.out.println("MAX Z = 5X1 + 8X2 +7X3\n"
//                + "        \n"
//                + "        S.A.R\n"
//                + "            3X1 + 3X2 + 3X3 <= 30\n"
//                + "                + 2X2 + 5X3 <= 30\n"
//                + "            4X1 + 4X2       <= 24\n\n");
        //Vaciando los campos
        vHolguraIndice.removeAll(vHolguraIndice);
        vArtificialIndice.removeAll(vArtificialIndice);

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
            procedimiento += "Variables de holgura\n";
            vHolguraIndice.forEach((Integer a) -> {
                System.out.println("X" + (a + 1));
                procedimiento += "X" + (a + 1) + "\n";
            });
        }

        if (!vArtificialIndice.isEmpty()) {
            System.out.println("\nVariables artificiales");
            procedimiento += "\n\nVariables artificiales";
            vArtificialIndice.forEach((Integer b) -> {
                System.out.println("X" + (b + 1));
                procedimiento += "X" + (b + 1) + "\n";
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
            procedimiento += "\n\nSe esta trabajando con la Técnica M";
            procedimiento += "\nla penultima fila corresponde a los valores de M";
            procedimiento += "\ny la última a los términos independientes de M\n";
            return aux2;
        }

    }

    public static boolean verificarCondiciones(int[] condicion) {
        boolean menorOIgual = false;
        int pos = 0;
        for (int k : condicion) {
            //System.out.println(k);
            if (k == 1 || k == 2) {
                vArtificialFilas.add(pos);
            }
            pos++;
        }

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
        } else {
            System.out.println("tenemos una combinación de diferentes tipos de restricción");
        }
        return menorOIgual;
    }

    public static void setfObjetivo(double[] fObjetivo2) {
        fObjetivo = fObjetivo2;
    }

    public static void mostrarMatriz(double[][] array) throws IOException {

        determinarVarEnBase(array, true);
        System.out.println("");

        String[][] arrayFraction = new String[array.length][array[0].length];

        for (int i = 0; i < arrayFraction.length; i++) {

            for (int j = 0; j < arrayFraction[i].length; j++) {
                arrayFraction[i][j] = fraccion.fraction(array[i][j]);
            }
        }

        System.out.println(Arrays.deepToString(arrayFraction).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        System.out.println("");
        procedimiento += "\n\n" + Arrays.deepToString(arrayFraction).replace("], ", "]\n").replace("[[", "[").replace("]]", "]") + "\n";
    }

    public static double[][] ConvertirVariableEnBase(double[][] igualdades, int posX, int posY) {
        /*
            Este metodo se encarga de convertir en 1 la variable que le pidamos
            y en convertir en ceros los valores de la columna donde se encuentra 
            esa variable.
         

        
            Este bucle se enccarga de dividir la fila Pivote entre el elemento
            pivote
         */

        double divisor = igualdades[posX][posY];
        for (int j = 0; j < igualdades[posX].length; j++) {
            igualdades[posX][j] = igualdades[posX][j] / divisor;
        }

        /*
            Estos bucles se encargan de convertir en ceros los elementos de la
            columna pivote
         */
        //Realizando eliminacion
        double[][] matrizEliminaciones = new double[igualdades.length][igualdades[0].length];
        double[] fila = new double[igualdades[0].length];
        double[] filaAux = new double[igualdades[0].length];
        System.arraycopy(igualdades[posX], 0, fila, 0, fila.length);
        System.arraycopy(igualdades[posX], 0, filaAux, 0, filaAux.length);

        /*
            POR CUALQUIER ERROR, DEBO REVISAR AQUIIIIIIIIIIIIIIIIII
         */
        for (int i = 0; i < matrizEliminaciones.length; i++) {
            for (int k = 0; k < fila.length; k++) {
                fila[k] = (fila[k] * -igualdades[i][posY]);
                if (i == posX) {
                    fila[k] = 0;
                }

            }
            //System.out.println(Arrays.toString(fila));

            for (int x = 0; x < matrizEliminaciones.length; x++) {
                System.arraycopy(fila, 0, matrizEliminaciones[i], 0, matrizEliminaciones[i].length);
            }
            //matrizEliminaciones[i] = fila;
            System.arraycopy(filaAux, 0, fila, 0, fila.length);
        }

        double[][] suma = new double[igualdades.length][igualdades[0].length];
        for (int x = 0; x < igualdades.length; x++) {
            for (int y = 0; y < igualdades[x].length; y++) {
                suma[x][y] = igualdades[x][y] + matrizEliminaciones[x][y];
            }
        }
        return suma;
    }

    public static double[][] eliminarM(double[][] igualdades, ArrayList<Integer> artificiales, int MAXMIN) {
        double matrixEliminacion[][] = new double[artificiales.size()][igualdades[0].length];
        for (int x = 0; x < matrixEliminacion.length; x++) {
            for (int y = 0; y < matrixEliminacion[x].length; y++) {
                matrixEliminacion[x][y] = igualdades[artificiales.get(x)][y];
            }
        }

        System.out.println("Esta es la matrix para eliminar las M\n");
        System.out.println(Arrays.deepToString(matrixEliminacion));

        //Haciendo vector de eliminación
        /*
            Este vector, no es más que sumar las filas donde hay variables artificiales, 
            TRUCO ENSEÑADO EN CLASE POR:
            ING. HERBERTH, MoP
         */
        double vector[] = new double[matrixEliminacion[0].length];
        double acumulador = 0;
        for (int filas = 0; filas < matrixEliminacion[0].length; filas++) {
            for (int columnas = 0; columnas < matrixEliminacion.length; columnas++) {
                acumulador += matrixEliminacion[columnas][filas];
            }
            vector[filas] = acumulador;
            acumulador = 0;
        }


        /*
            DEBO HACER CODIGO PARA SOLUCIONAR EL PROBLEMA CON LA TECNICA M PARA 
            MAXIMIZAR....
            (EN LA FUNCIÓN, ELIMINAR M)
         */
        if (MAXMIN == 1) {

            //MAXIMIZAR
//            int x = vArtificialIndice.size();
//            System.out.println("El tamañao es: " + x);
//            for (int i = 0; i < igualdades.length - 2; i++) {
//                System.out.println("Elemento en base --->  (X, y): (" + i + ", " + vArtificialIndice.get(i) + ") ");
//                igualdades = ConvertirVariableEnBase(igualdades, i, vArtificialIndice.get(i));
//            }
//
//            System.out.println("Esta es la matrix aux para MAX\n");
//            System.out.println(Arrays.deepToString(igualdades).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
//            System.out.println("");
            for (int i = 0; i < vector.length; i++) {
                vector[i] *= -1;
            }

            System.out.println("Este es el vector que eliminará a M");
            System.out.println(Arrays.toString(vector));

            //Por ultimo, hay que sumar el vector, en la matriz y retornar el valor
            for (int j = 0; j < igualdades[0].length; j++) {
                igualdades[(igualdades.length - 2)][j] = igualdades[(igualdades.length - 2)][j] + vector[j];
            }
        } else {
            System.out.println("Este es el vector que eliminará a M");
            System.out.println(Arrays.toString(vector));

            //Por ultimo, hay que sumar el vector, en la matriz y retornar el valor
            for (int j = 0; j < igualdades[0].length; j++) {
                igualdades[(igualdades.length - 2)][j] = igualdades[(igualdades.length - 2)][j] + vector[j];
            }
        }

        //La matriz con M eliminada es: 
        //System.out.println("La matriz con M eliminada de Z es: ");
        //System.out.println(Arrays.deepToString(igualdades));
        //System.out.println("");
        return igualdades;
    }

    public static double[][] determinarVarEnBase(double[][] igualdades, boolean mostrarVB) throws IOException {

        boolean esCero = false;
        ArrayList<Integer> posX = new ArrayList<>();
        ArrayList<Integer> posY = new ArrayList<>();
        System.out.println("");
        //ArrayList<ArrayList<Double>> varBasicas = new ArrayList<>();
        for (int i = 0; i < (igualdades.length - 1)/*-1 para no evaluar Z*/; i++) {
            for (int j = 0; j < igualdades[i].length; j++) {
                if (igualdades[i][j] == 1) {

                    for (int k = 0; k < igualdades.length; k++) {
                        if (i != k) {
                            if (igualdades[k][j] == 0) {
                                esCero = true;
                            } else {
                                esCero = false;
                                break;
                            }
                        }

                    }
                    if (esCero) {
                        posX.add(i);
                        posY.add(j);
                    }
                }

            }

        }

        double[][] varBasicas = new double[posX.size()][2];

        for (int i = 0; i < varBasicas.length; i++) {

            for (int j = 0; j < varBasicas[i].length; j++) {
                if (j == 0) {
                    varBasicas[i][j] = posX.get(i);
                } else {
                    varBasicas[i][j] = posY.get(i);
                }

            }
        }

        //  System.out.println("Las variables basicas son: ");
        //  System.out.println(Arrays.deepToString(varBasicas));
        for (int i = 0, x = 0, y = 0; i < varBasicas.length; i++) {
            for (int j = 0; j < varBasicas[i].length; j++) {
                if (j == 0) {
                    x = (int) varBasicas[i][j];
                } else {
                    y = (int) varBasicas[i][j];
                }
            }
            if (mostrarVB) {
                System.out.println(Arrays.toString(varBasicas[i]) + " = X" + (y + 1) + " = " + fraccion.fraction(igualdades[x][igualdades[0].length - 1]));
            }

        }

        return varBasicas;
    }

    public static void solBasicaInicial(double array[][], boolean tecnicaM) throws IOException {
        System.out.println("");
        System.out.println("SOLUCIÓN BÁSICA INICIAL");
        procedimiento += "\n\nSOLUCIÓN BÁSICA INICIAL\n";
        ArrayList<Integer> Vbasicas = new ArrayList<>();
        double[][] VB = determinarVarEnBase(array, false);
        for (int i = 0, x = 0, y = 0; i < VB.length; i++) {
            for (int j = 0; j < VB[i].length; j++) {
                if (j == 0) {
                    x = (int) VB[i][j];
                } else {
                    y = (int) VB[i][j];
                    Vbasicas.add((y + 1));
                }
            }
        }

        for (int x = 0; x < Vbasicas.size(); x++) {
            System.out.print("X" + Vbasicas.get(x));
            System.out.print(" = " + fraccion.fraction(array[x][(array[x].length - 1)]) + "\n");
            procedimiento += "X" + Vbasicas.get(x) + " = " + fraccion.fraction(array[x][(array[x].length - 1)]) + "\n";
        }
        if (tecnicaM) {
            System.out.print("Z = " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n");
            procedimiento += "Z = " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n";
        } else {
            //System.out.println("tenica M");

            if (array[array.length - 2][(array[array.length - 1].length - 1)] == 0) {//Si no hay M
                System.out.print("Z = " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n");
                procedimiento += "Z = " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n";
            } else if (array[array.length - 1][(array[array.length - 1].length - 1)] == 0) {//Si hay M

                System.out.print("Z = " + fraccion.fraction(array[array.length - 2][(array[array.length - 1].length - 1)]) + "M" + "\n\n");
                procedimiento += "Z = " + fraccion.fraction(array[array.length - 2][(array[array.length - 1].length - 1)]) + "M" + "\n\n";
            } else {
                System.out.print("Z = " + fraccion.fraction(array[array.length - 2][(array[array.length - 1].length - 1)]) + "M");
                procedimiento += "Z = " + fraccion.fraction(array[array.length - 2][(array[array.length - 1].length - 1)]) + "M";
                if (array[array.length - 1][(array[array.length - 1].length - 1)] > 0) {
                    System.out.print(" + " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n");
                    procedimiento += " + " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n";
                } else if (array[array.length - 1][(array[array.length - 1].length - 1)] < 0) {
                    System.out.print(" " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n");
                    procedimiento += " " + fraccion.fraction(array[array.length - 1][(array[array.length - 1].length - 1)]) + "\n\n";
                }

            }

        }
    }

    public static boolean comprobarFactibilidadZ(double igualdades[][], int MAXMIN, boolean tecnicaM) {
        boolean condicionZ = false;/*
            Arreglo de boolean, para determinar si si todos los valores son
            positivos o cero (MAX) o negativos o cero (MIN).
            
            Donde:
                True indica que el bucle, va a continuar porque la condición
                se cumple.
         */
        if (tecnicaM) {
            OUTER:
            for (int j = 0; j < igualdades[0].length; j++) {
                switch (MAXMIN) {
                    case 1://Maximización
                        if (igualdades[(igualdades.length - 1)][j] >= 0) {

                            condicionZ = false;/*Si al terminar de revisar todos los
                        valores de Z, la condición continua siendo false, eso quiere
                        decir que ya encontramos la SOLUCIÓN FACTIBLE ÓPTIMA.
                             */
                        } else {
                            condicionZ = true;/*True indica que el valor es < 0
                        por lo que, si al evaluar cualquiera de los valores, uno
                        es negativo, eso nos indica que el algoritmo Simplex debe
                        seguir
                             */ break OUTER;
                            /*Ya no es necesario seguir evaluando los demás valores
                        entonces terminamos el bucle con la instrucción break;
                        porque seria redundante seguir comparando.
                             */
                        }
                        break;
                    case 2://Minimización
                        if (igualdades[(igualdades.length - 1)][j] <= 0) {

                            condicionZ = false;/*True indica que el valor es > 0
                        por lo que, si al evaluar cualquiera de los valores, uno
                        es positivo, eso nos indica que el algoritmo Simplex debe
                        seguir
                             */
                        } else {
                            condicionZ = true;/*True indica que el valor es <= 0
                        por lo que, si al terminar de revisar todos los valores,
                        la condición se queda en false, eso nos indica que el
                        algoritmo Simplex debe terminar porque en ese momento,
                        habremos obtenido una SOLUCIÓN FACTIBLE ÓPTIMA.
                             */ break OUTER;
                            /*Ya no es necesario seguir evaluando los demás valores
                        entonces terminamos el bucle con la instrucción break;
                        porque seria redundante seguir comparando.
                             */
                        }
                        break;
                    default:
                        break OUTER; //Salir del bucle porque el usuario no ha definido si MAX o MIN.
                }
            }
        } else {
            //Se esta aplicando la Tecnica M, y hay que verificar dos columnas

            boolean filaRES[] = new boolean[igualdades[0].length - 1];
            double[] vectorM = new double[igualdades[0].length - 1];
            double[] vectorZ = new double[igualdades[0].length - 1];
            double[] vectorRES = new double[igualdades[0].length - 1];

            for (int i = 0; i < vectorZ.length; i++) {
                vectorZ[i] = igualdades[(igualdades.length - 1)][i];
                vectorM[i] = igualdades[(igualdades.length - 2)][i];
            }

            for (int x = 0; x < vectorRES.length; x++) {
                vectorRES[x] = (vectorM[x] * Math.pow(10, 6)) + vectorZ[x];
            }

            //System.out.println(Arrays.toString(vectorM));
            for (int i = 0; i < vectorZ.length; i++) {

                if (MAXMIN == 1) {
                    //MAXIMIZACIÓN
                    filaRES[i] = vectorRES[i] < 0;
                } else if (MAXMIN == 2) {
                    //MINIMIZACIÓN
                    filaRES[i] = vectorRES[i] > 0;
                }

            }

//            System.out.println("FilaRES");
//            System.out.println(Arrays.toString(filaRES));
            boolean M = false, Z = false;
            for (boolean a : filaRES) {
                if (a) {
                    M = true;
                }
            }

            if (M) {
                condicionZ = true;
            } else {
                condicionZ = false;
            }

        }

        if (condicionZ) {
            System.out.println("No es óptima");
            procedimiento += "\nNo es óptima";
        } else {
            System.out.println("Es óptima");
            procedimiento += "\nEs óptima";
        }
        return condicionZ;
    }

    public static int varEntrada(double[][] igualdades, int MAXMIN, boolean tecnicaM) throws IOException {
        //Del paso 5, determinando la variable de entrada.
        ArrayList<Integer> varEntradaPosI = new ArrayList<>();
        ArrayList<Integer> varEntradaPosJ = new ArrayList<>();
        ArrayList<Double> mayorMenor = new ArrayList<>();

        double numMayorMenor = 0;

        if (tecnicaM == false) {
            //Se aplica la tecnica M
            if (MAXMIN == 1) {

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    double valor = (igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j];
                    mayorMenor.add(valor);
                }

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    /*
                        valor más pequeño(MAXIMIZACIÓN)
                     */
                    if (((igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j]) == mayorMenor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                        numMayorMenor = (igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j];
                    }
                }

            } else if (MAXMIN == 2) {

                for (int j = 0; j < igualdades[0].length - 1; j++) {

                    double valor = (igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j];
                    mayorMenor.add(valor);
                }

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    /*
                        valor más grande(MINIMIZACIÓN)
                     */
                    if (((igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j]) == mayorMenor.stream().mapToDouble(i -> i).max().getAsDouble()) {
                        numMayorMenor = (igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j];
                    }
                }

            }

            for (int j = 0; j < igualdades[0].length; j++) {
                if (((igualdades[(igualdades.length - 2)][j] * Math.pow(10, 2)) + igualdades[(igualdades.length - 1)][j]) == numMayorMenor) {/*
                        obteniendo el par ordenado de la variable de entrada.
                     */

                    varEntradaPosI.add(igualdades[(igualdades.length - 1)].length);
                    varEntradaPosJ.add(j);
                }
            }
            System.out.println("La variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                    + " = " + fraccion.fraction(numMayorMenor));//Variable de entrada.
            procedimiento += "\nLa variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                    + " = " + fraccion.fraction(numMayorMenor);
        } else {
            //Se aplica el método simplex normal
            if (MAXMIN == 1) {

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    mayorMenor.add(igualdades[(igualdades.length - 1)][j]);
                }

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    /*
                        valor más pequeño(MAXIMIZACIÓN)
                     */
                    if (igualdades[(igualdades.length - 1)][j] == mayorMenor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                        numMayorMenor = igualdades[(igualdades.length - 1)][j];
                    }
                }

            } else if (MAXMIN == 2) {

                for (int j = 0; j < igualdades[0].length - 1; j++) {

                    mayorMenor.add(igualdades[(igualdades.length - 1)][j]);
                }

                for (int j = 0; j < igualdades[0].length - 1; j++) {
                    /*
                        valor más grande(MINIMIZACIÓN)
                     */
                    if (igualdades[(igualdades.length - 1)][j] == mayorMenor.stream().mapToDouble(i -> i).max().getAsDouble()) {
                        numMayorMenor = igualdades[(igualdades.length - 1)][j];
                    }
                }

            }

            for (int j = 0; j < igualdades[0].length - 1; j++) {
                if (igualdades[(igualdades.length - 1)][j] == numMayorMenor) {/*
                        obteniendo el par ordenado de la variable de entrada.
                     */

                    varEntradaPosI.add(igualdades[(igualdades.length - 1)].length);
                    varEntradaPosJ.add(j);
                }
            }
            System.out.println("La variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                    + " = " + fraccion.fraction(numMayorMenor));//Variable de entrada.
            procedimiento += "\nLa variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                    + " = " + fraccion.fraction(numMayorMenor);
        }
        return (varEntradaPosJ.get(0));
    }

    public static int varSalida(double[][] igualdades, boolean tecnicaM) throws IOException {
        double[] divisionMenor = new double[igualdades.length];
        ArrayList<Integer> varSalidaPosJ = new ArrayList<>();

        ArrayList<Double> divisionMenorValor = new ArrayList<>();
        int posConM = 0;
        if (tecnicaM) {
            posConM = 1;
        } else {
            System.out.println("Se usa una fila menos, por la \"M\"");
            posConM = 2;
        }
        for (int i = 0; i < igualdades.length - posConM /*-1 para no considerar a Z*/; i++) {

            if (igualdades[i][(filaPivote)] == 0) {
                //Si el denominador es cero, devolver infinito
                divisionMenorValor.add(Double.POSITIVE_INFINITY);
                System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + ") = " + "Infinity");
                procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + ") = " + "Infinity";
            } else {
                divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][(filaPivote)];

                if (divisionMenor[i] <= 0) {
                    //Validando que no se puedan tomar valores negativos
                    divisionMenor[i] = Double.POSITIVE_INFINITY;
                }
                System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + ") = " + divisionMenor[i]);
                procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + ") = " + divisionMenor[i];
                divisionMenorValor.add(divisionMenor[i]);
            }
        }

        for (int k = 0; k < divisionMenor.length; k++) {

            if (divisionMenor[k] == divisionMenorValor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                //determinar cual es valor finito positivo menor.
                varSalidaPosJ.add(k);//Solo se guarda la fila pivote en esta variable
            }
        }

        ArrayList<Integer> Vbasicas = new ArrayList<>();
        double[][] VB = determinarVarEnBase(igualdades, false);
        for (int i = 0, x = 0, y = 0; i < VB.length; i++) {
            for (int j = 0; j < VB[i].length; j++) {
                if (j == 0) {
                    x = (int) VB[i][j];
                } else {
                    y = (int) VB[i][j];
                    Vbasicas.add((y + 1));
                }
            }
        }
        System.out.println("La variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1));
        procedimiento += "\nLa variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1);
        return varSalidaPosJ.get(0);
    }

    public static double[][] paso6(double[][] igualdades, int MAXMIN, boolean condicionZ) throws IOException {
        if (condicionZ) {//Si no es óptimo, se ejecuta esto.-
            igualdades = ConvertirVariableEnBase(igualdades, columnaPivote, filaPivote);
            mostrarMatriz(igualdades);
        } else {
            switch (MAXMIN) {
                case 1://Maximización
                    System.out.println("Paso 5: \n\tTodos los valores son positivos o cero."
                            + "\n\tla solución es óptima.");
                    break;
                case 2://Minimización
                    System.out.println("Paso 5: \n\tTodos los valores son negativos o cero."
                            + "\n\tla solución es óptima.");
                    break;
                default:
                    break;
            }
            //System.out.println("Paso 5:");

        }
        return igualdades;
    }

    public static double[] determinarVarEnBaseValor(double[][] igualdades) {

        boolean esCero = false;
        ArrayList<Integer> posX = new ArrayList<>();
        ArrayList<Integer> posY = new ArrayList<>();

        //ArrayList<ArrayList<Double>> varBasicas = new ArrayList<>();
        for (int i = 0; i < (igualdades.length - 1)/*-1 para no evaluar Z*/; i++) {
            for (int j = 0; j < igualdades[i].length; j++) {
                if (igualdades[i][j] == 1) {

                    for (int k = 0; k < igualdades.length; k++) {
                        if (i != k) {
                            if (igualdades[k][j] == 0) {
                                esCero = true;
                            } else {
                                esCero = false;
                                break;
                            }
                        }

                    }
                    if (esCero) {
                        posX.add(i);
                        posY.add(j);
                    }
                }

            }

        }

        double[][] varBasicas = new double[posX.size()][2];

        for (int i = 0; i < varBasicas.length; i++) {

            for (int j = 0; j < varBasicas[i].length; j++) {
                if (j == 0) {
                    varBasicas[i][j] = posX.get(i);
                } else {
                    varBasicas[i][j] = posY.get(i);
                }

            }
        }

//        System.out.println("Las variables basicas son: ");
//        System.out.println(Arrays.deepToString(varBasicas));
        double valores[] = new double[varBasicas.length];
        for (int i = 0, x = 0, y = 0; i < varBasicas.length; i++) {
            for (int j = 0; j < varBasicas[i].length; j++) {
                if (j == 0) {
                    x = (int) varBasicas[i][j];
                } else {
                    y = (int) varBasicas[i][j];
                }
            }

            valores[i] = igualdades[x][igualdades[0].length - 1];

            //System.out.println(Arrays.toString(varBasicas[i]) + " = X" + (y + 1) + " = " + igualdades[x][igualdades[0].length - 1]);
        }

        return valores;
    }

    public static ArrayList<Integer> VBasicasSubIndice(double[][] array) throws IOException {

        ArrayList<Integer> Vbasicas = new ArrayList<>();
        double[][] VB = determinarVarEnBase(array, false);
        for (int i = 0, x = 0, y = 0; i < VB.length; i++) {
            for (int j = 0; j < VB[i].length; j++) {
                if (j == 0) {
                    x = (int) VB[i][j];
                } else {
                    y = (int) VB[i][j];
                    Vbasicas.add((y + 1));
                }
            }
        }

//        for (int x = 0; x < Vbasicas.size(); x++) {
//            System.out.print("X" + Vbasicas.get(x));
//            System.out.print(" = " + array[x][(array[x].length - 1)] + "\n");
//        }
//        System.out.print("Z = " + array[array.length - 1][(array[array.length - 1].length - 1)] + "\n\n");
        //Collections.sort(Vbasicas); //Ordenar las variables básicas
        return Vbasicas;
    }

    public static void comprobarZ(double[][] matrix, double[] ZObj, boolean tecnicaM) throws IOException {
        //Probar el valor de Z

        double Z = 0;
        HashMap<Integer, Double> hmapVB = new HashMap<>();
        ArrayList<Integer> subIndiceVB = VBasicasSubIndice(matrix);

        System.out.println("La solución es: ");
        procedimiento += "\nLa solución es: \n";
        for (int x = 0; x < subIndiceVB.size(); x++) {
            System.out.print("X" + subIndiceVB.get(x));
            hmapVB.put(subIndiceVB.get(x), matrix[x][(matrix[x].length - 1)]);
            System.out.println(" = " + fraccion.fraction(hmapVB.get(subIndiceVB.get(x))));
            procedimiento += "X" + subIndiceVB.get(x) + " = " + fraccion.fraction(hmapVB.get(subIndiceVB.get(x))) + "\n";
        }

        //Obteniendo las llaves del hmap
        List keys = new ArrayList(hmapVB.keySet());
        //Ordenando variables básicas
        Collections.sort(keys);//Aqui ya esta ordenadas

        //Creando un contadores, para saber si es la primera iteración, y agregar o no, un signo(concatenado).
//        int x = 0;
//
//        System.out.print("\nZ = ");
//        procedimiento += "\n\nZ = ";
//        for (Object a : keys) {
//
//            if (x == ZObj.length) {
//                //Nos salimos del bucle, porque ya se evaluó la función objetivo.
//                //si hubiese más variables, serian, de holgura o de exceso.
//                break;
//            }
//            if ((x != 0)) {
//                if (hmapVB.get((int) a) > 0) {
//                    System.out.print(" + ");
//                    procedimiento += " + ";
//                }
//            }
//
//            if (x == (int) a) {
//                System.out.print(fraccion.fraction(ZObj[x])
//                        + "(" + fraccion.fraction(hmapVB.get((int) a)) + ")");
//                Z += (ZObj[x] * hmapVB.get((int) a));
//                procedimiento += fraccion.fraction(ZObj[x])
//                        + "(" + fraccion.fraction(hmapVB.get((int) a)) + ")";
//            }
//
//            //System.out.println("Llave: " + a + " su valor es: " + hmaVB.get((int) a));
//            x++;
//        }
        System.out.print("Z = ");
        procedimiento += "\nZ = ";
        for (int i = 0, k = 0; i < ZObj.length; i++) {
            if (hmapVB.containsKey((i + 1))) {

                if ((k != 0)) {
                    if (hmapVB.get((i + 1)) > 0) {
                        System.out.print(" + ");
                        procedimiento += " + ";
                    }
                }

                System.out.print(fraccion.fraction(ZObj[i])
                        + "(" + fraccion.fraction(hmapVB.get((i + 1))) + ")");

                Z += (ZObj[i] * hmapVB.get((i + 1)));
                procedimiento += fraccion.fraction(ZObj[i])
                        + "(" + fraccion.fraction(hmapVB.get((i + 1))) + ")";
                k++;
            }
        }

        if (tecnicaM) {
            System.out.print(" ---> " + fraccion.fraction(Z) + "\n\n");
            procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
        } else {

            if (matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)] == 0) {//Si no hay M
                System.out.print(" ---> " + fraccion.fraction(Z) + "\n\n");
                procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
            } else if (matrix[matrix.length - 1][(matrix[matrix.length - 1].length - 1)] == 0) {//Si hay M

                System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M" + "\n\n");
                procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
            } else {
                //System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M");
//                if (Z > 0) {
//                    System.out.print(" + " + fraccion.fraction(Z) + "\n\n");
//                } else if (Z < 0) {
//                    System.out.print(" " + fraccion.fraction(Z) + "\n\n");
//                }
                System.out.print(" = " + fraccion.fraction(Z) + "\n\n");

            }

//            System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M "+ fraccion.fraction(Z) + "\n\n");
//            procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
        }

    }
}
