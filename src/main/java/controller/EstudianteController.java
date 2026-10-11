package controller;

import dao.EstudianteDAO;
import model.*;
import vista.*;

public class EstudianteController {
  private final GestionPanel<Estudiante> vista;
  private final EstudianteDAO dao = new EstudianteDAO();
  private final GestionController<Estudiante> gestion;

  public EstudianteController(GestionPanel<Estudiante> vista, Usuario usuario) {
    this.vista = vista;
    gestion =
        new GestionController<>(
            vista, usuario, dao::listar, this::formulario, dao::eliminarConCuenta);
  }

  private void formulario(Estudiante actual) {
    Formularios.DatosEstudiante datos = Formularios.estudiante(vista, actual);
    if (datos != null)
      gestion.guardar(
          () -> {
            dao.guardarConCuenta(
                datos.estudiante(), actual == null ? null : actual.getRut(), datos.clave());
            return null;
          });
  }
}
