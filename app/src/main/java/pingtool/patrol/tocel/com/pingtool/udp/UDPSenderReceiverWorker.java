package pingtool.patrol.tocel.com.pingtool.udp;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import androidx.work.Data;
import androidx.work.Worker;

/**
 * UDP 发送接收数据类
 */
public class UDPSenderReceiverWorker extends Worker{

    public static final String TAG = "UDPSenderReceiver";
    public static final String HOST = "host";
    public static final String PORT = "port";
    protected DatagramSocket udpSocket;

    public static final int MSG_SEND = 0;
    public static final int MSG_RECEIVE = 1;
    protected InetAddress address;

    public boolean isStarted() {
        return isStarted;
    }

    public volatile boolean isStarted;

    public UDPSenderReceiverWorker() {

    }

    @NonNull
    @Override
    public WorkerResult doWork() {
        Data data = getInputData();
        String host = data.getString(HOST,"127.0.0.1");
        int port = data.getInt(PORT,65535);

        Data outData ;
        try {
            init(host,port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            outData = new Data.Builder().putString("err_msg",e.getMessage()).build();
            setOutputData(outData);
            return WorkerResult.FAILURE;
        } catch (SocketException e) {
            outData = new Data.Builder().putString("err_msg",e.getMessage()).build();
            setOutputData(outData);
            e.printStackTrace();
            return WorkerResult.FAILURE;
        }
//        setOutputData(outData);
        return WorkerResult.SUCCESS;
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
