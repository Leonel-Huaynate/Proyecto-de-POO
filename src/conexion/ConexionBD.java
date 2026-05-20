
package conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {
    
    private static final String SERVIDOR   = "localhost";
    private static final String PUERTO     = "50786";
    private static final String BASE_DATOS = "minimarket_favorita";
    private static final String USUARIO    = "sa";
    private static final String CONTRASENA = "leo2007";

    public static Connection obtenerConexion() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String url = "jdbc:sqlserver://" + SERVIDOR + ":" + PUERTO
                       + ";databaseName=" + BASE_DATOS
                       + ";encrypt=false"
                       + ";trustServerCertificate=true";

            // usuario y contraseña van aquí, NO dentro de la URL
            conn = DriverManager.getConnection(url, USUARIO, CONTRASENA);

        } catch (ClassNotFoundException e) {
            System.out.println("[ConexionBD] Driver no encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ConexionBD] Error al conectar: " + e.getMessage());
        }
        return conn;
    }
}
