package com.juegito.model;

import java.util.Objects;

/**
 * Representa coordenadas en un sistema hexagonal usando coordenadas cúbicas (q, r, s).
 * Las coordenadas cúbicas garantizan que q + r + s = 0.
 */
public class HexCoordinate {
    private final int q; // Columna
    private final int r; // Fila
    private final int s; // Diagonal (calculado)
    
    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
        this.s = -q - r;
    }
    
    public int getQ() {
        return q;
    }
    
    public int getR() {
        return r;
    }
    
    public int getS() {
        return s;
    }
    
    /**
     * Calcula la distancia Manhattan entre esta coordenada y otra.
     */
    public int distanceTo(HexCoordinate other) {
        return (Math.abs(q - other.q) + Math.abs(r - other.r) + Math.abs(s - other.s)) / 2;
    }
    
    /**
     * Retorna una lista de coordenadas vecinas a esta casilla.
     */
    public HexCoordinate[] getNeighbors() {
        return new HexCoordinate[] {
            new HexCoordinate(q + 1, r),      // Este
            new HexCoordinate(q + 1, r - 1),  // Noreste
            new HexCoordinate(q, r - 1),      // Noroeste
            new HexCoordinate(q - 1, r),      // Oeste
            new HexCoordinate(q - 1, r + 1),  // Suroeste
            new HexCoordinate(q, r + 1)       // Sureste
        };
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexCoordinate that = (HexCoordinate) o;
        return q == that.q && r == that.r && s == that.s;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(q, r, s);
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", q, r, s);
    }
}
