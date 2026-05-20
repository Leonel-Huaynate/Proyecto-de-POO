
package modelo;


public class DetalleVenta {
    
    private int id;
    private int idVenta;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subTotal;
    
    //Constructor Vacio
    public DetalleVenta(){}

    //Constructor con parametros nesecario para cunado el cajero agrega al carrito de la venta actual
    public DetalleVenta(int idProducto, String nombreProducto, int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotal=precioUnitario*cantidad;
    }
    
    //Getters y Setters 
    public int    getId()                     { return id; }
    public void   setId(int id)               { this.id = id;}

    public int    getIdVenta()                { return idVenta; }
    public void   setIdVenta(int idVenta)     { this.idVenta = idVenta;}

    public int    getIdProducto()             { return idProducto; }
    public void   setIdProducto(int idProducto)  { this.idProducto = idProducto;}

    public String getNombreProducto()         { return nombreProducto;}
    public void   setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto;}

    public int    getCantidad()               { return cantidad; }

 
    //Al cambiar la cantidad se recalcula el subtotal
    //automáticamente — no hay que hacerlo manualmente.
    public void   setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subTotal = cantidad * precioUnitario;
    }

    public double getPrecioUnitario()             { return precioUnitario; }

    //Al cambiar el precio también se recalcula el subtotal.
    public void   setPrecioUnitario(double precioUni) {
        this.precioUnitario = precioUni;
        this.subTotal   = cantidad * precioUni;
    }

    // El subtotal no tiene setter — solo se calcula internamente
    public double getSubtotal()               { return subTotal; }
    
    //Metodos para mostrar los precios formateados
    public String getPrecioFormateado()   { return String.format("S/ %.2f", precioUnitario); }
    public String getSubtotalFormateado() { return String.format("S/ %.2f", subTotal); }

    @Override
    public String toString() {
        return nombreProducto + " x" + cantidad
             + " = " + getSubtotalFormateado();
    }
    
}
