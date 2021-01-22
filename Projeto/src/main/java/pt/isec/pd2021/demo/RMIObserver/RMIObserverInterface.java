package pt.isec.pd2021.demo.RMIObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIObserverInterface extends Remote {

    public void notifyNewOperationConcluded(String description) throws RemoteException;
}
