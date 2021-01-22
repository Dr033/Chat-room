package pt.isec.pd2021.demo.Server;


import pt.isec.pd2021.demo.Constants.ConnectionResponse;
import pt.isec.pd2021.demo.Constants.PairPortCap;

import java.io.*;
import java.net.*;

import static pt.isec.pd2021.demo.Constants.Constants.CONNECTION_REQUEST;
import static pt.isec.pd2021.demo.Constants.Constants.MAX_SIZE;

class UDPConnectionSlave extends Thread {
    DatagramSocket socket;
    DatagramPacket packet;
    int ocupiedCap;
    int myId;
    private static int LISTENING_PORT_TCP;

    public UDPConnectionSlave(DatagramSocket socket, DatagramPacket packet, int ocupiedCap, int myId,int port) {
        this.LISTENING_PORT_TCP = port;
        this.socket = socket;
        this.myId = myId;
        this.ocupiedCap = ocupiedCap;
        this.packet = packet;
    }

    public static boolean isOverCharged() {
        return false;
    }

    @Override
    public void run() {
        //DatagramPacket packet;
        try {
            ObjectInputStream in;
            Object receivedObj;

            ConnectionResponse response;
            String str = new String();

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bOut);

            //packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            //socket.receive(packet);

            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            receivedObj = (String) in.readObject();
            if (receivedObj.getClass() == str.getClass()) {
                str = (String) receivedObj;
            } else {
                return;
            }

            if (str.equalsIgnoreCase(CONNECTION_REQUEST) == false) {
                return;
            }

            response = new ConnectionResponse();
            response.addPairPortCap(new PairPortCap("localhost", LISTENING_PORT_TCP, ocupiedCap));
            //adicionar restantes servers

            if (isOverCharged() == false) {
                response.setAvailable(true);
            } else {
                response.setAvailable(false);
            }

            InetAddress clientAdress = packet.getAddress();
            int clientPort = packet.getPort();

            out.writeUnshared(response);
            out.flush();

            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), clientAdress, clientPort);
            socket.send(packet);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

public class ServerUDP extends Thread {
    DatagramSocket socket;
    int ocupiedCap;
    private static boolean alive;
    private static int LISTENING_PORT_TCP;

    public ServerUDP(DatagramSocket socket, int ocupiedCap, int port) {
        this.LISTENING_PORT_TCP = port;
        alive=true;
        this.socket = socket;
        this.ocupiedCap = ocupiedCap;
    }

    public static void terminate() {
        alive = false;
    }

    @Override
    public void run() {
        Thread t;
        int threadID = 0;

        try {
            while (alive) {
                DatagramPacket packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                try {
                    socket.setSoTimeout(500);
                    socket.receive(packet);
                }catch (SocketTimeoutException e){
                    continue;
                }

                new UDPConnectionSlave(socket, packet, ocupiedCap, ++threadID,LISTENING_PORT_TCP).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}