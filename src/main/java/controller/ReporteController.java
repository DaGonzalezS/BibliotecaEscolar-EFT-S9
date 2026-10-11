package controller;

import dao.*;
import javax.swing.JOptionPane;
import model.*;
import vista.ReportesPanel;

public class ReporteController {
  private final ReportesPanel vista;
  private final Usuario usuario;
  private final ReporteDAO dao = new ReporteDAO();

  public ReporteController(ReportesPanel vista, Usuario usuario) {
    this.vista = vista;
    this.usuario = usuario;
    vista.alGenerar(e -> generar());
    vista.alActualizar(e -> cargar());
    cargar();
  }

  private boolean permiso() {
    if (usuario.esBibliotecario()) return true;
    JOptionPane.showMessageDialog(vista, "Acción exclusiva del bibliotecario.");
    return false;
  }

  private void cargar() {
    if (permiso())
      Tareas.ejecutar(
          vista, vista::setCargando, new EstudianteDAO()::listar, vista::mostrarEstudiantes);
  }

  private void generar() {
    if (!permiso()) return;
    int tipo = vista.getTipo();
    Estudiante e = vista.getEstudiante();
    if (tipo == 1 && e == null) {
      JOptionPane.showMessageDialog(vista, "Selecciona un estudiante.");
      return;
    }
    Tareas.ejecutar(
        vista,
        vista::setCargando,
        () ->
            switch (tipo) {
              case 0 -> dao.librosMasPrestados();
              case 1 -> dao.historialEstudiante(e.getId());
              case 2 -> dao.prestamosPendientes();
              default -> throw new IllegalArgumentException("Reporte inválido.");
            },
        vista::mostrarReporte);
  }
}
