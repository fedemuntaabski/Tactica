package com.juegito.protocol.dto;

/**
 * DTO para transferir información de coordenadas hexagonales.
 */
public class HexCoordinateDTO {
    private int q;
    private int r;
    private int s;
    
    public HexCoordinateDTO() {}
    
    public HexCoordinateDTO(int q, int r, int s) {
        this.q = q;
        this.r = r;
        this.s = s;
    }
    
    public int getQ() {
        return q;
    }
    
    public void setQ(int q) {
        this.q = q;
    }
    
    public int getR() {
        return r;
    }
    
    public void setR(int r) {
        this.r = r;
    }
    
    public int getS() {
        return s;
    }
    
    public void setS(int s) {
        this.s = s;
    }
}
