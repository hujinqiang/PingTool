package pingtool.patrol.tocel.com.pingtool.udp;

import android.util.Log;

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

    public static final String TAG = "UDPSenderReceiver";
    protected DatagramSocket udpSocket;

    public static final int MSG_SEND = 0;
    public static final int MSG_RECEIVE = 1;
    protected InetAddress address;

    public boolean isStarted() {
        return isStarted;
    }

    public volatile boolean isStarted;

    public UDPSenderReceiver() {

    }

    public void init(String host, int port) throws UnknownHostException, SocketException {

        address = InetAddress.getByName(host);
        if(address == null){
            Log.e(TAG, "UDPSenderReceiver: address not fonud...");
        }else {
            udpSocket = new DatagramSocket(port);
            udpSocket.setSoTimeout(150);
            isStarted = true;
            Log.e(TAG, "init: connect to server successful");
            Log.e(TAG, "init: is start " + isStarted);
        }
    }

    public void sendPackage(DatagramPacket packet) throws IOException {
        if (checkUdpSocket()) {
            Log.e(TAG, "sendPackage: socket is null...");
            return;
        }
        packet.setAddress(address);
        udpSocket.send(packet);
    }

    private boolean checkUdpSocket() {
        if(udpSocket == null){

            return true;
        }
        return false;
    }

    public void receivePackage(DatagramPacket packet) throws IOException {
        if (checkUdpSocket()){
            Log.e(TAG, "receivePackage: socket is null...");
            return;
        }
        packet.setAddress(address);

        udpSocket.receive(packet);

    }

    public void release(){
        isStarted = false;
        if(udpSocket != null){
            udpSocket.disconnect();
            udpSocket.close();
        }

    }

}
