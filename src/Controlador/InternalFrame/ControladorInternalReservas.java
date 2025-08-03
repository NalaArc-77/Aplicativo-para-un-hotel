package Controlador.InternalFrame;

import Modelo.Reserva;
import Vista.InternalReservas;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorInternalReservas implements ActionListener {

    InternalReservas intReservas;
    DefaultTableModel modeloTabla;
    Reserva reserva;

    public ControladorInternalReservas() {
    }
    
    public ControladorInternalReservas(InternalReservas intReser) {
        intReservas = intReser;
        intReservas.setTitle("Gestinar y crear reservas");
        intReservas.setVisible(true);

        intReservas.btnGuardar.addActionListener(this);
        intReservas.btnEditar.addActionListener(this);
        intReservas.btnEliminar.addActionListener(this);

        intReservas.spnIDCliente.addChangeListener(e -> cargarCliente());
        intReservas.spnIDHabitacion.addChangeListener(e -> cargarHabitacion());

        String[] columnas = {"Id", "Id. Cliente.", "Id. Habitación", "Fecha de ingreso", "Fecha de salida", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        intReservas.tblReservas.setModel(modeloTabla);
        cargarReservasDesdeBD();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Guardar reserva
        if (e.getSource() == intReservas.btnGuardar) {
            String estado = "Pendiente";

            int idCliente = (int) intReservas.spnIDCliente.getValue();
            int idHabitacion = (int) intReservas.spnIDHabitacion.getValue();
            Date fecha1 = intReservas.dateFechaIngreso.getDate();
            Date fecha2 = intReservas.dateFechaSalida.getDate();

            if (idCliente <= 0 || idHabitacion <= 0 || fecha1 == null || fecha2 == null) {
                JOptionPane.showMessageDialog(intReservas, "Llene todos los campos.");
                return;
            }

            if (fecha1.after(fecha2)) {
                JOptionPane.showMessageDialog(intReservas, "La fecha de ingreso no puede ser posterior a la fecha de salida.");
                return;
            }

            SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
            String fechaIngreso = formato.format(fecha1);
            String fechaSalida = formato.format(fecha2);

            reserva = new Reserva(idCliente, idHabitacion, fechaIngreso, fechaSalida, estado);

            try {
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                String sql = "INSERT INTO reserva (idCliente, idHabitacion, fechaIngreso, fechaSalida, estado) VALUES (?,?,?,?,?)";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, idCliente);
                ps.setInt(2, idHabitacion);
                ps.setDate(3, new java.sql.Date(fecha1.getTime()));
                ps.setDate(4, new java.sql.Date(fecha2.getTime()));
                ps.setString(5, estado);

                int resultado = ps.executeUpdate();
                if (resultado > 0) {
                    System.out.println("Reserva guardada en la base de datos.");
                    limpiarCampos();

                    String updateSql = "UPDATE habitacion SET estado = 'Ocupado' WHERE idHabitacion = ?";
                    PreparedStatement psUpdate = conexion.prepareStatement(updateSql);
                    psUpdate.setInt(1, idHabitacion);
                    psUpdate.executeUpdate();

                    cargarReservasDesdeBD();
                }

                conexion.close();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(intReservas, "No se pudo guardar la reserva en la base de datos. Error: " + ex);
            }
        }
        //Editar reserva
        if (e.getSource() == intReservas.btnEditar) {
            int fila = intReservas.tblReservas.getSelectedRow();
            if (fila >= 0) {
                try {
                    int idReserva = (int) modeloTabla.getValueAt(fila, 0);

                    int nuevoCliente = (int) intReservas.spnIDCliente.getValue();
                    int nuevaHabitacion = (int) intReservas.spnIDHabitacion.getValue();
                    Date nuevaFeIngreso = intReservas.dateFechaIngreso.getDate();
                    Date nuevaFeSalida = intReservas.dateFechaSalida.getDate();

                    if (nuevoCliente <= 0 || nuevaHabitacion <= 0 || nuevaFeIngreso == null || nuevaFeSalida == null) {
                        JOptionPane.showMessageDialog(intReservas, "Complete todos los campos para editar.");
                        return;
                    }

                    if (nuevaFeIngreso.after(nuevaFeSalida)) {
                        JOptionPane.showMessageDialog(intReservas, "La fecha de ingreso no puede ser posterior a la fecha de salida.");
                        return;
                    }

                    Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                    String sql = "UPDATE reserva SET idCliente=?, idHabitacion=?, fechaIngreso=?, fechaSalida=? WHERE idReserva=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setInt(1, nuevoCliente);
                    ps.setInt(2, nuevaHabitacion);
                    ps.setDate(3, new java.sql.Date(nuevaFeIngreso.getTime()));
                    ps.setDate(4, new java.sql.Date(nuevaFeSalida.getTime()));
                    ps.setInt(5, idReserva);

                    int filasAfectadas = ps.executeUpdate();
                    if (filasAfectadas > 0) {
                        modeloTabla.setValueAt(nuevoCliente, fila, 1);
                        modeloTabla.setValueAt(nuevaHabitacion, fila, 2);
                        modeloTabla.setValueAt(nuevaFeIngreso, fila, 3);
                        modeloTabla.setValueAt(nuevaFeSalida, fila, 5);

                        intReservas.tblReservas.clearSelection();
                        limpiarCampos();
                        JOptionPane.showMessageDialog(intReservas, "Datos de la reserva actualizados correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(intReservas, "Error al actualizar en la base de datos.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(intReservas, "Error al actualizar: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(intReservas, "Seleccione una fila para editar, luego llene los campos y presione EDITAR.");
            }
        }
        //Eliminar reserva
        if (e.getSource() == intReservas.btnEliminar) { 
            int filaSeleccionada = intReservas.tblReservas.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int opcion = JOptionPane.showConfirmDialog(intReservas,
                        "Está a punto de eliminar una reserva, ¿Desea continuar?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    try {
                        int idReserva = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
                        int idHabitacion = (int)modeloTabla.getValueAt(filaSeleccionada, 2);

                        Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                        String sql = "DELETE FROM reserva WHERE idReserva = ?";
                        PreparedStatement ps = conexion.prepareStatement(sql);
                        ps.setInt(1, idReserva);

                        int filasAfectadas = ps.executeUpdate();
                        if (filasAfectadas > 0) {
                            modeloTabla.removeRow(filaSeleccionada);
                            intReservas.tblReservas.clearSelection();
                            JOptionPane.showMessageDialog(intReservas, "Reserva eliminada correctamente.");

                            String updateSql = "UPDATE habitacion SET estado = 'Activo' WHERE idHabitacion = ?";
                            PreparedStatement psUpdate = conexion.prepareStatement(updateSql);
                            psUpdate.setInt(1, idHabitacion);
                            psUpdate.executeUpdate();

                        } else {
                            JOptionPane.showMessageDialog(intReservas, "No se pudo eliminar la reserva de la base de datos.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(intReservas, "Error al eliminar: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(intReservas, "Seleccione una fila para eliminar.");
            }
        }
    }

    protected void cargarCliente() {
        int idCliente = (int) intReservas.spnIDCliente.getValue();

        if (idCliente <= 0) {
            intReservas.spnIDCliente.setValue(0);
            limpiarCamposCliente();
            return;
        }

        String sql = "SELECT nombres, apellidos, numDocumento, tipoDocumento, telefono FROM cliente WHERE idCliente=?";

        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", ""); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    intReservas.txtNombreCliente.setText(rs.getString("nombres"));
                    intReservas.txtApellidoCliente.setText(rs.getString("apellidos"));
                    intReservas.txtNumDocumento.setText(rs.getString("numDocumento"));
                    intReservas.txtTipoDocumento.setText(rs.getString("tipoDocumento"));
                    intReservas.txtNumeroTelefono.setText(rs.getString("telefono"));
                } else {
                    limpiarCamposCliente();
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(intReservas, "No se pudo cargar los datos del cliente. Error: " + ex.getMessage());
        }
    }

    private void cargarHabitacion() {
        int idHabitacion = (int) intReservas.spnIDHabitacion.getValue();

        if (idHabitacion <= 0) {
            intReservas.spnIDHabitacion.setValue(0);
            limpiarCamposHabitacion();
            return;
        }
        String sql = "SELECT estado, numero, tipo, precioXNoche, piso FROM habitacion WHERE idHabitacion=?";

        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", ""); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idHabitacion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("estado");

                    if (estado.equalsIgnoreCase("Activo")) {
                        intReservas.txtNumHabitacion.setText(rs.getString("numero"));
                        intReservas.txtTipoHabitacion.setText(rs.getString("tipo"));
                        intReservas.txtPrecioNoche.setText(String.valueOf(rs.getDouble("precioXNoche")));
                        intReservas.txtNumeroPiso.setText(String.valueOf(rs.getInt("piso")));
                    } else {
                        limpiarCamposHabitacion();
                    }
                } else {
                    limpiarCamposHabitacion();
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(intReservas, "Error al cargar datos de la habitación: " + ex.getMessage());
        }
    }

    protected void cargarReservasDesdeBD() {
        modeloTabla.setRowCount(0);
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
            String sql = "SELECT * FROM reserva";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idReserva"),
                    rs.getInt("idCliente"),
                    rs.getInt("idHabitacion"),
                    rs.getDate("fechaIngreso"),
                    rs.getDate("fechaSalida"),
                    rs.getString("estado")
                };
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(intReservas, "No se pudo cargar los datos en la tabla. Error: " + ex);
        }
    }

    private void limpiarCampos() {
        intReservas.spnIDCliente.setValue(0);
        intReservas.spnIDHabitacion.setValue(0);
        intReservas.dateFechaIngreso.setDate(null);
        intReservas.dateFechaSalida.setDate(null);
        intReservas.txtNombreCliente.setText("");
        intReservas.txtApellidoCliente.setText("");
        intReservas.txtTipoDocumento.setText("");
        intReservas.txtNumDocumento.setText("");
        intReservas.txtNumeroTelefono.setText("");
        intReservas.txtNumHabitacion.setText("");
        intReservas.txtTipoHabitacion.setText("");
        intReservas.txtPrecioNoche.setText("");
        intReservas.txtNumeroPiso.setText("");
    }

    private void limpiarCamposCliente() {
        intReservas.txtNombreCliente.setText("");
        intReservas.txtApellidoCliente.setText("");
        intReservas.txtTipoDocumento.setText("");
        intReservas.txtNumDocumento.setText("");
        intReservas.txtNumeroTelefono.setText("");
    }

    private void limpiarCamposHabitacion() {
        intReservas.txtNumHabitacion.setText("");
        intReservas.txtTipoHabitacion.setText("");
        intReservas.txtPrecioNoche.setText("");
        intReservas.txtNumeroPiso.setText("");
    }

}
