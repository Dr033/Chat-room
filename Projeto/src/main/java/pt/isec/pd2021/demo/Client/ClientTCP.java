package pt.isec.pd2021.demo.Client;

import pt.isec.pd2021.demo.Constants.ConnectionResponse;
import pt.isec.pd2021.demo.Constants.MSG;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

class ClientTCP {
    static ConnectionResponse response;
    static Socket socket;
    static OutputStream out;
    InputStream in;
    private static boolean alive;
    private String id;
    /* String que separa os vários parametros dos comandos
    tem de ser coerente com o separator em ServerTCP */
    private String separator;

    public ClientTCP(ConnectionResponse response) {
        alive = true;
        this.response = response;
        this.separator = ".";//String que separa os vários parametros dos comandos

        trataComando("login");

        ReceiveMesseges rm = new ReceiveMesseges();
        rm.start();

        Scanner sc = new Scanner(System.in);
        while (alive) {
//            System.out.println("\nClicar enter para inserir commandos -\n\n");
//            sc.nextLine();
            System.out.print("-> ");
            String str = sc.nextLine();

            trataComando(str);
        }
    }

    //TODO
    private void trataComando(String cmd) {
        if (cmd.toLowerCase(Locale.ROOT).equals("login")) {
            comandoLogIn();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("criar canal")) {
            comandoCriarCanal();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("entra canal")) {
            comandoEntraCanal();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("apaga canal")) {
            comandoApagaCanal();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("msg privada")) {
            comandoEnviaMsgPrivada();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("msg canal")) {
            comandoEnviaMsgCanal();
        }

        if (cmd.toLowerCase(Locale.ROOT).equals("sair")) {
            try {
                socket.close();
                alive = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private void comandoEntraCanal() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduza o nome do canal: ");
        String channelName = sc.nextLine();
        if (channelName.length() < 1) {
            System.out.println("Necessita de introduzir o nome do canal");
            return;
        }

        System.out.print("Introduza a password do canal: ");
        String password = sc.nextLine();
        if (password.length() < 1) {
            System.out.println("Necessita de uma password");
            return;
        }

        if (!enviaResposta("entra canal", channelName + separator + password, id)) {
            System.out.println("Erro a entrar no server");
            return;
        }
    }

    private void comandoCriarCanal() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduza o nome do canal: ");
        String channelName = sc.nextLine();
        if (channelName.length() < 1) {
            System.out.println("Necessita de introduzir o nome do canal");
            return;
        }

        System.out.print("Introduza a password do canal: ");
        String password = sc.nextLine();
        if (password.length() < 1) {
            System.out.println("Necessita de uma password");
            return;
        }

        if (!enviaResposta("criar canal", channelName + separator + password, id)) {
            System.out.println("Erro a entrar no server");
            return;
        }
    }

    private void comandoApagaCanal() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduza o nome do canal: ");
        String channelName = sc.nextLine();
        if (channelName.length() < 1) {
            System.out.println("Necessita de introduzir o nome do canal");
            return;
        }

        if (!enviaResposta("apaga canal", channelName + separator, id)) {
            System.out.println("Erro a entrar no server");
            return;
        }
    }

    private void comandoLogIn() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduza o seu username: ");
        String username = sc.nextLine();
        if (username.length() < 1) {
            System.out.println("Necessita de introduzir username");
            return;
        }

        System.out.print("Introduza a sua password: ");
        String password = sc.nextLine();
        if (password.length() < 1) {
            System.out.println("Necessita de uma password");
            return;
        }

        if (!enviaResposta("login", password, username)) {
            System.out.println("Erro a entrar no server");
            return;
        }
        id = username;
    }

    private void comandoEnviaMsgPrivada(){
        Scanner sc = new Scanner(System.in);

        System.out.print("Destinatário: ");
        String dest = sc.nextLine();
        if (dest.length() < 1) {
            System.out.println("Necessita de um destinatário");
            return;
        }

        System.out.print("Mensagem: ");
        String msg = sc.nextLine();
        if (msg.length() < 1) {
            System.out.println("Mensagem necessita de conteúdo");
            return;
        }

        if (!enviaResposta("msg privada", dest + separator + msg, id)) {
            System.out.println("Erro a entrar no server");
            return;
        };
    }

    private void comandoEnviaMsgCanal(){
        Scanner sc = new Scanner(System.in);

        System.out.print("canal: ");
        String dest = sc.nextLine();
        if (dest.length() < 1) {
            System.out.println("Necessita de um canal");
            return;
        }

        System.out.print("Mensagem: ");
        String msg = sc.nextLine();
        if (msg.length() < 1) {
            System.out.println("Mensagem necessita de conteúdo");
            return;
        }

        if (!enviaResposta("msg canal", dest + separator + msg, id)) {
            System.out.println("Erro a entrar no server");
            return;
        };
    }

    private static void terminaThread() {
        alive = false;
    }

    public static void terminate() {
        alive = false;
    }

    private static boolean enviaResposta(String comando, String descricao, String username) {
        try {
            socket = new Socket(response.getBestPort().getAdress(), response.getBestPort().getPort());
            out = socket.getOutputStream();
        } catch (ConnectException e) {
            System.out.println("Servidor foi desligado");
            terminaThread();
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MSG rq = new MSG(comando, descricao, username);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream outObj = null;
            outObj = new ObjectOutputStream(bos);
            outObj.writeObject(rq);
            outObj.flush();
            byte[] yourBytes = bos.toByteArray();
            OutputStream sendObject = socket.getOutputStream();
            sendObject.write(yourBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    class ReceiveMesseges extends Thread {
        MSG msg;

        public ReceiveMesseges() {

        }

        private void leResposta() {
            try {
                try {
                    in = socket.getInputStream();
                } catch (NullPointerException e) {
                    return;
                } catch (SocketException e) {
                    if (alive)
                        System.out.println("Servidor foi desligado");
                    terminate();
                    return;
                }
                byte[] bufStr = new byte[2560];
                while (!socket.isClosed()) {
                    int nBytes = in.read(bufStr);
                    ByteArrayInputStream bis = new ByteArrayInputStream(bufStr);
                    ObjectInput oin = null;
                    oin = new ObjectInputStream(bis);
                    Object o = oin.readObject();
                    MSG msg = (MSG) o;

                    System.out.print(msg.getDescricao() + "\n-> ");
                    //socket.close();
                }
            } catch (SocketException e) {
                if (alive)
                    System.out.println("Servidor foi desligado");
                terminate();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            while (alive) {
                leResposta();
            }
        }
    }
}
