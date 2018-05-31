/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import metodosimplex.Fraction;

/**
 *
 * @author striker
 */
public class inter extends javax.swing.JFrame {

    /**
     * Creates new form inter
     */
    //Campos de clase
    public static Fraction fraccion = new Fraction();//Instancia de la clase Fraction para convertir decimales a quebrados
    public static int columnaPivote, filaPivote, MAXMIN;
    public static double[][] matriz;
    public static double[] fObjetivo;
    public static int[] condicion;
    public static boolean condicionZ = false;
    public static String procedimiento = "";
    //Nuevos campos de clase
    public static ArrayList<Integer> vHolguraIndice = new ArrayList<>();
    public static ArrayList<Integer> vArtificialIndice = new ArrayList<>();

    //Necesario para establecer el tamaño de la ventana
    Toolkit toolkit;

    //Necesario para crear la tabla
    String[] tablaObjetivo = {"X1",
        "X2"};

    Object[][] datosFObjetivo = {
        {"", ""}
    };

    String[] columnasRestricciones = {"X1",
        "X2", "CONDICIÓN", "Bi"};

    Object[][] datosRestricciones = {
        {"", "",
            "<=", ""},
        {"", "",
            "<=", ""}
    };

    public inter() {
        initComponents();
        this.setTitle("Método SIMPLEX - Fernando Alvarado - UES-FMO");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dimension = new Dimension();
        toolkit = Toolkit.getDefaultToolkit();
        dimension = toolkit.getScreenSize();
        int ancho = dimension.width;
        int alto = dimension.height;
        double porcentajeScreen = 0.8;
        System.out.println("ancho: " + (int) (ancho * porcentajeScreen));
        System.out.println("alto: " + (int) (alto * porcentajeScreen));
        this.setSize((int) (ancho * porcentajeScreen), (int) (alto * porcentajeScreen));
        this.setLocationRelativeTo(null);

        //haciendo pruebas
        //Creando tabla de función Objetivo
        JTable table1 = new JTable(datosFObjetivo, tablaObjetivo);
        TableModel modelo1 = table1.getModel();
        this.jTableObjetivo.setModel(modelo1);
        jTableObjetivo.getTableHeader().setReorderingAllowed(false);

        //Creando tabla de restricciones
        JTable table = new JTable(datosRestricciones, columnasRestricciones);
        TableModel modelo = table.getModel();
        this.jTableRestricciones.setModel(modelo);
        TableColumn sportColumn = this.jTableRestricciones.getColumnModel().getColumn(2);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("<=");
        comboBox.addItem("=");
        comboBox.addItem(">=");
        sportColumn.setCellRenderer(new DefaultTableCellRenderer());
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));
        //Evitar que se puedan mover columnas
        jTableRestricciones.getTableHeader().setReorderingAllowed(false);

        //Estableciendo tamaño de las celdas
        jTableObjetivo.setRowHeight(30);
        jTableRestricciones.setRowHeight(30);
        jTableSimplex.setRowHeight(30);

        //Establecer la fuente de la tabla
        Font fuente = new Font("Dialog", Font.BOLD, 14);
        jTableObjetivo.setFont(fuente);
        jTableRestricciones.setFont(fuente);
        jTableSimplex.setFont(fuente);

        //ESTABLECIENDO VALORES INICIALES DE LA TABLA SIMPLEX
        String[] tablaSimplex = {"Las variables apareceran aquí, cuando se resuelva su modelo"};

        Object[][] tablaSimplexDato = {
            {"Los valores apareceran aquí, cuando se resuelva su modelo"}};

        //Creando tabla de la tabla SIMPLEX
        JTable table2 = new JTable(tablaSimplexDato, tablaSimplex);
        TableModel modelo2 = table2.getModel();
        this.jTableSimplex.setModel(modelo2);
        jTableSimplex.getTableHeader().setReorderingAllowed(false);

        this.spinnerVD.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tablaRestricciones();
                System.out.println("Variables de decisión: " + spinnerVD.getValue());
            }
        });

        this.spinnerRES.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tablaRestricciones();
                System.out.println("Restricciones: " + spinnerRES.getValue());
            }
        });

        this.jTableObjetivo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //Cuando el usuario presiona una tecla
                //System.out.println(e.);
                if (((int) e.getKeyChar() >= 32 && (int) e.getKeyChar() <= 44)
                        || ((int) e.getKeyChar() == 47)
                        || ((int) e.getKeyChar() >= 58 && (int) e.getKeyChar() <= 255)) {
                    JOptionPane.showMessageDialog(rootPane, "Ha pulsado una tecla inválida: " + e.getKeyChar());
                }
            }

        });

        this.jTableRestricciones.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //Cuando el usuario presiona una tecla
                //System.out.println(e.);
                if (jTableRestricciones.getSelectedColumn() != ((int) spinnerVD.getModel().getValue())) {
                    if (((int) e.getKeyChar() >= 32 && (int) e.getKeyChar() <= 44)
                            || ((int) e.getKeyChar() == 47)
                            || ((int) e.getKeyChar() >= 58 && (int) e.getKeyChar() <= 255)) {
                        JOptionPane.showMessageDialog(rootPane, "Ha pulsado una tecla inválida: " + e.getKeyChar());
                    }
                }

            }

        });

        this.jTableSimplex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //Cuando el usuario presiona una tecla
                //System.out.println(e.);
                if (((int) e.getKeyChar() >= 32 && (int) e.getKeyChar() <= 45)
                        || ((int) e.getKeyChar() == 47)
                        || ((int) e.getKeyChar() >= 58 && (int) e.getKeyChar() <= 255)) {
                    JOptionPane.showMessageDialog(rootPane, "Ha pulsado una tecla inválida: " + e.getKeyChar());
                }
            }

        });

    }

    private void tablaRestricciones() {
        String[] columnasTabla = new String[(((int) spinnerVD.getModel().getValue()) + 2)];

        for (int i = 0; i < columnasTabla.length; i++) {
            if (i == (columnasTabla.length - 2)) {
                columnasTabla[i] = "CONDICIÓN";
            } else if (i == (columnasTabla.length - 1)) {
                columnasTabla[i] = "Bi";
            } else {
                columnasTabla[i] = "X" + (i + 1);
            }
        }

        Object[][] datos = new Object[((int) spinnerRES.getModel().getValue())][(((int) spinnerVD.getModel().getValue()) + 2)];

        for (int i = 0; i < datos.length; i++) {
            for (int j = 0; j < datos[i].length; j++) {
                if (j == (datos[i].length - 2)) {
                    datos[i][j] = "<=";
                } else {
                    datos[i][j] = "";
                }
            }
        }
        JTable table = new JTable(datos, columnasTabla);
        TableModel modelo = table.getModel();
        jTableRestricciones.setModel(modelo);
        TableColumn sportColumn = this.jTableRestricciones.getColumnModel().getColumn(datos[0].length - 2);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("<=");
        comboBox.addItem("=");
        comboBox.addItem(">=");
        sportColumn.setCellRenderer(new DefaultTableCellRenderer());
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Evitar que se puedan mover columnas
        jTableRestricciones.getTableHeader().setReorderingAllowed(false);

        //Tabla función objetivo
        String[] columnasTablaObj = new String[(((int) spinnerVD.getModel().getValue()))];

        for (int i = 0; i < columnasTablaObj.length; i++) {
            columnasTablaObj[i] = "X" + (i + 1);
        }

        Object[][] datosOBj = new Object[1][(((int) spinnerVD.getModel().getValue()))];

        for (int i = 0; i < datosOBj.length; i++) {
            for (int j = 0; j < datosOBj[i].length; j++) {
                datosOBj[i][j] = "";

            }
        }
        JTable table1 = new JTable(datosOBj, columnasTablaObj);
        TableModel modelo1 = table1.getModel();
        jTableObjetivo.setModel(modelo1);
        jTableObjetivo.getTableHeader().setReorderingAllowed(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spinnerVD = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spinnerRES = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableObjetivo = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableRestricciones = new javax.swing.JTable();
        btnResolver = new javax.swing.JButton();
        cbxMAXMIN = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtProcedimiento = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableSimplex = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        spinnerVD.setModel(new javax.swing.SpinnerNumberModel(2, 2, null, 1));

        jLabel1.setText("Variables de decisión");

        jLabel2.setText("Restricciones");

        spinnerRES.setModel(new javax.swing.SpinnerNumberModel(2, 2, null, 1));

        jLabel3.setText("Sujeto a restricciones:");

        jLabel4.setText("Función objetivo, Z = ");

        jTableObjetivo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableObjetivo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTableObjetivo);

        jTableRestricciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableRestricciones.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(jTableRestricciones);

        btnResolver.setText("Resolver");
        btnResolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResolverActionPerformed(evt);
            }
        });

        cbxMAXMIN.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MAXIMIZAR", "MINIMIZAR" }));

        txtProcedimiento.setColumns(20);
        txtProcedimiento.setRows(5);
        txtProcedimiento.setText("El procedimiento aparecerá aquí, \nuna vez se haya resuelto su modelo.");
        txtProcedimiento.setToolTipText("");
        jScrollPane2.setViewportView(txtProcedimiento);

        jLabel5.setText("Procedimiento: ");

        jLabel6.setText("TABLA SIMPLEX");

        jTableSimplex.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        jTableSimplex.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTableSimplex);

        jButton1.setText("Quitar foco de tablas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spinnerVD, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spinnerRES, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(btnResolver)
                                .addComponent(cbxMAXMIN, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                                    .addComponent(spinnerVD, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spinnerRES)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbxMAXMIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnResolver))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnResolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResolverActionPerformed
        //Traer todos los valores desde la interfaz grafica.
        procedimiento = "";
        //Ejecutar algoritmo SIMPLEX
        matriz = paso1(antePaso(), fObjetivo, condicion, MAXMIN, verificarCondiciones(condicion));

        //Paso2 -->Mostrar matriz
        System.out.println("");
        System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        procedimiento += "\n";
        this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
        //Agregando la Matriz al procedimiento
        procedimiento += Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");
        this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

        try {
            //Paso3 --> Solucion basica inial
            solBasicaInicial(matriz);
            this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
        } catch (IOException ex) {
            System.out.println("Ocurrio un error en el paso 3:\n" + ex);
            Logger.getLogger(inter.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Paso4 -->Determinar si la funcion es optima
        System.out.println("Determinando si la funcion es optima...");
        procedimiento += "Determinando si la funcion es optima...";
        this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
        condicionZ = comprobarFactibilidadZ(matriz, MAXMIN);
        this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

        int iteracion = 1;
        while (condicionZ) {
            System.out.println("Iteración: " + iteracion);
            procedimiento += "\nIteración: " + iteracion;
            this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

            try {
                //paso5 --> Determinar variable de entrada
                filaPivote = varEntrada(matriz, MAXMIN);
                this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            } catch (IOException ex) {
                System.out.println("Ocurrio un error en el paso 5:\n" + ex);
                Logger.getLogger(inter.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                //paso6 --> Determinar variable de salida
                columnaPivote = varSalida(matriz);
                this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            } catch (IOException ex) {
                System.out.println("Ocurrio un error en el paso 6:\n" + ex);
                Logger.getLogger(inter.class.getName()).log(Level.SEVERE, null, ex);
            }

            /////////ELEMEMTO PIVOTE//////////////////////////////
            System.out.println("Variable de entrada con valor: " + (columnaPivote + 1));
            System.out.println("Variable de salida con valor: " + (filaPivote + 1));
            System.out.println("A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote]);
            procedimiento += "\nVariable de entrada con valor: " + (columnaPivote + 1)
                    + "\n" + "Variable de salida con valor: " + (filaPivote + 1)
                    + "\n" + "A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote];
            this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            matriz = ConvertirVariableEnBase(matriz, columnaPivote, filaPivote);

            try {
                mostrarMatriz(matriz);
                this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            } catch (IOException ex) {
                System.out.println("Ocurrio un error en el paso 2, mostrar Matriz:\n" + ex);
                Logger.getLogger(inter.class.getName()).log(Level.SEVERE, null, ex);
            }
            condicionZ = comprobarFactibilidadZ(matriz, MAXMIN);
            this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            iteracion++;
        }

        try {
            comprobarZ(matriz, fObjetivo);
            this.txtProcedimiento.setText(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
        } catch (IOException ex) {
            System.out.println("Ocurrio un error en el paso final, comprobando Z:\n" + ex);
            Logger.getLogger(inter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnResolverActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        if (jTableObjetivo.isEditing()) {
            jTableObjetivo.removeEditor();
        }

        if (jTableRestricciones.isEditing()) {
            jTableRestricciones.removeEditor();
        }

        if (jTableSimplex.isEditing()) {
            jTableSimplex.removeEditor();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(inter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(inter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(inter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(inter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new inter().setVisible(true);
            }
        });
    }

    //FUNCIONES DEL MÉTODO SIMPLEX
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
                System.out.println("X" + a);
                procedimiento += "X" + a + "\n";
            });
        }

        if (!vArtificialIndice.isEmpty()) {
            System.out.println("\nVariables artificiales");
            procedimiento += "\n\nVariables artificiales";
            vArtificialIndice.forEach((Integer b) -> {
                System.out.println("X" + b);
                procedimiento += "X" + b + "\n";
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
            System.out.print(" = " + array[x][(array[x].length - 1)] + "\n");
            procedimiento += "X" + Vbasicas.get(x) + " = " + array[x][(array[x].length - 1)] + "\n";
        }
        System.out.print("Z = " + array[array.length - 1][(array[array.length - 1].length - 1)] + "\n\n");
        procedimiento += "Z = " + array[array.length - 1][(array[array.length - 1].length - 1)] + "\n\n";

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
            System.out.println("No es óptima");
            procedimiento += "\nNo es óptima";
        } else {
            System.out.println("Es óptima");
            procedimiento += "\nEs óptima";
        }
        return condicionZ;
    }

    public static int varEntrada(double[][] igualdades, int MAXMIN) throws IOException {
        //Del paso 5, determinando la variable de entrada.
        ArrayList<Integer> varEntradaPosI = new ArrayList<>();
        ArrayList<Integer> varEntradaPosJ = new ArrayList<>();
        ArrayList<Double> mayorMenor = new ArrayList<>();

        double numMayorMenor = 0;
        if (MAXMIN == 1) {

            for (int j = 0; j < igualdades[0].length; j++) {
                mayorMenor.add(igualdades[(igualdades.length - 1)][j]);
            }

            for (int j = 0; j < igualdades[0].length; j++) {
                /*
                        valor más pequeño(MAXIMIZACIÓN)
                 */
                if (igualdades[(igualdades.length - 1)][j] == mayorMenor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                    numMayorMenor = igualdades[(igualdades.length - 1)][j];
                }
            }

        } else if (MAXMIN == 2) {

            for (int j = 0; j < igualdades[0].length; j++) {

                mayorMenor.add(igualdades[(igualdades.length - 1)][j]);
            }

            for (int j = 0; j < igualdades[0].length; j++) {
                /*
                        valor más grande(MINIMIZACIÓN)
                 */
                if (igualdades[(igualdades.length - 1)][j] == mayorMenor.stream().mapToDouble(i -> i).max().getAsDouble()) {
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
        procedimiento += "\nLa variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1)
                + " = " + fraccion.fraction(numMayorMenor);
        return (varEntradaPosJ.get(0));
    }

    public static int varSalida(double[][] igualdades) throws IOException {
        double[] divisionMenor = new double[igualdades.length];
        ArrayList<Integer> varSalidaPosJ = new ArrayList<>();

        ArrayList<Double> divisionMenorValor = new ArrayList<>();
        for (int i = 0; i < igualdades.length - 1 /*-1 para no considerar a Z*/; i++) {

            if (igualdades[i][(filaPivote)] == 0) {
                //Si el denominador es cero, devolver infinito
                divisionMenorValor.add(Double.POSITIVE_INFINITY);
                System.out.println((i + 1) + " : " + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + "/" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + " = " + "Infinity");
                procedimiento += "\n" + (i + 1) + " : " + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + "/" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + " = " + "Infinity";
            } else {
                divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][(filaPivote)];

                System.out.println((i + 1) + " : " + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + "/" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + " = " + divisionMenor[i]);
                procedimiento += "\n" + (i + 1) + " : " + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + "/" + fraccion.fraction(igualdades[i][(filaPivote)])
                        + " = " + divisionMenor[i];
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
        procedimiento += "\nLa variable de salida (Xs) es: " + "X" + varSalidaPosJ.get(0);
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

        System.out.print("\nZ = ");
        procedimiento += "\n\nZ = ";
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

        System.out.print(" = " + fraccion.fraction(Z) + "\n\n");
        procedimiento += " = " + fraccion.fraction(Z) + "\n\n";
    }

    //CÓDIGO PARA OBTENER TODOS LOS VALORES MEDIANTE LA INTERFAZ GRAFICA
    public double[][] antePaso() {
        //Trayendo combobox MAXMIN
        String opcionMAXMIN = this.cbxMAXMIN.getModel().getSelectedItem().toString();
        System.out.println(opcionMAXMIN);
        if (opcionMAXMIN.equals("MAXIMIZAR")) {
            MAXMIN = 1;
        } else {
            MAXMIN = 2;
        }

        //Restricciones
        double[] fObjetivo = new double[(int) spinnerVD.getModel()
                .getValue()];//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
        setfObjetivo(fObjetivo);

        //Intentando obtener los valores de la funcion Objetivo
        for (int row = 0; row < this.jTableObjetivo.getRowCount(); row++) {
            for (int column = 0; column < this.jTableObjetivo.getColumnCount(); column++) {
                if (this.jTableObjetivo.getValueAt(row, column).toString().isEmpty()) {
                    fObjetivo[column] = 0;

                    //Si la columna esta vacia, se añade cero, en la tabla
                    this.jTableObjetivo.setValueAt(0, row, column);
                } else {
                    fObjetivo[column] = Double.parseDouble(this.jTableObjetivo.getValueAt(row, column).toString());
                }

            }
        }

        for (double a : fObjetivo) {
            System.out.println(a);
        }
        System.out.println("La función objetivo que obtuve es:");
        System.out.println(Arrays.toString(fObjetivo));

        //Intetando obtener condiciones
        /*
                   CONDICIÓN DE LAS RESTRICCIONES:
        
            0 ---->  (<=)
            1 ---->  (=)
            2 ---->  (>=)
         */
        System.out.println("condiciones");
        int[] condicionAUX = new int[(int) spinnerRES.getModel().getValue()];
        for (int x = 0; x < this.jTableRestricciones.getRowCount(); x++) {
            System.out.println(this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()));

            if (this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()).toString().equals("<=")) {
                condicionAUX[x] = 0;
            } else if (this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()).toString().equals("=")) {
                condicionAUX[x] = 1;
            } else {
                condicionAUX[x] = 2;
            }
        }

        System.out.println("Las condiciones son: ");
        System.out.println(Arrays.toString(condicionAUX));

        condicion = condicionAUX;//asignando el valor al campo de clase

        //Por último, obteniendo los valores de la Matriz(Restricciones)
        double[][] array = new double[(int) spinnerRES.getModel().getValue()][(int) spinnerVD.getModel().getValue() + 1]; //Restricciones.
        ArrayList<Double> auxiliar = new ArrayList<>();
        for (int x = 0; x < this.jTableRestricciones.getRowCount(); x++) {

            for (int y = 0; y < this.jTableRestricciones.getColumnCount(); y++) {
                if (y != (int) spinnerVD.getModel().getValue()) {
                    if (this.jTableRestricciones.getValueAt(x, y).toString().isEmpty()) {
                        auxiliar.add(0.0);
                        //Si no se escribe ningun valor, entonces se coloca cero, en la tabla
                        this.jTableRestricciones.setValueAt(0, x, y);
                    } else {
                        auxiliar.add(Double.parseDouble(this.jTableRestricciones.getValueAt(x, y).toString()));
                    }
                }
            }
        }

        for (int i = 0, k = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++, k++) {
                array[i][j] = auxiliar.get(k);
            }
        }

        System.out.println(Arrays.deepToString(array));
        return array;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnResolver;
    public javax.swing.JComboBox<String> cbxMAXMIN;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTable jTableObjetivo;
    public javax.swing.JTable jTableRestricciones;
    public javax.swing.JTable jTableSimplex;
    public javax.swing.JSpinner spinnerRES;
    public javax.swing.JSpinner spinnerVD;
    public javax.swing.JTextArea txtProcedimiento;
    // End of variables declaration//GEN-END:variables
}
