/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodosimplex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Fernando
 */
public class MetodoSimplex2 {

    static Fraction fraccion = new Fraction();//Instancia de la clase Fraction para convertir decimales a quebrados
    private static int columnaPivote, filaPivote, MAXMIN;
    private static double[][] matriz;
    private static double[] fObjetivo;
    private static boolean condicionZ = false;
     //Nuevos campos de clase
    static ArrayList<Integer> vHolguraIndice = new ArrayList<>();
    static ArrayList<Integer> vArtificialIndice = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //matriz = paso1();
        
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
            2,};
        
        matriz = paso1(array, fObjetivo, condicion, MAXMIN, verificarCondiciones(condicion));
        
        //Paso2 -->Mostrar matriz
        System.out.println("");
        System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));

        //Paso3 --> Solucion basica inial
        solBasicaInicial(matriz);

        //Paso4 -->Determinar si la funcion es optima
        System.out.println("Determinando si la funcion es optima...");
        condicionZ = comprobarFactibilidadZ(matriz, MAXMIN);

        int iteracion = 1;
        while (condicionZ) {
            System.out.println("Iteración: " + iteracion);
            //paso5 --> Determinar variable de entrada
            filaPivote = varEntrada(matriz, MAXMIN);

            //paso6 --> Determinar variable de salida
            columnaPivote = varSalida(matriz);

            /////////ELEMEMTO PIVOTE//////////////////////////////
            System.out.println("Variable de entrada con valor: " + (columnaPivote + 1));
            System.out.println("Variable de salida con valor: " + (filaPivote + 1));
            System.out.println("A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote]);

            matriz = ConvertirVariableEnBase(matriz, columnaPivote, filaPivote);
            mostrarMatriz(matriz);
            condicionZ = comprobarFactibilidadZ(matriz, MAXMIN);
            iteracion++;
        }

        comprobarZ(matriz, fObjetivo);
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
    
    public static boolean verificarCondiciones(int[] condicion) {
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

    public static void solBasicaInicial(double array[][]) throws IOException {
        System.out.println("");
        System.out.println("SOLUCIÓN BÁSICA INICIAL");
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
            System.out.print(" = " + array[x][(array[x].length - 1)] + "\n");
        }
        System.out.print("Z = " + array[array.length - 1][(array[array.length - 1].length - 1)] + "\n\n");

    }

    public static boolean comprobarFactibilidadZ(double igualdades[][], int MAXMIN) {
        boolean condicionZ = false;/*
            Arreglo de boolean, para determinar si si todos los valores son
            positivos o cero (MAX) o negativos o cero (MIN).
            
            Donde:
                True indica que el bucle, va a continuar porque la condición
                se cumple.
         */
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

        if (condicionZ) {
            System.out.println("No es optima");
        } else {
            System.out.println("Es optima");
        }
        return condicionZ;
    }

    public static int varEntrada(double[][] igualdades, int MAXMIN) throws IOException {
        //Del paso 5, determinando la variable de entrada.
        ArrayList<Integer> varEntradaPosI = new ArrayList<>();
        ArrayList<Integer> varEntradaPosJ = new ArrayList<>();

        double numMayorMenor = 0;
        if (MAXMIN == 1) {

            for (int j = 0; j < igualdades[0].length; j++) {

                if (igualdades[(igualdades.length - 1)][j] < numMayorMenor) {/*
                        valor más pequeño(MAXIMIZACIÓN)
                     */
                    numMayorMenor = igualdades[(igualdades.length - 1)][j];
                }
            }
        } else if (MAXMIN == 2) {

            for (int j = 0; j < igualdades[0].length; j++) {

                if (igualdades[(igualdades.length - 1)][j] > numMayorMenor) {/*
                        valor más grande(MINIMIZACIÓN)
                     */
                    numMayorMenor = igualdades[(igualdades.length - 1)][j];
                }
            }

        }

        for (int j = 0; j < igualdades[0].length; j++) {
            if (igualdades[(igualdades.length - 1)][j] == numMayorMenor) {/*
                        obteniendo el par ordenado de la variable de entrada.
                 */

                varEntradaPosI.add(igualdades[(igualdades.length - 1)].length);
                varEntradaPosJ.add(j);
            }
        }
        System.out.println("La variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                + " = " + fraccion.fraction(numMayorMenor));//Variable de entrada.
        return (varEntradaPosJ.get(0));
    }

    public static int varSalida(double[][] igualdades) throws IOException {
        double[] divisionMenor = new double[igualdades.length];
        ArrayList<Integer> varSalidaPosJ = new ArrayList<>();

        ArrayList<Double> divisionMenorValor = new ArrayList<>();
        for (int i = 0; i < igualdades.length - 1 /*-1 para no considerar a Z*/; i++) {
            
            if(igualdades[i][(filaPivote)] == 0){
               //Si el denominador es cero, devolver infinito
                divisionMenorValor.add(Double.POSITIVE_INFINITY);
            }else{
            divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][(filaPivote)];

            System.out.println((i + 1) + " : " + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                    + "/" + fraccion.fraction(igualdades[i][(filaPivote)])
                    + " = " + divisionMenor[i]);
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
        System.out.println("La variable de salida (Xs) es: " + "X" + varSalidaPosJ.get(0));
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

    public static void comprobarZ(double[][] matrix, double[] ZObj) throws IOException {
        //Probar el valor de Z

        double Z = 0;
        System.out.println("La solución es: ");
        ArrayList<Integer> subIndiceVB = VBasicasSubIndice(matrix);
        for (int x = 0; x < subIndiceVB.size(); x++) {
            System.out.print("X" + subIndiceVB.get(x));
            System.out.print(" = " + fraccion.fraction(matrix[x][(matrix[x].length - 1)]) + "\n");
        }

        System.out.print("\nZ = ");
        for (int i = 1, k = 0; k < subIndiceVB.size(); k++) {
            if (i == subIndiceVB.get(k)) {
                //esta si es la buena :'v
                if ((i - 1) > 1) {
                    if (fObjetivo[(i - 1)] > 0) {
                        System.out.print(" + ");
                    }
                }
                System.out.print(fraccion.fraction(ZObj[(i - 1)])
                        + "(" + fraccion.fraction(matrix[k][(matrix[k].length - 1)]) + ")");
                Z += (ZObj[(i - 1)] * matrix[k][(matrix[k].length - 1)]);
            }
            if (k == subIndiceVB.size() - 1) {
                if (i < fObjetivo.length) {
                    i++;
                    k = 0;
                }
            }
        }

        System.out.print(" = " + fraccion.fraction(Z) + "\n\n");
    }
}
