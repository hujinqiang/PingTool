package pingtool.patrol.tocel.com.pingtool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * UDP 发送接收数据类
 */
public class UDPSenderReceiver {

    protected DatagramSocket udpSocket;

    public static final int MSG_SEND = 0;
    public static final int MSG_RECEIVE = 1;

    public UDPSenderReceiver(String host, int port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            udpSocket = new DatagramSocket(port,address);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void sendPackage(DatagramPacket packet) throws IOException {
        udpSocket.send(packet);
    }

    public void receivePackage(DatagramPacket packet) throws IOException {
        udpSocket.receive(packet);
    }

    public void release(){
        if(udpSocket != null && udpSocket.isConnected()){
            udpSocket.disconnect();
            udpSocket.close();
        }
    }

}
