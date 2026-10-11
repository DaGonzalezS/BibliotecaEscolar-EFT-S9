package model;

import java.time.LocalDate;

public class Prestamo {
  private int id;
  private Estudiante estudiante;
  private Libro libro;
  private LocalDate fechaPrestamo;
  private LocalDate fechaDevolucion;
  private boolean devuelto;

  public Prestamo(
      int id,
      Estudiante estudiante,
      Libro libro,
      LocalDate fechaPrestamo,
      LocalDate fechaDevolucion,
      boolean devuelto) {
    this.id = id;
    this.estudiante = estudiante;
    this.libro = libro;
    this.fechaPrestamo = fechaPrestamo;
    this.fechaDevolucion = fechaDevolucion;
    this.devuelto = devuelto;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Estudiante getEstudiante() {
    return estudiante;
  }

  public void setEstudiante(Estudiante estudiante) {
    this.estudiante = estudiante;
  }

  public Libro getLibro() {
    return libro;
  }

  public void setLibro(Libro libro) {
    this.libro = libro;
  }

  public LocalDate getFechaPrestamo() {
    return fechaPrestamo;
  }

  public void setFechaPrestamo(LocalDate fechaPrestamo) {
    this.fechaPrestamo = fechaPrestamo;
  }

  public LocalDate getFechaDevolucion() {
    return fechaDevolucion;
  }

  public void setFechaDevolucion(LocalDate fechaDevolucion) {
    this.fechaDevolucion = fechaDevolucion;
  }

  public boolean isDevuelto() {
    return devuelto;
  }

  public void setDevuelto(boolean devuelto) {
    this.devuelto = devuelto;
  }

  private LocalDate fechaDevolucionReal;

  public LocalDate getFechaDevolucionReal() {
    return fechaDevolucionReal;
  }

  public void setFechaDevolucionReal(LocalDate fecha) {
    fechaDevolucionReal = fecha;
  }

  public boolean estaAtrasado() {
    return !devuelto && LocalDate.now().isAfter(fechaDevolucion);
  }

  public boolean fueDevueltoConAtraso() {
    return devuelto && fechaDevolucionReal != null && fechaDevolucionReal.isAfter(fechaDevolucion);
  }

  public String getEstado() {
    if (devuelto)
      return fechaDevolucionReal == null
          ? "Devuelto (fecha no informada)"
          : (fueDevueltoConAtraso() ? "Devuelto con atraso" : "Devuelto");
    return estaAtrasado() ? "Atrasado" : "En préstamo";
  }
}
