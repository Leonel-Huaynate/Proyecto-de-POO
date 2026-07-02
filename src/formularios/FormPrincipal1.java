
package formularios;

import utilidades.*;
import modelo.Usuario;
import javax.swing.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormPrincipal1 extends javax.swing.JFrame {

    private Usuario usuarioActual;
    
    public FormPrincipal1(Usuario u) {
        this.usuarioActual=u;
        initComponents();
        configurarVentana();
        menuSegunRol();
        
    }

    private void configurarVentana() {
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // abre maximizado
        
        jPanel.setBackground(new java.awt.Color(255, 223, 100));
        lblBienvenida.setForeground(new java.awt.Color(60, 40, 0));
        lblBienvenida.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRol.setForeground(new java.awt.Color(60, 40, 0));
        lblFecha.setForeground(new java.awt.Color(60, 40, 0));

        // Mostrar info del usuario en el panel inferior
        lblBienvenida.setText(usuarioActual.getSaludo());
        lblRol.setText("Rol: " + usuarioActual.getRol());

        // Mostrar fecha actual
        DateTimeFormatter formato = DateTimeFormatter
                                   .ofPattern("dd/MM/yyyy HH:mm");
        lblFecha.setText("Fecha: " + LocalDateTime.now().format(formato));

        // Controlar el cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });
    }
    
    private void menuSegunRol(){
        
        mnuUsuarios.setVisible(false);
        mnuProductos.setVisible(false);
        mnuReportes.setVisible(false);
        mnuClientes.setVisible(false);
        mnuStock.setVisible(false);
        mnuVentas.setVisible(false);

         if (usuarioActual.esSuperAdmin()) {
            mnuUsuarios.setVisible(true);
            mnuProductos.setVisible(true);
            mnuReportes.setVisible(true);
            mnuClientes.setVisible(true);
            mnuStock.setVisible(true);
            mnuVentas.setVisible(true);
                
        } else if (usuarioActual.esAdmin()) {
            mnuProductos.setVisible(true);  
            mnuReportes.setVisible(true);
            mnuClientes.setVisible(true);

        } else if (usuarioActual.esCajero()) {
            mnuClientes.setVisible(true);
            mnuStock.setVisible(true);
            mnuVentas.setVisible(true);
        }
    }
    
    
    private void abrirVentanaInterna(JInternalFrame frame) {
        // Verificar si ya está abierto
        for (JInternalFrame f : dspEscritorio.getAllFrames()) {
            if (f.getClass() == frame.getClass()) {
                try {
                    f.setSelected(true);  // trae al frente el que ya está abierto
                } catch (Exception ex) { }
                return;
            }
        }
        
        // Centrar el JInternalFrame dentro del JDesktopPane 
        int x=(dspEscritorio.getWidth()-frame.getWidth())/2;
        int y=(dspEscritorio.getHeight()-frame.getHeight())/2;
        frame.setLocation(x, y);
        
        // Si no está abierto, agregarlo
        dspEscritorio.add(frame);
        frame.setVisible(true);
        frame.toFront();
    }
    
    private void cerrarSesion() {
        if (Mensajes.confirmar("¿Deseas cerrar sesión?")) {
            this.dispose();
            new FormLogin().setVisible(true);
            }
        }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dspEscritorio = new javax.swing.JDesktopPane();
        jPanel = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        lblRol = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuUsuarios = new javax.swing.JMenu();
        itemUsuarios = new javax.swing.JMenuItem();
        mnuProductos = new javax.swing.JMenu();
        itemProductos = new javax.swing.JMenuItem();
        mnuReportes = new javax.swing.JMenu();
        itemReportes = new javax.swing.JMenuItem();
        mnuClientes = new javax.swing.JMenu();
        itemClientes = new javax.swing.JMenuItem();
        mnuStock = new javax.swing.JMenu();
        itemStock = new javax.swing.JMenuItem();
        mnuVentas = new javax.swing.JMenu();
        itemVenta = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dspEscritorio.setForeground(new java.awt.Color(255, 255, 255));

        jPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBienvenida.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblBienvenida.setText("Bienvenido :");
        jPanel.add(lblBienvenida, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, -1, -1));

        lblFecha.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblFecha.setText("Fecha :");
        jPanel.add(lblFecha, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 70, -1, -1));

        lblRol.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblRol.setText("Rol :");
        jPanel.add(lblRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, -1, -1));

        dspEscritorio.setLayer(jPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dspEscritorioLayout = new javax.swing.GroupLayout(dspEscritorio);
        dspEscritorio.setLayout(dspEscritorioLayout);
        dspEscritorioLayout.setHorizontalGroup(
            dspEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 848, Short.MAX_VALUE)
        );
        dspEscritorioLayout.setVerticalGroup(
            dspEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dspEscritorioLayout.createSequentialGroup()
                .addGap(0, 305, Short.MAX_VALUE)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenuBar1.setBackground(new java.awt.Color(255, 102, 102));

        mnuUsuarios.setText("Usuarios");

        itemUsuarios.setText("Gestionar Usuarios");
        itemUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemUsuariosActionPerformed(evt);
            }
        });
        mnuUsuarios.add(itemUsuarios);

        jMenuBar1.add(mnuUsuarios);

        mnuProductos.setText("Productos ");

        itemProductos.setText("Gestionar Productos");
        itemProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemProductosActionPerformed(evt);
            }
        });
        mnuProductos.add(itemProductos);

        jMenuBar1.add(mnuProductos);

        mnuReportes.setText("Reportes");

        itemReportes.setText("Ver Reportes");
        itemReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemReportesActionPerformed(evt);
            }
        });
        mnuReportes.add(itemReportes);

        jMenuBar1.add(mnuReportes);

        mnuClientes.setText("Clientes");

        itemClientes.setText("Gestionar Clientes");
        itemClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemClientesActionPerformed(evt);
            }
        });
        mnuClientes.add(itemClientes);

        jMenuBar1.add(mnuClientes);

        mnuStock.setText("Stock");

        itemStock.setText("Consultar Stock");
        itemStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemStockActionPerformed(evt);
            }
        });
        mnuStock.add(itemStock);

        jMenuBar1.add(mnuStock);

        mnuVentas.setText("Ventas");

        itemVenta.setText("Realizar Venta");
        itemVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemVentaActionPerformed(evt);
            }
        });
        mnuVentas.add(itemVenta);

        jMenuBar1.add(mnuVentas);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dspEscritorio)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dspEscritorio)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemUsuariosActionPerformed
        abrirVentanaInterna(new FormUsuarios());
    }//GEN-LAST:event_itemUsuariosActionPerformed

    private void itemProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemProductosActionPerformed
        abrirVentanaInterna(new FormProductos());
    }//GEN-LAST:event_itemProductosActionPerformed

    private void itemClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemClientesActionPerformed
        abrirVentanaInterna(new FormClientes());
    }//GEN-LAST:event_itemClientesActionPerformed

    private void itemVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemVentaActionPerformed
        abrirVentanaInterna(new FormVentas(usuarioActual));
    }//GEN-LAST:event_itemVentaActionPerformed

    private void itemReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemReportesActionPerformed
        abrirVentanaInterna(new FormReportes());
    }//GEN-LAST:event_itemReportesActionPerformed

    private void itemStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemStockActionPerformed
        abrirVentanaInterna(new FormStock());
    }//GEN-LAST:event_itemStockActionPerformed

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
            java.util.logging.Logger.getLogger(FormPrincipal1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPrincipal1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPrincipal1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPrincipal1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new FormPrincipal1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDesktopPane dspEscritorio;
    private javax.swing.JMenuItem itemClientes;
    private javax.swing.JMenuItem itemProductos;
    private javax.swing.JMenuItem itemReportes;
    private javax.swing.JMenuItem itemStock;
    private javax.swing.JMenuItem itemUsuarios;
    private javax.swing.JMenuItem itemVenta;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblRol;
    private javax.swing.JMenu mnuClientes;
    private javax.swing.JMenu mnuProductos;
    private javax.swing.JMenu mnuReportes;
    private javax.swing.JMenu mnuStock;
    private javax.swing.JMenu mnuUsuarios;
    private javax.swing.JMenu mnuVentas;
    // End of variables declaration//GEN-END:variables
}
