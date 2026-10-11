package model;

import java.util.List;

public record Reporte (String[] columnas, List<Object[]> filas) {}
