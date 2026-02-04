package test;

import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.DiagramPacket.getPacket;

public class DiagramPacketTest {
    /**
     * Tests the getPacket method.
     * - The returned DatagramPacket contains the correct IP address
     * - The returned DatagramPacket contains the correct port number
     * - The payload data matches the input message
     */
    @Test
    void getPacketTest(){
        InetAddress i;
        try {
            i = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        int p = 8000;
        DatagramPacket d = getPacket("Yipee", i, p);

        assertEquals(i, d.getAddress());
        assertEquals(p, d.getPort());
        assertEquals("Yipee", new String(d.getData(),0,d.getLength()));
    }

}
