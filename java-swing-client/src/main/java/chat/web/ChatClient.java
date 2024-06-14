package chat.web;

import chat.config.AppProps;
import chat.config.BeanFactory;
import chat.filter.EncryptingFilter;
import chat.web.model.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ChatClient {

    private static final String SELF_PORT = "self.port";

    private final ResponseRouter responseRouter = BeanFactory.responseRouter();

    private static DatagramSocket datagramSocket;

    private boolean stopped = false;

    static {
        try {
            datagramSocket = new DatagramSocket(Integer.parseInt(AppProps.getProp(SELF_PORT)));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message, InetAddress inetAddress, int port) {
        System.out.println("I'm sending: %s".formatted(message.toJson()));

        String encryptedMessage = new EncryptingFilter().process(message);

        byte[] data = encryptedMessage.getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, inetAddress, port);
        try {
            this.datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        while (!stopped) {
            try {
                byte[] buff = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buff, buff.length);
                this.datagramSocket.receive(receivePacket);
                String data = new String(receivePacket.getData()).trim();
                System.out.println("Received data: %s".formatted(data));

                responseRouter.process(data);

            } catch (IOException e) {
                stopped = true;
                e.printStackTrace();
            }
        }
    }
}
