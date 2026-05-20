
package utilidades;

//Clase de utilidades para validar los datos antes de registrarlos al sistema desde los formularios

public class Validaciones {
    
    //Metodo para verificar si un texto es vacio y no lo esta retorna "TRUE"
    public static boolean noVacio(String texto){
        return texto!=null && !texto.trim().isEmpty();
    }
    
    //Metodo para verificar que todos los campos obligatorios este llenos
    //El parametro (String... campos) significa que va a recibir varios Strings
    //Es como hacer un String[] campos pero ya no es nesecario crear un arreglo
    public static boolean camposLlenos(String... campos) {
        for (String campo : campos) {
            if (!noVacio(campo)) return false;
        }
        return true;
    }
    
    //Metodo para verifiacar que un DNI tenga solo 8 digitos
    public static boolean dniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }
    
    //Metodo para verificar que un texto sea un numero entero valido
    //Intenta convertirlo a un entero si se puede retorna true simo entra al catch y retorna false
    public static boolean esEntero(String texto) {
        try {
            Integer.parseInt(texto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    //Metodo para verificar que un texto sea un decimal valido
    //Intenta convertirlo a un decimal si se puede retorna true simo entra al catch y retorna false
    public static boolean esDecimal(String texto){
        try{
            Double.parseDouble(texto.trim());
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
    
    //Metodo para verificar que un numero sea mayor que cero
    public static boolean mayorCero(String texto){
        if(!esEntero(texto)) return false;  
        return Integer.parseInt(texto.trim())>0;
    }
    
    //Metodo para verificar que un precio sea mayor a cero
    public static boolean precioValido(String texto){
        if(!esDecimal(texto))return false;
        return Double.parseDouble(texto.trim())>0;
    }
}
