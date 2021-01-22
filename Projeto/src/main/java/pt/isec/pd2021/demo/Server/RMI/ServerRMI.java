package pt.isec.pd2021.demo.Server.RMI;

import pt.isec.pd2021.demo.Client.RMI.ClientRMIInt;
import pt.isec.pd2021.demo.RMIObserver.RMIObserverInterface;
import pt.isec.pd2021.demo.Server.SQL_Methods;
import pt.isec.pd2021.demo.Server.clientSockets;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerRMI extends UnicastRemoteObject implements ServerRMIInterface {
    public static final String SERVICE_NAME = "GetRemoteFile";
    int serverNumber;
    SQL_Methods sb;
    List<RMIObserverInterface> observers;
    List<ClientRMIInt> clientesLogados;

    public ServerRMI() throws RemoteException, ClassNotFoundException, SQLException {
        observers = new ArrayList<>();
        clientesLogados = new ArrayList<>();
        sb = new SQL_Methods();
    }

    public synchronized void addObserver(RMIObserverInterface observer) throws java.rmi.RemoteException {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("+ um observador.");
        }
    }

    public synchronized void removeObserver(RMIObserverInterface observer) throws java.rmi.RemoteException {
        if (observers.remove(observer))
            System.out.println("- um observador.");
    }

    public synchronized void notifyObservers(String msg) {
        int i;

        for (i = 0; i < observers.size(); i++) {
            try {
                observers.get(i).notifyNewOperationConcluded(msg);
            } catch (RemoteException e) {
                observers.remove(i--);
                System.out.println("- um observador (observador inacessivel).");
            }
        }
    }

    public void registoCliente(String comando, String descricao, String id_Sender,ClientRMIInt user) throws java.rmi.RemoteException, SQLException {

        String aux = descricao;
        System.out.println("Recebido:" + comando + " " + descricao + " " + id_Sender);
        clientSockets xpto = new clientSockets();
        String name = id_Sender;

        if(user == null){
            String pw = descricao;
            sb.adicionaUser(id_Sender, pw, "0.0.0.0", 0);
            return;
        }
        if (sb.getUser(id_Sender) == null) {
            String pw = descricao;

            if (!sb.adicionaUser(id_Sender, pw, "0.0.0.0", 0)) {
                user.receberMensagem(sb.getLOG());
                //System.out.println(sql.getLOG());
            } else {
                user.receberMensagem("Foi registado com sucesso");
                //System.out.println("Foi registado com sucesso");
            }
        } else if (sb.getPasswordUser(id_Sender).equals(descricao)) {
            clientesLogados.add(user);
            notifyObservers("Utilizador entrou no server");
            //System.out.println("Bem vindo");
        } else {
            user.receberMensagem("Password errada");
        }

        notifyObservers("Registo concluido");
    }

    public void enviaMensagem(String conteudo) throws java.rmi.RemoteException {
        for (var it : clientesLogados)
            it.receberMensagem(conteudo);
        notifyObservers("mensagem: " + conteudo);
    }

    public void enviaMensagem(String conteudo, String destinatario) throws java.rmi.RemoteException {
        for(var it : clientesLogados) {
            if(it.getNome() == destinatario)
                it.receberMensagem(conteudo);
        }
    }

    public static void main(String[] args) {

        try{
            Registry r;
            try{
                System.out.println("Tentativa de lancamento do registry no porto " +
                        Registry.REGISTRY_PORT + "...");

                r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

                System.out.println("Registry lancado!");

            } catch(RemoteException e){
                System.out.println("Registry provavelmente ja' em execucao!");
                r = LocateRegistry.getRegistry();
            }

            /*
             * Cria o servico
             */

            ServerRMI serverRegisto = new ServerRMI();

            System.out.println("Servico GetRemoteFile criado e em execucao ("+serverRegisto.getRef().remoteToString()+"...");

            /*
             * Regista o servico no rmiregistry local para que os clientes possam localiza'-lo, ou seja,
             * obter a sua referencia remota (endereco IP, porto de escuta, etc.).
             */

            r.bind(SERVICE_NAME, serverRegisto);

            System.out.println("Servico " + SERVICE_NAME + " registado no registry...");

            /*
             * Para terminar um servico RMI do tipo UnicastRemoteObject:
             *
             *  UnicastRemoteObject.unexportObject(fileService, true);
             */

        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
