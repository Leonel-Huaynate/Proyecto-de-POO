
package dao;

import conexion.ConexionBD;
import modelo.Producto;
import utilidades.ErrorBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.ProductoAlimento;
import modelo.ProductoBase;
import modelo.ProductoBebida;
import modelo.ProductoLimpieza;


//DAO de Productos esta clase va a centralizar todo el SQL con la tabla Productos
public class ProductoDAO {
    
    //Metodo para listar todos los Productos
     public static List<ProductoBase> listarProductos() {
        List<ProductoBase> listaProductos = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, categoria, tipo, "
                   + "precio, stock, stock_min, activo "
                   + "FROM Productos ORDER BY nombre";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                ProductoBase p;
                switch (tipo) {
                    case "ALIMENTO": p = new ProductoAlimento(); break;
                    case "BEBIDA":   p = new ProductoBebida();   break;
                    case "LIMPIEZA": p = new ProductoLimpieza(); break;
                    default:         p = new Producto();         break;
            }
            p.setId(rs.getInt("id"));
            p.setCodigo(rs.getString("codigo"));
            p.setNombre(rs.getString("nombre"));
            p.setCategoria(rs.getString("categoria"));
            p.setTipo(rs.getString("tipo"));
            p.setPrecio(rs.getDouble("precio"));
            p.setStock(rs.getInt("stock"));
            p.setStock_min(rs.getInt("stock_min"));
            p.setActivo(rs.getBoolean("activo"));
            listaProductos.add(p);
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al listar productos: "
                            + e.getMessage(), "listarTodos");
        }
        return listaProductos;
    }
     
     //Metodo para buscar Producto por filtro
     public static List<ProductoBase> buscarProducto(String filtro, String texto){
         List<ProductoBase> listaProductos = new ArrayList<>();
         String columna;
         
         switch (filtro) {
             case "CODIGO": columna = "codigo"; break;
             case "CATEGORIA": columna = "categoria"; break;
             case "TIPO": columna = "tipo"; break;
             default : columna = "nombre"; break;
         }
         
         String sql="SELECT id, codigo, nombre, categoria, tipo, "
                   + "precio, stock, stock_min, activo "
                   + "FROM Productos "
                 + "WHERE "+columna+" LIKE ? "
                 + "ORDER BY nombre ";
         
         try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            String tipo = rs.getString("tipo");
            ProductoBase p;
            switch (tipo) {
                case "ALIMENTO": p = new ProductoAlimento(); break;
                case "BEBIDA":   p = new ProductoBebida();   break;
                case "LIMPIEZA": p = new ProductoLimpieza(); break;
                default:         p = new Producto();         break;
            }
            p.setId(rs.getInt("id"));
            p.setCodigo(rs.getString("codigo"));
            p.setNombre(rs.getString("nombre"));
            p.setCategoria(rs.getString("categoria"));
            p.setTipo(rs.getString("tipo"));
            p.setPrecio(rs.getDouble("precio"));
            p.setStock(rs.getInt("stock"));
            p.setStock_min(rs.getInt("stock_min"));
            p.setActivo(rs.getBoolean("activo"));
            listaProductos.add(p);
            }
        } catch (Exception e) {
            throw new ErrorBD("Error al buscar productos: "
                            + e.getMessage(), "buscar");
        }
        return listaProductos;
     }
     
     //Metodo para insertar nuevo Producto
     public static boolean insertarProducto(ProductoBase p){
         String sql="INSERT INTO Productos "
                 + "(codigo, nombre, categoria, tipo, precio, stock, stock_min) "
                 + "VALUES (?,?,?,?,?,?,?)";
         
         try(Connection con=ConexionBD.obtenerConexion();
             PreparedStatement ps=con.prepareStatement(sql)){
             
             ps.setString(1,p.getCodigo());
             ps.setString(2,p.getNombre());
             ps.setString(3,p.getCategoria());
             ps.setString(4, p.getTipo());
             ps.setDouble(5,p.getPrecio());
             ps.setInt(6,p.getStock());
             ps.setInt(7,p.getStock_min());
             
             return ps.executeUpdate()>0;
             
         }catch(Exception e){
             throw new ErrorBD("Error al insertar producto: "
                            + e.getMessage(), "insertar");
         }
     }
     
     //Metodo para actualizar un Producto ya existente
     public static boolean actualizarProducto(ProductoBase p){
         String sql="UPDATE Productos "
                   + "SET codigo = ?, nombre = ?, categoria = ?, tipo = ?, "
                   + "precio = ?, stock = ?, stock_min = ?, "
                   + "activo = ? "
                   + "WHERE id = ? ";
         
         try(Connection con=ConexionBD.obtenerConexion();
             PreparedStatement ps=con.prepareStatement(sql)){   
             
             ps.setString(1,p.getCodigo());
             ps.setString(2,p.getNombre());
             ps.setString(3,p.getCategoria());
             ps.setString(4,p.getTipo());
             ps.setDouble(5,p.getPrecio());
             ps.setInt(6,p.getStock());
             ps.setInt(7,p.getStock_min());
             ps.setBoolean(8,p.isActivo());
             ps.setInt(9, p.getId());
             
             return ps.executeUpdate()>0;

         }catch(Exception e){
             throw new ErrorBD("Error al actualizar producto: "
                            + e.getMessage(), "actualizar");
         }
     }
   
     //Metodo para verificar si un codigo ya existe
     public static boolean existeCodigo(String codigo){
         String sql="SELECT COUNT(*) FROM Productos "
                 + " WHERE codigo = ? ";
         
         try(Connection con=ConexionBD.obtenerConexion();
             PreparedStatement ps=con.prepareStatement(sql)){
             
             ps.setString(1,codigo);
             ResultSet rs=ps.executeQuery();
             if(rs.next()){
                 return rs.getInt(1)>0;
             }
         
         }catch(Exception e){
            throw new ErrorBD("Error al verificar código: "
                         + e.getMessage(), "existeCodigo");
         }
         return false;
     }
     
     //Metodo para verificar el codigo para otro producto (actualizar)
      public static boolean existeCodigoActualizar(String codigo, int id) {
        String sql = "SELECT COUNT(*) FROM Productos "
                   + "WHERE codigo = ? AND id != ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return rs.getInt((1))>0;
            }

        } catch (Exception e) {
            throw new ErrorBD("Error al verificar código: "
                            + e.getMessage(), "existeCodigoParaOtro");
        }
        return false;
    }
     //Metodo para Listar los Productos solo activos 
     //Lo va a utilizar el cajero para que veo los productos disponibles
      public static List<ProductoBase> listarPrdActivos (){
          
          List<ProductoBase> lista = new ArrayList<>();
          String sql=" SELECT id, codigo, nombre, categoria, tipo, "
                   + "precio, stock, stock_min, activo "
                   + "FROM Productos WHERE activo = 1 AND stock > 0 "
                  + "ORDER BY nombre ";
          try(Connection con=ConexionBD.obtenerConexion();
              PreparedStatement ps=con.prepareStatement(sql);
              ResultSet rs=ps.executeQuery()){
              
              while(rs.next()){
                  String tipo = rs.getString("tipo");
                    ProductoBase p;
                    switch (tipo) {
                        case "ALIMENTO": p = new ProductoAlimento(); break;
                        case "BEBIDA":   p = new ProductoBebida();   break;
                        case "LIMPIEZA": p = new ProductoLimpieza(); break;
                        default:         p = new Producto();         break;
                    }
                    p.setId(rs.getInt("id"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setNombre(rs.getString("nombre"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setTipo(rs.getString("tipo"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setStock_min(rs.getInt("stock_min"));
                    p.setActivo(true);
                    lista.add(p);
              }
              
          
          }catch(Exception e){
                throw new ErrorBD("Error al listar productos activos: "
                            + e.getMessage(), "listarActivos");
          }
          return lista;
      }
     
     //Metodo para actualizar solo el stock
     //Se usa cuando el cajero realiza una venta
      public static boolean actualizarStock(int idProducto, int nuevoStock){
          
          String sql="UPDATE Productos SET stock = ? "
                  + "WHERE id = ? ";
          try(Connection con=ConexionBD.obtenerConexion();
              PreparedStatement ps=con.prepareStatement(sql)){
              
              ps.setInt(1, nuevoStock);
              ps.setInt(2, idProducto);
              
              return ps.executeUpdate()>0;
          
          }catch(Exception e){
              throw new ErrorBD("Error al actualizar el estock : "
                              + e.getMessage(), "actualizarStock ");
          }
      }
     
      //Metodo para Eliminar un Producto solo si no esta asociado a ninguna venta
      public static boolean eliminarProducto(int id){
          String sql = "DELETE FROM Productos WHERE id = ? ";
          try(Connection con=ConexionBD.obtenerConexion();
              PreparedStatement ps=con.prepareStatement(sql)){
              
              ps.setInt(1, id);
              return ps.executeUpdate()>0;
          
          }catch(Exception e){
                throw new ErrorBD("Error al eliminar producto: "
                        + e.getMessage(), "eliminar"); 
          }
      }
     

}
