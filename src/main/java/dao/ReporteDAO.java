package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.*;
import model.Reporte;

public class ReporteDAO {
  private final DatabaseConnection db = DatabaseConnection.getInstance();
  private static final String ESTADO =
      """
CASE WHEN p.devuelto=TRUE AND p.fecha_devolucion_real IS NULL THEN 'Devuelto (fecha no informada)'
     WHEN p.devuelto=TRUE AND p.fecha_devolucion_real>p.fecha_devolucion THEN 'Devuelto con atraso'
     WHEN p.devuelto=TRUE THEN 'Devuelto'
     WHEN p.fecha_devolucion<CURDATE() THEN 'Atrasado' ELSE 'En préstamo' END
""";

  public Reporte librosMasPrestados() throws SQLException {
    return consultar(
        "SELECT l.titulo,l.autor,COUNT(p.id) FROM libros l JOIN prestamos p ON p.id_libro=l.id"
            + " GROUP BY l.id,l.titulo,l.autor ORDER BY COUNT(p.id) DESC,l.titulo",
        new String[] {"Libro", "Autor", "Cantidad de préstamos"},
        null);
  }

  public Reporte historialEstudiante(int id) throws SQLException {
    return consultar(
        "SELECT l.titulo,p.fecha_prestamo,p.fecha_devolucion,p.fecha_devolucion_real,"
            + ESTADO
            + " FROM prestamos p JOIN libros l ON l.id=p.id_libro WHERE p.id_estudiante=? ORDER BY"
            + " p.id DESC",
        new String[] {"Libro", "Fecha préstamo", "Vencimiento", "Devolución real", "Estado"},
        id);
  }

  public Reporte prestamosPendientes() throws SQLException {
    return consultar(
        "SELECT e.nombre,e.rut,l.titulo,p.fecha_prestamo,p.fecha_devolucion,"
            + ESTADO
            + " FROM prestamos p JOIN estudiantes e ON e.id=p.id_estudiante JOIN libros l ON"
            + " l.id=p.id_libro WHERE p.devuelto=FALSE ORDER BY p.fecha_devolucion",
        new String[] {"Estudiante", "RUT", "Libro", "Fecha préstamo", "Vencimiento", "Estado"},
        null);
  }

  private Reporte consultar(String sql, String[] columnas, Integer id) throws SQLException {
    return db.consultar(
        conn -> {
          List<Object[]> filas = new ArrayList<>();
          try (PreparedStatement s = conn.prepareStatement(sql)) {
            if (id != null) s.setInt(1, id);
            try (ResultSet r = s.executeQuery()) {
              while (r.next()) {
                Object[] fila = new Object[columnas.length];
                for (int i = 0; i < fila.length; i++) fila[i] = r.getObject(i + 1);
                filas.add(fila);
              }
            }
          }
          return new Reporte(columnas, filas);
        });
  }
}
