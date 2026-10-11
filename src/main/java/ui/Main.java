package ui;

import javax.swing.*;

import controller.LoginController;
import vista.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {
                        System.err.println("Apariencia predeterminada.");
                    }
                    mostrarLogin();
                });
    }

    private static void mostrarLogin() {
        LoginVista login = new LoginVista();
        new LoginController(
                login,
                usuario -> {
                    PrincipalVista principal = new PrincipalVista(usuario);
                    principal.alCerrarSesion(
                            e -> {
                                principal.dispose();
                                mostrarLogin();
                            });
                    principal.setVisible(true);
                });
        login.setVisible(true);
    }
}
