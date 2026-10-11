package vista;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class ReportesPanel extends JPanel {
  private final JComboBox<String> tipo =
      new JComboBox<>(
          new String[] {
            "Libros más prestados", "Historial por estudiante", "Préstamos pendientes"
          });
  private final JComboBox<Estudiante> estudiantes = new JComboBox<>();
  private final JButton generar = new JButton("Generar reporte"),
      actualizar = new JButton("Actualizar estudiantes");
  private final JLabel estado = new JLabel("Selecciona un reporte.");
  private final DefaultTableModel modelo =
      new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int r, int c) {
          return false;
        }
      };
  private final JTable tabla = new JTable(modelo);
  private boolean cargando;

  public ReportesPanel() {
    setLayout(new BorderLayout(10, 10));
    JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
    form.add(new JLabel("Reporte"));
    form.add(tipo);
    form.add(new JLabel("Estudiante"));
    form.add(estudiantes);
    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    botones.add(generar);
    botones.add(actualizar);
    JPanel superior = new JPanel(new BorderLayout(0, 10));
    superior.add(form, BorderLayout.NORTH);
    superior.add(botones, BorderLayout.SOUTH);
    tabla.setRowHeight(26);
    tabla.getTableHeader().setReorderingAllowed(false);
    tipo.addActionListener(e -> filtro());
    add(superior, BorderLayout.NORTH);
    add(new JScrollPane(tabla), BorderLayout.CENTER);
    add(estado, BorderLayout.SOUTH);
    filtro();
  }

  private void filtro() {
    estudiantes.setEnabled(!cargando && tipo.getSelectedIndex() == 1);
  }

  public int getTipo() {
    return tipo.getSelectedIndex();
  }

  public Estudiante getEstudiante() {
    return (Estudiante) estudiantes.getSelectedItem();
  }

  public void mostrarEstudiantes(List<Estudiante> es) {
    estudiantes.removeAllItems();
    for (Estudiante e : es) estudiantes.addItem(e);
  }

  public void mostrarReporte(Reporte r) {
    tabla.setRowSorter(null);
    modelo.setRowCount(0);
    modelo.setColumnIdentifiers(r.columnas());
    for (Object[] fila : r.filas()) modelo.addRow(fila);
    tabla.setAutoCreateRowSorter(true);
    estado.setText("Registros: " + r.filas().size());
  }

  public void setCargando(boolean c) {
    cargando = c;
    tipo.setEnabled(!c);
    generar.setEnabled(!c);
    actualizar.setEnabled(!c);
    filtro();
    estado.setText(c ? "Procesando..." : "Registros: " + modelo.getRowCount());
  }

  public void alGenerar(ActionListener a) {
    generar.addActionListener(a);
  }

  public void alActualizar(ActionListener a) {
    actualizar.addActionListener(a);
  }
}
