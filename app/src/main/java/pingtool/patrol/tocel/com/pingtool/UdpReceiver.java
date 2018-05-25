package pingtool.patrol.tocel.com.pingtool;


import android.os.Handler;

import java.io.IOException;

public class UdpReceiver extends UDP {

    public UdpReceiver(UDPSenderReceiver udpSenderReceiver,Handler handler) {
        super(udpSenderReceiver, handler);
    }

    @Override
    public void doWork() throws IOException {
        udpSenderReceiver.receivePackage(datagramPacket);
    }

    @Override
    public int getMsgWhat() {
        byte[] recCount = new byte[4];
        System.arraycopy(data,0,recCount,0,4);
        msg.arg1 = byteArrayToInt(recCount);
        return UDPSenderReceiver.MSG_RECEIVE;
    }
}
