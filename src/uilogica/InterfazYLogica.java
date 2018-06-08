/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uilogica;

import java.awt.Color;
import metodosimplex.LlenarTabla;
import java.awt.Component;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import metodosimplex.Fraction;

/**
 *
 * @author striker
 */
public class InterfazYLogica extends javax.swing.JFrame {

    /**
     * Creates new form InterfazYLogica
     */
    //Campos de clase
    public static Fraction fraccion;
    public static int columnaPivote, filaPivote, MAXMIN;
    public static double[][] matriz;
    public static double[] fObjetivo;
    public static int[] condicion;
    public static boolean condicionZ = false, tecnicaM = false, solIlim = false, detener = false;
    public static String procedimiento = "";
    //Nuevos campos de clase
    public static ArrayList<Integer> vHolguraIndice = new ArrayList<>();
    public static ArrayList<Integer> vArtificialIndice = new ArrayList<>();
    //Filas
    public static ArrayList<Integer> vArtificialFilas = new ArrayList<>();

    //Valor arbitrario de tetha(si es que hay sol ilimitada)
    static int n = 10000;//Limite de teta
    static int teta = (int) (Math.random() * n) + 1;

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

    //Necesario para la tabla Simplex
    DefaultTableModel tableModel = new DefaultTableModel();

    public InterfazYLogica() throws IOException {
        initComponents();
        setIcon();
        fraccion = new Fraction(true);//Instancia de la clase Fraction para convertir decimales a quebrados
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);//a pantalla completa
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

        //quitando botón de soluciones multiples
        this.solMultiple.setEnabled(false);
        this.solMultiple.setVisible(false);

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
        Font fuente2 = new Font("Dialog", Font.BOLD, 16);
        jTableSimplex.setFont(fuente2);

        //ESTABLECIENDO VALORES INICIALES DE LA TABLA SIMPLEX
        String[] tablaSimplex = {"Las variables apareceran aquí, cuando se resuelva su modelo"};

        Object[][] tablaSimplexDato = {
            {"Los valores apareceran aquí, cuando se resuelva su modelo"}};

        //Creando tabla de la tabla SIMPLEX
        JTable table2 = new JTable(tablaSimplexDato, tablaSimplex);
        TableModel modelo2 = table2.getModel();
        this.jTableSimplex.setModel(modelo2);
        jTableSimplex.getTableHeader().setReorderingAllowed(false);

        //Desactivando boton detener, porque el programa esta creando todos los objetos
        btnDetener.setVisible(false);
        btnDetener.setEnabled(false);

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

        //ESTABLECIENDO MODELO DE JTABLESIMPLEX, QUE NOS PERMITE MODIFICARLA DESPUES
        //tableModel.addColumn("Los resultados aparecerán aquí, una vez se haya resuelto su modelo");
        jTableSimplex.setModel(tableModel);
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

//        //Listener para la tabla simplex
//        this.jTableSimplex.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                jTableSimplex.scrollRectToVisible(jTableSimplex.getCellRect(jTableSimplex.getRowCount() - 1, 0, true));
//            }
//        });
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
        solMultiple = new javax.swing.JButton();
        btnDetener = new javax.swing.JButton();

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
        jTableSimplex.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableSimplex.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTableSimplex);

        jButton1.setText("Quitar foco de tablas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        solMultiple.setText("Sol múltiple");
        solMultiple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solMultipleActionPerformed(evt);
            }
        });

        btnDetener.setText("DETENER");
        btnDetener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetenerActionPerformed(evt);
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
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spinnerVD, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spinnerRES, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cbxMAXMIN, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(btnResolver)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnDetener))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel5))
                    .addComponent(solMultiple))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxMAXMIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnResolver)
                            .addComponent(btnDetener))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(solMultiple)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnResolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResolverActionPerformed

        //Desactivando botón para que no ejecute otro hilo.
        btnResolver.setEnabled(false);

        //Activando boton detener, esperando acción del usuario
        btnDetener.setVisible(true);
        btnDetener.setEnabled(true);

        //Estableciendo variables en su valor inicial;
        condicionZ = false;
        tecnicaM = false;
        solIlim = false;
        vHolguraIndice.removeAll(vHolguraIndice);
        vArtificialIndice.removeAll(vArtificialIndice);
        vArtificialFilas.removeAll(vArtificialFilas);

        //quitando botón de soluciones multiples
        solMultiple.setEnabled(false);
        solMultiple.setVisible(false);
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                /*Estableciendo un problema aquí. para no estarlo escribiendo en la interfaz grafica
                
                 */

                //  MAXMIN = 1; //MAX
//                MAXMIN = 2; //MIN
//                //PROBLEMA CON EMPATE EN VAR DE SALIDA (DEGENERACIÓN)
//                double[] fObjetivo = {-3, -3, -5, -4};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {4, 2, 4, -1, 2},
//                    {6, 1, 6, -1, 3}};
//
//                int[] condicion = new int[]{
//                    0,
//                    0};
//                double[] fObjetivo = {4, 6, 7, 5, 9};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn//MAX
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {1, 3, 4, 5, 7, 20},
//                    {0, 6, 7, 8, 0, 15},
//                    {7, 8, 0, 7, 9, 30},
//                    {7, 2, 1, 0, 8, 20}};
//
//                //        Problema de amaya
//                int[] condicion = new int[]{
//                    2,
//                    2,
//                    2,
//                    0};
//                //PROBLEMA CON SOLUCIONES MÚLTIPLES
//                double[] fObjetivo = {6, 8, 4};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {3, 4, 2, 18},
//                    {4, 5, 1, 20}};
//
//                //   Restricciones con sol multiples
//                int[] condicion = new int[]{
//                    0, 
//                    0};
//                //PROBLEMA CON SOLUCIONES Ilimitadas //MAX
//                double[] fObjetivo = {4, 4, 3};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {3, -4, 1, 2},
//                    {2, 0, 2, 12}};
//
//                //   Restricciones con sol Ilimitadas
//                int[] condicion = new int[]{
//                    1,
//                    2};
//                //PROBLEMA CON Insoluble //MAX
//                double[] fObjetivo = {3, 2};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {2, 1, 2},
//                    {3, 4, 12}};
//
//                //   Restricciones con sol Ilimitadas
//                int[] condicion = new int[]{
//                    0,
//                    2};
////PROBLEMA CON SOLUCIONES Ilimitadas //MIN
//                double[] fObjetivo = {-4, -4, 3};//Función objetivo, Z = C1X1 + C2X2 + ... + CnXn
//                setfObjetivo(fObjetivo);
//                double[][] array = new double[][]{ //Restricciones, para este caso, todas <=
//                    {3, -4, 1, 2},
//                    {2, 0, 2, 12}};
//
//                //   Restricciones con sol Ilimitadas
//                int[] condicion = new int[]{
//                    0,
//                    1};
                //////////////////////FIN  DE ESTABLECER EL PROBLEMA//////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////
                //Traer todos los valores desde la interfaz grafica.
                procedimiento = "";
                //Ejecutar algoritmo SIMPLEX
                antePaso();
                tecnicaM = verificarCondiciones(condicion);//Si es false, todas son <=, sino, hay que usar la Tecnica M
                matriz = paso1(antePaso()/*array*/, fObjetivo, condicion, MAXMIN, tecnicaM);

                prepararTabla(matriz);//Preparar las columnas de la JTable

                //Paso2 -->Mostrar matriz
                System.out.println("");
                System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
                procedimiento += "\n";
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                //Agregando la Matriz al procedimiento
                procedimiento += Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

                if (!tecnicaM) {
                    System.out.println("\nSe eliminan las M, para obtener la solución básica inicial...");
                    vArtificialFilas.forEach((a) -> {
                        System.out.println(a);
                    });
                    matriz = eliminarM(matriz, vArtificialFilas, MAXMIN);

                    System.out.println("");
                    procedimiento += "\n";
                    System.out.println(Arrays.deepToString(matriz).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
                }

                try {
                    //Paso3 --> Solucion basica inial
                    solBasicaInicial(matriz, tecnicaM);
                    actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                } catch (IOException ex) {
                    System.out.println("Ocurrio un error en el paso 3:\n" + ex);
                    Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {//Mostrar la tabla simplex en la interfaz grafica
                    TablaSimplexIteracion(matriz, tecnicaM);
                } catch (IOException ex) {
                    Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Paso4 -->Determinar si la funcion es optima
                System.out.println("Determinando si la funcion es optima...");
                procedimiento += "Determinando si la funcion es optima...";
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                condicionZ = comprobarFactibilidadZ(matriz, MAXMIN, tecnicaM);
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

                int iteracion = 1;
                while (condicionZ) {
                    if (detener) {
                        //Si se presiona el boton de detener, se para el método simplex
                        break;
                    }
                    System.out.println("Iteración: " + iteracion);
                    procedimiento += "\nIteración: " + iteracion;
                    actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

                    try {
                        //paso5 --> Determinar variable de entrada
                        filaPivote = varEntrada(matriz, MAXMIN, tecnicaM);
                        actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                    } catch (IOException ex) {
                        System.out.println("Ocurrio un error en el paso 5:\n" + ex);
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        //paso6 --> Determinar variable de salida
                        columnaPivote = varSalida(matriz, tecnicaM);
                        actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                    } catch (IOException ex) {
                        System.out.println("Ocurrio un error en el paso 6:\n" + ex);
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (!solIlim) {
                        /////////ELEMEMTO PIVOTE//////////////////////////////
                        System.out.println("Variable de entrada con valor: " + (columnaPivote + 1));
                        System.out.println("Variable de salida con valor: " + (filaPivote + 1));
                        System.out.println("A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote]);
                        procedimiento += "\nVariable de entrada con valor: " + (columnaPivote + 1)
                                + "\n" + "Variable de salida con valor: " + (filaPivote + 1)
                                + "\n" + "A" + (columnaPivote + 1) + (filaPivote + 1) + " = " + matriz[columnaPivote][filaPivote];
                        actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                        matriz = ConvertirVariableEnBase(matriz, columnaPivote, filaPivote);

                        try {

                            mostrarMatriz(matriz);
                            actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                            try {//Mostrar la tabla simplex en la interfaz grafica
                                TablaSimplexIteracion(matriz, tecnicaM);
                            } catch (IOException ex) {
                                Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (IOException ex) {
                            System.out.println("Ocurrio un error en el paso 2, mostrar Matriz:\n" + ex);
                            Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        System.out.println("Determinando si la funcion es optima...");
                        procedimiento += "\n\nDeterminando si la funcion es optima...";
                        condicionZ = comprobarFactibilidadZ(matriz, MAXMIN, tecnicaM);
                        actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                        iteracion++;
                    } else {
                        //TENEMOS SOLUCIONES ILIMITADAS, TERMINAMOS EL BUCLE
                        condicionZ = false;
                        solMultiple.setEnabled(true);
                        solMultiple.setVisible(true);
                        solMultiple.setText("calcular nueva solución");
                    }

                }

                if (detener) {
                    System.out.println("Ha decidido detener el algoritmo");
                    procedimiento += "\n\n¡¡¡HA DECIDIDO DETENER EL ALGORTIMO\n\n";
                    actualizarProcedimiento(procedimiento);
                    detener = false;
                } else {
                    try {
                        comprobarZ(matriz, fObjetivo, tecnicaM);
                        actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                    } catch (IOException ex) {
                        System.out.println("Ocurrio un error en el paso final, comprobando Z:\n" + ex);
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Aqui se verifica si el problema tiene soluciones múltiples
                    //Este método da pie a comprobar las soluciones multiples...
                    //Buscando variables no basicas
                    System.out.println("VARIABLES NO BÁSICAS");
                    try {
                        System.out.println(determinarVarNOBasicas(matriz, tecnicaM));
                    } catch (IOException ex) {
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Reactivando botón para que pueda calcular una nueva solución.
                    btnResolver.setEnabled(true);

                    //Desactivando boton detener, porque la acción ya se ejecuto
                    btnDetener.setVisible(false);
                    btnDetener.setEnabled(false);
                }
            }

        });
        t1.start();


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

    private void solMultipleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solMultipleActionPerformed

        Thread t1 = new Thread(new Runnable() {
            public void run() {

                if (solIlim) {
                    try {
                        //Si tenemos soluciones ilimitadas
                        calcularNuevalSolI();
                    } catch (IOException ex) {
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    //Si tenemos múltiples soluciones
                    try {
                        solucionesMultiples(matriz, MAXMIN, tecnicaM);
                        actualizarProcedimiento(procedimiento);
                    } catch (IOException ex) {
                        Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        t1.start();
    }//GEN-LAST:event_solMultipleActionPerformed

    private void btnDetenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetenerActionPerformed
        //Presionar este botón detiene la ejecución del programa
        detener = true;

        //Reactivando botón de resolver
        btnResolver.setEnabled(true);

        //Desactivando boton detener, porque la acción ya se ejecuto
        btnDetener.setVisible(false);
        btnDetener.setEnabled(false);
    }//GEN-LAST:event_btnDetenerActionPerformed

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
            java.util.logging.Logger.getLogger(InterfazYLogica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazYLogica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazYLogica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazYLogica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                
                UIManager.put("control", new Color(128, 128, 128));
                UIManager.put("info", new Color(128, 128, 128));
                UIManager.put("nimbusBase", new Color(18, 30, 49));
                UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                UIManager.put("nimbusFocus", new Color(115, 164, 209));
                UIManager.put("nimbusGreen", new Color(176, 179, 50));
                UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
                UIManager.put("nimbusOrange", new Color(191, 98, 4));
                UIManager.put("nimbusRed", new Color(169, 46, 34));
                UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                UIManager.put("text", new Color(230, 230, 230));
                try {
                    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            javax.swing.UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    //e.printStackTrace();
                } catch (InstantiationException e) {
                    //e.printStackTrace();
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                } catch (javax.swing.UnsupportedLookAndFeelException e) {
                    //e.printStackTrace();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                try {
                    new InterfazYLogica().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            System.out.println("");
            procedimiento += "\n\n";
        }

        if (!vArtificialIndice.isEmpty()) {
            System.out.println("\nVariables artificiales");
            procedimiento += "Variables artificiales\n";
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
        System.out.println("La matriz con ceros en las VArt-base de Z es: ");
        System.out.println(Arrays.deepToString(igualdades));
        System.out.println("");

        procedimiento += "\n\nLa matriz con ceros en las VArt-base de Z es: ";
        procedimiento += "\n\n" + Arrays.deepToString(igualdades).replace("], ", "]\n").replace("[[", "[").replace("]]", "]");

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
            System.out.println("Es óptima\n");
            procedimiento += "\nEs óptima\n";
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
            System.out.println("La variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1));//Variable de entrada.
            procedimiento += "\nLa variable de entrada (Xe) es: " + "X" + (varEntradaPosJ.get(0) + 1);
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
        double[] divisionMenorEmpate = new double[(igualdades[0].length - 1)];
        ArrayList<Integer> varSalidaPosJ = new ArrayList<>();
        //ArrayList para determinar las soluciones ilimitadas
        ArrayList<Double> solIlimitadas = new ArrayList<>();
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
                solIlimitadas.add(Double.POSITIVE_INFINITY);//Esta variable permite identificar las soluciones ilimitadas
                System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + igualdades[i][(filaPivote)]
                        + ") = " + "Infinity");
                procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                        + ")/(" + igualdades[i][(filaPivote)]
                        + ") = " + "Infinity";

            } else {
                divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][(filaPivote)];

                if (divisionMenor[i] <= 0) {
                    //Validando que no se puedan tomar valores negativos
                    divisionMenor[i] = Double.POSITIVE_INFINITY;
                    //solIlimitadas.add(Double.POSITIVE_INFINITY);//Esta variable permite identificar las soluciones ilimitadas
                    System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                            + ")/(" + igualdades[i][(filaPivote)]
                            + ") = " + divisionMenor[i]);
                    procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                            + ")/(" + igualdades[i][(filaPivote)]
                            + ") = " + fraccion.fraction((igualdades[i][(igualdades[0].length - 1)]) / igualdades[i][(filaPivote)]);
                } else {
                    System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                            + ")/(" + igualdades[i][(filaPivote)]
                            + ") = " + divisionMenor[i]);
                    procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                            + ")/(" + igualdades[i][(filaPivote)]
                            + ") = " + divisionMenor[i];
                }

                divisionMenorValor.add(divisionMenor[i]);
                solIlimitadas.add(divisionMenor[i]);//Esta variable permite identificar las soluciones ilimitadas
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

        //Determinando si existen soluciones ilimtadas
        int x = 0;
        for (int i = 0; i < solIlimitadas.size(); i++) {
            if (solIlimitadas.get(i) == Double.POSITIVE_INFINITY) {
                x++;
            }
//            if(solIlimitadas.get(i) < 0){
//                x++;
//            }
            //System.out.println("sol I" + solIlimitadas.get(i));
        }
        //System.out.println("Tamaño de solIlimitadas: " + solIlimitadas.size());
        if (x == (solIlimitadas.size())) {
            solIlim = true;
        }

        System.out.println("Tamaño de varSalidaPosJ: " + varSalidaPosJ.size());
        System.out.println(varSalidaPosJ);

        if (solIlim) {
            System.out.println("Estamos en el caso de las soluciones ilimitadas");
            System.out.println("No se puede determinar variable de salida");
            procedimiento += "\n\nEstamos en el caso de las soluciones ilimitadas"
                    + "\nNo se puede determinar variable de salida";

        } else {

            if (varSalidaPosJ.size() > 1) {
                System.out.println("Entre a verificar");
                ArrayList<Double> auxEmpate = new ArrayList<>();
                for (int k = 0; k < divisionMenor.length; k++) {

                    if (divisionMenor[k] == divisionMenorValor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                        //determinar cual es valor finito positivo menor.

                        auxEmpate.add(divisionMenor[k]);//Solo se guarda la fila pivote en esta variable
                        System.out.println(auxEmpate.get(k));

                    }
                }
                int contadorRepetidos = 0;
                for (double a : auxEmpate) {
                    if (a != Double.POSITIVE_INFINITY) {
                        contadorRepetidos++;
                    }
                }
                if (contadorRepetidos != 0) {
                    System.out.println("¡¡¡TENEMOS UN EMPATE DE VARIABLES!!!");
                    System.out.println("Aplicando algoritmo de desempate");
                    procedimiento += "\n\n¡¡¡TENEMOS UN EMPATE DE VARIABLES!!!\n"
                            + "Aplicando algoritmo de desempate\n";

                    //Aplicar algoritmo para determinar variable de salida
                    //Primero hay que determinar las variables básicas
                    /*
                        DEBO CONTINUAR PROGRAMANDO AQUÍ, YA TENGO SUEÑO :´V
                     */
                    //Restableciendo arrayList´s para determinar la nueva variable de salida
                    divisionMenorValor.removeAll(divisionMenorValor);
                    varSalidaPosJ.removeAll(varSalidaPosJ);
                    System.out.println("Estas son las variables basicas, para empate");
                    System.out.println(Vbasicas);

                    ArrayList<Double> menor = new ArrayList<>();
                    for (int j = 0; j < divisionMenorEmpate.length; j++) {

                        for (int i = 0; i < igualdades.length - posConM /*-1 para no considerar a Z*/; i++) {

                            if (j != filaPivote) {
                                if (j > filaPivote) {
                                    if (igualdades[i][filaPivote] <= 0) {
                                        //Es negativo y no se considera
                                        menor.add(Double.POSITIVE_INFINITY);
                                    } else {
                                        //Si el numerador es cero, tampoco se considera
                                        if (igualdades[i][(j + 1)] == 0) {
                                            menor.add(Double.POSITIVE_INFINITY);
                                        } else {
                                            //Es un resultado válido.
                                            menor.add((igualdades[i][(j + 1)] / igualdades[i][filaPivote]));
                                        }
                                    }

                                } else {
                                    if (igualdades[i][filaPivote] <= 0) {
                                        //Es negativo y no se considera
                                        menor.add(Double.POSITIVE_INFINITY);
                                    } else {
                                        if (igualdades[i][(j)] == 0) {
                                            menor.add(Double.POSITIVE_INFINITY);
                                        } else {
                                            //Es un resultado válido.
                                            menor.add((igualdades[i][j] / igualdades[i][filaPivote]));
                                        }
                                    }
                                }
                            }
                        }

                        for (int a = 0; a < menor.size(); a++) {
                            //COMPROBANDO VALOR INTERNAMENTE
                            if (j != filaPivote) {
                                if (j > filaPivote) {
                                    System.out.println((j + 1) + " : " + "(" + fraccion.fraction(igualdades[a][(j + 1)])
                                            + ")/(" + igualdades[a][filaPivote] + ") = " + menor.get(a));
                                    procedimiento += (j + 1) + " : " + "(" + fraccion.fraction(igualdades[a][(j + 1)])
                                            + ")/(" + igualdades[a][filaPivote] + ") = " + menor.get(a) + "\n";
                                } else {
                                    System.out.println((j + 1) + " : " + "(" + fraccion.fraction(igualdades[a][j])
                                            + ")/(" + igualdades[a][filaPivote] + ") = " + menor.get(a));
                                    procedimiento += (j + 1) + " : " + "(" + fraccion.fraction(igualdades[a][j])
                                            + ")/(" + igualdades[a][filaPivote] + ") = " + menor.get(a) + "\n";
                                }
                            }

                        }

                        //Evaluando variables para determinar desempate
                        for (int a = 0; a < menor.size(); a++) {
                            //COMPROBANDO VALOR INTERNAMENTE
                            if (menor.get(a) == menor.stream().mapToDouble(r -> r).min().getAsDouble()) {
                                varSalidaPosJ.add(a);
                            }

                        }

                        System.out.println("Las var sallll" + varSalidaPosJ);
                        if (varSalidaPosJ.size() > 1) {
                            //AÚN EXISTE UN EMPATE, HAY QUE SEGUIR EVALUANDO
                            System.out.println("He salido, se ha determinado la variable de salida");
                            varSalidaPosJ.removeAll(varSalidaPosJ);
                            menor.removeAll(menor);
                        } else {
                            //SE HE DETERMINADO LA VARIABLE DE SALIDA, SALIENDO DEL BUCLE
                            break;
                        }
                    }
                    System.out.println("menor" + menor);

                }

                System.out.println("El algoritmo de desempate ha finalizado...");
                procedimiento += "\nEl algoritmo de desempate ha finalizado...\n";
                if (varSalidaPosJ.isEmpty()) {
                    //NO SE DETERMINO VARIABLE DE SALIDA, TOMANDO CUALQUIERA
                    System.out.println("No se pudo determinar variable de salida, tomando cualquiera");
                    procedimiento += "\nNo se pudo determinar variable de salida, tomando cualquiera\n";
                    //RESTABLECIENDO VARIABLES A SU VALOR ORIGINAL
                    divisionMenorValor.removeAll(divisionMenorValor);
                    solIlimitadas.removeAll(solIlimitadas);
                    for (int i = 0; i < igualdades.length - posConM /*-1 para no considerar a Z*/; i++) {

                        if (igualdades[i][(filaPivote)] == 0) {
                            //Si el denominador es cero, devolver infinito
                            divisionMenorValor.add(Double.POSITIVE_INFINITY);
                            solIlimitadas.add(Double.POSITIVE_INFINITY);//Esta variable permite identificar las soluciones ilimitadas
                            System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                    + ")/(" + igualdades[i][(filaPivote)]
                                    + ") = " + "Infinity");
                            procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                    + ")/(" + igualdades[i][(filaPivote)]
                                    + ") = " + "Infinity";

                        } else {
                            divisionMenor[i] = igualdades[i][(igualdades[0].length - 1)] / igualdades[i][(filaPivote)];

                            if (divisionMenor[i] <= 0) {
                                //Validando que no se puedan tomar valores negativos
                                divisionMenor[i] = Double.POSITIVE_INFINITY;
                                //solIlimitadas.add(Double.POSITIVE_INFINITY);//Esta variable permite identificar las soluciones ilimitadas
                                System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                        + ")/(" + igualdades[i][(filaPivote)]
                                        + ") = " + divisionMenor[i]);
                                procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                        + ")/(" + igualdades[i][(filaPivote)]
                                        + ") = " + fraccion.fraction((igualdades[i][(igualdades[0].length - 1)]) / igualdades[i][(filaPivote)]);
                            } else {
                                System.out.println((i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                        + ")/(" + igualdades[i][(filaPivote)]
                                        + ") = " + divisionMenor[i]);
                                procedimiento += "\n" + (i + 1) + " : " + "(" + fraccion.fraction(igualdades[i][(igualdades[0].length - 1)])
                                        + ")/(" + igualdades[i][(filaPivote)]
                                        + ") = " + divisionMenor[i];
                            }

                            divisionMenorValor.add(divisionMenor[i]);
                            solIlimitadas.add(divisionMenor[i]);//Esta variable permite identificar las soluciones ilimitadas
                        }

                    }

                    for (int k = 0; k < divisionMenor.length; k++) {

                        if (divisionMenor[k] == divisionMenorValor.stream().mapToDouble(i -> i).min().getAsDouble()) {
                            //determinar cual es valor finito positivo menor.
                            varSalidaPosJ.add(k);//Solo se guarda la fila pivote en esta variable
                        }
                    }
                    System.out.println("La variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1));
                    procedimiento += "\nLa variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1);
                } else {
                    System.out.println("La variable de salida (Xs) es: " + "X" + (Vbasicas.get(0)));
                    procedimiento += "\nLa variable de salida (Xs) es: " + "X" + (Vbasicas.get(0));
                }
            } else {
                //NO EXISTE EMPATE, SE PUEE DEVOLVER LA VARIABLE DE SALIDA
                System.out.println("La variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1));
                procedimiento += "\nLa variable de salida (Xs) es: " + "X" + (varSalidaPosJ.get(0) + 1);
            }
        }

        return varSalidaPosJ.get(0);
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

        //Aqui va la parte de soluciones ilimitadas
        if (solIlim) {

            System.out.println("El valor de tetha es: " + teta);
            System.out.println("La solución es: ");
            procedimiento += "\nLa solución es: \n";

            for (int x = 0; x < subIndiceVB.size(); x++) {
                System.out.print("X" + subIndiceVB.get(x));
                hmapVB.put(subIndiceVB.get(x), (matrix[x][(matrix[x].length - 1)] - (teta * matrix[x][filaPivote])));
                System.out.println(" = " + fraccion.fraction(hmapVB.get(subIndiceVB.get(x))));
                procedimiento += "X" + subIndiceVB.get(x) + " = " + fraccion.fraction(hmapVB.get(subIndiceVB.get(x))) + "\n";
            }

            //Agregar la nueva variable de entrada
            System.out.println("X" + (filaPivote + 1) + " = " + teta);
            procedimiento += "X" + (filaPivote + 1) + " = " + teta + "\n";
            hmapVB.put((filaPivote + 1), Double.parseDouble(Integer.toString(teta)));

            //Realizar el mismo proceso que se hace cuando hay solución factible óptima
            //Obteniendo las llaves del hmap
            List keys = new ArrayList(hmapVB.keySet());
            //Ordenando variables básicas
            Collections.sort(keys);//Aqui ya esta ordenadas

            System.out.print("Z = ");
            procedimiento += "\nZ = ";
            Z = matrix[(matrix.length - 1)][(matrix[0].length - 1)];

            //DEBO REVISAR ESTO
            if (MAXMIN == 1) {
                //Maximizar
                Z = Z + (Math.abs(matrix[(matrix.length - 1)][filaPivote]) * teta);
            } else {
                Z = Z - (Math.abs(matrix[(matrix.length - 1)][filaPivote]) * teta);
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
                    System.out.print("---> " + fraccion.fraction(Z) + "\n\n");
                    procedimiento += "---> " + fraccion.fraction(Z) + "\n\n";

                }

//            System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M "+ fraccion.fraction(Z) + "\n\n");
//            procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
            }

        } else {

            //Verificar si el problema no tiene solución.
            if (tecnicaM) {
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
                        System.out.print(" " + fraccion.fraction(Z) + "\n\n");
                        procedimiento += " " + fraccion.fraction(Z) + "\n\n";

                    }

//            System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M "+ fraccion.fraction(Z) + "\n\n");
//            procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
                }
            } else {
                if (Math.round(matrix[(matrix.length - 2)][(matrix[0].length - 1)]) != 0.0) {
                    System.out.println("El problema NO tiene solución:");
                    System.out.println("Aparece variable artificial");
                    System.out.println("\"NO HAY SOLUCIÓN FACTIBLE\"");
                    System.out.println("");
                    procedimiento += "\nEl problema NO tiene solución: \n"
                            + "\nAparece variable artificial\n"
                            + "\"NO HAY SOLUCIÓN FACTIBLE\"\n";
                    solMultiple.setVisible(false);
                    solMultiple.setEnabled(false);
                } else {
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
                            System.out.print("---> " + fraccion.fraction(Z) + "\n\n");
                            procedimiento += "---> " + fraccion.fraction(Z) + "\n\n";

                        }

//            System.out.print(" ---> " + fraccion.fraction(matrix[matrix.length - 2][(matrix[matrix.length - 1].length - 1)]) + "M "+ fraccion.fraction(Z) + "\n\n");
//            procedimiento += " --->" + fraccion.fraction(Z) + "\n\n";
                    }
                }
            }

        }
    }

    public static HashMap<Integer, Double> determinarVarNOBasicas(double[][] matrix, boolean tecnicaM) throws IOException {
        HashMap<Integer, String> hmapNVB = new HashMap<>();
        HashMap<Integer, String> hmapVB = new HashMap<>();
        HashMap<Integer, Double> hmapNVBDouble = new HashMap<>();
        ArrayList<Integer> subIndiceVB = VBasicasSubIndice(matrix);

        for (int x = 0; x < subIndiceVB.size(); x++) {
            hmapVB.put(subIndiceVB.get(x), "X" + (subIndiceVB.get(x)));
        }

        int[] columnasTabla = new int[matrix[0].length - 1];

        for (int i = 0; i < columnasTabla.length; i++) {
            columnasTabla[i] = (i + 1);
            hmapNVB.put((i + 1), "X" + (i + 1));
        }

        for (int a : columnasTabla) {
            if (hmapVB.containsKey(a)) {
                hmapNVB.remove(a);
                System.out.println("Eliminando variable básica de VNB: " + a);
            }
        }

        System.out.println(hmapNVB);
        //Obteniendo las llaves del hmap
        List keys = new ArrayList(hmapNVB.keySet());
        //Ordenando variables básicas
        Collections.sort(keys);//Aqui ya esta ordenadas

        double valor = 0;
        System.out.println("Keys Size: " + keys.size());
        for (int i = 0; i < keys.size(); i++) {

            if (tecnicaM) {
                System.out.println("sin usar la M, sol Multiple");
                valor = matrix[(matrix.length - 1)][((int) keys.get(i) - 1)];
            } else {
                //if ((Math.round(matrix[(matrix.length - 2)][(int) keys.get(i)]) == 0.0) && ((matrix[(matrix.length - 1)][(int) keys.get(i)]) == 0.0)) {
                System.out.println("USANDO la M, sol Multiple");
                System.out.println("M: " + (Math.round(matrix[(matrix.length - 2)][(int) keys.get(i)-1])));
                System.out.println("I: " + (Math.round(matrix[(matrix.length - 2)][(int) keys.get(i)-1])));
                valor = (Math.round(matrix[(matrix.length - 2)][(int) keys.get(i)-1])) + (matrix[(matrix.length - 1)][(int) keys.get(i)-1]);
                //}
            }
            hmapNVBDouble.put((int) keys.get(i), valor);
        }

        //Obteniendo las llaves del hmap
        List keysAUX = new ArrayList(hmapNVBDouble.keySet());
        //Ordenando variables básicas
        Collections.sort(keysAUX);//Aqui ya esta ordenadas
        for (int i = 0; i < hmapNVBDouble.size(); i++) {
            if (hmapNVBDouble.get((int) keysAUX.get(i)) != 0.0) {
                hmapNVBDouble.remove((int) keys.get(i));
            }
        }

        keysAUX.removeAll(keysAUX);
        keysAUX = new ArrayList(hmapNVBDouble.keySet());
        Collections.sort(keysAUX);//Aqui ya esta ordenadas
        for (int i = 0; i < hmapNVBDouble.size(); i++) {
            if (hmapNVBDouble.get((int) keysAUX.get(i)) == 0.0) {
                solMultiple.setEnabled(true);
                solMultiple.setVisible(true);
                break;
            } else {
                //Si no es cero, entonces no se muestra el boton, y se cierra este bucle
                System.out.println("NO EXISTEN SOLUCIONES MÚLTIPLES");
                solMultiple.setEnabled(false);
                solMultiple.setVisible(false);
                if (solIlim) {
                    solMultiple.setEnabled(true);
                    solMultiple.setVisible(true);
                }
                break;
            }
        }
        return hmapNVBDouble;
    }

    public void solucionesMultiples(double[][] igualdades, int MAXMIN, boolean tecnicaM) throws IOException {
        HashMap<Integer, Double> hmapNVBDouble = new HashMap<>();
        HashMap<Integer, Double> hmapNVBDoubleAUX = new HashMap<>();
        hmapNVBDouble = determinarVarNOBasicas(igualdades, tecnicaM);

        List keysAUX = new ArrayList(hmapNVBDouble.keySet());
        //Ordenando variables básicas
        Collections.sort(keysAUX);//Aqui ya esta ordenadas
        //Eliminando elementos que pueden alterar la función objetivo
        for (int i = 0; i < hmapNVBDouble.size(); i++) {
            if (hmapNVBDouble.get((int) keysAUX.get(i)) == 0.0) {
                hmapNVBDoubleAUX.put((int) keysAUX.get(i), 0.0);
            }
        }

        List keys = new ArrayList(hmapNVBDoubleAUX.keySet());
        //Ordenando variables básicas
        Collections.sort(keys);//Aqui ya esta ordenadas
        String opciones = "";
        for (int i = 0; i < keys.size(); i++) {
            if (i != keys.size() - 1) {
                opciones += "X" + keys.get(i).toString() + ", ";
            } else {
                opciones += "X" + keys.get(i).toString();
            }

        }
        String eleccion = "";
        boolean error = true;
        do {
            eleccion = JOptionPane.showInputDialog(this, "¿Qué variable desea sacar?\n" + opciones + "\nEscriba el número entero");
            try {
                Double.parseDouble(eleccion);
                if (hmapNVBDoubleAUX.containsKey(Integer.parseInt(eleccion))) {
                    error = false;
                    break;
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, elija entre las variables disponibles");
                }

            } catch (Exception e) {
                if (!(eleccion == null)) {
                    JOptionPane.showMessageDialog(this, "Por favor, solo ingrese números");
                } else {
                    error = true;
                    break;
                }
            }

        } while (true);
        //System.out.println("Ha elegido usar X" + Integer.parseInt(eleccion));
        if (!error) {//No se hace ningun procedimiento
            System.out.println("Ha elegido usar X" + Integer.parseInt(eleccion));
            procedimiento += "\n\nHa elegido usar X" + Integer.parseInt(eleccion);
            actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
            filaPivote = (Integer.parseInt(eleccion) - 1);
            columnaPivote = varSalida(igualdades, tecnicaM);

            System.out.println("Columna pivote" + columnaPivote);
            System.out.println("Fila pivote" + filaPivote);
            matriz = ConvertirVariableEnBase(igualdades, columnaPivote, filaPivote);

            try {
                System.out.println("\nObteniendo una nueva solución...");
                procedimiento += "\nObteniendo una nueva solución...\n";
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz
                mostrarMatriz(matriz);
                actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

            } catch (IOException ex) {
                System.out.println("Ocurrio un error en el paso 2, mostrar Matriz:\n" + ex);
                Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {//Mostrar la tabla simplex en la interfaz grafica
                TablaSimplexIteracion(matriz, tecnicaM);
            } catch (IOException ex) {
                Logger.getLogger(InterfazYLogica.class.getName()).log(Level.SEVERE, null, ex);
            }
            comprobarZ(matriz, fObjetivo, tecnicaM);
            actualizarProcedimiento(procedimiento);//Esta instrucción actualiza el procedimiento en la interfaz

        }
    }

    //CÓDIGO PARA OBTENER TODOS LOS VALORES MEDIANTE LA INTERFAZ GRAFICA
    public double[][] antePaso() {
        //Trayendo combobox MAXMIN
        String opcionMAXMIN = this.cbxMAXMIN.getModel().getSelectedItem().toString();
        //System.out.println(opcionMAXMIN);
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
        //System.out.println("La función objetivo que obtuve es:");
        //System.out.println(Arrays.toString(fObjetivo));

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
            //System.out.println(this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()));

            if (this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()).toString().equals("<=")) {
                condicionAUX[x] = 0;
            } else if (this.jTableRestricciones.getValueAt(x, (int) spinnerVD.getModel().getValue()).toString().equals("=")) {
                condicionAUX[x] = 1;
            } else {
                condicionAUX[x] = 2;
            }
        }

        //System.out.println("Las condiciones son: ");
        //System.out.println(Arrays.toString(condicionAUX));
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

    public void calcularNuevalSolI() throws IOException {
        System.out.println("calculando una nueva solución \na petición del usuario");
        procedimiento += "\n\ncalculando una nueva solución \na petición del usuario\n\n";
        int aux;
        do {
            aux = (int) (Math.random() * n) + 1;
            if (aux > teta) {
                teta = aux;
                break;
            } else if (aux == n) {
                System.out.println("\nEl limite albitrario preestablecido de tetha es 10,000");
                System.out.println("Por lo tanto, no puedo calcular más soluciones.");
                procedimiento += "\nEl limite albitrario preestablecido de tetha es 10,000\n"
                        + "Por lo tanto, no puedo calcular más soluciones.\n";
                actualizarProcedimiento(procedimiento);
                solMultiple.setVisible(false);
                solMultiple.setEnabled(false);
                break;
            }
        } while (true);
        actualizarProcedimiento(procedimiento);
        comprobarZ(matriz, fObjetivo, tecnicaM);
        actualizarProcedimiento(procedimiento);
    }

    ///////////////////////////////////////////////////////////////
    //Codigo para actualizar el JTextArea con el procedimiento
    public void actualizarProcedimiento(String procedimiento) {
        this.txtProcedimiento.setText(procedimiento);
        int len = this.txtProcedimiento.getDocument().getLength();
        this.txtProcedimiento.setCaretPosition(len);
    }

    //Codigo para llenar la tabla simplex
    public void TablaSimplexIteracion(double[][] matrix, boolean tecnicaM) throws IOException {

        LlenarTabla llenar = new LlenarTabla();

        //Obtener las variables básicas
        double[][] VB = determinarVarEnBase(matrix, false);
        ArrayList<Integer> VBFila = new ArrayList<>();
        System.out.println(Arrays.deepToString(VB));
        for (int i = 0, x = 0, y = 0; i < VB.length; i++) {
            for (int j = 0; j < VB[i].length; j++) {
                if (j != 0) {
                    y = (int) VB[i][j];
                    VBFila.add((y + 1));
                }
            }
        }

        //Crear filas de la tabla simplex
        int columnas, filas;
        if (tecnicaM) {
            columnas = matrix.length;
            filas = matrix[0].length + 1;
        } else {
            columnas = matrix.length - 1;
            filas = matrix[0].length + 1;
        }
        Object[][] datosFila = new Object[columnas][filas];

        for (int i = 0; i < VBFila.size(); i++) {
            for (int j = 0, k = 0; j < matrix[i].length + 1; j++) {
                if (j == 0) {
                    datosFila[i][j] = "X" + VBFila.get(i);
                } else {
                    datosFila[i][j] = fraccion.fraction(matrix[(i)][k]);
                    k++;
                }
            }
        }

        // System.out.println("El array de objetos...");
        // System.out.println(Arrays.deepToString(datosFila));
        //VectorMZ se encarga de unir la función objetivo, en caso tenga M y Z, sino solo guarda Z
        String[] vectorMZ = new String[matrix[0].length];
        datosFila[(datosFila.length - 1)][0] = "Z";

        for (int x = 0; x < vectorMZ.length; x++) {
            if (!tecnicaM) {
                if (Math.round(matrix[(matrix.length - 2)][x]) != 0) {
                    if (Math.round(matrix[(matrix.length - 2)][x]) % 1 == 0) {
                        if ((Math.round(matrix[(matrix.length - 2)][x]) == 1.0)) {
                            //Aqui solo si es 1.0
                            vectorMZ[x] = "M";
                        } else if (((Math.round(matrix[(matrix.length - 2)][x]) == -1.0))) {
                            //Aqui solo si es -1.0
                            vectorMZ[x] = "-M";
                        } else {
                            //En este caso puede ser o no entero, para lo cual el siguiente codigo
                            if (Math.round(matrix[(matrix.length - 2)][x]) % 1 == 0) {
                                try {
                                    if (Double.parseDouble(fraccion.fraction(matrix[(matrix.length - 2)][x])) % 1 == 0) {
                                        vectorMZ[x] = fraccion.fraction(matrix[(matrix.length - 2)][x]) + "M";
                                    }
                                } catch (Exception e) {
                                    vectorMZ[x] = "(" + fraccion.fraction(matrix[(matrix.length - 2)][x]) + ")M";
                                }

                            } else {
                                vectorMZ[x] = "(" + fraccion.fraction(matrix[(matrix.length - 2)][x]) + ")M";
                            }
                            //vectorMZ[x] = "(" + fraccion.fraction(matrix[(matrix.length - 2)][x]) + ")M";
                        }
                    } else {
                        //Aqui definitivamente no es un numero entero
                        vectorMZ[x] = "(" + fraccion.fraction(matrix[(matrix.length - 2)][x]) + ")M";
                    }

                    //AQUI HEMOS TERMINADO DE AÑADIR LA M, AHORA EL TERMINO INDEPENDIENTE
                    if (matrix[(matrix.length - 1)][x] > 0) {
                        vectorMZ[x] += " + " + fraccion.fraction(matrix[(matrix.length - 1)][x]);
                    } else if (matrix[(matrix.length - 1)][x] < 0) {
                        vectorMZ[x] += " " + fraccion.fraction(matrix[(matrix.length - 1)][x]);
                    }
                } else {
                    vectorMZ[x] = fraccion.fraction(matrix[(matrix.length - 1)][x]);
                }

            } else {

                //Si todas las restricciones son <=
                vectorMZ[x] = fraccion.fraction(matrix[(matrix.length - 1)][x]);
            }
        }

        System.out.println("Impriimiendo vectorMZ");
        System.out.println(Arrays.toString(vectorMZ));

        for (int j = 0, k = 0; j < vectorMZ.length + 1; j++) {
            if (j == 0) {
                datosFila[(datosFila.length - 1)][j] = "Z";
            } else {
                datosFila[(datosFila.length - 1)][j] = vectorMZ[k];
                k++;
            }
        }

        System.out.println("El array de objetos...");
        System.out.println(Arrays.deepToString(datosFila));

        Object[][] datosReturn = new Object[(datosFila.length + 1)][(datosFila[0].length)];

        for (int i = 0; i < datosReturn.length; i++) {
            for (int j = 0; j < datosReturn[i].length; j++) {
                if (i == (datosReturn.length - 1)) {
                    datosReturn[i][j] = "-";

                } else {
                    datosReturn[i][j] = datosFila[i][j];
                }
            }
        }

        System.out.println(Arrays.deepToString(datosReturn));

        for (Object[] a : datosReturn) {
            llenar.Llenar(tableModel, a);

        }

        ajustarAnchoColumna(this.jTableSimplex);//se encarga de ajustar las columnas al contenido 
        mover_a_FilaJtable(this.jTableSimplex, this.jTableSimplex.getModel().getRowCount() - 1, 0);//Se encarga de desplazar el JTable al final

    }

    private String[] tablaSimplexCabeceras(int longitud) {
        String[] columnasTabla = new String[longitud + 1];

        for (int i = 0; i < columnasTabla.length; i++) {
            if (i == (longitud)) {
                columnasTabla[i] = "Bi";
                break;
            }
            if (i == 0) {
                columnasTabla[i] = "VB";
            } else {
                columnasTabla[i] = "X" + i;
            }
        }

        return columnasTabla;
    }

    private void prepararTabla(double[][] matrix) {
        String[] cabecera = tablaSimplexCabeceras(matrix[0].length);
        Object[][] datos = new Object[0][matrix[0].length];
        for (int i = 0; i < datos.length; i++) {
            for (int j = 0; j < datos[i].length; j++) {
                datos[i][j] = "";
            }
        }
        Object[] header = new Object[cabecera.length];
        for (int i = 0; i < header.length; i++) {
            header[i] = cabecera[i];

        }

        tableModel = new DefaultTableModel();
        for (Object a : header) {
            tableModel.addColumn(a);
        }
        this.jTableSimplex.setModel(tableModel);

    }

    public void ajustarAnchoColumna(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int ancho = 30; //Ancho míonimo
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                ancho = Math.max(comp.getPreferredSize().width + 1, ancho);
            }
            if (ancho > 300) {
                ancho = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(ancho);
        }

    }

    public void mover_a_FilaJtable(final JTable table, final int indiceFilaa, final int indiceColumna) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                table.scrollRectToVisible(table.getCellRect(indiceFilaa, indiceColumna, false));
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDetener;
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
    private static javax.swing.JButton solMultiple;
    public javax.swing.JSpinner spinnerRES;
    public javax.swing.JSpinner spinnerVD;
    public static javax.swing.JTextArea txtProcedimiento;
    // End of variables declaration//GEN-END:variables

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icono.jpeg")));
    }
}
