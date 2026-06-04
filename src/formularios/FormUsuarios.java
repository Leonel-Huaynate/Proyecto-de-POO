
package formularios;

import modelo.Usuario;
import dao.UsuarioDAO;
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
    // Panel de datos — fondo azul oscuro
    jPanel1.setBackground(new java.awt.Color(30, 30, 60));

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

    // Labels en blanco sobre fondo oscuro
    for (java.awt.Component c : jPanel1.getComponents()) {
        if (c instanceof javax.swing.JLabel) {
            c.setForeground(java.awt.Color.WHITE);
        }
    }
}
    
    private void configurarTabla(){
        modelo = new DefaultTableModel(
            new String[]{"ID", "Usuario", "Nombres",
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
    }
    
    //Metodo para cargar datos a los txts de la tabla
    private void cargarDatosTabla(){
        int fila=tblUsuarios.getSelectedRow();
        if(fila==-1) return;
        
        idSeleccionado=(int)modelo.getValueAt(fila, 0);
        txtUsuario.setText((String) modelo.getValueAt(fila, 1));
        txtContraseña.setText("");
        txtNombres.setText((String) modelo.getValueAt(fila, 2));
        txtApellidos.setText((String) modelo.getValueAt(fila, 3));
        cmbRol.setSelectedItem(modelo.getValueAt(fila, 4));
        chkActivo.setSelected(
        modelo.getValueAt(fila, 5).equals("Activo"));
    }
        
    //Metodo para limpiar los campós
    private void limpiarCampos() {
        idSeleccionado = -1;
        txtNombres.setText("");
        txtApellidos.setText("");
        txtUsuario.setText("");
        txtContraseña.setText("");
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
        String rol=cmbRol.getSelectedItem().toString();
        boolean activo    = chkActivo.isSelected(); 
        
        if(!Validaciones.camposLlenos(nombres,apellidos,usuario)){
           Mensajes.advertencia("Nombres, apellidos y usuario son obligatorios.");
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
                Usuario nuevo = new Usuario(usuario, contraseña,
                                            rol, nombres, apellidos);
                UsuarioDAO.insertarUsuario(nuevo);
                Mensajes.exito("Usuario creado correctamente.");
            }
            else{
                //Significa que se va a Editar un Usuario ya exsistente
                Usuario usuarioEditar=new Usuario(usuario, contraseña, rol, nombres, apellidos);
                
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
            // Busca por nombre, apellido o usuario
                if (u.getNombres().toLowerCase().contains(buscar.toLowerCase())
                || u.getApellidos().toLowerCase().contains(buscar.toLowerCase())
                || u.getUsuario().toLowerCase().contains(buscar.toLowerCase())) {

                    modelo.addRow(new Object[]{
                    u.getId(),
                    u.getUsuario(),
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

        jLabel1.setText("Nombres :");

        jLabel2.setText("Apellidos :");

        jLabel3.setText("Usuario :");

        btnNuevo.setText("NUEVO");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnGuardar.setText("GUARDAR");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jLabel4.setText("Contraseña :");

        jLabel5.setText("Rol :");

        cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "CAJERO" }));

        chkActivo.setText("Activo");

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        txtBuscar.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscar Usuario :"));

        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                                    .addComponent(btnNuevo)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnGuardar)
                                    .addGap(23, 23, 23)
                                    .addComponent(btnEliminar))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(42, 42, 42)
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(59, 59, 59)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addGap(18, 18, 18)
                                            .addComponent(cmbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(25, 25, 25)
                                            .addComponent(chkActivo))))))
                        .addGap(49, 49, 49))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(txtNombres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbRol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(txtApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtContraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkActivo))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscar))
                        .addGap(21, 21, 21))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevo)
                            .addComponent(btnGuardar)
                            .addComponent(btnEliminar))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

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
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(scrollTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtApellidos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JPasswordField txtContraseña;
    private javax.swing.JTextField txtNombres;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
