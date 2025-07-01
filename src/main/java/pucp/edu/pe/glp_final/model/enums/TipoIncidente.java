package pucp.edu.pe.glp_final.model.enums;

public enum TipoIncidente {
    TI1(2), TI2(6), TI3(72);   // ejemplo

    private final int horasReparacion;
    TipoIncidente(int horasReparacion) { this.horasReparacion = horasReparacion; }
    public int getHorasReparacion() { return horasReparacion; }
}