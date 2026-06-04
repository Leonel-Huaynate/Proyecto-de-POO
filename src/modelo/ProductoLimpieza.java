
package modelo;


public class ProductoLimpieza extends ProductoBase{
    
    private boolean esInflamable;
    
    //Constructor vacio
    public ProductoLimpieza(){
        super();
    }
    
    //Constructor con parametros
    public ProductoLimpieza(boolean esInflamable, int id, String codigo, String nombre, String categoria,String tipo, double precio, int stock, int stock_min) {
        super(id, codigo, nombre, categoria,"LIMPIEZA", precio, stock, stock_min);
        this.esInflamable = esInflamable;
    }
    
    //Getter y Setter propio
    public boolean isEsInflamable() {
        return esInflamable;
    }
    public void setEsInflamable(boolean esInflamable) {
        this.esInflamable = esInflamable;
    }

    @Override
    public String getTipo() { return "LIMPIEZA"; }
    
    //Metodos abstractos
    
    @Override
    public boolean nesecitaRefri() {
        return false;
    }

    @Override
    public String getIcono() {
        return esInflamable ? "⚠️" : "🧴";
    }
    
}
