package controller;

import dao.*;
import model.*;
import vista.*;

public class LibroController {
  private final GestionPanel<Libro> vista;
  private final LibroDAO dao = new LibroDAO();
  private final GestionController<Libro> gestion;

  public LibroController(GestionPanel<Libro> vista, Usuario usuario) {
    this.vista = vista;
    gestion =
        new GestionController<>(
            vista, usuario, dao::listar, this::formulario, l -> dao.eliminar(l.getId()));
  }

  private void formulario(Libro actual) {
    Tareas.ejecutar(
        vista,
        vista::setCargando,
        new CategoriaDAO()::listar,
        categorias -> {
          Libro libro = Formularios.libro(vista, actual, categorias);
          if (libro != null)
            gestion.guardar(
                () -> {
                  if (actual == null) dao.crear(libro);
                  else dao.actualizar(libro);
                  return null;
                });
        });
  }
}
