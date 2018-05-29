package pingtool.patrol.tocel.com.pingtool;


import android.os.Handler;

import java.io.IOException;
import java.text.DecimalFormat;

public class UdpReceiver extends UDP {

    DecimalFormat format ;
    private float mLossCount;

    public UdpReceiver(UDPSenderReceiver udpSenderReceiver, Handler handler) {
        super(udpSenderReceiver, handler);
        format = new DecimalFormat("0.000000");
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
        msg.arg2 = (int) count;

        float lossCount = msg.arg1-count;

        float lossPacketCount = (lossCount) <= 0?0: lossCount/ msg.arg1;
        String lossStr = format.format(lossPacketCount);
        if(lossPacketCount > 0 && mLossCount != lossCount){
            this.mLossCount = lossCount;
            Log.getLOG().severe("total:"+msg.arg1 +",receive:"+msg.arg2 +",loss packet count:"+ lossCount +",loss packet rate:"+lossStr);
        }
        msg.obj = lossStr;
        return UDPSenderReceiver.MSG_RECEIVE;
    }
}
