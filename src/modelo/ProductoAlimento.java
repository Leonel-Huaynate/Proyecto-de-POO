
package modelo;

public class ProductoAlimento extends ProductoBase{
    
    private boolean perecedero;
    
    //Constructor vacio
    public ProductoAlimento(){
        super();
    }
    
    //Constructor con parametros
    public ProductoAlimento(boolean perecedero, int id, String codigo, String nombre, String categoria, double precio, int stock, int stock_min) {
        super(id, codigo, nombre, categoria, precio, stock, stock_min);
        this.perecedero = perecedero;
    }

    //Getter y Setter propio
    public boolean isPerecedero() {
        return perecedero;
    }
    public void setPerecedero(boolean perecedero) {
        this.perecedero = perecedero;
    }

    
    //Metodos abstractos:
    
    //Retorna el tipo producto (ALIMENTO)
    @Override
    public String Tipo() {
        return "ALIMENTO";
    }

    //Retorna si nesecita refrigeracion si es un aliemnto perecedero
    @Override
    public boolean nesecitaRefri() {
        return perecedero;
    }

    //Icono para mostrar en la tabla del inventario
    @Override
    public String getIcono() {
        return perecedero ? "🥛" : "🍞";
    }
    
    
    
}
