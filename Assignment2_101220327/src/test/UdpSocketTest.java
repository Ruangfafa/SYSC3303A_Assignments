package test;

import org.junit.jupiter.api.Test;
import service.UdpSocket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.DiagramPacket.getPacket;

public class UdpSocketTest {
    /**
     * Tests UDP send and receive behavior between two UdpSocket instances.
     * - One socket acts as the receiver bound to a specific port
     * - Another socket acts as the sender
     * - A message is sent, received, and validated
     * - A reply message is sent back and validated
     */
    @Test
    public void testSendAndReceive() {
        InetAddress i;
        try {
            i = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int p = 8000;

        UdpSocket receiver = new UdpSocket(p);
        UdpSocket sender = new UdpSocket();

        DatagramPacket packetSend = getPacket("Yipee", i, p);
        sender.send(packetSend);

        DatagramPacket packetReceived = receiver.receive();

        InetAddress receivedAddress = packetReceived.getAddress();
        int receivedPort = packetReceived.getPort();
        String receivedMsg = new String(packetReceived.getData(), 0, packetReceived.getLength());
        assertEquals("Yipee", receivedMsg);

        DatagramPacket packetBack = getPacket("Yapee", i, p);
        sender.sendBack(packetBack);

        DatagramPacket packetBackReceived = receiver.receive();

        assertEquals(receivedAddress, packetBackReceived.getAddress());
        assertEquals(receivedPort, packetBackReceived.getPort());
        String receivedMsgBack = new String(packetBackReceived.getData(), 0, packetBackReceived.getLength());
        assertEquals("Yapee", receivedMsgBack);
    }

}
