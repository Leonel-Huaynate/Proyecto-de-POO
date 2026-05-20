
package modelo;


public abstract class ProductoBase {
    
    private int id;
    private String codigo;
    private String nombre;
    private String categoria;
    private double precio;
    private int stock;
    private int stock_min;
    private boolean activo;
    
    //Cosntructor vacio
    public ProductoBase(){};

    //Constructor con parametros
    public ProductoBase(int id, String codigo, String nombre, String categoria, double precio, int stock, int stock_min) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.stock_min = stock_min;
        this.activo = true;
    }
    
    //Getters y Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getStock_min() {
        return stock_min;
    }
    public void setStock_min(int stock_min) {
        this.stock_min = stock_min;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    //Metodos con implementacion que lo van a poder heredar sus hijos
    
    //Retorna True si el stock esta por debajo del stock minimo
    public boolean tieneStockBajo(){
        return stock<stock_min;
    }
    
    //Retorna True si el producto no tiene stock
    public boolean stockAgotado(){
        return stock==0;
    }
    
    //Metodo para mostrar el precio formateado 
    public String getPrecioFormateado() {
        return String.format("S/ %.2f", precio);
    }
    
    //Metodos abstractos
    
    public abstract String Tipo();
    public abstract boolean nesecitaRefri();
    public abstract String getIcono();
    
    @Override
    public String toString() {
        return "[" + Tipo() + "] " + codigo + " - " + nombre
             + " | " + getPrecioFormateado()
             + " | Stock: " + stock;
    }
}
