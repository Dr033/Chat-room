package pt.isec.pd2021.demo.RMIObserver;

import pt.isec.pd2021.demo.Server.RMI.ServerRMIInterface;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringTokenizer;

public class RMIObserver extends UnicastRemoteObject implements RMIObserverInterface {

    public RMIObserver() throws RemoteException {
    }

    @Override
    public void notifyNewOperationConcluded(String description) throws RemoteException {
        System.out.println(description);
    }

    public static void main(String[] args) {
        try {
//Cria e lanca o servico
            RMIObserver observer = new RMIObserver();
            System.out.println("Servico GetRemoteFileObserver criado e em execucao...");

            //Localiza o servico remoto nomeado "GetRemoteFile"
            String objectUrl = "rmi://" + args[0] + "/GetRemoteFile"; //rmiregistry on localhost

            if (args.length > 0)
                objectUrl = "rmi://" + args[0] + "/GetRemoteFile";

            ServerRMIInterface getRemoteFileService = (ServerRMIInterface) Naming.lookup(objectUrl);

            //adiciona observador no servico remoto
            getRemoteFileService.addObserver(observer);

            System.out.println("<Enter> para terminar...");
            System.out.println();
            System.in.read();

            getRemoteFileService.removeObserver(observer);
            UnicastRemoteObject.unexportObject(observer, true);

        } catch (RemoteException e) {
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        } catch (IOException | NotBoundException e) {
            System.out.println("Erro - " + e);
            System.exit(1);
        }
    }
}
