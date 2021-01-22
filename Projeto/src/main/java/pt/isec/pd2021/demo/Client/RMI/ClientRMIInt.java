package pt.isec.pd2021.demo.Client.RMI;

public interface ClientRMIInt extends java.rmi.Remote {

    public void receberMensagem(String conteudo) throws java.rmi.RemoteException;
    public String getNome() throws java.rmi.RemoteException;
}
