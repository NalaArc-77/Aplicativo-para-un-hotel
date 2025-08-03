package Controlador;
import Controlador.InternalFrame.ControladorInternalClientes;
import Vista.JFPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Controlador.InternalFrame.ControladorInternalHabitaciones;
import Vista.InternalHabitaciones;
import Vista.InternalClientes;
import Vista.InternalReservas;
import Vista.InternalFacturacion;
import Vista.InternalReporteFacturas;
import Controlador.InternalFrame.ControladorInternalReservas;
import Controlador.InternalFrame.ControladorInternalFacturacion;
import Controlador.InternalFrame.ControladorInternalReporteFacturas;
import javax.swing.JFrame;

public class ControladorJFPrincipal implements ActionListener{
    JFPrincipal frPrincipal;
    
    InternalHabitaciones intHabitaciones;
    InternalClientes intClientes;
    InternalReservas intReservas;
    InternalFacturacion intFacturacion;
    InternalReporteFacturas intRepoFact;
    
    ControladorInternalHabitaciones intControlHabitaciones;
    ControladorInternalClientes intControlClientes;
    ControladorInternalReservas intControlReservas;
    ControladorInternalFacturacion intControlFacturacion;
    ControladorInternalReporteFacturas intControlRepoFact;
    
    
    
    public ControladorJFPrincipal(JFPrincipal frP){
        frPrincipal = frP;
        
        frPrincipal.setTitle("Hotel: Eco distante del horizonte");
        frPrincipal.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frPrincipal.setLocationRelativeTo(null);
        frPrincipal.setVisible(true);
       
        frPrincipal.itemGestionHabitaciones.addActionListener(this);
        frPrincipal.itemClientes.addActionListener(this);
        frPrincipal.itemReservas.addActionListener(this);
        frPrincipal.itemFacturacion.addActionListener(this);
        frPrincipal.itemReporteFacturas.addActionListener(this);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == frPrincipal.itemGestionHabitaciones) {
            intHabitaciones = new InternalHabitaciones();
            intControlHabitaciones = new ControladorInternalHabitaciones(intHabitaciones);
            frPrincipal.pContenedor.add(intHabitaciones);
        }
        if (e.getSource() == frPrincipal.itemClientes) {
            intClientes = new InternalClientes();
            intControlClientes = new ControladorInternalClientes(intClientes);
            frPrincipal.pContenedor.add(intClientes);
        }
        if(e.getSource() == frPrincipal.itemReservas){
            intReservas = new InternalReservas();
            intControlReservas = new ControladorInternalReservas(intReservas);
            frPrincipal.pContenedor.add(intReservas);
        }
        if (e.getSource() == frPrincipal.itemFacturacion) {
            intFacturacion = new InternalFacturacion();
            intControlFacturacion = new ControladorInternalFacturacion(intFacturacion);
            frPrincipal.pContenedor.add(intFacturacion);
        }
        if(e.getSource() == frPrincipal.itemReporteFacturas){
            intRepoFact = new InternalReporteFacturas();
            intControlRepoFact = new ControladorInternalReporteFacturas(intRepoFact);
            frPrincipal.pContenedor.add(intRepoFact);
        }
    }
}
