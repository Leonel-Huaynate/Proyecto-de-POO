
package dao;

import conexion.ConexionBD;
import modelo.Cliente;
import utilidades.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    //Metodo para listar todos los clientes
    public static List<Cliente> listarTodo(){
        List<Cliente> listaClientes=new ArrayList<>();
        String sql="SELECT id, nombres, apellidos, dni FROM Clientes "
                + "ORDER BY apellidos, nombres";
        
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            
            while(rs.next()){
                Cliente c=new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("dni")
                );
                listaClientes.add(c);
            }
        }catch(Exception e){
            throw new ErrorBD("Error al listar clientes: "
                            + e.getMessage(), "listarTodos");
        }
        return listaClientes;
    }
    
    //Metodo para buscar cliente por Nombre,Apellido o DNI
    public static List<Cliente> buscarCliente(String filtro,String texto){
        
        List<Cliente> lista=new ArrayList<>();
        String columna;
        
        switch (filtro) {
            case "DNI": columna = "dni"; break;
            case "APELLIDO": columna = "apellidos"; break;
            default: columna = "nombres"; break;      
        }
        
        String sql = "SELECT id, nombres, apellidos, dni "
                     + "FROM Clientes WHERE "+ columna + " LIKE ? "
                     + "ORDER BY apellidos,nombres ";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,"%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                Cliente c = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("dni"));
                lista.add(c);
            }
        
        }catch(Exception e){
            throw new ErrorBD("Error al buscar cliente por filtro "
                             + e.getMessage()," buscarCliente");
        }
        return lista;
    }
    
    //Metodo para insetar nuevo cliente
    public static boolean insertarCliente(Cliente c){
        String sql="INSERT INTO Clientes (nombres, apellidos, dni) "
                + "VALUES (?,?,?)";
        
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,c.getNombres());
            ps.setString(2,c.getApellidos());
            ps.setString(3,c.getDni());
            
            return ps.executeUpdate()>0;
           
        }catch(Exception e){
            throw new ErrorBD("Error al insertar cliente "
                             + e.getMessage()," insertarCliente");
        }
    }
    
    //Metodo para actualizar cliente
    public static boolean actualizarCliente(Cliente c){
        String sql="UPDATE Clientes "
                + "SET nombres = ?, apellidos = ?, dni = ? "
                + "WHERE id = ? ";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,c.getNombres());
            ps.setString(2,c.getApellidos());
            ps.setString(3,c.getDni());
            ps.setInt(4,c.getId());
            
            return ps.executeUpdate() > 0;
        
        }catch(Exception e){
            throw new ErrorBD("Error al actualizar cliente "
                             + e.getMessage()," actualizarCliente");
        }
    }
   
    //Metodo para eliminar cliente
    public static boolean eliminarCliente(int id){
        String sql="DELETE FROM Clientes WHERE id = ? ";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setInt(1, id);
            
            return ps.executeUpdate()>0;
        
        }catch(Exception e){
            throw new ErrorBD("Error al eliminar cliente "
                             + e.getMessage()," eliminarCliente");
        }
    }
    
    //Metodo para verificar si ya existe un DNI en la BD (Creacion de un Cliente)
    public static boolean existeDNI(String dni){
        String sql = "SELECT COUNT(*) FROM Clientes WHERE dni = ? ";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,dni);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        
        }catch(Exception e){
            throw new ErrorBD("Error al verificar DNI: "
                              + e.getMessage(),"existeDNI");
        }
        return false;
    }
    
    //Metodo para verificar DNI para otro cliente igual (EDITAR)
    public static boolean existeDniEditar(String dni, int id){
        String sql = "SELECT COUNT(*) FROM Clientes "
                    + "WHERE dni = ? AND id != ?";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,dni);
            ps.setInt(2,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;
            }
        
        }catch(Exception e){
            throw new ErrorBD("Error al verificar DNI: "
                              + e.getMessage(),"existeDniEditar");
        }
        return false;
    }
    
    
    // Metodo para Buscar cliente por DNI exacto 
    // Se usa en FormVentas para verificar si el cliente ya está registrado antes de una venta
    public static Cliente buscarPorDNI(String dni){
        String sql = "SELECT id, nombres, apellidos, dni FROM Clientes "
                    + "WHERE dni = ? ";
        try(Connection con = ConexionBD.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,dni);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("dni")
                );
            }
        
        }catch(Exception e){
            throw new ErrorBD("Error al buscar DNI: "
                              + e.getMessage(),"buscarPorDNI");
        }
        return null;
    }
    
}
