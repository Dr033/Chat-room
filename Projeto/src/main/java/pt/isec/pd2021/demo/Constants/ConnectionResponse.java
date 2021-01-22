package pt.isec.pd2021.demo.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ConnectionResponse implements Serializable {
    private boolean available;
    private ArrayList<PairPortCap> portList;

    public ConnectionResponse(){
        portList = new ArrayList<>();
        available = false;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void addPairPortCap(PairPortCap portCap){
        this.portList.add(portCap);
    }

    //eliminar se nunca for usado
    public ArrayList<PairPortCap> getPortList() {
        Collections.sort(portList);
        return portList;
    }

    public PairPortAdress getBestPort(){
        if(portList.size() <= 0){
            return null;
        }

        Collections.sort(portList);
        return portList.get(0).getPort();
    }

    @Override
    public String toString() {
        return "ConnectionResponse{" +
                "available=" + available +
                ", portList=" + portList +
                '}';
    }
}
