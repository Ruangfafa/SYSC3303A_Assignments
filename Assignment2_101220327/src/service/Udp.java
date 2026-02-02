package service;

import common.ConfigLoader;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class Udp {
    private static final Logger logger = LoggerService.getLogger(ConfigLoader.getInstance().ifLogOutput());
    private DatagramSocket sendSocket, receiveSocket;

    public Udp() {
        int port = ConfigLoader.getInstance().getServerReceivePort();
        try {
            receiveSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.info("Failed to set UDP receive socket (port: " + port + ")");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public DatagramSocket getReceiveSocket() {
        return receiveSocket;
    }

    public DatagramPacket receive() {
        try {
            byte[] data = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            logger.info("Waiting for Packet");

            logger.info("Waiting...");
            receiveSocket.receive(receivePacket);

            int len = receivePacket.getLength();
            InetAddress host = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String msg = new String(data, 0, len);
            logger.info("Packet Received");
            logger.info("From: " + host + ":" + port);
            logger.info("Length: " + len);
            logger.info("Containing: " + msg);

            return receivePacket;
        } catch (Exception e) {
            logger.warning("UDP receive failed");
            e.printStackTrace();
            return null;
        }
    }

    public void send(String msg, DatagramPacket request) {
        try {
            byte[] data = msg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(
                    data,
                    data.length,
                    request.getAddress(),
                    request.getPort()
            );

            if (sendSocket == null) {
                sendSocket = new DatagramSocket();
            }

            sendSocket.send(sendPacket);
            logger.info("Sent: " + msg);

        } catch (Exception e) {
            logger.warning("UDP send failed");
        }
    }
}
