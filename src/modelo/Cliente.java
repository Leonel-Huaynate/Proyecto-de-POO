
package modelo;

public class Cliente extends Persona{
    
    //Aributos propios de la clase Usuario
    private int id;
    private String dni;
    
    //Constructor vacio
    public Cliente(){
        super();
    }
    
    //Constructor con parametros que lee desde la BD
    public Cliente(int id, String dni, String nombres, String apellidos) {
        super(nombres, apellidos);
        this.id = id;
        this.dni = dni;
    }
    
    //Contructor cuando el cajero registra los datos del cliente para emitir la boleta
    public Cliente(String nombres, String apellidos, String dni) {
        super(nombres, apellidos);
        this.dni = dni;
    }
    
    //Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    //Metodo para validar si el DNI tiene solo 8 caracteres
    public boolean validarDNI(){
        return dni!= null && dni.matches("\\d{8}");
    }
    
    //Metodo para retornar los datos del cliente formateados para la boleta
    public String getDatosParaBoleta() {
        return nombreCompleto()+ "  —  DNI: " + dni;
    }

    @Override
    public String toString() {
        return nombreCompleto()+ " (DNI: " + dni + ")";
    }
}
