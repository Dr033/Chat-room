package pt.isec.pd2021.demo.Server;

import java.net.*;
import java.util.Scanner;

import static pt.isec.pd2021.demo.Constants.Constants.getServerTcpPort;
import static pt.isec.pd2021.demo.Constants.Constants.getServerUdpPort;


public class Server {
    private static int UDP_PORT = getServerUdpPort();
    private static int TCP_PORT = getServerTcpPort();
    private static int nClientes = 0;
    private static int id = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //processUDP();
        try {
            new ServerUDP(new DatagramSocket(UDP_PORT), nClientes, TCP_PORT).start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new ServerTCP(TCP_PORT).start();
        //Auxiliar
        String leComandosServer;
        while (true) {
            System.out.println("\n- Clicar enter para inserir comandos -\n");
            scanner.nextLine();
            System.out.println("----------");
            System.out.print("Comando: ");
            leComandosServer = scanner.nextLine();

            if (leComandosServer.equals("lista users")) {
                String nrJogadores = ServerTCP.sql.listaUsers();
                if (nrJogadores.length() == 0) {
                    System.out.println("Nao existem jogadores conectados ao server");
                } else {
                    System.out.println(ServerTCP.sql.listaUsers());
                }
            }
            if (leComandosServer.equals("exit")) {
                ServerUDP.terminate();
                ServerTCP.terminate();
                ServerTCP.sql.apagaBD();
                return;
            }
            System.out.println("----------");
        }
    }
}