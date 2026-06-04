
package modelo;


public class ProductoBebida extends ProductoBase{
    
    private boolean esAlcoholica;
    
    //Constructor vacio
    public ProductoBebida(){
        super();
    }

    //Constructor con parametros
    public ProductoBebida(boolean esAlcoholica, int id, String codigo, String nombre, String categoria,String tipo, double precio, int stock, int stock_min) {
        super(id, codigo, nombre, categoria,"BEBIDA", precio, stock, stock_min);
        this.esAlcoholica = esAlcoholica;
    }
    
    //Getter y Setter propio
    public boolean isEsAlcoholica() {
        return esAlcoholica;
    }
    public void setEsAlcoholica(boolean esAlcoholica) {
        this.esAlcoholica = esAlcoholica;
    }

    @Override
    public String getTipo() { return "BEBIDA"; }
    
    //Metodos abstractos:
    
    //Retorna si nesecita Refrigeracion en este caso retorna false ya que las bebias se van a vender a temperatura 
    //ambiente en el minimarket
    @Override
    public boolean nesecitaRefri() {
        return false;
    }

    @Override
     public String getIcono() {
        return esAlcoholica ? "🍺" : "🥤";
    }
}
