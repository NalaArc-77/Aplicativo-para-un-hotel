package Controlador.InternalFrame;

import Modelo.Habitacion;
import Vista.InternalHabitaciones;
import java.sql.DriverManager;
import java.sql.Connection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class ControladorInternalHabitaciones implements ActionListener {

    InternalHabitaciones Habitaciones;
    DefaultTableModel modeloTabla;
    Habitacion habitacion;

    public ControladorInternalHabitaciones() {
    }

    public ControladorInternalHabitaciones(InternalHabitaciones intHabitaciones) {
        Habitaciones = intHabitaciones;

        Habitaciones.setTitle("Gestión de habitaciones");
        Habitaciones.setVisible(true);
        Habitaciones.setResizable(false);

        Habitaciones.btnAgregar.addActionListener(this);
        Habitaciones.btnEditar.addActionListener(this);
        Habitaciones.btnEliminar.addActionListener(this);
        Habitaciones.btnEstado.addActionListener(this);

        //String 아거일 =이거일";
        String[] columnas = {"Id", "Número habitación.", "Tipo habitación", "Precio noche", "Estado", "Número de piso"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Habitaciones.tblHabitaciones.setModel(modeloTabla);
        cargarHabitacionesDesdeBD();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Guardar habitación
        if (e.getSource() == Habitaciones.btnAgregar) {
            String estado = "Activo";

            String numHabitacion = String.valueOf(Habitaciones.spnNumHabitaciones.getValue());
            String tipoHabitacion = (String) Habitaciones.cbxTipoHabitacion.getSelectedItem();
            double precioNoche = Double.parseDouble(String.valueOf(Habitaciones.spnPrecioXNoche.getValue()));
            int numPiso = (int) Habitaciones.spnNumPiso.getValue();

            if (numHabitacion.equals("0") || tipoHabitacion.equals("--Seleccione--") || precioNoche == 0 || numPiso == 0) {
                JOptionPane.showMessageDialog(Habitaciones, "Los campos tienen que ser diferentes de cero, asegúrese de llenarlos bien.");
                return;

            } else {
                habitacion = new Habitacion(numHabitacion, tipoHabitacion, precioNoche, estado, numPiso);
            }

            try {
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");

                String sql = "INSERT INTO habitacion (numero, tipo, precioXNoche, estado, piso) VALUES (?,?,?,?,?)";
                PreparedStatement statement = conexion.prepareStatement(sql);
                statement.setString(1, numHabitacion);
                statement.setString(2, tipoHabitacion);
                statement.setDouble(3, precioNoche);
                statement.setString(4, estado);
                statement.setInt(5, numPiso);

                int resultado = statement.executeUpdate();
                if (resultado > 0) {
                    System.out.println("Cliente guardado en la base de datos.");
                    cargarHabitacionesDesdeBD();
                    limpiarCampos();

                }

                conexion.close();

            } catch (SQLException ex) {
                JOptionPane.showConfirmDialog(Habitaciones, "No se pudo conectar a la base de datos. Error: " + ex);
            }

        }
        //Editar habitación
        if (e.getSource() == Habitaciones.btnEditar) {
            int fila = Habitaciones.tblHabitaciones.getSelectedRow();
            if (fila >= 0) {
                try {
                    int idHabitacion = (int) modeloTabla.getValueAt(fila, 0);

                    String nuevoNum = String.valueOf(Habitaciones.spnNumHabitaciones.getValue());
                    String nuevoTipo = (String) Habitaciones.cbxTipoHabitacion.getSelectedItem();
                    double nuevoPrecio = Double.parseDouble(String.valueOf(Habitaciones.spnPrecioXNoche.getValue()));
                    int nuevoPiso = (int) Habitaciones.spnNumPiso.getValue();

                    if (nuevoPiso <= 0 || nuevoNum.equals("0") || nuevoPrecio <= 0 || nuevoTipo.equals("--Seleccione--")) {
                        JOptionPane.showMessageDialog(Habitaciones, "Complete todos los campos para editar.");
                        return;
                    }

                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                    String sql = "UPDATE habitacion SET numero=?, tipo=?, precioXNoche=?, piso=? WHERE idHabitacion=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, nuevoNum);
                    ps.setString(2, nuevoTipo);
                    ps.setDouble(3, nuevoPrecio);
                    ps.setInt(4, nuevoPiso);
                    ps.setInt(5, idHabitacion);

                    int filasAfectadas = ps.executeUpdate();
                    if (filasAfectadas > 0) {
                        modeloTabla.setValueAt(nuevoNum, fila, 1);
                        modeloTabla.setValueAt(nuevoTipo, fila, 2);
                        modeloTabla.setValueAt(nuevoPrecio, fila, 3);
                        modeloTabla.setValueAt(nuevoPiso, fila, 5);

                        Habitaciones.tblHabitaciones.clearSelection();
                        limpiarCampos();
                        JOptionPane.showMessageDialog(Habitaciones, "Datos de la habitación actualizados correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(Habitaciones, "Error al actualizar en la base de datos.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Habitaciones, "Error al actualizar: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(Habitaciones, "Seleccione una fila para editar, luego llene los campos y presione EDITAR.");
            }

        }
        //Eliminar habitación
        if (e.getSource() == Habitaciones.btnEliminar) {
            int filaSeleccionada = Habitaciones.tblHabitaciones.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int opcion = JOptionPane.showConfirmDialog(Habitaciones,
                        "Está a punto de eliminar una habitación, ¿Desea continuar?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    try {
                        int idHabitacion = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

                        // ELIMINANDING DE LA BD
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                        String sql = "DELETE FROM habitacion WHERE idHabitacion = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setInt(1, idHabitacion);

                        int filasAfectadas = ps.executeUpdate();
                        if (filasAfectadas > 0) {
                            modeloTabla.removeRow(filaSeleccionada);
                            Habitaciones.tblHabitaciones.clearSelection();
                            JOptionPane.showMessageDialog(Habitaciones, "Habitación eliminada correctamente.");

                        } else {
                            JOptionPane.showMessageDialog(Habitaciones, "No se pudo eliminar la habitación de la base de datos.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Habitaciones, "Error al eliminar: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Habitaciones, "Seleccione una fila para eliminar.");
            }
        }
        //Cambiar estado
        if (e.getSource() == Habitaciones.btnEstado) {
            int filaSeleccionada = Habitaciones.tblHabitaciones.getSelectedRow();
            if (filaSeleccionada != -1) {
                String estado = Habitaciones.tblHabitaciones.getValueAt(filaSeleccionada, 4).toString();
                int idHabitacion = Integer.parseInt(Habitaciones.tblHabitaciones.getValueAt(filaSeleccionada, 0).toString());

                String nuevoEstado = estado.equals("Activo") ? "Inactivo" : "Activo";

                try {
                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                    String sql = "UPDATE habitacion SET estado = ? WHERE idHabitacion = ?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, nuevoEstado);
                    ps.setInt(2, idHabitacion);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(Habitaciones, "Estado actualizado correctamente.");
                    cargarHabitacionesDesdeBD();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Habitaciones, "No se pudo cambiar el estado... Error: " + ex);
                }
            } else {
                JOptionPane.showMessageDialog(Habitaciones, "Selecciona una fila primero.");
            }
        }
    }

    private void limpiarCampos() {
        Habitaciones.spnNumHabitaciones.setValue(0);
        Habitaciones.spnNumPiso.setValue(0);
        Habitaciones.spnPrecioXNoche.setValue(0);
        Habitaciones.cbxTipoHabitacion.setSelectedIndex(0);
    }

    protected void cargarHabitacionesDesdeBD() {
        modeloTabla.setRowCount(0);
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
            String sql = "SELECT * FROM habitacion";
            PreparedStatement statement = conexion.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idHabitacion"),
                    rs.getString("numero"),
                    rs.getString("tipo"),
                    rs.getDouble("precioXNoche"),
                    rs.getString("estado"),
                    rs.getInt("piso")
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Habitaciones, "No se pudo recuperar los datos!!! Error: " + ex);
        }
    }
}
