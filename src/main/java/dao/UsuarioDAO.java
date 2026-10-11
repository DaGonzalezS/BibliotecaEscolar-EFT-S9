package dao;

import java.sql.*;
import model.Usuario;

public class UsuarioDAO extends BaseDAO<Usuario> {
  protected String tabla() {
    return "usuarios";
  }

  protected String columnas() {
    return "nombre,rut,correo,rol,`contraseña`";
  }

  protected int id(Usuario u) {
    return u.getId();
  }

  protected String seleccion() {
    return "SELECT id,nombre,rut,correo,rol FROM usuarios ORDER BY nombre";
  }

  protected void cargar(PreparedStatement s, Usuario u) throws SQLException {
    if (u.getClave() == null || u.getClave().isBlank())
      throw new SQLException("Ingresa una contraseña para crear la cuenta.");
    s.setString(1, u.getNombre());
    s.setString(2, u.getRut());
    s.setString(3, u.getCorreo());
    s.setString(4, u.getRol());
    s.setString(5, u.getClave());
  }

  protected Usuario leer(ResultSet r) throws SQLException {
    return new Usuario(
        r.getInt("id"),
        r.getString("nombre"),
        r.getString("rut"),
        r.getString("correo"),
        r.getString("rol"));
  }

  public Usuario autenticar(String rut, String clave) throws SQLException {
    return db.consultar(
        conn -> {
          try (PreparedStatement s =
              conn.prepareStatement(
                  "SELECT id,nombre,rut,correo,rol FROM usuarios WHERE rut=? AND `contraseña`=?")) {
            s.setString(1, rut);
            s.setString(2, clave);
            try (ResultSet r = s.executeQuery()) {
              return r.next() ? leer(r) : null;
            }
          }
        });
  }

  public Usuario buscarPorRut(String rut) throws SQLException {
    return db.consultar(
        conn -> {
          try (PreparedStatement s =
              conn.prepareStatement("SELECT id,nombre,rut,correo,rol FROM usuarios WHERE rut=?")) {
            s.setString(1, rut);
            try (ResultSet r = s.executeQuery()) {
              return r.next() ? leer(r) : null;
            }
          }
        });
  }

  @Override
  public void actualizar(Usuario u) throws SQLException {
    db.consultar(
        conn -> {
          boolean clave = u.getClave() != null && !u.getClave().isBlank();
          String sql =
              "UPDATE usuarios SET nombre=?,rut=?,correo=?,rol=?"
                  + (clave ? ",`contraseña`=?" : "")
                  + " WHERE id=?";
          try (PreparedStatement s = conn.prepareStatement(sql)) {
            s.setString(1, u.getNombre());
            s.setString(2, u.getRut());
            s.setString(3, u.getCorreo());
            s.setString(4, u.getRol());
            if (clave) s.setString(5, u.getClave());
            s.setInt(clave ? 6 : 5, u.getId());
            if (s.executeUpdate() == 0) throw new SQLException("El usuario ya no existe.");
          }
          return null;
        });
  }
}
