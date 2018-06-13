package pingtool.patrol.tocel.com.pingtool.udp;

import java.io.IOException;
import java.text.DecimalFormat;

import pingtool.patrol.tocel.com.pingtool.util.ByteUtil;
import pingtool.patrol.tocel.com.pingtool.util.Log;

public class UDPReceiverWorker extends UDPWorker {

    DecimalFormat format = new DecimalFormat("0.000000");

    private float mLossCount;

    @Override
    protected void executeWork() throws IOException {
        udpSenderReceiver.receivePackage(datagramPacket);
    }

    @Override
    public int getMsgWhat() {
        byte[] recCount = new byte[4];
        System.arraycopy(data,0,recCount,0,4);
        msg.arg1 = ByteUtil.byteArrayToInt(recCount);
        msg.arg2 = (int) count;

        float lossCount = msg.arg1-count;

        float lossPacketCount = (lossCount) <= 0?0: lossCount/ msg.arg1;
        String lossStr = format.format(lossPacketCount);
        if(lossPacketCount > 0 && mLossCount != lossCount){
            this.mLossCount = lossCount;
            Log.LOG().severe("total:"+msg.arg1 +",receive:"+msg.arg2 +",loss packet count:"+ lossCount +",loss packet rate:"+lossStr);
        }
        msg.obj = lossStr;
        return UDPSenderReceiver.MSG_RECEIVE;
    }
}
