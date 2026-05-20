
package utilidades;


public class ErrorBD extends RuntimeException {
    
    private String operacion;
    
    public ErrorBD(String mensaje){
        super(mensaje);
    }
    
    public ErrorBD(String mensaje,String operacion){
        super(mensaje);
        this.operacion=operacion;
    }
    
    public String getOperacion(){
        return operacion;
    }
    
   public String mensajeCompleto(){
       if(operacion!=null){
            return "("+ operacion +")" + getMessage();
       }
       return getMessage();
   }
}
