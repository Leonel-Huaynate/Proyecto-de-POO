
package modelo;

public class Cliente extends Persona{
    
    //Aributos propios de la clase Usuario
    private int id;
    
    //Constructor vacio
    public Cliente(){
        super();
    }
    
    //Constructor con parametros que lee desde la BD
    public Cliente(int id, String dni, String nombres, String apellidos) {
        super(nombres, apellidos,dni);
        this.id = id;
    }
    
    //Contructor cuando el cajero registra los datos del cliente para emitir la boleta
    public Cliente(String nombres, String apellidos, String dni) {
        super(nombres, apellidos,dni);
    }
    
    //Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    //Metodo para retornar los datos del cliente formateados para la boleta
    public String getDatosParaBoleta() {
        return nombreCompleto()+ "  —  DNI: " +getDni();
    }

    @Override
    public String toString() {
        return nombreCompleto()+ " (DNI: " + getDni() + ")";
    }
}
