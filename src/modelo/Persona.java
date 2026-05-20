
package modelo;


public class Persona {
    
    //Atributos que van a herederar sus clases Hijas
    private String nombres;
    private String apellidos;
    
    //Consructor vacio
    public Persona(){};
    
    //Constructor con parametros
    public Persona(String nombres,String apellidos){
        this.nombres=nombres;
        this.apellidos=apellidos;
    }

    //Getters y Setters
    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
    //Metodos que pueden herederar sus clases hijas
    
    //Para retornar el nombre completo:
    public String nombreCompleto(){
        return nombres+" "+apellidos;
    }
    
    //Para retornar un saludo dependiendo de que si termina en "a" o en otra letra:
     public String getSaludo() {
        String saludo = nombres.trim().toLowerCase().endsWith("a")
                        ? "Bienvenida" : "Bienvenido";
        return saludo + ", " + nombreCompleto();
    }
    
}
