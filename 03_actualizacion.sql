USE biblioteca;
SET @existe = (SELECT COUNT(*) FROM information_schema.columns
 WHERE table_schema = DATABASE() AND table_name = 'prestamos'
 AND column_name = 'fecha_devolucion_real');
SET @sql = IF(@existe = 0,
 'ALTER TABLE prestamos ADD COLUMN fecha_devolucion_real DATE NULL',
 'SELECT ''La columna fecha_devolucion_real ya existe'' AS resultado');
PREPARE actualizacion FROM @sql;
EXECUTE actualizacion;
DEALLOCATE PREPARE actualizacion;
