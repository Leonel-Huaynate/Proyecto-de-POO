
package modelo;

import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.List;

public class Venta {
    
    private int id;
    private Timestamp fecha;
    private int idUsuario;
    private String nombreCajero;
    private Cliente cliente;
    private double total;
    private String tipoPago;
    private List<DetalleVenta> detalles;
    private String nombreCliente;
    
    //Constructor Vacio para crear la lista vacia y la fecha 
    public Venta() {
        this.detalles = new ArrayList<>();
        this.fecha    = new Timestamp(System.currentTimeMillis());
        this.tipoPago = "EFECTIVO";
        this.total    = 0;
    }
    
    //Constructor con datos el this() sirve para llamar al constructor vacio 
    public Venta(int idUsuario, String nombreCajero, String tipoPago) {
        this();
        this.idUsuario    = idUsuario;
        this.nombreCajero = nombreCajero;
        this.tipoPago     = tipoPago;
    }
    
    //Getters y Setters
    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id;}

    public Timestamp    getFecha()               { return fecha; }
    public void    setFecha(Timestamp fecha)     { this.fecha = fecha;}

    public int     getIdUsuario()           { return idUsuario; }
    public void    setIdUsuario(int idUsuario)    { this.idUsuario = idUsuario;}

    public String  getNombreCajero()        { return nombreCajero; }
    public void    setNombreCajero(String nombreCajero) { this.nombreCajero = nombreCajero;}

    public Cliente getCliente()             { return cliente; }
    public void    setCliente(Cliente c)    { this.cliente = c;}

    public double  getTotal()               { return total; }
    // El total se calcula automáticamente con calcularTotal()
    // Este setter solo se usa en el DAO al leer desde la BD
    public void setTotal(double total) {
    this.total = total;
}

    public String  getTipoPago()            { return tipoPago; }
    public void    setTipoPago(String tipoPago)    { this.tipoPago = tipoPago;}

    public List<DetalleVenta> getDetalles() { return detalles;}
    
    public String getNombreCliente()    {return nombreCliente;}
    public void setNombreCliente(String nombreCliente)  {this.nombreCliente=nombreCliente;}
    
    //Metodos del carrito
    
    //Metodo para agregar un producto al carrito y recalcula el total 
    public void agregarDetalle(DetalleVenta d){
        detalles.add(d);
        calcularTotal();
    }
    
    //Metodo que elimina un producto del carrito y recalcula el total
    public void eliminarDetalle(int indice){
        if(indice>=0 && indice<detalles.size()){
            detalles.remove(indice);
            calcularTotal();
        }
    }
    //Metodo para cacular el total con un for recorre con objeto detalleventa para acceder a su metodo getSubtotal
    //toda la lista "detalles" y lo suma al total
    public void calcularTotal(){
        total=0;
        for(DetalleVenta d:detalles){
            total +=d.getSubtotal();
        }
    }
    
    //Metodos de utilidad y formateo
    
    //Retorna el total formateado. Ejemplo: "S/ 25.50"
    public String getTotalFormateado() {
        return String.format("S/ %.2f", total);
    }
    
    ///Retorna true si el carrito tiene al menos un producto
    public boolean tieneDetalles() {
        return !detalles.isEmpty();
    }

    ///Retorna true si la venta tiene cliente registrado
    public boolean tieneCliente() {
        return cliente != null;
    }

    //Retorna el número de productos distintos en el carrito
    public int getCantidadItems() {
        return detalles.size();
    }

    @Override
    public String toString() {
        return "Venta{id=" + id
             + ", cajero=" + nombreCajero
             + ", items=" + getCantidadItems()
             + ", total=" + getTotalFormateado()
             + ", pago=" + tipoPago + "}";
    }

}
