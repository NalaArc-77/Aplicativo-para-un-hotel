package Controlador.InternalFrame;

import Modelo.Cliente;
import Vista.InternalClientes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ControladorInternalClientes implements ActionListener {

    InternalClientes intClientes;
    Cliente cliente;

    DefaultTableModel modeloTabla;

    public ControladorInternalClientes(InternalClientes intCli) {
        intClientes = intCli;
        intClientes.setTitle("Clientes");
        intClientes.setVisible(true);

        intClientes.btnGuardarDatos.addActionListener(this);
        intClientes.btnEditarDatos.addActionListener(this);
        intClientes.btnEliminarDatos.addActionListener(this);

        String[] columnas = {"ID", "Nombre", "Apellido", "Num Doc", "Tipo Doc", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }; //para que las celdas no sean editables.
        intClientes.tblDatosClientes.setModel(modeloTabla);
        cargarClientesDesdeBD();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Guardar datos del cliente
        if (e.getSource() == intClientes.btnGuardarDatos) {
            String nombre = intClientes.txtNombre.getText();
            String apellido = intClientes.txtApellido.getText();
            String tipoDoc = (String) intClientes.cbxDocumento.getSelectedItem();
            if (tipoDoc.equals("--Seleccione--")) {
                JOptionPane.showMessageDialog(intClientes, "Seleccione el tipo de documento");
                return;
            }

            String numDoc = intClientes.txtNumDocumento.getText();
            switch (tipoDoc) {
                case "DNI" -> {
                    if (numDoc.length() != 8) {
                        JOptionPane.showMessageDialog(intClientes, "Para DNI ingrese exactamente 8 números.");
                        return;
                    }
                }
                case "CE" -> {
                    if (numDoc.length() != 9) {
                        JOptionPane.showMessageDialog(intClientes, "Para CE ingrese exactamente 9 números.");
                        return;
                    }
                }
            }

            String telefono = intClientes.txtTelefono.getText();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || numDoc.isEmpty()) {
                JOptionPane.showMessageDialog(intClientes, "Todos los campos deben estar completos.");
                return;
            }
            cliente = new Cliente(nombre, apellido, numDoc, tipoDoc, telefono);

            // GUARDANDING EN LA BD
            try {
                Connection conexion = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/hoteldb", "root", ""
                );

                String sql = "INSERT INTO Cliente (nombres, apellidos, numDocumento, tipoDocumento, telefono) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = conexion.prepareStatement(sql);

                statement.setString(1, nombre);
                statement.setString(2, apellido);
                statement.setString(3, numDoc);
                statement.setString(4, tipoDoc);
                statement.setString(5, telefono);

                int resultado = statement.executeUpdate();
                if (resultado > 0) {
                    System.out.println("Cliente guardado en la base de datos.");
                    cargarClientesDesdeBD();
                }

                conexion.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(intClientes, "Error al guardar en la base de datos: " + ex.getMessage());
            }
            limpiarCampos();

        }
        //Eliminar datos del cliente
        if (e.getSource() == intClientes.btnEliminarDatos) {
            int filaSeleccionada = intClientes.tblDatosClientes.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int opcion = JOptionPane.showConfirmDialog(intClientes,
                        "Está a punto de eliminar a un cliente, ¿Desea continuar?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    try {
                        int idCliente = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

                        Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");

                        String SQLidHabitacion = "SELECT idHabitacion FROM reserva WHERE idCliente=?";
                        PreparedStatement psid = conexion.prepareStatement(SQLidHabitacion);
                        psid.setInt(1, idCliente);
                        ResultSet rs = psid.executeQuery();

                        ArrayList<Integer> habitaciones = new ArrayList<>();
                        while (rs.next()) {
                            habitaciones.add(rs.getInt("idHabitacion"));
                        }
                        rs.close();
                        psid.close();

                        String sql = "DELETE FROM cliente WHERE idCliente = ?";
                        PreparedStatement ps = conexion.prepareStatement(sql);
                        ps.setInt(1, idCliente);
                        int filasAfectadas = ps.executeUpdate();
                        ps.close();

                        if (filasAfectadas > 0) {
                            modeloTabla.removeRow(filaSeleccionada);
                            intClientes.tblDatosClientes.clearSelection();
                            JOptionPane.showMessageDialog(intClientes, "Cliente eliminado correctamente.");

                            String updateSql = "UPDATE habitacion SET estado = 'Activo' WHERE idHabitacion = ?";
                            PreparedStatement psUpdate = conexion.prepareStatement(updateSql);
                            for (int idHab : habitaciones) {
                                psUpdate.setInt(1, idHab);
                                psUpdate.executeUpdate();
                            }
                            psUpdate.close();

                        } else {
                            JOptionPane.showMessageDialog(intClientes, "No se pudo eliminar el cliente de la base de datos.");
                        }

                        conexion.close();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(intClientes, "Error al eliminar: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(intClientes, "Seleccione una fila para eliminar.");
            }
        }
        //Editar datos del cliente
        if (e.getSource() == intClientes.btnEditarDatos) {
            int fila = intClientes.tblDatosClientes.getSelectedRow();
            if (fila >= 0) {
                try {
                    int idCliente = (int) modeloTabla.getValueAt(fila, 0);

                    String nuevoNombre = intClientes.txtNombre.getText();
                    String nuevoApellido = intClientes.txtApellido.getText();
                    String nuevoTipoDoc = intClientes.cbxDocumento.getSelectedItem().toString();
                    String nuevoNumDoc = intClientes.txtNumDocumento.getText();
                    String nuevoTelefono = intClientes.txtTelefono.getText();

                    if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoTipoDoc.equals("--Seleccione--")
                            || nuevoNumDoc.isEmpty() || nuevoTelefono.isEmpty()) {
                        JOptionPane.showMessageDialog(intClientes, "Complete todos los campos para editar.");
                        return;
                    }

                    //ACTUALIZANDING EN LA BD
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
                    String sql = "UPDATE cliente SET nombres=?, apellidos=?, numDocumento=?, tipoDocumento=?, telefono=? WHERE idCliente=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, nuevoNombre);
                    ps.setString(2, nuevoApellido);
                    ps.setString(3, nuevoNumDoc);
                    ps.setString(4, nuevoTipoDoc);
                    ps.setString(5, nuevoTelefono);
                    ps.setInt(6, idCliente);

                    int filasAfectadas = ps.executeUpdate();
                    if (filasAfectadas > 0) {
                        modeloTabla.setValueAt(nuevoNombre, fila, 1);
                        modeloTabla.setValueAt(nuevoApellido, fila, 2);
                        modeloTabla.setValueAt(nuevoNumDoc, fila, 3);
                        modeloTabla.setValueAt(nuevoTipoDoc, fila, 4);
                        modeloTabla.setValueAt(nuevoTelefono, fila, 5);

                        intClientes.tblDatosClientes.clearSelection();
                        limpiarCampos();
                        JOptionPane.showMessageDialog(intClientes, "Datos del cliente actualizados correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(intClientes, "Error al actualizar en la base de datos.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(intClientes, "Error al actualizar: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(intClientes, "Seleccione una fila para editar, luego modifique los campos y presione EDITAR.");
            }
        }
    }

    private void limpiarCampos() {
        intClientes.txtNombre.setText("");
        intClientes.txtApellido.setText("");
        intClientes.cbxDocumento.setSelectedIndex(0);
        intClientes.txtNumDocumento.setText("");
        intClientes.txtTelefono.setText("");
    }

    private void cargarClientesDesdeBD() {
        modeloTabla.setRowCount(0);
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
            String sql = "SELECT * FROM Cliente";
            PreparedStatement statement = conexion.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idCliente"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("numDocumento"),
                    rs.getString("tipoDocumento"),
                    rs.getString("telefono")
                };
                modeloTabla.addRow(fila);
            }

            conexion.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(intClientes, "Error al cargar datos de la base de datos: " + ex.getMessage());
        }
    }
}
