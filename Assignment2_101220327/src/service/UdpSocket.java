package service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class UdpSocket {
    private static final Logger logger = LoggerService.getLogger();
    private DatagramSocket sendSocket, receiveSocket;

    public UdpSocket(int port) {
        try {
            receiveSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.info("Failed to set UDP receive socket (port: " + port + ")");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public UdpSocket() {

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

    public void send(DatagramPacket packet) {
        try {
            if (sendSocket == null) {
                sendSocket = new DatagramSocket();
            }

            if (receiveSocket == null) {
                receiveSocket = sendSocket;
            }

            sendSocket.send(packet);
            logger.info("Packet sent to: " + packet.getAddress() + ":" + packet.getPort());

        } catch (Exception e) {
            logger.warning("UDP send failed");
        }
    }

    public void sendBack(DatagramPacket packet) {
        try {
            receiveSocket.send(packet);
            logger.info("Packet sent to: " + packet.getAddress() + ":" + packet.getPort());

        } catch (Exception e) {
            logger.warning("UDP send failed");
        }
    }
}
