
package modelo;


public class Usuario extends Persona{
    
    //Atributos propios de la clase Usuario
    private int id;
    private String usuario;
    private String contraseña;
    private String rol;
    private boolean activo;
    
    //Constructor vacio
    public Usuario(){
        super();
    }

    //Constructor con parametros que lee desde la BD (sin contraseña)
    public Usuario(int id, String usuario,String rol, String nombres, String apellidos,String dni) {
        super(nombres, apellidos,dni);
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
        this.activo = true;
    }
    
    //Este constructor es para cunado creamos un nuevo Usuario
    public Usuario(String usuario, String contraseña, String rol, String nombres, String apellidos,String dni) {
        super(nombres, apellidos,dni);
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.rol = rol;
        this.activo = true;
    }

    //Getters y Setters:
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getContraseña() {
        return contraseña;
    }
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    //Metodos propios de la clase Usuario (valida que tipo de rol tiene)
    public boolean esSuperAdmin(){
        return "SUPERADMIN".equalsIgnoreCase(rol);
    }
    public boolean esAdmin(){
        return "ADMIN".equalsIgnoreCase(rol) || esSuperAdmin();
    }
    public boolean esCajero(){
        return "CAJERO".equalsIgnoreCase(rol);
    }
    
    @Override
    public String toString() {
        return "Usuario{id=" + id
             + ", usuario=" + usuario
             + ", rol=" + rol
             + ", nombre=" + nombreCompleto()+ "}";
    }
}
