package controller;

import config.DatabaseConnection;
import dao.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JOptionPane;
import model.*;
import vista.PrestamosPanel;

public class PrestamoController {
  private final PrestamosPanel vista;
  private final Usuario usuario;
  private final PrestamoDAO dao = new PrestamoDAO();
  private final EstudianteDAO estudiantes = new EstudianteDAO();

  private record Datos(
      List<Estudiante> estudiantes, List<Libro> libros, List<Prestamo> prestamos) {}

  public PrestamoController(PrestamosPanel vista, Usuario usuario) {
    this.vista = vista;
    this.usuario = usuario;
    vista.alActualizar(e -> cargar());
    vista.alPrestar(e -> prestar());
    vista.alDevolver(e -> devolver());
    cargar();
  }

  private Estudiante propio() throws SQLException {
    Estudiante e = estudiantes.buscarPorRut(usuario.getRut());
    if (e == null) throw new SQLException("Tu cuenta no tiene un estudiante asociado.");
    return e;
  }

  private Datos consultar() throws SQLException {
    return DatabaseConnection.getInstance()
        .consultar(
            conn ->
                new Datos(
                    usuario.esBibliotecario() ? estudiantes.listar() : List.of(propio()),
                    new LibroDAO().listar(),
                    dao.listar(usuario)));
  }

  private void mostrar(Datos d) {
    vista.mostrar(d.estudiantes(), d.libros(), d.prestamos());
  }

  private void cargar() {
    Tareas.ejecutar(vista, vista::setCargando, this::consultar, this::mostrar);
  }

  private void prestar() {
    Libro l = vista.getLibro();
    Estudiante e = vista.getEstudiante();
    if (l == null || (usuario.esBibliotecario() && e == null)) {
      JOptionPane.showMessageDialog(vista, "Selecciona un estudiante y un libro disponible.");
      return;
    }
    operar(
        () -> {
          dao.registrar(
              usuario, usuario.esBibliotecario() ? e.getId() : propio().getId(), l.getId());
          return null;
        },
        "Préstamo registrado. Plazo: 7 días.");
  }

  private void devolver() {
    Prestamo p = vista.getPrestamo();
    if (p == null) {
      JOptionPane.showMessageDialog(vista, "Selecciona un préstamo.");
      return;
    }
    if (p.isDevuelto()) {
      JOptionPane.showMessageDialog(vista, "Este préstamo ya fue devuelto.");
      return;
    }
    if (vista.confirmar(p))
      operar(
          () -> {
            dao.devolver(usuario, p.getId());
            return null;
          },
          "Devolución registrada.");
  }

  private void operar(Callable<Void> tarea, String mensaje) {
    Tareas.ejecutar(
        vista,
        vista::setCargando,
        tarea,
        ignored -> {
          JOptionPane.showMessageDialog(vista, mensaje);
          cargar();
        });
  }
}
