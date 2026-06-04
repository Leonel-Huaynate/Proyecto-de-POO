
package dao;

import conexion.ConexionBD;
import modelo.Usuario;
import java.sql.*;
import utilidades.ErrorBD;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    //Metodo para validar las credenciales en el login
    //Retorna el Usuario autenticado o null si es incorrecto
    public static Usuario login(String usuario,String contraseña){
        
        String sql="SELECT id,rol,usuario,contraseña,nombres,apellidos FROM Usuarios "
                + "WHERE usuario=? AND contraseña=? AND activo=1";
        
        try(Connection con=ConexionBD.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setString(1,usuario);
            ps.setString(2,contraseña);
            ResultSet rs=ps.executeQuery();
            
            if(rs.next()){
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("rol"),
                        rs.getString("nombres"),
                        rs.getString("apellidos")
                        );
            }
        }catch(Exception  e){
            throw new ErrorBD("Error al iniciar sesion: "+e.getMessage()," login");
        }
    return null;
    }
    
    //Metodo para retornar todos los usuarios para llenar la tabla en el FormUsuarios
    public static List<Usuario> listarTodos() {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT id, usuario, rol, nombres, apellidos, activo "
               + "FROM Usuarios ORDER BY apellidos, nombres";
    try (Connection conn = ConexionBD.obtenerConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Usuario u = new Usuario(
                rs.getInt("id"),
                rs.getString("usuario"),
                rs.getString("rol"),
                rs.getString("nombres"),
                rs.getString("apellidos")
                );
            u.setActivo(rs.getBoolean("activo"));
            lista.add(u);
            }
        } catch (Exception e) {
        throw new ErrorBD("Error al listar usuarios: "
                        + e.getMessage(), "listarTodos");
        }
        return lista;
    }
    //Metodo para insertar un nuevo usuario a la BD
    //retorna true si se inserto correctamente
    public static boolean insertarUsuario(Usuario u){
        String sql="INSERT INTO Usuarios (usuario,contraseña,rol,nombres,apellidos) "
                + "VALUES (?,?,?,?,?)";
        
        try(Connection con=ConexionBD.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setString(1,u.getUsuario());
            ps.setString(2,u.getContraseña());
            ps.setString(3,u.getRol());
            ps.setString(4,u.getNombres());
            ps.setString(5,u.getApellidos());
            
            return ps.executeUpdate()>0;    
        
        }catch(Exception e){
            throw new ErrorBD("Error al insertar usuario: "+e.getMessage(), "insertar");
        }
    }
    
    //Metodo para actualizar los datos de un usuario ya existente
    //retorna true si se actualizo correctamente
    public static boolean actualizarDatos(Usuario u){
        String sql;
        
        //Si la contraseña esta vacia no va a actualizar ese campo
        if (u.getContraseña() == null || u.getContraseña().isEmpty()) {
                sql = "UPDATE Usuarios "
                + "SET usuario = ?, rol = ?, nombres = ?, apellidos = ? "
                + "WHERE id = ?";
        
            try(Connection con=ConexionBD.obtenerConexion();
                PreparedStatement ps=con.prepareStatement(sql)){
            
                ps.setString(1,u.getUsuario());
                ps.setString(2,u.getRol());
                ps.setString(3,u.getNombres());
                ps.setString(4,u.getApellidos());
                ps.setInt(5,u.getId());
            
                return ps.executeUpdate()>0;
        
                }catch(Exception e){
                        throw new ErrorBD("Error al actualizar los datos: "+e.getMessage(),"actualizar");
                        }
        }
        else{
                // Si hay contraseña nueva sí la actualiza (contraseña)
                sql = "UPDATE Usuarios "
                + "SET usuario = ?, contraseña = ?, "
                + "rol = ?, nombres = ?, apellidos = ? "
                + "WHERE id = ?";

                try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, u.getUsuario());
                    ps.setString(2, u.getContraseña());
                    ps.setString(3, u.getRol());
                    ps.setString(4, u.getNombres());
                    ps.setString(5, u.getApellidos());
                    ps.setInt(6, u.getId());
                    
                    return ps.executeUpdate() > 0;

                } catch (Exception e) {
                        throw new ErrorBD("Error al actualizar: "
                            + e.getMessage(), "actualizar");
                }   
        }
    }
    
    //Metodo para desactivar un Usuario (no se borra)
    //retorna true si se desactivo correctamente
    public static boolean desactivarUsuario(int id){
        String sql="UPDATE Usuarios SET activo = 0 "
                + "WHERE id=?";
        
        try(Connection con=ConexionBD.obtenerConexion();
                PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setInt(1,id);
            return ps.executeUpdate()>0;
            
        }catch(Exception e){
            throw new ErrorBD("Error al desactivar usuario: "+e.getMessage(),"desactivar");
        }
    }
    
    //Metodo para reactivar un Usuario desactivado
    public static boolean activarUsuario(int id){
        String sql="UPDATE Usuarios SET activo = 1 "
                + "WHERE id=?";
        
        try(Connection con=ConexionBD.obtenerConexion();
                PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setInt(1,id);
            return ps.executeUpdate()>0;
            
        }catch(Exception e){
            throw new ErrorBD("Error al activar al usuario: "+e.getMessage(),"activar");
        }
    }
    
    //Metodo para verificar si ya existe un nombre de usuario igual en la BD
    public static boolean existeUsuario(String usuario){
        String sql="SELECT COUNT(*) FROM Usuarios WHERE usuario = ?";
        
        try(Connection con=ConexionBD.obtenerConexion();
                PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setString(1,usuario);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1)>0;
            }
            
        }catch(Exception e){
            throw new ErrorBD("Error al verificar usuario: "
                        + e.getMessage(), "existeUsuario");

        }
        return false;
    }
    
    public static boolean eliminarUsuario(int id){
        String sql="DELETE Usuarios WHERE id = ? ";
        try(Connection con=ConexionBD.obtenerConexion();
            PreparedStatement ps=con.prepareStatement(sql)){
            
            ps.setInt(1, id);
            
            return ps.executeUpdate()>0;
        
        }catch(Exception e){
            throw new ErrorBD("Error al eliminar usuario: "
                              + e.getMessage(), "eliminarUsuario");
        }
    }
}
