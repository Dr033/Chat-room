package pt.isec.pd2021.demo.Client;

import pt.isec.pd2021.demo.Constants.ConnectionResponse;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static pt.isec.pd2021.demo.Constants.Constants.CONNECTION_REQUEST;
import static pt.isec.pd2021.demo.Constants.Constants.MAX_SIZE;

public class Client {
    private static String currentServerAdress;
    private static int currentServerPort;
    private static ConnectionResponse response = null;

    private static void tryConnectionUDP() throws IOException, ClassNotFoundException {
        DatagramSocket socket = null;

        try {
            InetAddress serverAddress = InetAddress.getByName(currentServerAdress);
            int serverPort = currentServerPort;

            response = new ConnectionResponse();
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bOut);
            ObjectInputStream in;
            Object receivedObj;

            socket = new DatagramSocket();
            //socket.setSoTimeout(TIMEOUT * 1000);

            out.writeUnshared(CONNECTION_REQUEST);
            out.flush();
            DatagramPacket packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), serverAddress, serverPort);
            socket.send(packet);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

            receivedObj = (ConnectionResponse) in.readObject();
            if (receivedObj.getClass() == response.getClass()) {
                response = (ConnectionResponse) receivedObj;
            } else {
                response = null;
            }
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static boolean canConnect() {
        while (true) {
            try {
                tryConnectionUDP();
                if (response == null) {
                    System.exit(-1);
                }

                if (response.isAvailable()) {
                    new ClientTCP(response);
                    //entraNoServer();
                    return true;
                } else {
                    currentServerPort = response.getBestPort().getPort();
                    currentServerAdress = response.getBestPort().getAdress();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ERROR:\t" + e);
                return false;
            }
        }
    }

    public static void main(String[] args) {
        if(args.length!=2) {
            System.out.println("Sintaxe: <edereco> <porta>");
            return;
        }
        currentServerAdress = args[0];
        currentServerPort = Integer.parseInt(args[1]);

        canConnect();
    }
}