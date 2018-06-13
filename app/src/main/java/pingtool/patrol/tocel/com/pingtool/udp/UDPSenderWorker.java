package pingtool.patrol.tocel.com.pingtool.udp;

import java.io.IOException;

import pingtool.patrol.tocel.com.pingtool.util.ByteUtil;

public class UDPSenderWorker extends UDPWorker {

    @Override
    protected void executeWork() throws IOException {
        System.arraycopy(ByteUtil.intToByteArray((int) count),0,data,0,4);
        udpSenderReceiver.sendPackage(datagramPacket);
    }

    @Override
    public int getMsgWhat() {
        msg.arg1 = (int) count;
        return UDPSenderReceiver.MSG_SEND;
    }
}
