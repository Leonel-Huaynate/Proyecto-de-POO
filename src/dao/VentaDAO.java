
package dao;
import conexion.ConexionBD;
import modelo.Venta;
import modelo.DetalleVenta;
import utilidades.ErrorBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    
    //Metodo para guardar una Venta
    public static boolean guardarVenta(Venta venta) {
        Connection con = null;
        try {
            con = ConexionBD.obtenerConexion();
            con.setAutoCommit(false); // inicia transacción

            // PASO 1 — Insertar encabezado de la venta
            String sqlVenta = "INSERT INTO Ventas "
                            + "(id_usuario, total, tipo_pago, id_cliente) "
                            + "VALUES (?, ?, ?, ?)";

            PreparedStatement psVenta = con.prepareStatement(
                sqlVenta, Statement.RETURN_GENERATED_KEYS);

            psVenta.setInt(1, venta.getIdUsuario());
            psVenta.setDouble(2, venta.getTotal());
            psVenta.setString(3, venta.getTipoPago());

            // id_cliente es opcional — puede ser null
            if (venta.tieneCliente()) {
                psVenta.setInt(4, venta.getCliente().getId());
            } else {
                psVenta.setNull(4, Types.INTEGER);
            }

            psVenta.executeUpdate();

            // Obtener el id generado por SQL Server
            ResultSet keys = psVenta.getGeneratedKeys();
            int idVenta = 0;
            if (keys.next()) {
                idVenta = keys.getInt(1);
            }
            
            venta.setId(idVenta);

            // PASO 2 — Insertar cada detalle del carrito
            String sqlDetalle = "INSERT INTO DetalleVentas "
                              + "(id_venta, id_producto, cantidad, "
                              + "precio_unitario, subtotal) "
                              + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement psDetalle =
                con.prepareStatement(sqlDetalle);

            for (DetalleVenta d : venta.getDetalles()) {
                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, d.getIdProducto());
                psDetalle.setInt(3, d.getCantidad());
                psDetalle.setDouble(4, d.getPrecioUnitario());
                psDetalle.setDouble(5, d.getSubtotal());
                psDetalle.addBatch(); // agrega al lote
            }
            psDetalle.executeBatch(); // ejecuta todos los detalles

            // PASO 3 — Descontar stock de cada producto
            String sqlStock = "UPDATE Productos "
                            + "SET stock = stock - ? "
                            + "WHERE id = ?";

            PreparedStatement psStock =
                con.prepareStatement(sqlStock);

            for (DetalleVenta d : venta.getDetalles()) {
                psStock.setInt(1, d.getCantidad());
                psStock.setInt(2, d.getIdProducto());
                psStock.addBatch();
            }
            psStock.executeBatch();

            con.commit(); // confirma todo
            return true;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback(); // deshace todo
            } catch (Exception ex) { }
            throw new ErrorBD("Error al guardar la venta: "
                            + e.getMessage(), "guardarVenta");
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception e) { }
        }
    }

    //Metodo para Listar todas las ventas 
    public static List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT V.id, V.fecha, V.total, V.tipo_pago, "
                   + "U.nombres + ' ' + U.apellidos AS cajero, "
                   + "C.nombres + ' ' + C.apellidos AS cliente "
                   + "FROM Ventas V "
                   + "JOIN Usuarios U ON V.id_usuario = U.id "
                   + "LEFT JOIN Clientes C ON V.id_cliente = C.id "
                   + "ORDER BY V.fecha DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id"));
                v.setFecha(rs.getTimestamp("fecha"));
                v.setTotal(rs.getDouble("total"));
                v.setTipoPago(rs.getString("tipo_pago"));
                v.setNombreCajero(rs.getString("cajero"));
                String cliente = rs.getString("cliente");
                // cliente puede ser null si no se registró
                if (cliente != null) {
                    v.setNombreCajero(rs.getString("cajero"));
                }
                lista.add(v);
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al listar ventas: "
                            + e.getMessage(), "listarTodas");
        }
        return lista;
    }

    //Metodo para Listar ventas por rango de fechas
    public static List<Venta> listarPorFechas(
            String fechaInicio, String fechaFin) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT V.id, V.fecha, V.total, V.tipo_pago, "
                   + "U.nombres + ' ' + U.apellidos AS cajero, "
                   + "ISNULL(C.nombres + ' ' + C.apellidos, "
                   + "'Sin cliente') AS cliente "
                   + "FROM Ventas V "
                   + "JOIN Usuarios U ON V.id_usuario = U.id "
                   + "LEFT JOIN Clientes C ON V.id_cliente = C.id "
                   + "WHERE CAST(V.fecha AS DATE) "
                   + "BETWEEN ? AND ? "
                   + "ORDER BY V.fecha DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id"));
                v.setFecha(rs.getTimestamp("fecha"));
                v.setTotal(rs.getDouble("total"));
                v.setTipoPago(rs.getString("tipo_pago"));
                v.setNombreCajero(rs.getString("cajero"));
                v.setNombreCliente(rs.getString("cliente"));
                lista.add(v);
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al listar ventas por fechas: "
                            + e.getMessage(), "listarPorFechas");
        }
        return lista;
    }

    //Metodo para obtener detalles de una venta
    public static List<DetalleVenta> obtenerDetalles(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT D.id, D.id_producto, P.nombre, "
                   + "D.cantidad, D.precio_unitario, D.subtotal "
                   + "FROM DetalleVentas D "
                   + "JOIN Productos P ON D.id_producto = P.id "
                   + "WHERE D.id_venta = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetalleVenta d = new DetalleVenta(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario")
                );
                d.setId(rs.getInt("id"));
                lista.add(d);
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al obtener detalles: "
                            + e.getMessage(), "obtenerDetalles");
        }
        return lista;
    }

    //Metodo para obtener el total de ventas del día
    public static double totalDelDia() {
        String sql = "SELECT ISNULL(SUM(total), 0) "
                   + "FROM Ventas "
                   + "WHERE CAST(fecha AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            throw new ErrorBD("Error al calcular total del día: "
                            + e.getMessage(), "totalDelDia");
        }
        return 0;
    }

    //Metodo para obtener Productos más vendidos 
    public static List<Object[]> productosMasVendidos(int top) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT TOP (?) P.nombre, "
                   + "SUM(D.cantidad) AS total_vendido, "
                   + "SUM(D.subtotal) AS ingresos "
                   + "FROM DetalleVentas D "
                   + "JOIN Productos P ON D.id_producto = P.id "
                   + "GROUP BY P.nombre "
                   + "ORDER BY total_vendido DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, top);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("total_vendido"),
                    rs.getDouble("ingresos")
                });
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al obtener productos más vendidos: "
                            + e.getMessage(), "productosMasVendidos");
        }
        return lista;
    }

    //Metodo para obtener ventas por tipo de pago
    public static List<Object[]> ventasTipoPago() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT tipo_pago, "
                   + "COUNT(*) AS cantidad, "
                   + "SUM(total) AS total "
                   + "FROM Ventas "
                   + "GROUP BY tipo_pago "
                   + "ORDER BY total DESC";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("tipo_pago"),
                    rs.getInt("cantidad"),
                    rs.getDouble("total")
                });
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al obtener ventas por tipo de pago: "
                            + e.getMessage(), "ventasPorTipoPago");
        }
        return lista;
    }

    //Metodo para contar ventas del día 
    public static int ventasDelDia() {
        String sql = "SELECT COUNT(*) FROM Ventas "
                   + "WHERE CAST(fecha AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            throw new ErrorBD("Error al contar ventas: "
                            + e.getMessage(), "cantidadVentasDelDia");
        }
        return 0;
    }
}
