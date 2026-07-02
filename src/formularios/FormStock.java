
package formularios;

import dao.ProductoDAO;
import modelo.ProductoBase;
import utilidades.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List; 

public class FormStock extends javax.swing.JInternalFrame {
    
    private DefaultTableModel modeloStock;


    public FormStock() {
        initComponents();
        configurarTabla();
        configurarEventos();
        aplicarEstilo();
        cargarTodos();
    }
    
    //Metodo para configurar la tabla
    private void configurarTabla(){
        modeloStock = new DefaultTableModel(new String[]{"Código", "Nombre", "Categoría",
                         "Precio", "Stock", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblStock.setModel(modeloStock);
        tblStock.setRowHeight(25);
        tblStock.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(90);
    }
    
    //Eventos
    private void configurarEventos() {
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarProducto();
                }
            }
        });
    }
    
    //Metodo para cargar todos los productos activos
    private void cargarTodos(){
        modeloStock.setRowCount(0);
        try{
            for(ProductoBase p : ProductoDAO.listarPrdActivos()){
                if(!p.isActivo()) continue; //Pasa a la siguiente iteracion proque solo lista los activos
                agregarFila(p);
            }
        
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para buscar producto por nombre, codigo o tipo
    private void buscarProducto(){
        String buscar = txtBuscar.getText().trim();
        String filtro = cmbFiltro.getSelectedItem().toString();
        if (buscar.isEmpty()) {
            cargarTodos();
            return;
        }
        modeloStock.setRowCount(0);
        try{
            List<ProductoBase> lista = ProductoDAO.buscarProducto(filtro, buscar);
            for(ProductoBase p : lista){
                if (!p.isActivo()) continue;
                agregarFila(p);
            }
            if (modeloStock.getRowCount() == 0) {
                Mensajes.advertencia("No se encontró ningún producto.");
            }
        
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para agregar una fila con el estado del Stock
    private void agregarFila(ProductoBase p){
        String estado;
        if(p.stockAgotado()){
            estado = "AGOTADO";
        }else if(p.tieneStockBajo()){
            estado = "STOCK BAJO";
        }else{
            estado = "OK";
        }
        
        modeloStock.addRow(new Object[]{
            p.getCodigo(),
            p.getNombre(),
            p.getCategoria(),
            p.getPrecioFormateado(),
            p.getStock(),
            estado
        });
    }
    
    // Estilo visual con colores según estado
    private void aplicarEstilo() {
        btnBuscar.setBackground(new java.awt.Color(255, 193, 7));
        btnBuscar.setFocusPainted(false);

        btnVerTodos.setBackground(new java.awt.Color(23, 162, 184));
        btnVerTodos.setFocusPainted(false);

        tblStock.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));

        // Colorea las filas según el estado (columna 5)
        tblStock.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(
                        javax.swing.JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {

                    java.awt.Component c = super
                        .getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);

                    String estado = table.getModel()
                        .getValueAt(row, 5).toString();

                    if (!isSelected) {
                        switch (estado) {
                            case "AGOTADO":
                                c.setBackground(
                                    new java.awt.Color(255, 200, 200));
                                break;
                            case "STOCK BAJO":
                                c.setBackground(
                                    new java.awt.Color(255, 245, 200));
                                break;
                            default:
                                c.setBackground(java.awt.Color.WHITE);
                                break;
                        }
                    }
                    return c;
                }
            });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnVerTodos = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStock = new javax.swing.JTable();
        cmbFiltro = new javax.swing.JComboBox<>();

        setClosable(true);
        setIconifiable(true);
        setTitle("Consultar Stock");

        jLabel1.setText("Buscar producto Por:");

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnVerTodos.setText("VER TODOS");
        btnVerTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTodosActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de Productos:"));

        tblStock.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblStock);

        cmbFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NOMBRE", "CODIGO", "TIPO" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(btnBuscar)
                        .addGap(57, 57, 57)
                        .addComponent(btnVerTodos))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnVerTodos)
                    .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarProducto();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnVerTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodosActionPerformed
        cargarTodos();
    }//GEN-LAST:event_btnVerTodosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnVerTodos;
    private javax.swing.JComboBox<String> cmbFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStock;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
