
package formularios;

import dao.VentaDAO;
import modelo.DetalleVenta;
import modelo.Venta;
import utilidades.*;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FormReportes extends javax.swing.JInternalFrame {
    
    private DefaultTableModel modeloProductosMas;
    private DefaultTableModel modeloVentasPeriodo;
    
    // Formato de fecha para mostrar y para SQL
    private final DateTimeFormatter FMT_DISPLAY =
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter FMT_SQL =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FormReportes() {
        initComponents();
        configurarTablas();
        aplicarEstilo();
        dateDesde.setLocale(Locale.of("es", "PE"));
        dateDesde.setDateFormatString("dd/MM/yyyy");
        dateHasta.setLocale(Locale.of("es", "PE"));
        dateHasta.setDateFormatString("dd/MM/yyyy");
        Date hoy = new Date();
        dateDesde.setDate(hoy);
        dateHasta.setDate(hoy);
        cargarReporte(
        LocalDate.now().format(FMT_SQL),
        LocalDate.now().format(FMT_SQL),
        false);  // ← false = no mostrar advertencia
    }
    
    //Metodo para configurar las tablas
    private void configurarTablas(){
        //Tabla de Ventas por el Periodo
        modeloVentasPeriodo = new DefaultTableModel(new String[]{"ID","Fecha","Cajero",
                                                                "Cliente","Pago","Total"}, 0){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        tblVentas.setModel(modeloVentasPeriodo);
        tblVentas.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVentas.setRowHeight(25);
        tblVentas.getColumnModel().getColumn(0).setMinWidth(0);
        tblVentas.getColumnModel().getColumn(0).setMaxWidth(0);
        tblVentas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tblVentas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblVentas.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblVentas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblVentas.getColumnModel().getColumn(5).setPreferredWidth(90);
        
        //Tabla Productos mas Vendidos
        modeloProductosMas = new DefaultTableModel(new String[]{"Producto","Unidades vendidas","Ingresos"}, 0){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        tblProductosMas.setModel(modeloProductosMas);
        tblProductosMas.setRowHeight(25);
        tblProductosMas.getColumnModel().getColumn(0)
                    .setPreferredWidth(200);
        tblProductosMas.getColumnModel().getColumn(1)
                    .setPreferredWidth(130);
        tblProductosMas.getColumnModel().getColumn(2)
                    .setPreferredWidth(100);
    }
    
    //Metodo para cargar el reporte de Hoy
    private void cargarReporteHoy(){
        Date hoy = new Date();
        dateDesde.setDate(hoy);
        dateHasta.setDate(hoy);
        String hoySQL = LocalDate.now().format(FMT_SQL);
        cargarReporte(hoySQL, hoySQL, false);
    }
    
    private void cargarReporteEstaSemana() {
        LocalDate hoy   = LocalDate.now();
        LocalDate lunes = hoy.with(java.time.DayOfWeek.MONDAY);

        // Actualizar los calendarios visualmente
        dateDesde.setDate(java.sql.Date.valueOf(lunes));
        dateHasta.setDate(java.sql.Date.valueOf(hoy));

        cargarReporte(lunes.format(FMT_SQL), hoy.format(FMT_SQL), true);
    }
    
    private void cargarReporteEsteMes() {
        LocalDate hoy     = LocalDate.now();
        LocalDate primero = hoy.withDayOfMonth(1);

        dateDesde.setDate(java.sql.Date.valueOf(primero));
        dateHasta.setDate(java.sql.Date.valueOf(hoy));

        cargarReporte(primero.format(FMT_SQL), hoy.format(FMT_SQL), true);
    }
    
    //Carga reporte por fechas ingresadas manualmente
    private void cargarReportePorFechas() {
            // Verificar que ambas fechas estén seleccionadas
        if (dateDesde.getDate() == null || dateHasta.getDate() == null) {
            Mensajes.advertencia("Selecciona las fechas de inicio y fin.");
            return;
        }

        // Convertir Date a LocalDate
        LocalDate desde = dateDesde.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate hasta = dateHasta.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        if (desde.isAfter(hasta)) {
            Mensajes.advertencia(
                "La fecha inicio no puede ser mayor que la fecha fin.");
            return;
        }

        cargarReporte(desde.format(FMT_SQL), hasta.format(FMT_SQL), true);
    }
    
    //Sobregar de Metodos
    //Metodo principal para cargar Reporte por fechas ingresadas
    private void cargarReporte(String desde, String hasta) {
        cargarReporte(desde, hasta, true);
    }
    
    //Metodo para ver la boleta seleccionada de la Tabla
    private void verBoleta(){
        int fila = tblVentas.getSelectedRow();
        if(fila==-1){
            Mensajes.advertencia("Seleccione una venta de la tabla");
            return;
        }
        
        int idVenta = (int) modeloVentasPeriodo.getValueAt(fila, 0);
        String rutaBoleta = "boletas/boleta_" + idVenta + ".pdf";
        java.io.File archivo = new java.io.File(rutaBoleta);
        
        if (!archivo.exists()) {
        Mensajes.advertencia(
                "No se encontró la boleta de esta venta.\n"
              + "Es posible que haya sido eliminada o\n"
              + "que la venta sea anterior al sistema.");
            return;
        }

        try {
            java.awt.Desktop.getDesktop().open(archivo);
        } catch (Exception e) {
            Mensajes.error(
                "No se pudo abrir la boleta:\n"
              + e.getMessage());
        }
    }
    
    //Metodo para cargar Reporte por fechas ingresadas y mostrar advertencia si no hay registros
    private void cargarReporte(String desde, String hasta, boolean mostrarAdvertencia) {
        modeloVentasPeriodo.setRowCount(0);
        modeloProductosMas.setRowCount(0);

        try {
            //Cargar tabla de ventas
            List<Venta> ventas = VentaDAO.listarPorFechas(desde, hasta);

            double totalAcumulado = 0;
            int totalProductosVendidos = 0;

            for (Venta v : ventas) {
                //Formatear fecha para mostrar
                String fechaStr = v.getFecha() != null
                    ? new java.text.SimpleDateFormat(
                        "dd/MM/yyyy HH:mm")
                        .format(v.getFecha())
                    : "-";

                // Cliente — puede ser null
                String clienteStr = v.getNombreCliente() != null
                    ? v.getNombreCliente()
                    : "Sin cliente";

                modeloVentasPeriodo.addRow(new Object[]{
                    v.getId(),
                    fechaStr,
                    v.getNombreCajero(),
                    clienteStr,
                    v.getTipoPago(),
                    v.getTotalFormateado()
                });

                totalAcumulado += v.getTotal();

                // Contar productos vendidos
                List<DetalleVenta> detalles = VentaDAO.obtenerDetalles(v.getId());
                for (DetalleVenta d : detalles) {
                    totalProductosVendidos += d.getCantidad();
                }
            }

            // Actualizar resumen
            lblTotal.setText(
                "Total: S/ " + String.format("%.2f",
                    totalAcumulado));
            lblNumVentas.setText(
                "Nº ventas: " + ventas.size());
            lblProVendidos.setText(
                "Productos vendidos: " + totalProductosVendidos);

            //Cargar productos más vendidos
            List<Object[]> topProductos = VentaDAO.productosMasVendidos(5);
            for (Object[] fila : topProductos) {
                modeloProductosMas.addRow(new Object[]{
                    fila[0],  // nombre
                    fila[1],  // unidades
                    String.format("S/ %.2f", (double) fila[2])
                });
            }

            if (ventas.isEmpty() && mostrarAdvertencia) {
                Mensajes.advertencia(
                    "No hay ventas en el período seleccionado.");
            }

        } catch (ErrorBD e) {
            Mensajes.error(e.getMessage());
        }
    }
    
    //Estilo visual 
    private void aplicarEstilo() {
        pnlVentas.setBackground(new java.awt.Color(245, 245, 250));
        pnlProVendidos.setBackground(
            new java.awt.Color(245, 245, 250));


        btnVerReporte.setBackground(
            new java.awt.Color(0, 123, 255));
        btnVerReporte.setFocusPainted(false);

        btnVerBoleta.setBackground(new java.awt.Color(23, 162, 184));
        btnVerBoleta.setFocusPainted(false);

        tblVentas.setRowHeight(25);
        tblVentas.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));

        tblProductosMas.setRowHeight(25);
        tblProductosMas.getTableHeader().setBackground(
            new java.awt.Color(30, 30, 60));
        tblProductosMas.setSelectionBackground(
            new java.awt.Color(0, 123, 255));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFiltro = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnVerReporte = new javax.swing.JButton();
        dateDesde = new com.toedter.calendar.JDateChooser();
        dateHasta = new com.toedter.calendar.JDateChooser();
        pnlResumen = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        lblNumVentas = new javax.swing.JLabel();
        lblProVendidos = new javax.swing.JLabel();
        pnlVentas = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        btnVerBoleta = new javax.swing.JButton();
        pnlProVendidos = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductosMas = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle("Reportes de Ventas");

        pnlFiltro.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro del Reporte:"));

        jLabel1.setText("DESDE:");

        jLabel2.setText("HASTA:");

        btnVerReporte.setText("VER REPORTE");
        btnVerReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerReporteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFiltroLayout = new javax.swing.GroupLayout(pnlFiltro);
        pnlFiltro.setLayout(pnlFiltroLayout);
        pnlFiltroLayout.setHorizontalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFiltroLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(26, 26, 26)
                        .addComponent(dateHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43))
                    .addGroup(pnlFiltroLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dateDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(167, 167, 167))))
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(btnVerReporte)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFiltroLayout.setVerticalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(24, 24, 24)
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFiltroLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVerReporte)
                        .addGap(22, 22, 22))
                    .addGroup(pnlFiltroLayout.createSequentialGroup()
                        .addComponent(dateHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pnlResumen.setBackground(new java.awt.Color(255, 204, 102));
        pnlResumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen del Periodo:"));
        pnlResumen.setLayout(null);

        lblTotal.setText("Total: S/0.00");
        pnlResumen.add(lblTotal);
        lblTotal.setBounds(30, 50, 120, 16);

        lblNumVentas.setText("N° Ventas: 0");
        pnlResumen.add(lblNumVentas);
        lblNumVentas.setBounds(160, 50, 100, 16);

        lblProVendidos.setText("Productos Vendidos: 0");
        pnlResumen.add(lblProVendidos);
        lblProVendidos.setBounds(280, 50, 160, 16);

        pnlVentas.setBorder(javax.swing.BorderFactory.createTitledBorder("Ventas del Periodo:"));

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblVentas);

        btnVerBoleta.setText("VER BOLETA");
        btnVerBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerBoletaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVentasLayout = new javax.swing.GroupLayout(pnlVentas);
        pnlVentas.setLayout(pnlVentasLayout);
        pnlVentasLayout.setHorizontalGroup(
            pnlVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(pnlVentasLayout.createSequentialGroup()
                .addGap(194, 194, 194)
                .addComponent(btnVerBoleta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlVentasLayout.setVerticalGroup(
            pnlVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnVerBoleta)
                .addContainerGap())
        );

        pnlProVendidos.setBorder(javax.swing.BorderFactory.createTitledBorder("Productos más Vendidos:"));

        tblProductosMas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProductosMas);

        javax.swing.GroupLayout pnlProVendidosLayout = new javax.swing.GroupLayout(pnlProVendidos);
        pnlProVendidos.setLayout(pnlProVendidosLayout);
        pnlProVendidosLayout.setHorizontalGroup(
            pnlProVendidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProVendidosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlProVendidosLayout.setVerticalGroup(
            pnlProVendidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProVendidosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlProVendidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addComponent(pnlVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlProVendidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlResumen, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerReporteActionPerformed
        cargarReportePorFechas();
    }//GEN-LAST:event_btnVerReporteActionPerformed

    private void btnVerBoletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerBoletaActionPerformed
        verBoleta();
    }//GEN-LAST:event_btnVerBoletaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnVerBoleta;
    private javax.swing.JButton btnVerReporte;
    private com.toedter.calendar.JDateChooser dateDesde;
    private com.toedter.calendar.JDateChooser dateHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblNumVentas;
    private javax.swing.JLabel lblProVendidos;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JPanel pnlProVendidos;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JPanel pnlVentas;
    private javax.swing.JTable tblProductosMas;
    private javax.swing.JTable tblVentas;
    // End of variables declaration//GEN-END:variables
}
