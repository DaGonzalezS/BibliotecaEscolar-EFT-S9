package vista;

import controller.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.*;

public class PrincipalVista extends JFrame {
  private final JButton salir = new JButton("Cerrar sesión");

  public PrincipalVista(Usuario usuario) {
    setTitle("Biblioteca Escolar");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    JLabel titulo = new JLabel("Biblioteca Escolar");
    titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
    JPanel datos = new JPanel(new GridLayout(2, 1, 0, 5));
    datos.add(titulo);
    datos.add(new JLabel(describir(usuario)));
    JPanel encabezado = new JPanel(new BorderLayout());
    encabezado.add(datos, BorderLayout.CENTER);
    encabezado.add(salir, BorderLayout.EAST);
    JTabbedPane pestanas = new JTabbedPane();
    GestionPanel<Libro> libros =
        new GestionPanel<>(
            new String[] {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"},
            l ->
                new Object[] {
                  l.getId(),
                  l.getTitulo(),
                  l.getAutor(),
                  l.getIsbn(),
                  l.getEditorial(),
                  l.getStock(),
                  l.getCategoria().getNombre()
                },
            usuario.esBibliotecario());
    new LibroController(libros, usuario);
    pestanas.addTab("Libros", libros);
    if (usuario.esBibliotecario()) {
      GestionPanel<Categoria> categorias =
          new GestionPanel<>(
              new String[] {"ID", "Nombre"}, c -> new Object[] {c.getId(), c.getNombre()}, true);
      new CategoriaController(categorias, usuario);
      pestanas.addTab("Categorías", categorias);
      GestionPanel<Estudiante> estudiantes =
          new GestionPanel<>(
              new String[] {"ID", "Nombre", "RUT", "Correo", "Curso"},
              e -> new Object[] {e.getId(), e.getNombre(), e.getRut(), e.getCorreo(), e.getCurso()},
              true);
      new EstudianteController(estudiantes, usuario);
      pestanas.addTab("Estudiantes", estudiantes);
    }
    PrestamosPanel prestamos = new PrestamosPanel(usuario.esBibliotecario());
    new PrestamoController(prestamos, usuario);
    pestanas.addTab("Préstamos", prestamos);
    if (usuario.esBibliotecario()) {
      ReportesPanel reportes = new ReportesPanel();
      new ReporteController(reportes, usuario);
      pestanas.addTab("Reportes", reportes);
    }
    panel.add(encabezado, BorderLayout.NORTH);
    panel.add(pestanas, BorderLayout.CENTER);
    setContentPane(panel);
    setSize(1150, 700);
    setMinimumSize(new Dimension(900, 550));
    setLocationRelativeTo(null);
  }

  private String describir(Persona persona) {
    return persona.getNombre() + " | " + persona.getTipo();
  }

  public void alCerrarSesion(ActionListener accion) {
    salir.addActionListener(accion);
  }
}
