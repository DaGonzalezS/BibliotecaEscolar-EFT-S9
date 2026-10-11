package controller;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import model.Usuario;
import vista.GestionPanel;

public class GestionController<T> {
  private final GestionPanel<T> vista;
  private final Usuario usuario;
  private final Callable<List<T>> listar;

  @FunctionalInterface
  public interface Eliminar<T> {
    void ejecutar(T objeto) throws Exception;
  }

  private final Eliminar<T> eliminar;

  public GestionController(
      GestionPanel<T> vista,
      Usuario usuario,
      Callable<List<T>> listar,
      Consumer<T> abrirFormulario,
      Eliminar<T> eliminar) {
    this.vista = vista;
    this.usuario = usuario;
    this.listar = listar;
    this.eliminar = eliminar;
    vista.alActualizar(e -> cargar());
    vista.alAgregar(
        e -> {
          if (permiso()) abrirFormulario.accept(null);
        });
    vista.alEditar(
        e -> {
          if (!permiso()) return;
          T item = vista.seleccionado();
          if (item == null) aviso();
          else abrirFormulario.accept(item);
        });
    vista.alEliminar(e -> borrar());
    cargar();
  }

  private boolean permiso() {
    if (usuario.esBibliotecario()) return true;
    JOptionPane.showMessageDialog(vista, "Acción exclusiva del bibliotecario.");
    return false;
  }

  private void aviso() {
    JOptionPane.showMessageDialog(vista, "Selecciona un registro.");
  }

  public void cargar() {
    Tareas.ejecutar(vista, vista::setCargando, listar, vista::mostrar);
  }

  private void borrar() {
    if (!permiso()) return;
    T item = vista.seleccionado();
    if (item == null) {
      aviso();
      return;
    }
    if (JOptionPane.showConfirmDialog(
            vista, "¿Eliminar el registro seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION)
        != JOptionPane.YES_OPTION) return;
    guardar(
        () -> {
          eliminar.ejecutar(item);
          return null;
        });
  }

  public void guardar(Callable<Void> tarea) {
    if (!permiso()) return;
    Tareas.ejecutar(
        vista,
        vista::setCargando,
        tarea,
        ignored -> {
          JOptionPane.showMessageDialog(vista, "Operación completada.");
          cargar();
        });
  }
}
