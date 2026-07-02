
package formularios;

import dao.ClienteDAO;
import dao.ProductoDAO;
import dao.VentaDAO;
import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.ProductoBase;
import modelo.Usuario;
import modelo.Venta;
import utilidades.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class FormVentas extends javax.swing.JInternalFrame {

    private DefaultTableModel modeloProducto;
    private DefaultTableModel modeloCarrito;
   
    //Venta actual en proceso
    private Venta ventaActual;
    
    //Usuario logeado viene del FormPrincipal1
    private Usuario usuarioActual;
    
    public FormVentas(modelo.Usuario u) {
        this.usuarioActual=u;
        initComponents();
        configurarTablas();
        configurarEventos();
        aplicarEstilo();
        nuevaVenta();
    }
    
    //Metodo para Configurar las Tabla
    private void configurarTablas(){
        //Tabla de busqueda de Producto
        modeloProducto = new DefaultTableModel(
                new String[]{"ID","Codigo","Nombre","Precio","Stock"},  0){
                    @Override
                    public boolean isCellEditable(int row, int column) {
                            return false;
                }
            };
        tblProducto.setModel(modeloProducto);
        tblProducto.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProducto.setRowHeight(25);
        tblProducto.getColumnModel().getColumn(0).setMinWidth(0);
        tblProducto.getColumnModel().getColumn(0).setMaxWidth(0);
        tblProducto.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblProducto.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblProducto.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblProducto.getColumnModel().getColumn(4).setPreferredWidth(60);
        
        //Tabla del Carrito
        modeloCarrito = new DefaultTableModel(
                new String[]{"ID","Nombre","Cantidad","P.Unitario","Subtotal"}, 0){
                @Override
                    public boolean isCellEditable(int row, int column) {
                            return false;
                }
            };
        tblCarrito.setModel(modeloCarrito);
        tblCarrito.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCarrito.setRowHeight(25);
        tblCarrito.getColumnModel().getColumn(0).setMinWidth(0);
        tblCarrito.getColumnModel().getColumn(0).setMaxWidth(0);
        tblCarrito.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblCarrito.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblCarrito.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblCarrito.getColumnModel().getColumn(4).setPreferredWidth(80);
    }
    
    //Inicia una nueva Venta Vacio
    private void nuevaVenta(){
        ventaActual = new Venta(
                usuarioActual.getId(),
                usuarioActual.nombreCompleto(),
                "EFECTIVO"
        );
        modeloCarrito.setRowCount(0);
        actualizarTotal();
        limpiarCamposCliente();
        limpiarCamposProducto();
    }
    
    //Eventos
    private void configurarEventos() {

        // Enter en buscar producto
        txtBuscarProducto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarProducto();
                }
            }
        });

        // Enter en DNI cliente
        txtDniCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarCliente();
                }
            }
        });

        // Cambio de tipo de pago
        cmbPago.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventaActual.setTipoPago(
                    cmbPago.getSelectedItem().toString());
            }
        });

        // Checkbox sin cliente — deshabilita campos
        chkSinCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean sinCliente = chkSinCliente.isSelected();
                txtDniCliente.setEnabled(!sinCliente);
                txtNombresCliente.setEnabled(!sinCliente);
                txtApellidosCliente.setEnabled(!sinCliente);
                btnBuscarCliente.setEnabled(!sinCliente);
                if (sinCliente) {
                    txtDniCliente.setText("");
                    txtNombresCliente.setText("");
                    txtApellidosCliente.setText("");
                    ventaActual.setCliente(null);
                }
            }
        });
    }
    
    //Metodo para buscar Cliente por DNI
    private void buscarCliente(){
        String dni=txtDniCliente.getText().trim();
        
        if(dni.isEmpty()){
            Mensajes.advertencia("Ingrese el DNI del Cliente.");
            return;
        }
        if(!Validaciones.dniValido(dni)){
            Mensajes.advertencia("Ingrese un DNI valido.");
        }
        
        try{
            Cliente c = ClienteDAO.buscarPorDNI(dni);
            if(c!=null){
                //Cliente encontrado lo muestra en los txts
                txtNombresCliente.setText(c.getNombres());
                txtApellidosCliente.setText(c.getApellidos());
                ventaActual.setCliente(c);
                Mensajes.exito("Cliente encontrado: "+c.nombreCompleto());
            }else{
                //No exsiste el CLiente 
                //Pregunta si quiere registrarlo
                if(Mensajes.confirmar("Cliente no encontrado.\n"
                                        +"¿Desea registralo?")){
                    abrirRegCliente(dni);
                }
            }
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Abre FormClientes para poder Registrar un nuevo Cliente
    private void abrirRegCliente(String dni){
        FormClientes formCliente = new FormClientes();
        //Llenamos el DNI automatico
        formCliente.setDniInicial(dni);
        // Agregar al desktop
        javax.swing.JDesktopPane desktop =
            (javax.swing.JDesktopPane) getParent();
        if (desktop != null) {
            desktop.add(formCliente);
            formCliente.setVisible(true);
            formCliente.toFront();
        }
    }
    
    //Metodo para buscar Producto disponible 
    private void buscarProducto(){
        String producto=txtBuscarProducto.getText().trim();
        modeloProducto.setRowCount(0);
        
        try{
            List<ProductoBase> lista;
            if(producto.isEmpty()){
                lista = ProductoDAO.listarPrdActivos();
            }else{
                lista = ProductoDAO.buscarProducto("NOMBRE",producto);
            }
            
            for(ProductoBase p : lista){
                modeloProducto.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getPrecioFormateado(),
                    p.getStock()
                });
            }
            if(modeloProducto.getRowCount()==0){
                Mensajes.advertencia("No se encontró ningún producto con ese nombre.");
            }
        
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para agregar producto al Carrito
    private void agregarAlCarrito(){
        int fila = tblProducto.getSelectedRow();
        
        //Si no selecciona ningun producto de la tabla
        if(fila==-1){
            Mensajes.advertencia("Seleccione un producto de la tabla.");
            return;
        }
        
        //Si selecciona un producto
        //Validamos la cantidad (esta en Strign)
        String cantidadText = txtCantidad.getText().trim();
        if(!Validaciones.mayorCero(cantidadText)){
            Mensajes.advertencia("Ingrese una cantidad valida mayor a cero.");
            return;
        }
        
        int cantidad = Integer.parseInt(cantidadText);
        int idProducto = (int) modeloProducto.getValueAt(fila, 0);
        String nombre = (String) modeloProducto.getValueAt(fila, 2);
        int stockDisp  = (int) modeloProducto.getValueAt(fila, 4);
        
        //Verificar si hay stock disponible
        if(cantidad>stockDisp){
            Mensajes.advertencia(" Stock insuficiente. Disponible: "+stockDisp);
            return;
        }
        
        //Obtener precio — quitar "S/ " del formato
        String precioText = modeloProducto.getValueAt(fila, 3)
                          .toString().replace("S/ ", "")
                          .replace(",", ".");
        double precio = Double.parseDouble(precioText);
        
        //Verificar si el producto ya está en el carrito
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            if ((int) modeloCarrito.getValueAt(i, 0) == idProducto) {
                Mensajes.advertencia(
                    "Ese producto ya está en el carrito.");
                return;
            }
        }
        
        //Crearmos Detalle Ventas y agregamos la Venta
        DetalleVenta detalle = new DetalleVenta(idProducto,nombre,cantidad,precio);
        ventaActual.agregarDetalle(detalle);
        
        //Agregamos el producto al carrito (visual)
        modeloCarrito.addRow(new Object[]{
            idProducto,
            nombre,
            cantidad,
            detalle.getPrecioFormateado(),
            detalle.getSubtotalFormateado()
        });
        
        actualizarTotal();
        txtCantidad.setText("1");
    }
    
    //Metodo para quitar Producto del Carrito
    private void quitarDelCarrito(){
        int fila = tblCarrito.getSelectedRow();
        if(fila==-1){
            Mensajes.advertencia("Seleccione un producto del Carrito.");
            return;
        }
        ventaActual.eliminarDetalle(fila);
        modeloCarrito.removeRow(fila);
        actualizarTotal();
    }
    
    //Actualizar el label del Total
    private void actualizarTotal(){
        lblTotal.setText("Total: "+ventaActual.getTotalFormateado());
    }
    
    //Metodo para confirmar la Venta
    private void confirmarVenta(){
        
        //Si el carrito esta vacio
        if(!ventaActual.tieneDetalles()){
            Mensajes.advertencia("Agrege al menos un producto al carrito para confirmar la venta.");
            return;
        }
        
        //Verificar cliente si no marcó sin cliente
        if (!chkSinCliente.isSelected()) {
            String dni = txtDniCliente.getText().trim();
            if (!dni.isEmpty() && ventaActual.getCliente() == null) {
                Mensajes.advertencia(
                    "Primero busca al cliente con "
                  + "'Buscar Cliente'.");
                return;
            }
        }
        
        //Actualizamos el tipo de Pago
        ventaActual.setTipoPago(cmbPago.getSelectedItem().toString());
        
        //Pedimos confirmacion 
        if (!Mensajes.confirmar(
                "¿Confirmar la venta?\n"
              + "Total: " + ventaActual.getTotalFormateado()
              + "\nTipo de pago: "
              + ventaActual.getTipoPago())) {
            return;
        }
        
        //Guardamos en la BD la transaccion completa
        try{
            VentaDAO.guardarVenta(ventaActual);
            Mensajes.exito("La venta se regsitro correctamente.");
            
            //Genera la boleta en PDF
            utilidades.GenerarBoleta.generar(ventaActual);
            nuevaVenta();
            
            
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para cancelar la venta
    private void cancelarVenta() {
        if (ventaActual.tieneDetalles()) {
            if (!Mensajes.confirmar(
                    "¿Desea cancelar la venta actual?\n"
                  + "Se perderán los productos del carrito.")) {
                return;
            }
        }
        nuevaVenta();
    }
    
    //Metodo para limpiar campos del cliente 
    private void limpiarCamposCliente() {
        txtDniCliente.setText("");
        txtNombresCliente.setText("");
        txtApellidosCliente.setText("");
        chkSinCliente.setSelected(false);
        txtDniCliente.setEnabled(true);
        txtNombresCliente.setEnabled(true);
        txtApellidosCliente.setEnabled(true);
        btnBuscarCliente.setEnabled(true);
    }

    //Metodo para limpiar campos de búsqueda de producto 
    private void limpiarCamposProducto() {
        txtBuscarProducto.setText("");
        txtCantidad.setText("1");
        modeloProducto.setRowCount(0);
    }

    // Estilo visual
    private void aplicarEstilo() {
        pnlProducto.setBackground(new java.awt.Color(245, 245, 250));
        pnlCarrito.setBackground(new java.awt.Color(245, 245, 250));

        btnBuscarCliente.setBackground(
            new java.awt.Color(23, 162, 184));
        btnBuscarCliente.setFocusPainted(false);

        btnBuscarProducto.setBackground(
            new java.awt.Color(255, 193, 7));
        btnBuscarProducto.setFocusPainted(false);

        btnAgregar.setBackground(new java.awt.Color(40, 167, 69));
        btnAgregar.setFocusPainted(false);

        btnQuitar.setBackground(new java.awt.Color(220, 53, 69));
        btnQuitar.setFocusPainted(false);

        btnConfirmar.setBackground(
            new java.awt.Color(0, 123, 255));
        btnConfirmar.setFocusPainted(false);

        btnCancelar.setBackground(
            new java.awt.Color(108, 117, 125));
        btnCancelar.setFocusPainted(false);

        lblTotal.setFont(new java.awt.Font("Arial",
            java.awt.Font.BOLD, 16));
        lblTotal.setForeground(new java.awt.Color(0, 100, 0));

        tblProducto.setRowHeight(25);
        tblProducto.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));
        tblProducto.setSelectionBackground(
            new java.awt.Color(0, 123, 255));

        tblCarrito.setRowHeight(25);
        tblCarrito.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));
        tblCarrito.setSelectionBackground(
            new java.awt.Color(220, 53, 69));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCliente = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDniCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombresCliente = new javax.swing.JTextField();
        txtApellidosCliente = new javax.swing.JTextField();
        chkSinCliente = new javax.swing.JCheckBox();
        pnlProducto = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtBuscarProducto = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducto = new javax.swing.JTable();
        btnAgregar = new javax.swing.JButton();
        txtCantidad = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnBuscarProducto = new javax.swing.JButton();
        pnlCarrito = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        btnQuitar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmbPago = new javax.swing.JComboBox<>();
        lblTotal = new javax.swing.JLabel();
        btnConfirmar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Realizar Venta");

        pnlCliente.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del Cliente:"));

        jLabel1.setText("DNI:");

        btnBuscarCliente.setText("BUSCAR CLIENTE");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        jLabel2.setText("Nombres:");

        jLabel3.setText("Apellidos:");

        chkSinCliente.setText("Venta sin Cliente");

        javax.swing.GroupLayout pnlClienteLayout = new javax.swing.GroupLayout(pnlCliente);
        pnlCliente.setLayout(pnlClienteLayout);
        pnlClienteLayout.setHorizontalGroup(
            pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteLayout.createSequentialGroup()
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel1)
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlClienteLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(27, 27, 27)))
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addComponent(txtDniCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btnBuscarCliente))
                    .addComponent(txtApellidosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addComponent(txtNombresCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78)
                        .addComponent(chkSinCliente)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlClienteLayout.setVerticalGroup(
            pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClienteLayout.createSequentialGroup()
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtDniCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarCliente))
                        .addGap(18, 18, 18)
                        .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtNombresCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlClienteLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkSinCliente)
                        .addGap(4, 4, 4)))
                .addGroup(pnlClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtApellidosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlClienteLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel3)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        pnlProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Buscar Producto:")));

        jLabel4.setText("Producto:");

        tblProducto.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProducto);

        btnAgregar.setText("AGREGAR AL CARRITO");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        txtCantidad.setText("1");

        jLabel5.setText("Cantidad:");

        btnBuscarProducto.setText("BUSCAR");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlProductoLayout = new javax.swing.GroupLayout(pnlProducto);
        pnlProducto.setLayout(pnlProductoLayout);
        pnlProductoLayout.setHorizontalGroup(
            pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductoLayout.createSequentialGroup()
                .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProductoLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAgregar)
                            .addGroup(pnlProductoLayout.createSequentialGroup()
                                .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4))
                                .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlProductoLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlProductoLayout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlProductoLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscarProducto)
                        .addGap(77, 77, 77)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        pnlProductoLayout.setVerticalGroup(
            pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProductoLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(btnBuscarProducto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(pnlProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(btnAgregar)
                .addGap(23, 23, 23))
            .addGroup(pnlProductoLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlCarrito.setBorder(javax.swing.BorderFactory.createTitledBorder("Carrito de Compra:"));

        tblCarrito.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblCarrito);

        btnQuitar.setText("QUITAR PRODUCTO");
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        jLabel6.setText("Tipo de Pago:");

        cmbPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EFECTIVO", "YAPE", "PLIN", "TARJETA" }));

        lblTotal.setText("Total: S/0.00");

        btnConfirmar.setText("CONFIRMAR");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCarritoLayout = new javax.swing.GroupLayout(pnlCarrito);
        pnlCarrito.setLayout(pnlCarritoLayout);
        pnlCarritoLayout.setHorizontalGroup(
            pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCarritoLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                        .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCarritoLayout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(btnQuitar))
                            .addGroup(pnlCarritoLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlCarritoLayout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(lblTotal)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(btnConfirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addGap(44, 44, 44))))
        );
        pnlCarritoLayout.setVerticalGroup(
            pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCarritoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCarritoLayout.createSequentialGroup()
                        .addComponent(btnQuitar)
                        .addGap(26, 26, 26)
                        .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cmbPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(lblTotal)
                        .addGap(18, 18, 18)
                        .addGroup(pnlCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnConfirmar)
                            .addComponent(btnCancelar))
                        .addGap(0, 38, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlCarrito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        buscarCliente();
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        buscarProducto();
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregarAlCarrito();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        quitarDelCarrito();
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        confirmarVenta();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        cancelarVenta();
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JCheckBox chkSinCliente;
    private javax.swing.JComboBox<String> cmbPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlCarrito;
    private javax.swing.JPanel pnlCliente;
    private javax.swing.JPanel pnlProducto;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblProducto;
    private javax.swing.JTextField txtApellidosCliente;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtDniCliente;
    private javax.swing.JTextField txtNombresCliente;
    // End of variables declaration//GEN-END:variables
}
