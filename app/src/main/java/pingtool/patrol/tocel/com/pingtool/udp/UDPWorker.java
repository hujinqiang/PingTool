package pingtool.patrol.tocel.com.pingtool.udp;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;

import androidx.work.Worker;

public abstract class UDPWorker extends Worker {

    public static final String TAG = UDPWorker.class.getSimpleName();
    volatile UDPSenderReceiver udpSenderReceiver ;

    DatagramPacket datagramPacket;

    byte[] data = new byte[1024];

    float count;

    Handler handler;
    protected Message msg;

    protected boolean isStart;

    float timeOutCount;

    CountDownLatch countDownLatch;

    public UDPWorker() {
    }

    public UDPWorker(UDPSenderReceiver udpSenderReceiver, Handler handler) {
        this.handler = handler;
        this.udpSenderReceiver = udpSenderReceiver;
        datagramPacket = new DatagramPacket(data,data.length);
    }

    @NonNull
    @Override
    public WorkerResult doWork() {
            try {
                if (!udpSenderReceiver.isStarted) {
                    Log.e(TAG, Thread.currentThread().getName() + " is waiting udp socket" );
                    countDownLatch.await();
                    Log.e(TAG, Thread.currentThread().getName() + " udp socket is create ok..." );
                }else{
//                    Log.e(TAG, "udp socket is started...");
                }
                executeWork();
                count++;
                msg = Message.obtain();
                msg.what = getMsgWhat();
                handler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){
                    timeOutCount ++ ;
                }
                e.printStackTrace();
            }finally {
            }

            Log.e(TAG, Thread.currentThread().getName() + "  is end.");
            return WorkerResult.SUCCESS;
    }

    protected abstract void executeWork()throws IOException;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setUdpSenderReceiver(UDPSenderReceiver udpSenderReceiver) {
        this.udpSenderReceiver = udpSenderReceiver;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    public abstract int getMsgWhat();
}
