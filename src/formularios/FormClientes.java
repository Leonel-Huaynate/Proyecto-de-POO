
package formularios;

import modelo.Cliente;
import dao.ClienteDAO;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import utilidades.*;
import javax.swing.table.DefaultTableModel;

public class FormClientes extends javax.swing.JInternalFrame {
    
    private DefaultTableModel modelo;
    private int idSeleccionado=-1;


    public FormClientes() {
        initComponents();
        configurarTabla();
        cargarClientes();
        configurarEventos();
        aplicarEstilo();
    }
    
    //Metodo para modificar la tabla
    private void configurarTabla(){
        modelo = new DefaultTableModel(
            new String[]{"ID", "DNI", "Nombres", "Apellidos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo lectura
            }
        };
        tblClientes.setModel(modelo);
        tblClientes.setModel(modelo);
        tblClientes.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblClientes.setRowHeight(25);

        // Ocultar columna ID
        tblClientes.getColumnModel().getColumn(0).setMinWidth(0);
        tblClientes.getColumnModel().getColumn(0).setMaxWidth(0);

        // Anchos
        tblClientes.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblClientes.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblClientes.getColumnModel().getColumn(3).setPreferredWidth(180);
    }
    
    //Metodo para cargar todos los Clientes en la Tabla
    private void cargarClientes(){
        modelo.setRowCount(0);
        try{
            for(Cliente c : ClienteDAO.listarTodo()){
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getDni(),
                    c.getNombres(),
                    c.getApellidos()
                });
            }
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Eventos 
    private void configurarEventos() {

        // Clic en tabla → carga datos en campos
        tblClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosCampos();
            }
        });

        // Enter en txtBuscar → busca
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarCliente();
                }
            }
        });
    }
    
    //Metodo para cargar datos a los txts de la tabla
    private void cargarDatosCampos(){
        int fila=tblClientes.getSelectedRow();
        if(fila==-1) return;
        
        idSeleccionado=(int)modelo.getValueAt(fila, 0);
        txtDni.setText((String) modelo.getValueAt(fila, 1));
        txtNombres.setText((String)  modelo.getValueAt(fila, 2));
        txtApellidos.setText((String) modelo.getValueAt(fila, 3));
    }
    
    //Metodo para Limpiar campos 
    private void limpiarCampos() {
        idSeleccionado = -1;
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDni.setText("");
        txtBuscar.setText("");
        cmbFiltro.setSelectedIndex(0);
        tblClientes.clearSelection();
        cargarClientes();
    }
    
    //Metodo para guardar Cliente nuevo o editar
    private void guardarCliente(){
        String nombres=txtNombres.getText().trim();
        String apellidos=txtApellidos.getText().trim();
        String dni=txtDni.getText().trim();
        
        if(!Validaciones.camposLlenos(nombres,apellidos,dni)){
            Mensajes.advertencia("Todos los campos son obligatorios");
            return;
        }
        
        if(!Validaciones.dniValido(dni)){
            Mensajes.advertencia("El DNI es invalido ingrese de nuevo");
            return;
        }
        
        try{
            if(idSeleccionado==-1){
                //Nuevo Cliente
                if(ClienteDAO.existeDNI(dni)){
                    Mensajes.advertencia("El DNI ya esta registrado");
                    return;
                }
                Cliente nuevoCliente = new Cliente(nombres,apellidos,dni);
                ClienteDAO.insertarCliente(nuevoCliente);
                Mensajes.exito("Cliente registrado correctamente.");
            }else{
                //Cliente ya exsiste (EDITAR)
                if(ClienteDAO.existeDniEditar(dni, idSeleccionado)){
                    Mensajes.advertencia("Ese DNI ya le pertenece a otro cliente");
                    return;
                }
                Cliente editarCliente = new Cliente(nombres,apellidos,dni);
                editarCliente.setId(idSeleccionado);
                ClienteDAO.actualizarCliente(editarCliente);
                Mensajes.exito("Cliente actualizado correctamente.");
            }
            limpiarCampos();
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para buscar Cliente por filtro
    private void buscarCliente(){
        String texto=txtBuscar.getText().trim();
        String filtro=cmbFiltro.getSelectedItem().toString();
        
        if(texto.isEmpty()){
            cargarClientes();
            return;
        }
        
        modelo.setRowCount(0);
        try{
            List<Cliente> lista = ClienteDAO.buscarCliente(filtro, texto);
            for(Cliente c : lista){
                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getDni(),
                    c.getNombres(),
                    c.getApellidos()});
            }
            if (modelo.getRowCount() == 0){
                Mensajes.advertencia("No se encontró ningún cliente.");
            }
            
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para eliminar Cliente
    private void eliminarCliente(){
        if (idSeleccionado == -1){
            Mensajes.advertencia("Selecciona un cliente de la tabla.");
            return;
        }
        if(Mensajes.confirmar("¿Esta seguro eliminar este Cliente permanentemente"
                + "Esta accion no se puede deshacer")){
            try {
                ClienteDAO.eliminarCliente(idSeleccionado);
                Mensajes.exito("Cliente eliminado correctamente.");
                limpiarCampos();
            } catch (ErrorBD e) {
                Mensajes.error("No se puede eliminar este cliente.\n"
                             + "Puede tener ventas asociadas.\n"
                             + e.getMessage());
            }
        }
    }
    // Estilo visual 
    private void aplicarEstilo() {
        jPanel2.setBackground(new java.awt.Color(245, 245, 250));

        btnNuevo.setBackground(new java.awt.Color(40, 167, 69));
        btnNuevo.setFocusPainted(false);

        btnGuardar.setBackground(new java.awt.Color(0, 123, 255));
        btnGuardar.setFocusPainted(false);

        btnBuscar.setBackground(new java.awt.Color(255, 193, 7));
        btnBuscar.setFocusPainted(false);

        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setFocusPainted(false);

        tblClientes.setRowHeight(25);
        tblClientes.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));
        tblClientes.setSelectionBackground(
            new java.awt.Color(0, 123, 255));

    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        txtApellidos = new javax.swing.JTextField();
        txtDni = new javax.swing.JTextField();
        txtBuscar = new javax.swing.JTextField();
        cmbFiltro = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Gestion de Clientes");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Cliente:"));

        jLabel1.setText("Nombres:");

        jLabel2.setText("Apellidos:");

        jLabel3.setText("DNI:");

        jLabel4.setText("Buscar por:");

        jLabel5.setText("Criterio:");

        cmbFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DNI", "NOMBRE", "APELLIDO" }));

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnNuevo.setText("NUEVO");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApellidos)
                            .addComponent(txtDni)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnBuscar)
                        .addGap(177, 177, 177)
                        .addComponent(btnEliminar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(117, 117, 117)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(130, 130, 130))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(btnNuevo)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addComponent(btnGuardar))))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(btnBuscar)
                    .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de Clientes:"));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblClientes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarCliente();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarCliente();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarCliente();
    }//GEN-LAST:event_btnBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cmbFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombres;
    // End of variables declaration//GEN-END:variables
}
