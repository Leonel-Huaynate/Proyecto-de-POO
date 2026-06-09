
package formularios;

import modelo.Usuario;
import dao.UsuarioDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import utilidades.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FormUsuarios extends javax.swing.JInternalFrame {

    private DefaultTableModel modelo;
    private int idSeleccionado=-1;
    public FormUsuarios() {
        initComponents();
        configurarTabla();
        cargarUsuarios();
        configurarEventos();
        setSize(700, 550);
        aplicarEstilo();
    }
    private void aplicarEstilo() {

    // Panel de tabla — fondo gris claro
    jPanel2.setBackground(new java.awt.Color(245, 245, 250));

    // Colores de los botones
    btnNuevo.setBackground(new java.awt.Color(40, 167, 69));    // verde
    btnNuevo.setFocusPainted(false);

    btnGuardar.setBackground(new java.awt.Color(0, 123, 255));  // azul
    btnGuardar.setFocusPainted(false);

    btnBuscar.setBackground(new java.awt.Color(220, 53, 69)); // rojo
    btnBuscar.setFocusPainted(false);

    btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
    btnEliminar.setFocusPainted(false);

    // Color de la tabla
    tblUsuarios.setRowHeight(25);
    tblUsuarios.getTableHeader().setBackground(
        new java.awt.Color(30, 30, 60));
    tblUsuarios.setSelectionBackground(
        new java.awt.Color(0, 123, 255));
    tblUsuarios.setSelectionForeground(java.awt.Color.WHITE);

}
    
    private void configurarTabla(){
        modelo = new DefaultTableModel(
            new String[]{"ID", "Usuario", "DNI", "Nombres",
                         "Apellidos", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo lectura
            }
        };
        tblUsuarios.setModel(modelo);
        tblUsuarios.setSelectionMode(
        javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Ocultar columna ID — se usa internamente
        tblUsuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tblUsuarios.getColumnModel().getColumn(0).setMaxWidth(0);

        // Ancho de columnas
        tblUsuarios.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblUsuarios.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblUsuarios.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblUsuarios.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblUsuarios.getColumnModel().getColumn(5).setPreferredWidth(70);
    }
    
    //Metodo para cargar los Usarios a la tabla
    private void cargarUsuarios(){
        modelo.setRowCount(0);
        try{
            for(Usuario u:UsuarioDAO.listarTodos()){
                modelo.addRow(new Object[]{
                    u.getId(),
                    u.getUsuario(),
                    u.getDni(),
                    u.getNombres(),
                    u.getApellidos(),
                    u.getRol(),
                    u.isActivo() ? "Activo" : "Inactivo"
                });
            }
        }catch(ErrorBD e){
        
            Mensajes.error(e.getMessage());
        }
    }
    
    //Eventos
    private void configurarEventos() {
        tblUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarDatosTabla();
            }
        });
        
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                buscarUsuario();
            }
        }
        });
        
        cmbRol.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actualizarObligatoriedad();
        }
    });
    }
    
    private void actualizarObligatoriedad() {
    String rol = cmbRol.getSelectedItem().toString();
    if (rol.equals("CAJERO")) {
        // DNI obligatorio — fondo amarillo
        txtDni.setBackground(new java.awt.Color(255, 255, 200));
        lblDni.setText("DNI: *");
        lblDni.setForeground(new java.awt.Color(180, 0, 0));
    } else {
        // DNI opcional — fondo blanco
        txtDni.setBackground(java.awt.Color.WHITE);
        lblDni.setText("DNI:");
        lblDni.setForeground(java.awt.Color.BLACK);
    }
}
    
    //Metodo para cargar datos a los txts de la tabla
    private void cargarDatosTabla(){
        int fila=tblUsuarios.getSelectedRow();
        if(fila==-1) return;
        
        idSeleccionado=(int)modelo.getValueAt(fila, 0);
        txtUsuario.setText((String) modelo.getValueAt(fila, 1));
        txtContraseña.setText("");
        //El DNI puede ser null 
        Object dni = modelo.getValueAt(fila, 2);
        txtDni.setText(dni != null ? dni.toString() : "");
        txtNombres.setText((String) modelo.getValueAt(fila, 3));
        txtApellidos.setText((String) modelo.getValueAt(fila, 4));
        cmbRol.setSelectedItem(modelo.getValueAt(fila, 5));
        chkActivo.setSelected(
        modelo.getValueAt(fila, 6).equals("Activo"));
    }
        
    //Metodo para limpiar los campós
    private void limpiarCampos() {
        idSeleccionado = -1;
        txtNombres.setText("");
        txtApellidos.setText("");
        txtUsuario.setText("");
        txtContraseña.setText("");
        txtDni.setText("");
        cmbRol.setSelectedIndex(0);
        txtBuscar.setText("");
        chkActivo.setSelected(true);
        tblUsuarios.clearSelection();
    }
    
    private void guardarUsuario(){
        String nombres=txtNombres.getText().trim();
        String apellidos=txtApellidos.getText().trim();
        String usuario=txtUsuario.getText().trim();
        String contraseña=new String(txtContraseña.getPassword()).trim();
        String dni=txtDni.getText().trim();
        String rol=cmbRol.getSelectedItem().toString();
        boolean activo    = chkActivo.isSelected(); 
        
        if(!Validaciones.camposLlenos(nombres,apellidos,usuario)){
           Mensajes.advertencia("Nombres, apellidos y usuario son obligatorios.");
           return;
        }
        
        if(rol.equals("CAJERO")){
            if(dni.isEmpty()){Mensajes.advertencia("El DNI es obligatorio para el Cajero.");
            return;
            }
            if(!Validaciones.dniValido(dni)){Mensajes.advertencia("El DNI ingresado es invalido.");
            return;
            }
        }
        if(!dni.isEmpty()&& !Validaciones.dniValido(dni)){
            Mensajes.advertencia("El DNI ingresado es invalido.");
            return;
        }
            
        
        try{
            if(idSeleccionado==-1){
                
                //Significa un nuevo Usuario
                if(contraseña.isEmpty()){
                    Mensajes.advertencia("La contraseña es obligatoria.");
                    return;
                }
                if(UsuarioDAO.existeUsuario(usuario)){
                    Mensajes.advertencia("Ese nombre de usuario ya existe.");
                    return;
                }
                if(!dni.isEmpty()&& UsuarioDAO.existeDni(dni)){
                    Mensajes.advertencia("Ese DNI ya esta registrado.");
                    return;
                }
                Usuario nuevo = new Usuario(usuario, contraseña,
                                            rol, nombres, apellidos,dni.isEmpty()? null:dni);
                UsuarioDAO.insertarUsuario(nuevo);
                Mensajes.exito("Usuario creado correctamente.");
            }
            else{
                //Significa que se va a Editar un Usuario ya exsistente
                Usuario usuarioEditar=new Usuario(usuario, contraseña, rol, nombres, apellidos,
                        dni.isEmpty()? null:dni);
                
                usuarioEditar.setId(idSeleccionado);
                UsuarioDAO.actualizarDatos(usuarioEditar);

                if (activo) {
                    UsuarioDAO.activarUsuario(idSeleccionado);
                } else {
                    UsuarioDAO.desactivarUsuario(idSeleccionado);
                }

                Mensajes.exito("Usuario actualizado correctamente.");
        }
            limpiarCampos();
            cargarUsuarios();
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    private void buscarUsuario() {
    String buscar = txtBuscar.getText().trim();

        if (buscar.isEmpty()) {
            cargarUsuarios(); // si está vacío muestra todos
            return;
        }

        modelo.setRowCount(0);
        try {
            for (Usuario u : UsuarioDAO.listarTodos()) {
            // Busca por usuario o DNI
                boolean bandera=u.getUsuario().toLowerCase().contains(buscar.toLowerCase())
                || (u.getDni()!= null && u.getDni().contains(buscar));
                
                    if(bandera){
                        modelo.addRow(new Object[]{
                        u.getId(),
                        u.getUsuario(),
                        u.getDni() != null ? u.getDni() : "", 
                        u.getNombres(),
                        u.getApellidos(),
                        u.getRol(),
                        u.isActivo() ? "Activo" : "Inactivo"
                        });
                    }
                }

        if (modelo.getRowCount() == 0) {
            Mensajes.advertencia("No se encontró ningún usuario.");
        }
        } catch (ErrorBD e) {
                Mensajes.error(e.getMessage());
        }
    }
    //Metodo para eliminar Usuario si no esta asociado a ninguna venta
    private void eliminarUsuario(){
        if(idSeleccionado==-1){
            Mensajes.advertencia("Seleccione un usuario de la tabla");
            return;
        }
        if(Mensajes.confirmar("¿Desea eliminar este usuario permanentemente?\n"
                             +"Esta accion no se podra deshacer." )){
            try{
                UsuarioDAO.eliminarUsuario(idSeleccionado);
                Mensajes.exito("El usuario se elimino correctamente.");
                limpiarCampos();
                cargarUsuarios();
            
            }catch(ErrorBD e){
                Mensajes.error("No se puede eliminar este usuario.\n"
                         + "Puede tener ventas asociadas.\n"
                         + e.getMessage());
            }
        }
    }
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombres = new javax.swing.JTextField();
        txtApellidos = new javax.swing.JTextField();
        txtUsuario = new javax.swing.JTextField();
        cmbRol = new javax.swing.JComboBox<>();
        chkActivo = new javax.swing.JCheckBox();
        txtContraseña = new javax.swing.JPasswordField();
        btnBuscar = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        lblBuscar = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lblDni = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        scrollTabla = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();

        setBorder(null);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestionar Usuarios");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Usuario"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Nombres :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel2.setText("Apellidos :");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        jLabel3.setText("Usuario :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, -1, -1));

        btnNuevo.setText("NUEVO");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        jPanel1.add(btnNuevo, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 186, -1, -1));

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 186, -1, -1));

        jLabel4.setText("Contraseña :");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, -1, -1));

        jLabel5.setText("Rol :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(533, 57, -1, -1));
        jPanel1.add(txtNombres, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 142, -1));
        jPanel1.add(txtApellidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 134, -1));
        jPanel1.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 129, -1));

        cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "CAJERO" }));
        jPanel1.add(cmbRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(574, 54, -1, -1));

        chkActivo.setText("Activo");
        jPanel1.add(chkActivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 115, -1, -1));
        jPanel1.add(txtContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, 130, -1));

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });
        jPanel1.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, -1, -1));

        txtBuscar.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, 190, 23));

        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jPanel1.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(575, 186, -1, -1));

        lblBuscar.setText("Buscar por Usuario o DNI:");
        jPanel1.add(lblBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, -1, -1));
        jPanel1.add(txtDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 130, 130, -1));

        lblDni.setText("DNI:");
        jPanel1.add(lblDni, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 130, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de Usuarios"));

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollTabla.setViewportView(tblUsuarios);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 739, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarUsuario();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarUsuario();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarUsuario();
    }//GEN-LAST:event_btnEliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JComboBox<String> cmbRol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBuscar;
    private javax.swing.JLabel lblDni;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JPasswordField txtContraseña;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
