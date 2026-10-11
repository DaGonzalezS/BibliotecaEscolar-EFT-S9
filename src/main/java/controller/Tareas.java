package controller;

import java.awt.Component;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;

public final class Tareas {
  private Tareas() {}

  public static <T> void ejecutar(
      Component vista, Consumer<Boolean> cargando, Callable<T> tarea, Consumer<T> exito) {
    cargando.accept(true);
    new SwingWorker<T, Void>() {
      protected T doInBackground() throws Exception {
        return tarea.call();
      }

      protected void done() {
        cargando.accept(false);
        try {
          exito.accept(get());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          JOptionPane.showMessageDialog(vista, "Operación interrumpida.");
        } catch (ExecutionException e) {
          mostrarError(vista, e.getCause());
        }
      }
    }.execute();
  }

  public static void mostrarError(Component vista, Throwable error) {
    String mensaje = error.getMessage();
    if (error instanceof SQLException sql) {
      mensaje =
          switch (sql.getErrorCode()) {
            case 1062 -> "Ya existe un registro con ese RUT o ISBN.";
            case 1451 -> "No puedes eliminar un registro asociado a libros o préstamos.";
            case 1452 -> "Un registro relacionado ya no existe. Actualiza los datos.";
            case 1054 -> "Falta actualizar la base de datos. Ejecuta sql/03_actualizacion.sql.";
            case 1045 -> "Revisa usuario y contraseña de MySQL en database.properties.";
            default -> mensaje;
          };
    }
    JOptionPane.showMessageDialog(
        vista,
        mensaje == null ? "No se pudo completar la operación." : mensaje,
        "Aviso",
        JOptionPane.WARNING_MESSAGE);
    error.printStackTrace();
  }
}
