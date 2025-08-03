package Controlador;

import Vista.JFInicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Vista.JFPrincipal;
import Controlador.ControladorJFPrincipal;
import javax.swing.JOptionPane;

public class ControladorJFInicio implements ActionListener {

    JFInicio frInicio;
    JFPrincipal frPrincipal;
    ControladorJFPrincipal controlPrincipal;

    public ControladorJFInicio(JFInicio fi) {
        frInicio = fi;

        frInicio.setTitle("Inicio");
        frInicio.setVisible(true);
        frInicio.setLocationRelativeTo(null);
        frInicio.btnIngresar.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usuario = (String) frInicio.cbxUsuario.getSelectedItem();
        String contrasenia = new String(frInicio.pwContrasenia.getPassword());

        if (e.getSource() == frInicio.btnIngresar) {
            if (!usuario.equals("---SELECCIONE---") && contrasenia.equals("1234")) {
                frInicio.setVisible(false);

                frPrincipal = new JFPrincipal();
                controlPrincipal = new ControladorJFPrincipal(frPrincipal);
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un usuario o escriba bien la contraseña!!!");
            }

        }
    }
}
