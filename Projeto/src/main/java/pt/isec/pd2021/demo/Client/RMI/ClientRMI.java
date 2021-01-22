package pt.isec.pd2021.demo.Client.RMI;

import pt.isec.pd2021.demo.Server.RMI.ServerRMIInterface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientRMI extends UnicastRemoteObject implements ClientRMIInt {

    static private String nome;

    public ClientRMI() throws RemoteException {

    }

    public void receberMensagem(String conteudo) throws java.rmi.RemoteException {
        System.out.println(conteudo);
    }

    public String getNome() throws java.rmi.RemoteException {
        return nome;
    }

    public static void menu(ServerRMIInterface remoteFileService, ClientRMI myRemoteService) throws RemoteException, SQLException {


        while(true) {
            System.out.print("\n\nIntroduza o comando: ");
            Scanner sc = new Scanner(System.in);
            String comando = sc.nextLine().toLowerCase();

            if(comando.compareTo("adiciona") == 0){
                System.out.print("Nome: ");
                String nome = sc.nextLine();

                System.out.print("Pass: ");
                String pass = sc.nextLine();

                remoteFileService.registoCliente("", nome, pass, myRemoteService);
                remoteFileService.enviaMensagem("User "+ nome + "adicionado");
            }
        }
    }

    public static void main(String[] args) {
        // Fazer aqui tipo menu


        String objectUrl;

        FileOutputStream localFileOutputStream = null;

        ClientRMI myRemoteService = null;
        ServerRMIInterface remoteFileService;

        /*
         * Trata os argumentos da linha de comando
         */

        if(args.length != 1){
            System.out.print("Deve passar na linha de comando: (1) a localizacao do servico ");
            return;
        }

        objectUrl = "rmi://"+args[0]+"/GetRemoteFile";
        try{
            /*
             * Obtem a referencia remota para o servico com nome "GetRemoteFile"
             */
            remoteFileService = (ServerRMIInterface) Naming.lookup(objectUrl);

            /*
             * Lanca o servico local para acesso remoto por parte do servidor.
             */
            myRemoteService = new ClientRMI();

            menu(remoteFileService,myRemoteService);
            //menu chamado para aqui:


        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
        }catch(NotBoundException e){
            System.out.println("Servico remoto desconhecido - " + e);
        }catch(IOException e){
            System.out.println("Erro E/S - " + e);
        }catch(Exception e){
            System.out.println("Erro - " + e);
        }finally{
            if(myRemoteService != null){
                /*
                 * Termina o serviço local
                 */
                try{
                    UnicastRemoteObject.unexportObject(myRemoteService, true);
                }catch(NoSuchObjectException e){}
            }
        }
    }
    }
