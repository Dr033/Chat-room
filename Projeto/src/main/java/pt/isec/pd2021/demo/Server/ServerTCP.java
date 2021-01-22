package pt.isec.pd2021.demo.Server;


import pt.isec.pd2021.demo.Constants.MSG;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ServerTCP extends Thread {
    static Socket socket;
    static ServerSocket serverSocket;
    private static int LISTENING_PORT_TCP;
    public static SQL_Methods sql;
    private static MSG rq;
    private static boolean alive;

    /* String que separa os vários parametros dos comandos
    tem de ser coerente com o separator em ClientTCP */
    private String separator;
    private static ArrayList<clientSockets> listaSockets = new ArrayList<>();

    public ServerTCP(int port) {
        this.LISTENING_PORT_TCP = port;
        alive = true;
        sql = new SQL_Methods();
        this.separator = ".";
    }

    public static void terminate() {
        alive = false;
    }

    private void setSocket(String nome) {
        for (int i = 0; i < listaSockets.size(); i++) {
            if (listaSockets.get(i).name.equals(nome)) {
                socket = listaSockets.get(i).socket;
                break;
            }
        }
    }

    private void respondeAoClient(String resposta) {
        try {
            OutputStream out = socket.getOutputStream();

            rq.setComando("resposta");
            rq.setDescricao(resposta);

            System.out.println(socket.getRemoteSocketAddress().toString());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream outObj = null;
            outObj = new ObjectOutputStream(bos);
            outObj.writeObject(rq);
            outObj.flush();
            byte[] yourBytes = bos.toByteArray();
            OutputStream sendObject = socket.getOutputStream();
            sendObject.write(yourBytes);
            sendObject.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO
    private void trataComanddo(String comando, String descricao, String id_Sender) {
        String aux = descricao;
        System.out.println("Recebido:" + comando + " " + descricao + " " + id_Sender);
        clientSockets xpto = new clientSockets();
        xpto.name = id_Sender;
        xpto.socket = socket;

        boolean entrou = false;
        for (int i = 0; i < listaSockets.size(); i++) {
            if (listaSockets.get(i).name.equals(id_Sender)) {
                entrou = true;
                break;
            }
        }
        if (!entrou) {
            listaSockets.add(xpto);
        }


        if (comando.equals("login")) {
            if (sql.getUser(id_Sender) == null) {
                String pw = descricao;
                String ip = socket.getRemoteSocketAddress().toString().substring(1, socket.getRemoteSocketAddress().toString().indexOf(":"));
                System.out.println(socket.getRemoteSocketAddress());

                if (!sql.adicionaUser(id_Sender, pw, ip, socket.getPort())) {
                    respondeAoClient(sql.getLOG());
                    //System.out.println(sql.getLOG());
                } else {
                    respondeAoClient("Foi registado com sucesso");
                    //System.out.println("Foi registado com sucesso");
                }
            } else if (sql.getPasswordUser(id_Sender).equals(descricao)) {
                respondeAoClient("Bem vindo");
                //System.out.println("Bem vindo");
            } else {
                respondeAoClient("Password errada");
            }
        }

        if (comando.equals("criar canal")) {
            String name = descricao.substring(0, descricao.indexOf(separator));
            String pw = descricao.substring(descricao.indexOf(separator) + 1);

            if (sql.getCanal(name) == null) {
                if (!sql.adicionaChannel(name, id_Sender, pw)) {
                    respondeAoClient(sql.getLOG());
                    //System.out.println(sql.getLOG());
                } else {
                    setSocket(id_Sender);
                    sql.adicionaUserAoCanal(name, id_Sender);
                    respondeAoClient("Canal " + name + " criado com sucesso");
                    //System.out.println("Foi registado com sucesso");
                }
            } else {
                setSocket(id_Sender);
                respondeAoClient("Canal " + name + " já existe");
            }
        }

        if (comando.equals("entra canal")) {
            String canal = descricao.substring(0, descricao.indexOf(separator));
            String pw = descricao.substring(descricao.indexOf(separator) + 1);

            if (sql.getCanal(canal) != null) {
                if (sql.getPwCanal(canal).equals(pw)) {
                    sql.adicionaUserAoCanal(canal, id_Sender);
                    setSocket(id_Sender);
                    respondeAoClient("Foi registado no canal");
                    return;
                }
                setSocket(id_Sender);
                respondeAoClient("PW errada");
                return;
            }
            setSocket(id_Sender);
            respondeAoClient("Canal nao existe");
            return;
        }

        if (comando.equals("apaga canal")) {
            String canal = descricao.substring(0, descricao.indexOf(separator));

            if (sql.getCanal(canal) != null) {
                if (sql.getOwnerCanal(canal).equals(id_Sender)) {
                    if (sql.apagaCanal(canal))
                        respondeAoClient("Canal Apagado");
                    else
                        respondeAoClient("Erro a apagar o canal");
                    return;
                }
                setSocket(id_Sender);
                respondeAoClient("So o criador do canal pode apagar o canal");
                return;
            }
            setSocket(id_Sender);
            respondeAoClient("Canal nao existe");
            return;
        }

        if (comando.equals("msg privada")) {
            String rec = descricao.substring(0, descricao.indexOf(separator));
            String msg = descricao.substring(descricao.indexOf(separator) + 1);

            if (sql.enviaMSGPrivada(id_Sender, rec, msg)) {
                setSocket(rec);
                respondeAoClient("[" + id_Sender + "]: " + msg);
            } else {
                setSocket(id_Sender);
                respondeAoClient("Nao existe o user introduzido");
            }
        }

        if (comando.equals("msg canal")) {
            String canal = descricao.substring(0, descricao.indexOf(separator));
            String msg = descricao.substring(descricao.indexOf(separator) + 1);

            if (sql.enviaMSGCanal(id_Sender, canal, msg)) {
                String str = sql.getUsersCanal(canal);
                while (str.length() > 1) {
                    String name = str.substring(0, str.indexOf(separator));
                    str = str.substring(str.indexOf(separator) + 1);
                    setSocket(name);
                    String mg = "[" + canal + "]" + "[" + id_Sender + "]" + msg;
                    respondeAoClient(mg);
                }
                return;
            }
            setSocket(id_Sender);
            respondeAoClient("Erro");
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(LISTENING_PORT_TCP);

            while (alive) {
                try {
                    serverSocket.setSoTimeout(500);
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }

                InputStream recebi = socket.getInputStream();

                byte[] bufStr = new byte[2560];
                int nBytes = recebi.read(bufStr);
                ByteArrayInputStream bis = new ByteArrayInputStream(bufStr);
                ObjectInput in = null;
                in = new ObjectInputStream(bis);
                Object o = in.readObject();
                rq = (MSG) o;

                trataComanddo(rq.getComando(), rq.getDescricao(), rq.getId_Cliente());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}