package pt.isec.pd2021.demo.Server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class MulticastSender {
    static DatagramSocket socket;
    static int id_Server;
    static int exp = 0;

    public MulticastSender(int id) {
        try {
            socket = new DatagramSocket();
            socket.setReuseAddress(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        id_Server = id;
        System.out.println("Vou enviar");
        try {
            envia(String.valueOf(id), "230.0.0.0", 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void envia(String message, String ipAddress, int port) throws IOException {
        while (true) {
            InetAddress group = InetAddress.getByName(ipAddress);
            byte[] msg = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
            socket.send(packet);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //socket.close();
        }
    }

    public static void main(String[] args) {
        new MulticastSender(exp++);
    }
}