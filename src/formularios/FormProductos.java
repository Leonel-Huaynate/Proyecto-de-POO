
package formularios;

import dao.ProductoDAO;
import modelo.*;
import utilidades.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;


public class FormProductos extends javax.swing.JInternalFrame {

    private DefaultTableModel modeloRegistro;
    private DefaultTableModel modeloBuscar;
    private int idSeleccionado = -1;
    private boolean modoEdicion=false;

    public FormProductos() {
        initComponents();
        diseñoTabla();
        cargarTablaRegistro();
        configurarEventos();
        aplicarEstilo();
    }
    
    private void diseñoTabla(){
        
        //Para la tabla de Registros
        String titulosRegistro[]={"ID","Codigo","Nombre","Categoria","Tipo","Precio","Stock","Stock Min.","Estado"};
        modeloRegistro=new DefaultTableModel(titulosRegistro, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRegistro.setModel(modeloRegistro);
        tblRegistro.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblRegistro.setRowHeight(25);
        tblRegistro.getColumnModel().getColumn(0).setMinWidth(0);
        tblRegistro.getColumnModel().getColumn(0).setMaxWidth(0);
        tblRegistro.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblRegistro.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblRegistro.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblRegistro.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblRegistro.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblRegistro.getColumnModel().getColumn(6).setPreferredWidth(70);
        
        //Para la tabla Buscar
        String titulosBuscar[]={"ID","Codigo","Nombre","Categoria","Tipo","Precio","Stock","Stock Min.","Estado"};
        modeloBuscar=new DefaultTableModel(titulosBuscar, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBuscar.setModel(modeloBuscar);
        tblBuscar.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBuscar.setRowHeight(25);
        tblBuscar.getColumnModel().getColumn(0).setMinWidth(0);
        tblBuscar.getColumnModel().getColumn(0).setMaxWidth(0);
        tblBuscar.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblBuscar.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblBuscar.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblBuscar.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblBuscar.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblBuscar.getColumnModel().getColumn(6).setPreferredWidth(70);
    }
    
    //Metodo para cargar los productos en la tabla de Registros
    private void cargarTablaRegistro(){
        modeloRegistro.setRowCount(0);
        try{
            for(ProductoBase p:ProductoDAO.listarProductos()){
                modeloRegistro.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getTipo(),
                    p.getPrecioFormateado(),
                    p.getStock(),
                    p.getStock_min(),
                    p.isActivo()? "Activo":"Inactivo"
                });
            }
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
            }
        }

    //Metodo para cargar los productos en la tabla de Buscar
    private void cargarTablaBuscar(List<ProductoBase> lista){
        modeloBuscar.setRowCount(0);
        try{
            for(ProductoBase p:lista){
                modeloBuscar.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getTipo(),
                    p.getPrecioFormateado(),
                    p.getStock(),
                    p.getStock_min(),
                    p.isActivo()? "Activo":"Inactivo"
                });
            }
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
            }
        }
    // ── Eventos ────────────────────────────────────────────────
    private void configurarEventos() {


        // Clic en tabla Registrar — solo guarda el id seleccionado
        // NO carga datos en los campos (Opción A)
        tblRegistro.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tblRegistro.getSelectedRow();
                if (fila != -1) {
                    idSeleccionado = (int) modeloRegistro
                                           .getValueAt(fila, 0);
                }
            }
        });
        
        // Clic en tabla Buscar → guarda id seleccionado
        tblBuscar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tblBuscar.getSelectedRow();
                if (fila != -1) {
                    idSeleccionado = (int) modeloBuscar
                                          .getValueAt(fila, 0);
                }
            }
        });
        // Enter en txtBuscar
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    buscarProducto();
                }
            }
        });

        // Cambio de pestaña
        tabProductos.addChangeListener(e -> {
            if (tabProductos.getSelectedIndex() == 0) {
                if(!modoEdicion){cargarTablaRegistro();}
            } else {
                try {
                    cargarTablaBuscar(ProductoDAO.listarProductos());
                } catch (ErrorBD ex) {
                    Mensajes.error(ex.getMessage());
                }
            }
        });
    }
    

    //Metodo para Limpiar campos 
    private void limpiarCampos() {
        idSeleccionado = -1;
        modoEdicion=false;
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMin.setText("");
        cmbTipo.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tblRegistro.clearSelection();
        cargarTablaRegistro();
    }
    
    //Metodo para guardar el producto ya sea nuevo o editar
    private void guardarProducto(){
        
        //Guarda los datos de las cajas, combos, checks en variables todos son tipos Strings
        //Para validarlo en las Validaciones
        String codigo      = txtCodigo.getText().trim();
        String nombre      = txtNombre.getText().trim();
        String categoria   = txtCategoria.getText().trim();
        String precio   = txtPrecio.getText().trim();
        String stock   = txtStock.getText().trim();
        String stockMin = txtStockMin.getText().trim();
        String tipo        = cmbTipo.getSelectedItem().toString();
        boolean activo     = chkActivo.isSelected();
        
        //Validaciones
        if(!Validaciones.camposLlenos(codigo,nombre,categoria,precio,stock,stockMin)){
            Mensajes.advertencia("Es obligatorio llenar todos los campos");
            return;
        }
        if(!Validaciones.precioValido(precio)){
            Mensajes.advertencia("El precio debe ser mayor a cero");
            return;
        }
        if (!Validaciones.mayorCero(stock)) {
            Mensajes.advertencia("El stock debe ser mayor a cero.");
            return;
        }
        if (!Validaciones.esEntero(stockMin)) {
            Mensajes.advertencia("Stock mínimo debe ser un número.");
            return;
        }
        
        //Creamos el objeto ProductoBase (PADRE)  segun el Tipo
        ProductoBase producto;
        switch (tipo) {
            case "ALIMENTO":
                producto = new ProductoAlimento();
                break;
            case "BEBIDA":
                producto = new ProductoBebida();
                break;
            default:
                producto = new ProductoLimpieza();
                break;
        }
        
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setTipo(tipo);
        producto.setPrecio(Double.parseDouble(precio));
        producto.setStock(Integer.parseInt(stock));
        producto.setStock_min(Integer.parseInt(stockMin));
        producto.setActivo(activo);
        
        try{
            if(idSeleccionado==-1){
                //Es un nuevo Producto
                if(ProductoDAO.existeCodigo(codigo)){
                    Mensajes.advertencia("Ese codigo ya existe. ");
                    return;
                }
                ProductoDAO.insertarProducto(producto);
                Mensajes.exito("Se guardo correctamente el producto. ");   
            }else{
                //Es un producto que se quiere Editar
                if(ProductoDAO.existeCodigoActualizar(codigo, idSeleccionado)){
                    Mensajes.advertencia("Ese codigo ya existe para editarlo. ");
                    return;
                }
                producto.setId(idSeleccionado);
                ProductoDAO.actualizarProducto(producto);
                Mensajes.exito("Se actualizo correctamente el producto. ");
            }
            limpiarCampos();
        
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para buscar un Producto
    private void buscarProducto(){
        
        String texto=txtBuscar.getText().trim();
        String filtro=cmbFiltro.getSelectedItem().toString();
        
        try{
            List<ProductoBase> lista;
            if(texto.isEmpty()){
                lista=ProductoDAO.listarProductos();
            }else{
                lista=ProductoDAO.buscarProducto(filtro, texto);
            }
            cargarTablaBuscar(lista);
             if (modeloBuscar.getRowCount() == 0) {
                Mensajes.advertencia("No se encontró ningún producto.");
            }
        }catch(ErrorBD e){
            Mensajes.error(e.getMessage());
        }
    }
    
    //Metodo para Editar
    private void editarSeleccionado() {
        int fila = tblBuscar.getSelectedRow();
        if (fila == -1) {
            Mensajes.advertencia("Selecciona un producto.");
            return;
        }

        idSeleccionado = (int) modeloBuscar.getValueAt(fila, 0);
        txtCodigo.setText((String) modeloBuscar.getValueAt(fila, 1));
        txtNombre.setText((String) modeloBuscar.getValueAt(fila, 2));
        txtCategoria.setText(
            (String) modeloBuscar.getValueAt(fila, 3));
        cmbTipo.setSelectedItem(modeloBuscar.getValueAt(fila, 4));

        String precio = modeloBuscar.getValueAt(fila, 5)
                       .toString().replace("S/ ", "");
        txtPrecio.setText(precio);
        txtStock.setText(
            String.valueOf(modeloBuscar.getValueAt(fila, 6)));
        txtStockMin.setText(
            String.valueOf(modeloBuscar.getValueAt(fila, 7)));
        chkActivo.setSelected(
            modeloBuscar.getValueAt(fila, 8).equals("Activo"));

        modoEdicion=true;
        // Cambiar a pestaña Registrar
        tabProductos.setSelectedIndex(0);
    }
    
    //Metodo para eliminar Producto
    private void eliminarProducto(){
        if (idSeleccionado == -1) {
        Mensajes.advertencia("Selecciona un producto de la tabla.");
        return;
    }
        if (Mensajes.confirmar(
            "¿Desea eliminar este producto permanentemente?\n"
          + "Esta acción no se puede deshacer.")) {
            try {
                ProductoDAO.eliminarProducto(idSeleccionado);
                Mensajes.exito("Producto eliminado correctamente.");
                limpiarCampos();
            } catch (ErrorBD e) {
            Mensajes.error("No se puede eliminar este producto.\n"
                         + "Puede tener ventas asociadas.\n"
                         + e.getMessage());
            }
        }
    }
     // ── Estilo visual ──────────────────────────────────────────
    private void aplicarEstilo() {
        btnNuevo.setBackground(new java.awt.Color(40, 167, 69));
        btnNuevo.setFocusPainted(false);

        btnGuardar.setBackground(new java.awt.Color(0, 123, 255));
        btnGuardar.setFocusPainted(false);

        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setFocusPainted(false);

        btnBuscar.setBackground(new java.awt.Color(255, 193, 7));
        btnBuscar.setFocusPainted(false);

        btnEditar.setBackground(new java.awt.Color(255, 133, 27));
        btnEditar.setFocusPainted(false);

        btnVerTodo.setBackground(new java.awt.Color(23, 162, 184));
        btnVerTodo.setFocusPainted(false);

        tblRegistro.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));

        tblRegistro.setSelectionBackground(
            new java.awt.Color(0, 123, 255));

        tblBuscar.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));

        tblBuscar.setSelectionBackground(
            new java.awt.Color(0, 123, 255));
    }
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabProductos = new javax.swing.JTabbedPane();
        panelRegistrar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCategoria = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtStockMin = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmbTipo = new javax.swing.JComboBox<>();
        chkActivo = new javax.swing.JCheckBox();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRegistro = new javax.swing.JTable();
        btnEliminar = new javax.swing.JButton();
        panelBuscar = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cmbFiltro = new javax.swing.JComboBox<>();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnVerTodo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBuscar = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle("Gestion de Productos");

        jLabel1.setText("Codigo :");

        jLabel2.setText("Nombre :");

        jLabel3.setText("Categoria :");

        jLabel6.setText("Stock Minimo :");

        jLabel5.setText("Stock :");

        jLabel4.setText("Precio :");

        jLabel7.setText("Tipo :");

        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALIMENTO", "BEBIDA", "LIMPIEZA", "GENERAL" }));

        chkActivo.setText("Activo");

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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de Productos :"));

        tblRegistro.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRegistro);

        btnEliminar.setText("ELIMINAR");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRegistrarLayout = new javax.swing.GroupLayout(panelRegistrar);
        panelRegistrar.setLayout(panelRegistrarLayout);
        panelRegistrarLayout.setHorizontalGroup(
            panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRegistrarLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCategoria))
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigo)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelRegistrarLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(10, 10, 10)
                                .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelRegistrarLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(100, 100, 100)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelRegistrarLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(28, 28, 28)
                                .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRegistrarLayout.createSequentialGroup()
                                .addComponent(btnNuevo)
                                .addGap(35, 35, 35)
                                .addComponent(btnGuardar)
                                .addGap(29, 29, 29)))))
                .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(chkActivo)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRegistrarLayout.createSequentialGroup()
                        .addComponent(btnEliminar)
                        .addGap(40, 40, 40))))
            .addGroup(panelRegistrarLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRegistrarLayout.setVerticalGroup(
            panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRegistrarLayout.createSequentialGroup()
                .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(txtStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(panelRegistrarLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(chkActivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelRegistrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEliminar)
                            .addComponent(btnGuardar)
                            .addComponent(btnNuevo))
                        .addGap(37, 37, 37)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        tabProductos.addTab("Registrar Producto", panelRegistrar);

        jLabel8.setText("Buscar por :");

        cmbFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CODIGO", "NOMBRE", "CATEGORIA", "TIPO" }));

        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnVerTodo.setText("VER TODOS");
        btnVerTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTodoActionPerformed(evt);
            }
        });

        btnEditar.setText("EDITAR PRODUCTO");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Lista de Productos :"));

        tblBuscar.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblBuscar);

        javax.swing.GroupLayout panelBuscarLayout = new javax.swing.GroupLayout(panelBuscar);
        panelBuscar.setLayout(panelBuscarLayout);
        panelBuscarLayout.setHorizontalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(49, 49, 49)
                        .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(btnBuscar))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                        .addComponent(btnVerTodo)
                        .addGap(62, 62, 62))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarLayout.createSequentialGroup()
                        .addComponent(btnEditar)
                        .addGap(43, 43, 43))))
        );
        panelBuscarLayout.setVerticalGroup(
            panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cmbFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addGroup(panelBuscarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVerTodo)
                        .addGap(81, 81, 81)
                        .addComponent(btnEditar)
                        .addGap(98, 98, 98))
                    .addGroup(panelBuscarLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(32, Short.MAX_VALUE))))
        );

        tabProductos.addTab("Buscar Producto", panelBuscar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabProductos, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabProductos))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarProducto();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarProducto();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnVerTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTodoActionPerformed
        txtBuscar.setText("");
        try {
            cargarTablaBuscar(ProductoDAO.listarProductos());
        } catch (ErrorBD e) {
            Mensajes.error(e.getMessage());
        }
    }//GEN-LAST:event_btnVerTodoActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        editarSeleccionado();
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProducto();
    }//GEN-LAST:event_btnEliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnVerTodo;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JComboBox<String> cmbFiltro;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelBuscar;
    private javax.swing.JPanel panelRegistrar;
    private javax.swing.JTabbedPane tabProductos;
    private javax.swing.JTable tblBuscar;
    private javax.swing.JTable tblRegistro;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCategoria;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtStockMin;
    // End of variables declaration//GEN-END:variables
}
