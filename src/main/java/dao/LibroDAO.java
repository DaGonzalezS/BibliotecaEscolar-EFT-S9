package dao;

import java.sql.*;
import model.*;

public class LibroDAO extends BaseDAO<Libro> {
  protected String tabla() {
    return "libros";
  }

  protected String columnas() {
    return "titulo,autor,isbn,editorial,stock,id_categoria";
  }

  protected int id(Libro l) {
    return l.getId();
  }

  protected String seleccion() {
    return "SELECT l.*, c.nombre AS categoria_nombre FROM libros l JOIN categorias c ON"
               + " c.id=l.id_categoria ORDER BY l.titulo";
  }

  protected void cargar(PreparedStatement s, Libro l) throws SQLException {
    if (l.getStock() < 0 || l.getCategoria() == null)
      throw new SQLException("Stock o categoría inválidos.");
    s.setString(1, l.getTitulo());
    s.setString(2, l.getAutor());
    s.setString(3, l.getIsbn());
    s.setString(4, l.getEditorial());
    s.setInt(5, l.getStock());
    s.setInt(6, l.getCategoria().getId());
  }

  protected Libro leer(ResultSet r) throws SQLException {
    return new Libro(
        r.getInt("id"),
        r.getString("titulo"),
        r.getString("autor"),
        r.getString("isbn"),
        r.getString("editorial"),
        r.getInt("stock"),
        new Categoria(r.getInt("id_categoria"), r.getString("categoria_nombre")));
  }

  @Override
  public void actualizar(Libro l) throws SQLException {
    db.consultar(
        conn -> {
          try (PreparedStatement s =
              conn.prepareStatement(
                  "UPDATE libros SET titulo=?,autor=?,isbn=?,editorial=?,stock=?,id_categoria=?"
                      + " WHERE id=? AND stock=?")) {
            cargar(s, l);
            s.setInt(7, l.getId());
            s.setInt(8, l.getStockOriginal());
            if (s.executeUpdate() == 0)
              throw new SQLException(
                  "El libro cambió o ya no existe. Actualiza la tabla y vuelve a editar.");
          }
          return null;
        });
  }
}
