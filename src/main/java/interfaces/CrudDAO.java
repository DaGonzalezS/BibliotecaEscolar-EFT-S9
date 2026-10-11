package interfaces;

import java.sql.SQLException;
import java.util.List;

public interface CrudDAO<T> {

    void crear(T objeto) throws SQLException;

    List<T> listar() throws SQLException;

    void actualizar(T objeto) throws SQLException;

    void eliminar(int id) throws SQLException;
}