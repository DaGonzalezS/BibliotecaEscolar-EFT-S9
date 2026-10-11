package dao;

import java.sql.*;
import model.Categoria;

public class CategoriaDAO extends BaseDAO<Categoria> {
  protected String tabla() {
    return "categorias";
  }

  protected String columnas() {
    return "nombre";
  }

  protected int id(Categoria c) {
    return c.getId();
  }

  protected void cargar(PreparedStatement s, Categoria c) throws SQLException {
    s.setString(1, c.getNombre());
  }

  protected Categoria leer(ResultSet r) throws SQLException {
    return new Categoria(r.getInt("id"), r.getString("nombre"));
  }
}
