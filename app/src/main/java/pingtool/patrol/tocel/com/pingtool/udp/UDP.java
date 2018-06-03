package pingtool.patrol.tocel.com.pingtool.udp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class UDP implements Runnable {

    public static final String TAG = UDP.class.getSimpleName();
    volatile UDPSenderReceiver udpSenderReceiver ;

    DatagramPacket datagramPacket;

    byte[] data = new byte[1024];

    float count;

    Handler handler;
    protected Message msg;

    protected boolean isStart;

    float timeOutCount;

    CountDownLatch countDownLatch;


    public UDP(UDPSenderReceiver udpSenderReceiver, Handler handler) {
        this.handler = handler;
        this.udpSenderReceiver = udpSenderReceiver;
        datagramPacket = new DatagramPacket(data,data.length);
        setStart(true);
    }

    @Override
    public void run() {
        while (isStart){
            try {
                if (!udpSenderReceiver.isStarted) {
                    Log.e(TAG, Thread.currentThread().getName() + " is waiting udp socket" );
                    countDownLatch.await();
                    Log.e(TAG, Thread.currentThread().getName() + " udp socket is create ok..." );
                }else{
//                    Log.e(TAG, "udp socket is started...");
                }
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
            }finally {
            }
        }

        Log.e(TAG, Thread.currentThread().getName() + "  is end.");

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

    public void setUdpSenderReceiver(UDPSenderReceiver udpSenderReceiver) {
        this.udpSenderReceiver = udpSenderReceiver;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public abstract void doWork() throws IOException;

    public abstract int getMsgWhat();
}
