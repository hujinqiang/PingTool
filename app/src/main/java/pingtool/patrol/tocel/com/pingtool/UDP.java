package pingtool.patrol.tocel.com.pingtool;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public abstract class UDP implements Runnable {

    UDPSenderReceiver udpSenderReceiver ;

    DatagramPacket datagramPacket;

    byte[] data = new byte[1024];

    float count;

    Handler handler;
    protected Message msg;

    protected boolean isStart;

    float timeOutCount;

    public UDP(UDPSenderReceiver udpSenderReceiver,Handler handler ) {
        this.handler = handler;
        this.udpSenderReceiver = udpSenderReceiver;
        datagramPacket = new DatagramPacket(data,data.length);
        setStart(true);
    }

    @Override
    public void run() {
        while (isStart){
            try {
                doWork();
                count++;
                msg = Message.obtain();
                msg.what = getMsgWhat();
                handler.sendMessage(msg);
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){
                    timeOutCount ++ ;
                }
                e.printStackTrace();
            }
        }
    }

    public int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public abstract void doWork() throws IOException;

    public abstract int getMsgWhat();
}
