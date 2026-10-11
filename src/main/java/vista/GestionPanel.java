package vista;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class GestionPanel<T> extends JPanel {
  private final DefaultTableModel modelo;
  private final JTable tabla;
  private final JButton agregar = new JButton("Agregar"),
      editar = new JButton("Editar"),
      eliminar = new JButton("Eliminar"),
      actualizar = new JButton("Actualizar");
  private final JLabel estado = new JLabel(" ");
  private List<T> lista = new ArrayList<>();
  private final Function<T, Object[]> fila;

  public GestionPanel(String[] columnas, Function<T, Object[]> fila, boolean gestion) {
    this.fila = fila;
    setLayout(new BorderLayout(10, 10));
    modelo =
        new DefaultTableModel(columnas, 0) {
          @Override
          public boolean isCellEditable(int r, int c) {
            return false;
          }
        };
    tabla = new JTable(modelo);
    tabla.setRowHeight(26);
    tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabla.getTableHeader().setReorderingAllowed(false);
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);
    tabla.setRowSorter(sorter);
    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    if (gestion) {
      botones.add(agregar);
      botones.add(editar);
      botones.add(eliminar);
    }
    botones.add(actualizar);
    JTextField buscar = new JTextField(20);
    botones.add(new JLabel("Buscar"));
    botones.add(buscar);
    buscar
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              private void filtrar() {
                sorter.setRowFilter(
                    RowFilter.regexFilter("(?i)" + Pattern.quote(buscar.getText())));
              }

              public void insertUpdate(DocumentEvent e) {
                filtrar();
              }

              public void removeUpdate(DocumentEvent e) {
                filtrar();
              }

              public void changedUpdate(DocumentEvent e) {
                filtrar();
              }
            });
    add(botones, BorderLayout.NORTH);
    add(new JScrollPane(tabla), BorderLayout.CENTER);
    add(estado, BorderLayout.SOUTH);
  }

  public void mostrar(List<T> lista) {
    this.lista = lista;
    modelo.setRowCount(0);
    for (T item : lista) modelo.addRow(fila.apply(item));
  }

  public T seleccionado() {
    int r = tabla.getSelectedRow();
    return r < 0 ? null : lista.get(tabla.convertRowIndexToModel(r));
  }

  public void setCargando(boolean value) {
    for (JButton b : new JButton[] {agregar, editar, eliminar, actualizar}) b.setEnabled(!value);
    tabla.setEnabled(!value);
    estado.setText(value ? "Procesando..." : "Registros: " + modelo.getRowCount());
  }

  public void alAgregar(ActionListener a) {
    agregar.addActionListener(a);
  }

  public void alEditar(ActionListener a) {
    editar.addActionListener(a);
  }

  public void alEliminar(ActionListener a) {
    eliminar.addActionListener(a);
  }

  public void alActualizar(ActionListener a) {
    actualizar.addActionListener(a);
  }
}
