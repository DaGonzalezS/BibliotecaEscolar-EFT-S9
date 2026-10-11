package interfaces;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlOperation<T> {
  T ejecutar(Connection connection) throws SQLException;
}
