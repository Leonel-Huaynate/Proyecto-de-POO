
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
        
        String sql="SELECT id,rol,usuario,contraseña FROM Usuarios"
                + " WHERE usuario=? AND contraseña=? AND activo=1";
        
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
    
    //Metodo para insertar un nuevo usuario a la BD
    //retorna true si se inserto correctamente
    
    //Metodo para actualizar los datos de un usuario ya existente
    //retorna true si se actualizo correctamente
    
    //Metodo para desactivar un Usuario (no se borra)
    //retorna true si se desactivo correctamente
    
    //Metodo para reactivar un Usuario desactivado
    
    //Metodo para verificar si ya existe un nombre de usuario igual en la BD
}
