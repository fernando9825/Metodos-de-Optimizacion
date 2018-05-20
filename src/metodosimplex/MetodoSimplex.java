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
    //VARIABLES A UTILIZAR
    static int filaPivote, columnaPivote;
    
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
        //Variables para el paso 4 (que está más adelante).
        /*
            0 - El usuario no ha decidido si es un problema de MAX o MIN.
            1 - Problema de Maximización
            2 - Problema de Minización
         */
        int MAXMIN = 1; //MAX

        //Restricciones
        double[] fObjetivo = {5, 8, 7};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
        double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
            {3, 3, 3, 30},
            {0, 2, 5, 30},
            {4, 4, 0, 24}};

        /*  
            PASO 1: Convertir las desigualdades en igualdades, introduciendo 
            variables de holgura, una por cada restricción que tengamos.
         
            debido a que este problema es con <=... para determinar cuantas variables de holguras vamos a tener...
            solo hay que ver multiplicar la cantidad de restricciones que tenemos, esto es:
            array.lenght * 2, y a eso hay que sumarle 1, para considerar a la función objetivo.
         */
        //CONVIRTIENDO LAS RESTRICCIONES EN IGUALDADES, AGREGANDO UNA VARIABLE DE HOLGURA (PROBLEMA CON <=)
        double[][] igualdades = new double[(array.length + 1)][2 * (array.length) + 1];
        int[] varBaseX = new int[array.length];
        
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
            PASO 2: Ordenar los datos en una tabla simplex. 
        
        
            Se definen las columnas del simplex, mediante el siguiente bucle, el
            el cual funciona obteniendo el número de filas que tiene la matriz
            "igualdades".
         */
        //System.out.println("VB     X1        X2        X3        X4        X5        X6        Bi");
        mostrarMatriz(igualdades, variablesBasicasPosI, variablesBasicasPosJ, varBaseX);

        /*
            PASO 3:  Determine la solución básica factible inicial. 
            Se seleccionan las variables holgura como variables de base inicial.
        
            Aquí se imprime la solución básica inicial, recorriendo la matriz 
            igualdades, donde se encuentren los valores 1.0 en la intersección
            de fila-columna, eso nos indica que la variable esta en la base.
         */
        //Obtenemos el valor Bi de Z
        double Z = igualdades[(igualdades.length - 1)][(igualdades[(igualdades.length - 1)].length - 1)];

        //Este arreglo nos sirva para almacenar los Bi de las variables básicas.
        double[] varBasicas = new double[variablesBasicasPosI.size()];
        for (int i = 0; i < variablesBasicasPosI.size(); i++) {
            varBasicas[i] = igualdades[i][(igualdades[i].length - 1)];
        }//Aquí hemos obtenido el valor de las variables básicas.    

        System.out.println("SOLUCIÓN BÁSICA INICIAL");
        //Se muestra la matriz con igualdades luego de haber convertido las desigualdades
        for (int i = 0, k = 0; i < igualdades.length; i++) {
            for (int j = 0; j < igualdades[i].length; j++) {
                
                if ((i == variablesBasicasPosI.get(i)) && (j == variablesBasicasPosJ.get(i))) {
                    //Si se entra aqui, quiere decir que esa variable esta en la base.

                    System.out.print("X" + (j + 1) + " = " + varBasicas[k]);
                    k++;/*si se encuentra una variable de base, se aumenta el valor de k
                          Para poder mostrar la siguiente variable que esta en la base
                          (si es que hay alguna)*/
                    break;//Terminamos, porque la variable de base ya se determino.
                }
                
            }//Fin del segundo bucle.
            System.out.println("");//Salto de linea
            if (k == variablesBasicasPosI.size()) {
                break;//Salimos del bucle puesto que ya no hay más variables en la Base.
            }
        }//Fin del primer bucle.

        //Mostrando el valor de Z en la Solución básica inicial.
        System.out.println("Z = " + Z + "\n\n");

        /*
            PASO 4: Determinar si esta solución es óptima, verificar si 
            la función objetivo puede ser mejorada, aumentando disminuyendo el 
            valor de cualquier variable no básica, esto se hace eliminando las 
            variables básicas de la fila Z y revisando el signo de los elementos 
            de esa fila para cada variable no básica. Si todos los valores son 
            positivos o ceros, entonces esta solución es óptima (maximización). 
            Si todos los valores son negativos o ceros, entonces esta solución 
            es óptima (minimización). 
         */
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

        /*
            PASO 5: Si la solución no es óptima determine de la tabla, 
            la variable de entrada (o variable básica nueva); Seleccione la 
            variable no básica que al incrementarse o disminuirse, aumentará o 
            disminuirá el valor de Z más rápidamente. Esto se hace revisando los 
            valores de la fila Z en la tabla y se selecciona la variable no 
            básica cuyo valor es más pequeño (maximización). Se selecciona la 
            variable no básica cuyo valor es más grande (minimización). 
            “Principio de Optimidad”.
         */
        if (condicionZ) {//Si no es óptimo, se ejecuta esto.-

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
                    } else if (igualdades[(igualdades.length - 1)][j] == numMayorMenor) {//DEGENERACIÓN
                        System.out.println("Tenemos un caso de degeneración porque hay dos variables iguales\n"
                                + "y el no sabemos que variable de entrada tomar");
                        break;
                    }
                }
            } else if (MAXMIN == 2) {
                
                for (int j = 0; j < igualdades[0].length; j++) {
                    
                    if (igualdades[(igualdades.length - 1)][j] < numMayorMenor) {/*
                        valor más grande(MINIMIZACIÓN)
                         */
                        numMayorMenor = igualdades[(igualdades.length - 1)][j];
                    } else if (igualdades[(igualdades.length - 1)][j] == numMayorMenor) {//DEGENERACIÓN
                        System.out.println("Tenemos un caso de degeneración porque hay dos variables iguales\n"
                                + "y el no sabemos que variable de entrada tomar");
                        break;
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
                    + " = " + numMayorMenor);//Variable de entrada.

            /*
                PASO 6: Determine la nueva variable básica que sale (variable de salida). 
                Divida los elementos de la última columna (donde se colocan los valores de Bi), 
                entre los valores correspondientes (que están en la misma fila) 
                de la columna de la variable de entrada Xe. Tome como variable 
                de salida Xs aquella de las variables básicas para la que el 
                coeficiente es el más pequeño, finito y positivo, para maximización 
                o minimización. “Principio de Factibilidad”.
             */
            double[] divisionMenor = new double[igualdades.length];
            ArrayList<Integer> varSalidaPosJ = new ArrayList<>();
            
            ArrayList<Double> divisionMenorValor = new ArrayList<>();
            for (int i = 0; i < igualdades.length - 1 /*-1 para no considerar a Z*/; i++) {
                divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][varEntradaPosJ.get(0)];
                
                System.out.println((i + 1) + " : " + igualdades[i][(igualdades[0].length - 1)]
                        + "/" + igualdades[i][varEntradaPosJ.get(0)]
                        + " = " + divisionMenor[i]);
                if (divisionMenor[i] > 0) {
                    divisionMenorValor.add(divisionMenor[i]);
                }
            }
            
            for (int k = 0; k < divisionMenor.length; k++) {
                
                if (divisionMenor[k] == divisionMenorValor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                    //determinar cual es valor finito positivo menor.
                    varSalidaPosJ.add(k);//Solo se guarda la fila pivote en esta variable
                }
            }
            columnaPivote = varSalidaPosJ.get(0);
            filaPivote = varEntradaPosJ.get(0);
            System.out.println("La variable de salida (Xs) es: " + "X" + varBaseX[varSalidaPosJ.get(0)]);
            System.out.println("Valor más pequeño, finito y positivo: "
                    + divisionMenorValor.stream().mapToDouble(i -> i).min().getAsDouble()
                    + "");
            
            System.out.println("Fila Pivote: " + (filaPivote + 1));
            System.out.println("Columna Pivote: " + (columnaPivote + 1));
            System.out.println("Elemento pivote A" + (columnaPivote + 1)
                    + (filaPivote + 1) + " = "
                    + igualdades[columnaPivote][filaPivote]);

            /*
                Ejecutar paso intermedio entre 6-7
             */
            //igualdades = dividirFila(igualdades, igualdades[columnaPivote][filaPivote], columnaPivote);
            igualdades = ConvertirVariableEnBase(igualdades, columnaPivote, filaPivote);
            mostrarMatriz(igualdades, variablesBasicasPosI, variablesBasicasPosJ, varBaseX);

            // System.out.println(Arrays.deepToString(igualdades));
        } else {

            /*
                En si, este else, deberia ser parte del paso 4, pero, al tratarse
                de un condicional, me parecio mejor ubicarlo en el paso 5, en el
                cuál solo hay dos posibles casos, o es óptimo o no.
                si no es óptimo, se ejecuta el codigo que esta en el if, sino (else)
                se ejecuta este código.
             */
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

        //Pruebas
        ConvertirVariableEnBase(igualdades, columnaPivote, filaPivote);
        determinarVarEnBase(igualdades);
        
    }
    
    public static double[][] dividirFila(double igualdades[][], double divisor, int columnaPivote) {
        
        for (int j = 0; j < (igualdades[0].length); j++) {
            //  System.out.println(igualdades[columnaPivote][j] + " " + columnaPivote + ", " + j + " " + "/" + igualdades[columnaPivote][filaPivote] + columnaPivote + ", " + filaPivote +  " = " + igualdades[columnaPivote][j]);
            igualdades[columnaPivote][j] = igualdades[columnaPivote][j] / divisor;
        }
        return igualdades;
    }
    
    public static void mostrarMatriz(double igualdades[][], ArrayList<Integer> variablesBasicasPosI, ArrayList<Integer> variablesBasicasPosJ, int[] varBaseX) {
        
        System.out.println("");//Salto de linea
        String espacioEntreColumnas = "             ";
        String espacioEntreColumnasNegativo = "            ";
        
        System.out.println("TABLA SIMPLEX\n");
        System.out.print("VB" + espacioEntreColumnas);
        for (int i = 1; i < igualdades[0].length; i++) {
            System.out.print("X" + i + espacioEntreColumnas + " ");
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
                                filaVariableBase += igualdades[i][x] + espacioEntreColumnasNegativo; //Si el valor es negativo se coloca un espacio menos
                            } else {
                                
                                filaVariableBase += igualdades[i][x] + espacioEntreColumnas;
                            }
                        }

                        //Se muestra la fila que contiene variable en la Base.
                        System.out.print("X" + (j + 1));
                        varBaseX[i] = (j + 1);
                        System.out.print(espacioEntreColumnas + filaVariableBase);
                        break;//Terminamos el bucle anterior, porque ya fue recorrido.

                    }
                    
                } else {
                    String filaZ = ""; //Se crea una nueva cadena para poder almacenar la Fila Z
                    for (int k = 0; k < igualdades[i].length; k++) { //Aqui se lee la fila Z y se concatena cada valor

                        if (igualdades[i][k] < 0) {
                            filaZ += igualdades[i][k] + espacioEntreColumnasNegativo; //Si el valor es negativo se coloca un espacio menos.
                        } else {
                            filaZ += igualdades[i][k] + espacioEntreColumnas;
                            
                        }
                    }
                    //Se muestra la fila Z
                    System.out.print("Z");
                    System.out.print(espacioEntreColumnas + " " + filaZ);
                    break; //Terminamos el bucle anterior, porque ya fue recorrido.
                }
                
            }//Fin del segundo bucle
            System.out.println("");//Dar el salto de linea para la siguiente fila
        }//Fin del primer bucle

        System.out.println("\n");//Doble salto de linea, luego de terminar el paso 2.
    }
    
    public static double[][] ConvertirVariableEnBase(double[][] igualdades, int posX, int posY) {
        /*
            Este metodo se encarga de convertir en 1 la variable que le pidamos
            y en convertir en ceros los valores de la columna donde se encuentra 
            esa variable.
         

        
            Este bucle se enccarga de dividir la fila Pivote entre el elemento
            pivote
         */
        for (int j = 0; j < igualdades[posX].length; j++) {
            igualdades[posX][j] = igualdades[posX][j] / igualdades[posX][posY];
        }

        /*
        Este bucle se encarga en convertir en ceros los elementos de la
        columna pivote
         */
        for (double[] a : igualdades) {
            if (a[posY] != igualdades[posX][posY]) {
                a[posY] = (-igualdades[posX][posY] * a[posY]) + a[posY];
            }
        }
        return igualdades;
    }
    
    public static void determinarVarEnBase(double[][] igualdades) {
        
        boolean esCero = false;
        ArrayList<Integer> posX = new ArrayList<>();
        ArrayList<Integer> posY = new ArrayList<>();
        
        //ArrayList<ArrayList<Double>> varBasicas = new ArrayList<>();
        for (int i = 0; i < igualdades.length; i++) {
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
        
        for(int i = 0; i < varBasicas.length; i++){
            
            for(int j = 0; j < varBasicas[i].length; j++){
                if( j == 0){
                     varBasicas[i][j] = posX.get(i);
                }else{
                    varBasicas[i][j] = posY.get(i);
                }
               
            }
        }
        
        System.out.println("Las variables basicas son: ");
        for(double[] a:varBasicas){
            
            System.out.println("X" + Arrays.toString(a));
        }
    }
}
