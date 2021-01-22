package pt.isec.pd2021.demo.Constants;

import java.io.Serializable;

public class PairPortCap implements Comparable<PairPortCap>, Serializable {
    private final PairPortAdress port;
    private final int cap;

    public PairPortCap(String adress, int port, int cap){
        this.port = new PairPortAdress(adress, port);
        this.cap = cap;
    }

    public PairPortAdress getPort(){
        return port;
    }

    public int getCap(){
        return cap;
    }

    @Override
    public int compareTo(PairPortCap o) {
        return cap - o.getCap();
    }

    @Override
    public String toString() {
        return "PairPortCap{" +
                "port=" + port +
                ", cap=" + cap +
                '}';
    }
}
