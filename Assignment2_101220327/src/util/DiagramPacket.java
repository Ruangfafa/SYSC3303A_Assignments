package util;

import service.LoggerService;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class DiagramPacket {
    private static final Logger logger = LoggerService.getLogger();

    public static DatagramPacket decodePacketBytes(byte[] packetBytes) {
        DataInputStream intermediatePayload = new DataInputStream(new ByteArrayInputStream(packetBytes));

        InetAddress address = null;
        int port = 0;
        byte[] data = null;

        try {
            address = InetAddress.getByName(intermediatePayload.readUTF());
            port = intermediatePayload.readInt();
            String payload = intermediatePayload.readUTF();
            data = payload.getBytes();
        } catch (IOException e) {
            logger.warning("Error decoding packet bytes");
        }

        return new DatagramPacket(
                data,
                data.length,
                address,
                port
        );
    }

    public static DatagramPacket getPacket(String msg, InetAddress address, int port) {
        byte[] data = msg.getBytes();

        return new DatagramPacket(
                data,
                data.length,
                address,
                port
        );
    }

//    public static DatagramPacket getPacket(DatagramPacket innerPacket, InetAddress address, int port) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(bos);
//
//        try {
//            dos.writeUTF(innerPacket.getAddress().getHostAddress());
//            dos.writeInt(innerPacket.getPort());
//            byte[] innerData = Arrays.copyOf(innerPacket.getData(), innerPacket.getLength());
//            dos.writeInt(innerData.length);
//            dos.write(innerData);
//        } catch (IOException e) {
//            logger.warning("Error decoding packet bytes");
//        }
//
//        byte[] outerData = bos.toByteArray();
//        return new DatagramPacket(outerData, outerData.length, address, port);
//    }
}
