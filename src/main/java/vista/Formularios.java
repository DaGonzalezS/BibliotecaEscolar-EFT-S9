package vista;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.*;

public final class Formularios {
  private Formularios() {}

  public record DatosEstudiante(Estudiante estudiante, String clave) {}

  private static JPanel panel(String[] etiquetas, JComponent[] campos) {
    JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
    for (int i = 0; i < etiquetas.length; i++) {
      p.add(new JLabel(etiquetas[i]));
      p.add(campos[i]);
    }
    return p;
  }

  private static boolean aceptar(Component parent, JPanel panel, String titulo) {
    return JOptionPane.showConfirmDialog(
            parent, panel, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
        == JOptionPane.OK_OPTION;
  }

  private static void validar(String texto, int max, String nombre) {
    if (texto.isBlank() || texto.length() > max)
      throw new IllegalArgumentException(nombre + ": ingresa entre 1 y " + max + " caracteres.");
  }

  public static Categoria categoria(Component parent, Categoria actual) {
    JTextField nombre = new JTextField(actual == null ? "" : actual.getNombre(), 25);
    JPanel p = panel(new String[] {"Nombre"}, new JComponent[] {nombre});
    while (aceptar(parent, p, actual == null ? "Agregar categoría" : "Editar categoría")) {
      try {
        String n = nombre.getText().trim();
        validar(n, 100, "Nombre");
        return new Categoria(actual == null ? 0 : actual.getId(), n);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage());
      }
    }
    return null;
  }

  public static Libro libro(Component parent, Libro actual, List<Categoria> categorias) {
    if (categorias.isEmpty()) {
      JOptionPane.showMessageDialog(parent, "Primero registra una categoría.");
      return null;
    }
    JTextField titulo = new JTextField(actual == null ? "" : actual.getTitulo(), 25),
        autor = new JTextField(actual == null ? "" : actual.getAutor()),
        isbn = new JTextField(actual == null ? "" : actual.getIsbn()),
        editorial = new JTextField(actual == null ? "" : actual.getEditorial());
    JSpinner stock =
        new JSpinner(
            new SpinnerNumberModel(
                actual == null ? 0 : actual.getStock(), 0, Integer.MAX_VALUE, 1));
    JComboBox<Categoria> categoria = new JComboBox<>(categorias.toArray(new Categoria[0]));
    if (actual != null)
      for (Categoria c : categorias)
        if (c.getId() == actual.getCategoria().getId()) categoria.setSelectedItem(c);
    JPanel p =
        panel(
            new String[] {"Título", "Autor", "ISBN", "Editorial", "Stock disponible", "Categoría"},
            new JComponent[] {titulo, autor, isbn, editorial, stock, categoria});
    while (aceptar(parent, p, actual == null ? "Agregar libro" : "Editar libro")) {
      try {
        String t = titulo.getText().trim(),
            a = autor.getText().trim(),
            i = isbn.getText().trim(),
            ed = editorial.getText().trim();
        validar(t, 200, "Título");
        validar(a, 100, "Autor");
        validar(i, 20, "ISBN");
        validar(ed, 100, "Editorial");
        stock.commitEdit();
        Libro l =
            new Libro(
                actual == null ? 0 : actual.getId(),
                t,
                a,
                i,
                ed,
                (Integer) stock.getValue(),
                (Categoria) categoria.getSelectedItem());
        if (actual != null) l.setStockOriginal(actual.getStock());
        return l;
      } catch (IllegalArgumentException | java.text.ParseException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage());
      }
    }
    return null;
  }

  public static DatosEstudiante estudiante(Component parent, Estudiante actual) {
    JTextField nombre = new JTextField(actual == null ? "" : actual.getNombre(), 25),
        rut = new JTextField(actual == null ? "" : actual.getRut()),
        correo = new JTextField(actual == null ? "" : actual.getCorreo()),
        curso = new JTextField(actual == null ? "" : actual.getCurso());
    JPasswordField clave = new JPasswordField();
    JPanel p =
        panel(
            new String[] {
              "Nombre",
              "RUT sin puntos",
              "Correo",
              "Curso",
              actual == null ? "Contraseña" : "Contraseña (vacía: conservar)"
            },
            new JComponent[] {nombre, rut, correo, curso, clave});
    while (aceptar(parent, p, actual == null ? "Agregar estudiante" : "Editar estudiante")) {
      try {
        String n = nombre.getText().trim(),
            r = rut.getText().trim().toUpperCase(),
            c = correo.getText().trim(),
            cu = curso.getText().trim(),
            cl = new String(clave.getPassword());
        validar(n, 100, "Nombre");
        validar(r, 12, "RUT");
        validar(c, 100, "Correo");
        validar(cu, 20, "Curso");
        if (!r.matches("\\d{7,8}-[0-9K]"))
          throw new IllegalArgumentException("Usa el RUT sin puntos y con guion.");
        if (!c.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
          throw new IllegalArgumentException("Ingresa un correo válido.");
        if (cl.length() > 100 || (actual == null && cl.isBlank()))
          throw new IllegalArgumentException("Ingresa una contraseña de hasta 100 caracteres.");
        return new DatosEstudiante(
            new Estudiante(actual == null ? 0 : actual.getId(), n, r, c, cu), cl);
      } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage());
      }
    }
    return null;
  }
}
