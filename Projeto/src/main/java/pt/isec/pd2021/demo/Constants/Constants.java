package pt.isec.pd2021.demo.Constants;

import java.io.Serializable;

public class Constants implements Serializable {
    public static final String REFERENCE_SERVER_ADRESS = "localhost";
    private static int SERVER_UDP_PORT = 6000;
    private static int SERVER_TCP_PORT = 6001;

    private static String multicastIP = "239.1.2.3";
    private static int multicastPort = 5432;

    public static final int MAX_SIZE = 1024;
    public static final int TIMEOUT = 10; //seconds

    public static final String CONNECTION_REQUEST = "CONNECT";

    public static int getServerUdpPort() {
        return SERVER_UDP_PORT;
    }

    public static int getServerTcpPort() {
        return SERVER_TCP_PORT;
    }
}