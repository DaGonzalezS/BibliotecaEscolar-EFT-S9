package dao;

import java.sql.*;
import model.*;

public class EstudianteDAO extends BaseDAO<Estudiante> {
  protected String tabla() {
    return "estudiantes";
  }

  protected String columnas() {
    return "nombre,rut,correo,curso";
  }

  protected int id(Estudiante e) {
    return e.getId();
  }

  protected void cargar(PreparedStatement s, Estudiante e) throws SQLException {
    s.setString(1, e.getNombre());
    s.setString(2, e.getRut());
    s.setString(3, e.getCorreo());
    s.setString(4, e.getCurso());
  }

  protected Estudiante leer(ResultSet r) throws SQLException {
    return new Estudiante(
        r.getInt("id"),
        r.getString("nombre"),
        r.getString("rut"),
        r.getString("correo"),
        r.getString("curso"));
  }

  public Estudiante buscarPorRut(String rut) throws SQLException {
    return db.consultar(
        conn -> {
          try (PreparedStatement s =
              conn.prepareStatement("SELECT * FROM estudiantes WHERE rut=?")) {
            s.setString(1, rut);
            try (ResultSet r = s.executeQuery()) {
              return r.next() ? leer(r) : null;
            }
          }
        });
  }

  public void guardarConCuenta(Estudiante e, String rutAnterior, String clave) throws SQLException {
    db.transaccion(
        conn -> {
          UsuarioDAO usuarios = new UsuarioDAO();
          Usuario cuenta = usuarios.buscarPorRut(rutAnterior == null ? e.getRut() : rutAnterior);
          if (cuenta != null && cuenta.esBibliotecario())
            throw new SQLException("Ese RUT pertenece al bibliotecario.");
          if (e.getId() == 0) {
            if (cuenta != null) throw new SQLException("Ya existe una cuenta con ese RUT.");
            crear(e);
          } else {
            Estudiante vigente = buscarPorRut(rutAnterior);
            if (vigente == null || vigente.getId() != e.getId())
              throw new SQLException("El estudiante cambió. Actualiza la tabla.");
            actualizar(e);
          }
          if (cuenta == null)
            usuarios.crear(
                new Usuario(0, e.getNombre(), e.getRut(), e.getCorreo(), "estudiante", clave));
          else {
            cuenta.setNombre(e.getNombre());
            cuenta.setRut(e.getRut());
            cuenta.setCorreo(e.getCorreo());
            cuenta.setClave(clave);
            usuarios.actualizar(cuenta);
          }
          return null;
        });
  }

  public void eliminarConCuenta(Estudiante e) throws SQLException {
    db.transaccion(
        conn -> {
          Estudiante vigente = buscarPorRut(e.getRut());
          if (vigente == null || vigente.getId() != e.getId())
            throw new SQLException("El estudiante cambió. Actualiza la tabla.");
          UsuarioDAO usuarios = new UsuarioDAO();
          Usuario cuenta = usuarios.buscarPorRut(e.getRut());
          if (cuenta != null && cuenta.esBibliotecario())
            throw new SQLException("No puedes eliminar al bibliotecario.");
          eliminar(e.getId());
          if (cuenta != null) usuarios.eliminar(cuenta.getId());
          return null;
        });
  }
}
