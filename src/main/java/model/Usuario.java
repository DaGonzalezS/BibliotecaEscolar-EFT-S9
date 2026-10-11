package model;

public class Usuario extends Persona {
  private String rol;
  private String clave;

  public Usuario(int id, String nombre, String rut, String correo, String rol, String clave) {
    super(id, nombre, rut, correo);
    this.rol = rol;
    this.clave = clave;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public Usuario(int id, String nombre, String rut, String correo, String rol) {
    this(id, nombre, rut, correo, rol, null);
  }

  public boolean esBibliotecario() {
    return "bibliotecario".equals(rol);
  }

  @Override
  public String getTipo() {
    return rol;
  }
}
