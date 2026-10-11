package controller;

import dao.CategoriaDAO;
import model.*;
import vista.*;

public class CategoriaController {
  private final GestionPanel<Categoria> vista;
  private final CategoriaDAO dao = new CategoriaDAO();
  private final GestionController<Categoria> gestion;

  public CategoriaController(GestionPanel<Categoria> vista, Usuario usuario) {
    this.vista = vista;
    gestion =
        new GestionController<>(
            vista, usuario, dao::listar, this::formulario, c -> dao.eliminar(c.getId()));
  }

  private void formulario(Categoria actual) {
    Categoria categoria = Formularios.categoria(vista, actual);
    if (categoria != null)
      gestion.guardar(
          () -> {
            if (actual == null) dao.crear(categoria);
            else dao.actualizar(categoria);
            return null;
          });
  }
}
