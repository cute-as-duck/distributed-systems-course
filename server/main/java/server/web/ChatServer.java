package server.web;

import server.config.AppProps;
import server.router.MessageRouter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ChatServer {

    private static final String PORT_PROP = "server.port";

    private static DatagramSocket datagramSocket;

    private final MessageRouter messageRouter = new MessageRouter();

    static {
        try {
            int serverPort = Integer.parseInt(AppProps.getProp(PORT_PROP));
            datagramSocket = new DatagramSocket(serverPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private volatile boolean stopped = false;

    public void start() {
        System.out.println("Server started...");
        do {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                new Thread(() -> {

                    String data = new String(datagramPacket.getData()).trim();

                    System.out.println("Received: \n" + data);

                    String response = messageRouter.route(data);
                    if (!response.isEmpty()) {
                        sendMessage(datagramPacket.getAddress(), datagramPacket.getPort(), response.toString());
                    }
                }).start();
            } catch (IOException e) {
                stopped = true;
                e.printStackTrace();
            }
        } while (!stopped);
    }

    public void sendMessage(InetAddress ipAddress, int port, String response) {
        byte[] data = response.getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
