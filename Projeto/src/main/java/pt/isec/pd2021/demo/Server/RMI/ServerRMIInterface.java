package pt.isec.pd2021.demo.Server.RMI;


import pt.isec.pd2021.demo.Client.RMI.ClientRMIInt;
import pt.isec.pd2021.demo.RMIObserver.RMIObserverInterface;

import java.sql.SQLException;

public interface ServerRMIInterface extends java.rmi.Remote {
    public void addObserver(RMIObserverInterface observer) throws java.rmi.RemoteException;
    public void removeObserver(RMIObserverInterface observer) throws java.rmi.RemoteException;
    public void registoCliente(String comando, String descricao, String id_Sender, ClientRMIInt user) throws java.rmi.RemoteException, SQLException;
    public void enviaMensagem(String conteudo) throws java.rmi.RemoteException;
    public void enviaMensagem(String conteudo, String destinatario) throws java.rmi.RemoteException;
}
