
package modelo;

 //Clase general que hereda de ProductoBase.
 //Se usa cuando no se necesita especificar el tipo exacto
 //del producto (Alimento, Bebida o Limpieza).

public class Producto extends ProductoBase{
    
    public Producto(){
        super();
    }

    public Producto(int id, String codigo, String nombre, String categoria,String tipo, double precio, int stock, int stock_min) {
        super(id, codigo, nombre, categoria,"GENERAL", precio, stock, stock_min);
    }

    @Override
    public String getTipo() { return "GENERAL"; }

    @Override
    public boolean nesecitaRefri() {
        return false;
    }

    @Override
    public String getIcono() {
        return "📦";
    } 
}
