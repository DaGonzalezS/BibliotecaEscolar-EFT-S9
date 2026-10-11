package vista;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginVista extends JFrame {
  private final JTextField rut = new JTextField();
  private final JPasswordField clave = new JPasswordField();
  private final JButton ingresar = new JButton("Ingresar");
  private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);

  public LoginVista() {
    setTitle("Biblioteca Escolar");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    JPanel panel = new JPanel(new BorderLayout(0, 20));
    panel.setBorder(new EmptyBorder(25, 30, 25, 30));
    JLabel titulo = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
    titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
    JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
    form.add(new JLabel("RUT sin puntos"));
    form.add(rut);
    form.add(new JLabel("Contraseña"));
    form.add(clave);
    JPanel inferior = new JPanel(new BorderLayout(0, 10));
    inferior.add(ingresar, BorderLayout.NORTH);
    inferior.add(mensaje, BorderLayout.SOUTH);
    panel.add(titulo, BorderLayout.NORTH);
    panel.add(form, BorderLayout.CENTER);
    panel.add(inferior, BorderLayout.SOUTH);
    setContentPane(panel);
    getRootPane().setDefaultButton(ingresar);
    setSize(420, 350);
    setResizable(false);
    setLocationRelativeTo(null);
  }

  public String getRut() {
    return rut.getText().trim().toUpperCase();
  }

  public String getClave() {
    return new String(clave.getPassword());
  }

  public void alIngresar(ActionListener a) {
    ingresar.addActionListener(a);
  }

  public void mensaje(String m) {
    mensaje.setText(m);
  }

  public void setCargando(boolean c) {
    ingresar.setEnabled(!c);
    rut.setEnabled(!c);
    clave.setEnabled(!c);
    ingresar.setText(c ? "Ingresando..." : "Ingresar");
  }

  public void limpiarClave() {
    clave.setText("");
  }
}
