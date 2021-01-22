package pt.isec.pd2021.demo.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

class MulticastReceiver extends Thread {
    static ArrayList<String> listaServidoresAntes = new ArrayList<>();
    static ArrayList<String> listaServidoresAgora = new ArrayList<>();
    static ArrayList<String> listaServidoresOff = new ArrayList<>();


    void comeca() {
        Thread t = new Thread(new MulticastReceiver());
        t.start();

        while (true) {
            verificaServidores();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(4321);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);

            while (true) {
                //System.out.println("Waiting for multicast message...");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.setSoTimeout(10000);
                try {
                    socket.receive(packet);
                } catch (SocketTimeoutException w) {
                    System.out.println("Nao ha mais servidores");
                    continue;
                }
                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
                System.out.println("[Multicast UDP message received]>>" + "[" + msg + "]" + " From:" + packet.getSocketAddress().toString());
                if (!listaServidoresAgora.contains(packet.getSocketAddress().toString()))
                    listaServidoresAgora.add(packet.getSocketAddress().toString());
                if ("OK".equals(msg)) {
                    System.out.println("No more message. Exiting : " + msg);
                    break;
                }
                System.out.println("\n");
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void verificaServidores() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int tam = 0;
        if (listaServidoresAgora.size() >= listaServidoresAntes.size()) {
            tam = listaServidoresAgora.size();
        } else {
            tam = listaServidoresAntes.size();
        }

        for (int i = 0; i < tam; i++) {
            if(!listaServidoresAntes.isEmpty()) {
                if (!listaServidoresAgora.contains(listaServidoresAntes.get(i))) {
                    listaServidoresOff.add(listaServidoresAgora.get(i));
                }
            }
        }

        for (int i = 0; i < listaServidoresOff.size(); i++) {
            System.out.println(listaServidoresOff.get(i));
        }
    }

    public static void main(String[] args) {
        MulticastReceiver mr = new MulticastReceiver();
        mr.comeca();
    }
}