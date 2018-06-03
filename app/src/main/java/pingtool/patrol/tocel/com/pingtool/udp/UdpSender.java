package pingtool.patrol.tocel.com.pingtool.udp;

import android.os.Handler;

import java.io.IOException;

public class UdpSender extends UDP implements Runnable {

    public UdpSender(UDPSenderReceiver udpSenderReceiver, Handler handler) {
        super(udpSenderReceiver,handler);
    }

    @Override
    public void doWork() throws IOException {
        System.arraycopy(intToByteArray((int) count),0,data,0,4);
        udpSenderReceiver.sendPackage(datagramPacket);
    }

    @Override
    public int getMsgWhat() {
        msg.arg1 = (int) count;
        return UDPSenderReceiver.MSG_SEND;
    }

}
