package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import model.*;

public class PrestamoDAO {
  private final DatabaseConnection db = DatabaseConnection.getInstance();

  private void validarEstudiante(Usuario usuario, int idEstudiante) throws SQLException {
    if (usuario.esBibliotecario()) return;
    Estudiante propio = new EstudianteDAO().buscarPorRut(usuario.getRut());
    if (propio == null || propio.getId() != idEstudiante)
      throw new SQLException("Solo puedes gestionar tus propios préstamos.");
  }

  public void registrar(Usuario usuario, int idEstudiante, int idLibro) throws SQLException {
    db.transaccion(
        conn -> {
          validarEstudiante(usuario, idEstudiante);
          try (PreparedStatement s =
              conn.prepareStatement("UPDATE libros SET stock=stock-1 WHERE id=? AND stock>0")) {
            s.setInt(1, idLibro);
            if (s.executeUpdate() == 0)
              throw new SQLException("El libro no tiene stock disponible.");
          }
          try (PreparedStatement s =
              conn.prepareStatement(
                  "INSERT INTO"
                      + " prestamos(id_estudiante,id_libro,fecha_prestamo,fecha_devolucion,devuelto)"
                      + " VALUES(?,?,?,?,FALSE)")) {
            s.setInt(1, idEstudiante);
            s.setInt(2, idLibro);
            s.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            s.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
            s.executeUpdate();
          }
          return null;
        });
  }

  public void devolver(Usuario usuario, int idPrestamo) throws SQLException {
    db.transaccion(
        conn -> {
          int libro, estudiante;
          try (PreparedStatement s =
              conn.prepareStatement(
                  "SELECT id_libro,id_estudiante,devuelto FROM prestamos WHERE id=? FOR UPDATE")) {
            s.setInt(1, idPrestamo);
            try (ResultSet r = s.executeQuery()) {
              if (!r.next()) throw new SQLException("El préstamo ya no existe.");
              if (r.getBoolean("devuelto"))
                throw new SQLException("Este préstamo ya fue devuelto.");
              libro = r.getInt("id_libro");
              estudiante = r.getInt("id_estudiante");
            }
          }
          validarEstudiante(usuario, estudiante);
          try (PreparedStatement s =
              conn.prepareStatement(
                  "UPDATE prestamos SET devuelto=TRUE,fecha_devolucion_real=? WHERE id=?")) {
            s.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            s.setInt(2, idPrestamo);
            s.executeUpdate();
          }
          try (PreparedStatement s =
              conn.prepareStatement("UPDATE libros SET stock=stock+1 WHERE id=?")) {
            s.setInt(1, libro);
            if (s.executeUpdate() == 0) throw new SQLException("El libro ya no existe.");
          }
          return null;
        });
  }

  public List<Prestamo> listar(Usuario usuario) throws SQLException {
    return db.consultar(
        conn -> {
          Integer estudiante = null;
          if (!usuario.esBibliotecario()) {
            Estudiante propio = new EstudianteDAO().buscarPorRut(usuario.getRut());
            if (propio == null)
              throw new SQLException("Tu cuenta no tiene un estudiante asociado.");
            estudiante = propio.getId();
          }
          String sql =
              """
SELECT p.*, e.nombre AS estudiante_nombre,e.rut,e.correo,e.curso,
       l.titulo,l.autor,l.isbn,l.editorial,l.stock,l.id_categoria,c.nombre AS categoria_nombre
FROM prestamos p JOIN estudiantes e ON e.id=p.id_estudiante
JOIN libros l ON l.id=p.id_libro JOIN categorias c ON c.id=l.id_categoria
"""
                  + (estudiante == null ? "" : " WHERE p.id_estudiante=?")
                  + " ORDER BY p.id DESC";
          List<Prestamo> lista = new ArrayList<>();
          try (PreparedStatement s = conn.prepareStatement(sql)) {
            if (estudiante != null) s.setInt(1, estudiante);
            try (ResultSet r = s.executeQuery()) {
              while (r.next()) {
                Estudiante e =
                    new Estudiante(
                        r.getInt("id_estudiante"),
                        r.getString("estudiante_nombre"),
                        r.getString("rut"),
                        r.getString("correo"),
                        r.getString("curso"));
                Libro l =
                    new Libro(
                        r.getInt("id_libro"),
                        r.getString("titulo"),
                        r.getString("autor"),
                        r.getString("isbn"),
                        r.getString("editorial"),
                        r.getInt("stock"),
                        new Categoria(r.getInt("id_categoria"), r.getString("categoria_nombre")));
                Prestamo p =
                    new Prestamo(
                        r.getInt("id"),
                        e,
                        l,
                        r.getDate("fecha_prestamo").toLocalDate(),
                        r.getDate("fecha_devolucion").toLocalDate(),
                        r.getBoolean("devuelto"));
                java.sql.Date real = r.getDate("fecha_devolucion_real");
                if (real != null) p.setFechaDevolucionReal(real.toLocalDate());
                lista.add(p);
              }
            }
          }
          return lista;
        });
  }
}
