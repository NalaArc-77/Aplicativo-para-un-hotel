package Controlador.InternalFrame;

import Vista.InternalFacturacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.swing.JOptionPane;

public class ControladorInternalFacturacion implements ActionListener {

    InternalFacturacion intFacturacion;

    public ControladorInternalFacturacion(InternalFacturacion intFact) {
        intFacturacion = intFact;
        intFacturacion.setTitle("Crear factura");
        intFacturacion.setVisible(true);

        intFacturacion.btnVistaPrevia.addActionListener(this);
        intFacturacion.btnGenerarFactura.addActionListener(this);
        intFacturacion.btnLimpiar.addActionListener(this);

        intFacturacion.spnIdReserva.addChangeListener(e -> verificarSpnIdReserva());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Generar una vista previa de la factura
        if (e.getSource() == intFacturacion.btnVistaPrevia) {
            int idReserva = (int) intFacturacion.spnIdReserva.getValue();

            try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "")) {

                String sql = "SELECT c.nombres, c.apellidos, r.fechaIngreso, r.fechaSalida, h.tipo, h.precioXNoche "
                        + "FROM reserva r "
                        + "JOIN cliente c ON r.idCliente = c.idCliente "
                        + "JOIN habitacion h ON r.idHabitacion = h.idHabitacion "
                        + "WHERE r.idReserva = ?";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, idReserva);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String nombre = rs.getString("nombres") + " " + rs.getString("apellidos");
                    Date ingreso = rs.getDate("fechaIngreso");
                    Date salida = rs.getDate("fechaSalida");
                    String tipo = rs.getString("tipo");
                    double precio = rs.getDouble("precioXNoche");

                    long dias = (salida.getTime() - ingreso.getTime()) / (1000 * 60 * 60 * 24);
                    dias = dias == 0 ? 1 : dias;
                    double total = dias * precio;

                    String info = "Cliente: " + nombre
                            + "\nTipo de habitación: " + tipo
                            + "\nFecha de ingreso: " + ingreso
                            + "\nFecha de salida: " + salida
                            + "\nPrecio por noche: $" + precio
                            + "\nCantidad de noches: " + dias
                            + "\n\nTOTAL A PAGAR: $" + total;

                    intFacturacion.txaDescripcionFactura.setText(info);
                } else {
                    intFacturacion.txaDescripcionFactura.setText("No se encontró la reserva con ID " + idReserva);
                }

                rs.close();
                ps.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(intFacturacion, "Error al buscar la reserva.");
            }

        }
        //Generar factura
        if (e.getSource() == intFacturacion.btnGenerarFactura) {
            int idReserva = (int) intFacturacion.spnIdReserva.getValue();

            try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "")) {

                String sql = "SELECT fechaIngreso, fechaSalida, h.precioXNoche "
                        + "FROM reserva r JOIN habitacion h ON r.idHabitacion = h.idHabitacion "
                        + "WHERE r.idReserva = ? AND r.estado = 'Pendiente'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, idReserva);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Date ingreso = rs.getDate("fechaIngreso");
                    Date salida = rs.getDate("fechaSalida");
                    double precio = rs.getDouble("precioXNoche");

                    long dias = (salida.getTime() - ingreso.getTime()) / (1000 * 60 * 60 * 24);
                    dias = dias == 0 ? 1 : dias;
                    double total = dias * precio;

                    // INSERTAR FACTURA
                    String insert = "INSERT INTO factura (idReserva, totalPagar, fechaEmision) VALUES (?, ?, ?)";
                    PreparedStatement psInsert = conexion.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setInt(1, idReserva);
                    psInsert.setDouble(2, total);
                    psInsert.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                    int resultado = psInsert.executeUpdate();

                    if (resultado > 0) {
                        ResultSet rsIdFactura = psInsert.getGeneratedKeys();
                        if (rsIdFactura.next()) {
                            int idFactura = rsIdFactura.getInt(1);

                            String sqlIdHabitacion = "SELECT r.idHabitacion "
                                    + "FROM factura f "
                                    + "JOIN reserva r ON f.idReserva = r.idReserva "
                                    + "WHERE f.idFactura = ?";
                            PreparedStatement psIdHabitacion = conexion.prepareStatement(sqlIdHabitacion);
                            psIdHabitacion.setInt(1, idFactura);
                            ResultSet rsIdHabitacion = psIdHabitacion.executeQuery();

                            if (rsIdHabitacion.next()) {
                                int idHabitacion = rsIdHabitacion.getInt("idHabitacion");

                                String updateEstadoHab = "UPDATE habitacion SET estado = 'Limpieza' WHERE idHabitacion = ?";
                                PreparedStatement psUpdateHab = conexion.prepareStatement(updateEstadoHab);
                                psUpdateHab.setInt(1, idHabitacion);
                                psUpdateHab.executeUpdate();
                                psUpdateHab.close();
                            }

                            rsIdHabitacion.close();
                            psIdHabitacion.close();
                        }

                        rsIdFactura.close();

                        JOptionPane.showMessageDialog(intFacturacion, "Factura generada con éxito.");
                    } else {
                        JOptionPane.showMessageDialog(intFacturacion, "Error al generar la factura.");
                    }

                    String updateEstadoReserva = "UPDATE reserva SET estado = 'Cancelado' WHERE idReserva = ?";
                    PreparedStatement psUpdateReserva = conexion.prepareStatement(updateEstadoReserva);
                    psUpdateReserva.setInt(1, idReserva);
                    psUpdateReserva.executeUpdate();
                    psUpdateReserva.close();

                    psInsert.close();
                } else {
                    JOptionPane.showMessageDialog(intFacturacion, "Reserva no encontrada o ya facturada.");
                }

                rs.close();
                ps.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(intFacturacion, "Error al generar factura.");
            }
        }

        //Limpiar campos añaseyo
        if (e.getSource() == intFacturacion.btnLimpiar) {
            intFacturacion.txaDescripcionFactura.setText("");
            intFacturacion.spnIdReserva.setValue(0);
        }
    }

    protected void verificarSpnIdReserva() {
        int idReserva = (int) intFacturacion.spnIdReserva.getValue();

        if (idReserva <= 0) {
            intFacturacion.spnIdReserva.setValue(0);
            return;
        }
    }
}
