package service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

/**
 * Just provides simple methods to send and receive UDP packets
 */
public class UdpSocket {
    private static final Logger logger = LoggerService.getLogger();
    private DatagramSocket sendSocket, receiveSocket;

    /**
     * Constructs a UdpSocket that listens on a specific port.
     *
     * This constructor initializes the receive socket and binds it
     * to the given UDP port.
     *
     * @param port UDP port number to listen on
     */
    public UdpSocket(int port) {
        try {
            receiveSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            logger.info("Failed to set UDP receive socket (port: " + port + ")");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Constructs a UdpSocket without binding to a port.
     *
     * This constructor is typically used when the socket
     * will only be used for sending packets.
     */
    public UdpSocket() {

    }

    /**
     * Wait until a UDP packet is received.
     *
     * @return the received DatagramPacket, or null if an error occurs
     */
    public DatagramPacket receive() {
        try {
            byte[] data = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            logger.info("Waiting for Packet");

            logger.info("Waiting...");
            receiveSocket.receive(receivePacket);

            int len = receivePacket.getLength();
            byte[] trimmedData = new byte[len];
            System.arraycopy(receivePacket.getData(), 0, trimmedData, 0, len);

            DatagramPacket trimmedPacket =
                    new DatagramPacket(trimmedData, trimmedData.length,
                            receivePacket.getAddress(),
                            receivePacket.getPort());

            InetAddress host = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String msg = new String(data, 0, len);
            logger.info("Packet Received");
            logger.info("From: " + host + ":" + port);
            logger.info("Length: " + len);
            logger.info("Containing: " + msg);

            return trimmedPacket;
        } catch (Exception e) {
            logger.warning("UDP receive failed");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a UDP packet using a dedicated sending socket.
     * If the sending socket has not been initialized yet, this method will create it automatically.
     *
     * @param packet the DatagramPacket to be sent
     */
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

    /**
     * Sends a UDP packet back using the receive socket.
     * This method is typically used to reply to a packet that was just received, ensuring the response is sent from the same local port.
     *
     * @param packet the DatagramPacket to be sent back
     */
    public void sendBack(DatagramPacket packet) {
        try {
            receiveSocket.send(packet);
            logger.info("Packet sent to: " + packet.getAddress() + ":" + packet.getPort());

        } catch (Exception e) {
            logger.warning("UDP send failed");
        }
    }
}
