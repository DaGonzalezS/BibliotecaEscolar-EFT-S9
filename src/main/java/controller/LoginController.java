package controller;

import dao.UsuarioDAO;
import java.util.function.Consumer;
import model.Usuario;
import vista.LoginVista;

public class LoginController {
  public LoginController(LoginVista vista, Consumer<Usuario> exito) {
    vista.alIngresar(
        event -> {
          String rut = vista.getRut(), clave = vista.getClave();
          if (rut.isBlank() || clave.isBlank()) {
            vista.mensaje("Completa RUT y contraseña.");
            return;
          }
          Tareas.ejecutar(
              vista,
              vista::setCargando,
              () -> new UsuarioDAO().autenticar(rut, clave),
              usuario -> {
                vista.limpiarClave();
                if (usuario == null) {
                  vista.mensaje("RUT o contraseña incorrectos.");
                  return;
                }
                exito.accept(usuario);
                vista.dispose();
              });
        });
  }
}
