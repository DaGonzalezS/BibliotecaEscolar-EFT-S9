package dao;

import config.DatabaseConnection;
import interfaces.CrudDAO;
import java.sql.*;
import java.util.*;

public abstract class BaseDAO<T> implements CrudDAO<T> {
  protected final DatabaseConnection db = DatabaseConnection.getInstance();

  protected abstract String tabla();

  protected abstract String columnas();

  protected abstract int id(T objeto);

  protected abstract void cargar(PreparedStatement statement, T objeto) throws SQLException;

  protected abstract T leer(ResultSet result) throws SQLException;

  protected String seleccion() {
    return "SELECT * FROM " + tabla() + " ORDER BY id";
  }

  @Override
  public List<T> listar() throws SQLException {
    return db.consultar(
        conn -> {
          List<T> lista = new ArrayList<>();
          try (PreparedStatement s = conn.prepareStatement(seleccion());
              ResultSet r = s.executeQuery()) {
            while (r.next()) lista.add(leer(r));
          }
          return lista;
        });
  }

  @Override
  public void crear(T objeto) throws SQLException {
    db.consultar(
        conn -> {
          String[] cols = columnas().split(",");
          String sql =
              "INSERT INTO "
                  + tabla()
                  + " ("
                  + columnas()
                  + ") VALUES ("
                  + String.join(",", Collections.nCopies(cols.length, "?"))
                  + ")";
          try (PreparedStatement s = conn.prepareStatement(sql)) {
            cargar(s, objeto);
            s.executeUpdate();
          }
          return null;
        });
  }

  @Override
  public void actualizar(T objeto) throws SQLException {
    db.consultar(
        conn -> {
          String[] cols = columnas().split(",");
          String cambios = String.join(",", Arrays.stream(cols).map(c -> c + "=?").toList());
          try (PreparedStatement s =
              conn.prepareStatement("UPDATE " + tabla() + " SET " + cambios + " WHERE id=?")) {
            cargar(s, objeto);
            s.setInt(cols.length + 1, id(objeto));
            if (s.executeUpdate() == 0) throw new SQLException("El registro ya no existe.");
          }
          return null;
        });
  }

  @Override
  public void eliminar(int id) throws SQLException {
    db.consultar(
        conn -> {
          try (PreparedStatement s =
              conn.prepareStatement("DELETE FROM " + tabla() + " WHERE id=?")) {
            s.setInt(1, id);
            if (s.executeUpdate() == 0) throw new SQLException("El registro ya no existe.");
          }
          return null;
        });
  }
}
