package vista;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class PrestamosPanel extends JPanel {
  private final JComboBox<Estudiante> estudiantes = new JComboBox<>();
  private final JComboBox<Libro> libros = new JComboBox<>();
  private final JButton prestar = new JButton("Registrar préstamo"),
      devolver = new JButton("Devolver seleccionado"),
      actualizar = new JButton("Actualizar");
  private final JLabel estado = new JLabel(" ");
  private final DefaultTableModel modelo =
      new DefaultTableModel(
          new String[] {
            "ID",
            "Estudiante",
            "Libro",
            "Fecha préstamo",
            "Vencimiento",
            "Devolución real",
            "Estado"
          },
          0) {
        @Override
        public boolean isCellEditable(int r, int c) {
          return false;
        }
      };
  private final JTable tabla = new JTable(modelo);
  private List<Prestamo> prestamos = new ArrayList<>();

  public PrestamosPanel(boolean bibliotecario) {
    setLayout(new BorderLayout(10, 10));
    JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
    if (bibliotecario) {
      form.add(new JLabel("Estudiante"));
      form.add(estudiantes);
    }
    form.add(new JLabel("Libro disponible"));
    form.add(libros);
    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    botones.add(prestar);
    botones.add(devolver);
    botones.add(actualizar);
    JPanel superior = new JPanel(new BorderLayout(0, 10));
    superior.add(form, BorderLayout.NORTH);
    superior.add(botones, BorderLayout.SOUTH);
    tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabla.setAutoCreateRowSorter(true);
    tabla.setRowHeight(26);
    tabla.getTableHeader().setReorderingAllowed(false);
    add(superior, BorderLayout.NORTH);
    add(new JScrollPane(tabla), BorderLayout.CENTER);
    add(estado, BorderLayout.SOUTH);
  }

  public void mostrar(List<Estudiante> es, List<Libro> ls, List<Prestamo> ps) {
    estudiantes.removeAllItems();
    libros.removeAllItems();
    for (Estudiante e : es) estudiantes.addItem(e);
    for (Libro l : ls) if (l.getStock() > 0) libros.addItem(l);
    prestamos = ps;
    modelo.setRowCount(0);
    for (Prestamo p : ps)
      modelo.addRow(
          new Object[] {
            p.getId(),
            p.getEstudiante().getNombre(),
            p.getLibro().getTitulo(),
            p.getFechaPrestamo(),
            p.getFechaDevolucion(),
            p.getFechaDevolucionReal(),
            p.getEstado()
          });
  }

  public Estudiante getEstudiante() {
    return (Estudiante) estudiantes.getSelectedItem();
  }

  public Libro getLibro() {
    return (Libro) libros.getSelectedItem();
  }

  public Prestamo getPrestamo() {
    int r = tabla.getSelectedRow();
    return r < 0 ? null : prestamos.get(tabla.convertRowIndexToModel(r));
  }

  public boolean confirmar(Prestamo p) {
    return JOptionPane.showConfirmDialog(
            this,
            "¿Devolver "
                + p.getLibro().getTitulo()
                + "?"
                + (p.estaAtrasado() ? "\nEl préstamo está atrasado." : ""),
            "Confirmar devolución",
            JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION;
  }

  public void setCargando(boolean c) {
    estudiantes.setEnabled(!c);
    libros.setEnabled(!c);
    for (JButton b : new JButton[] {prestar, devolver, actualizar}) b.setEnabled(!c);
    tabla.setEnabled(!c);
    estado.setText(c ? "Procesando..." : "Préstamos: " + modelo.getRowCount());
  }

  public void alPrestar(ActionListener a) {
    prestar.addActionListener(a);
  }

  public void alDevolver(ActionListener a) {
    devolver.addActionListener(a);
  }

  public void alActualizar(ActionListener a) {
    actualizar.addActionListener(a);
  }
}
