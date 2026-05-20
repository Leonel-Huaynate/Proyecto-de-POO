
package utilidades;

import javax.swing.JOptionPane;

//Clase de utilidad para mostrar mensajes al usuario
//Centraliza todos los JOption a esta clase para cuando quieras modificar algun
//JOption vienes a esta clase y lo cammbias

public class Mensajes {
    
    //Metodo para mostrar un mensaje de exito cuando algo salio bien
    public static void exito(String mensaje) {
        JOptionPane.showMessageDialog(null,
            mensaje,
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Metodo para mostrar un mensaje de error cuando algo no sale bien
    public static void error(String mensaje) {
        JOptionPane.showMessageDialog(null,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    //Metodo para mostrar mensaje de advertencia
    public static void advertencia(String mensaje) {
        JOptionPane.showMessageDialog(null,
            mensaje,
            "Advertencia",
            JOptionPane.WARNING_MESSAGE);
    }
    
    //Metodo para mostrar un mensaje de confirmacion con botones "SI" o "NO"
    //retorna true si el usuario marco "SI"
    public static boolean confirmar(String mensaje){
        int respuesta=JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return respuesta==JOptionPane.YES_OPTION;
    }
    
    //Metodo para mostrar un mensaje de informacion
    public static void informacion(String mensaje) {
        JOptionPane.showMessageDialog(null,
            mensaje,
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
