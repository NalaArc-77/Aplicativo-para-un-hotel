package Principal;
import Vista.JFInicio;
import Controlador.ControladorJFInicio;

public class Main {
    private static JFInicio frInicio;
    private static ControladorJFInicio controlInicio;
    
    public static void main (String args[]){
        frInicio = new JFInicio();
        controlInicio = new ControladorJFInicio(frInicio);
    }
}
