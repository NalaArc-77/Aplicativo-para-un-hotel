package Controlador.InternalFrame;

import Vista.InternalReporteFacturas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorInternalReporteFacturas {

    InternalReporteFacturas intRepoFact;
    DefaultTableModel modeloTabla;

    public ControladorInternalReporteFacturas(InternalReporteFacturas intReportesFacturas) {
        intRepoFact = intReportesFacturas;
        intRepoFact.setVisible(true);
        intRepoFact.setTitle("Reporte de facturas");

        String[] columnas = {"Id factura", "Id reserva", "Monto pagado", "Fecha de emisión"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cargarTodo();

        intRepoFact.tblReporteFacturas.setModel(modeloTabla);
        intRepoFact.cbxFiltrarPorMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String valorSeleccionado = intRepoFact.cbxFiltrarPorMes.getSelectedItem().toString();

                switch (valorSeleccionado) {
                    case "Enero" ->
                        cargarFacturasDesdeBD("1");
                    case "Febrero" ->
                        cargarFacturasDesdeBD("2");
                    case "Marzo" ->
                        cargarFacturasDesdeBD("3");
                    case "Abril" ->
                        cargarFacturasDesdeBD("4");
                    case "Mayo" ->
                        cargarFacturasDesdeBD("5");
                    case "Junio" ->
                        cargarFacturasDesdeBD("6");
                    case "Julio" ->
                        cargarFacturasDesdeBD("7");
                    case "Agosto" ->
                        cargarFacturasDesdeBD("8");
                    case "Septiembre" ->
                        cargarFacturasDesdeBD("9");
                    case "Octubre" ->
                        cargarFacturasDesdeBD("10");
                    case "Noviembre" ->
                        cargarFacturasDesdeBD("11");
                    case "Diciembre" ->
                        cargarFacturasDesdeBD("12");
                    default ->
                        cargarFacturasDesdeBD("--Seleccione--");
                }
            }
        });
    }

    protected void cargarFacturasDesdeBD(String valorSeleccionado) {
        modeloTabla.setRowCount(0);

        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
            PreparedStatement ps;

            if (valorSeleccionado.equals("--Seleccione--")) {
                String sql = "SELECT * FROM factura";
                ps = conexion.prepareStatement(sql);
            } else {
                String sql = "SELECT * FROM factura WHERE MONTH(fechaEmision) = ?";
                ps = conexion.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(valorSeleccionado));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idFactura"),
                    rs.getInt("idReserva"),
                    rs.getDouble("TotalPagar"),
                    rs.getDate("fechaEmision"),};
                modeloTabla.addRow(fila);
            }

            rs.close();
            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(intRepoFact, "No se pudo cargar los datos en la tabla. Error: " + ex);
        }
    }

    protected void cargarTodo() {
        modeloTabla.setRowCount(0);
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
            PreparedStatement ps;

            String sql = "SELECT * FROM factura";
            ps = conexion.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("idFactura"),
                    rs.getInt("idReserva"),
                    rs.getDouble("TotalPagar"),
                    rs.getDate("fechaEmision"),};
                modeloTabla.addRow(fila);
            }

            rs.close();
            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(intRepoFact, "No se pudo cargar los datos en la tabla. Error: " + ex);
        }
    }

}
