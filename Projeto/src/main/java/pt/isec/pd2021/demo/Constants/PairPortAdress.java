package pt.isec.pd2021.demo.Constants;

import java.io.Serializable;

public class PairPortAdress implements Serializable {
    private final int port;
    private final String adress;

    public PairPortAdress(String adress, int port){
        this.port = port;
        this.adress = adress;
    }

    public int getPort(){
        return port;
    }

    public String getAdress(){
        return adress;
    }

    @Override
    public String toString() {
        return "PairPortAdress{" +
                "port=" + port +
                ", adress='" + adress + '\'' +
                '}';
    }
}
