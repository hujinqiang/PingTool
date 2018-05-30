package pingtool.patrol.tocel.com.pingtool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class UDPActivity extends Activity implements View.OnClickListener ,Handler.Callback{
    public static final String IS_START = "is_start";
    public static final String THREAD_NAME_SENDER = "UDP_Sender";
    public static final String THREAD_NAME_RECEIVER = "UDP_Receiver";
    volatile UDPSenderReceiver senderReceiver ;
    private EditText remoteIP;
    private EditText remotePort;

    Handler handler;
    private EditText statics;
    private EditText sendPkgCount;
    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;
    protected WifiManager.MulticastLock lock;
    protected UdpSender sender;
    protected UdpReceiver receiver;
    private Messenger mMessenger;
    protected boolean isStart;
    protected Button start;
    private final static String TAG = "UDPActivity";
    protected MyThread sendT;
    protected MyThread receiverT;

    boolean isSending;

    boolean isSocketStart;
    protected MyApplication application;

    volatile CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp);
        remoteIP = findViewById(R.id.et_remote_ip);
        remotePort = findViewById(R.id.et_remote_port);


        statics = findViewById(R.id.recv_pkg_statics);
        sendPkgCount = findViewById(R.id.send_pkg_statics);

        start = findViewById(R.id.start);

        handler = new Handler(this);

        start.setOnClickListener(this);


        preferences = getSharedPreferences(PingActivity.PING_CONFIG,MODE_PRIVATE);
        remoteIP.setText(preferences.getString("udp_server",""));
        remotePort.setText(preferences.getInt("udp_port",65535)+"");

        editor = preferences.edit();
        isStart = preferences.getBoolean(IS_START,false);

        Thread.activeCount();
        Log.e(TAG, "thread active count:" + Thread.activeCount());
        Map<Thread,StackTraceElement[]> threads = Thread.getAllStackTraces();


        for (Map.Entry<Thread,StackTraceElement[]> thread:threads.entrySet()){
            Thread t = thread.getKey();
            if (THREAD_NAME_SENDER.equals(t.getName())){
                sendT = (MyThread) t;
                UDP target = (UDP) ((MyThread) t).getTarget();
                target.setHandler(handler);
                sender = (UdpSender) target;
                isSending = true;
                isSocketStart = true;
            }else if (THREAD_NAME_RECEIVER.equals(t.getName())){
                receiverT = (MyThread) t;
                UDP target = (UDP) ((MyThread) t).getTarget();
                target.setHandler(handler);
                receiver = (UdpReceiver) target;
                isSending = true;
                isSocketStart = true;
            }
        }
        application = (MyApplication) getApplication();

        if(!isSocketStart && !TextUtils.isEmpty(remoteIP.getText().toString())){
            //启动udp  Socket
            startUdpSocket();
        }

        changeButtonText();


        WifiManager manager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("test wifi");
        lock.acquire();


        if(application.udpSenderReceiver != null){
            senderReceiver = application.udpSenderReceiver;
        }

    }

    private void changeButtonText() {
        if(isSending){
            start.setText("停止发包");
        }else{
            start.setText("开始发包");
        }
    }

    @Override
    public void onClick(View v) {

        if(TextUtils.isEmpty(remoteIP.getText().toString())){
            showToast("请输入要发包的IP");
            return;
        }

        if(!isSocketStart){
            startUdpSocket();
        }

        if (!isSending) {
            isStart = preferences.getBoolean(IS_START,false);
            String ip = remoteIP.getText().toString();
            int port = Integer.parseInt(remotePort.getText().toString());

            editor.putString("udp_server",ip);
            editor.putInt("udp_port",port);
            editor.putBoolean(IS_START,true);
            editor.commit();

            sender = new UdpSender(senderReceiver,handler);
            sendT = new MyThread(sender, THREAD_NAME_SENDER);
            sender.setCountDownLatch(countDownLatch);
            sendT.start();

            receiver = new UdpReceiver(senderReceiver,handler);
            receiverT = new MyThread(receiver, THREAD_NAME_RECEIVER);
            receiver.setCountDownLatch(countDownLatch);
            receiverT.start();

            isSending = true;

        }else{
            stopSend();
            isSending = false;
        }

        changeButtonText();

    }

    public void stopSend(){
        if(lock != null && lock.isHeld()){
            lock.release();
        }
        if (sender != null || receiver != null) {
            sender.setStart(false);
            receiver.setStart(false);
        }
        senderReceiver.release();

        countDownLatch = null;
        countDownLatch = new CountDownLatch(1);
        application.udpSenderReceiver = null;
        isSocketStart = false;
    }

    private void startUdpSocket() {
        Thread startSocketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ip = remoteIP.getText().toString();
                    int port = Integer.parseInt(remotePort.getText().toString());

                    senderReceiver = new UDPSenderReceiver();

                    try {
                        senderReceiver.init(ip,port);
                        showToast("连接到"+ip +"成功!");
                        application.udpSenderReceiver = senderReceiver;
                        if (sender != null && receiver != null) {
                            sender.setUdpSenderReceiver(senderReceiver);
                            receiver.setUdpSenderReceiver(senderReceiver);
                        }
                        countDownLatch.countDown();
                        isSocketStart = true;
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        showToast("连接到"+ip+"失败，"+e.getMessage());
                    } catch (SocketException e) {
                        e.printStackTrace();
                        showToast("连接到"+ip+"失败，"+e.getMessage());
                    }
                } finally {
                }

            }
        });
        startSocketThread.start();
    }

    private void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UDPActivity.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        int order = msg.arg1;
        int pkgCount = msg.arg2;
        switch (msg.what){
            case UDPSenderReceiver.MSG_SEND:
                String s = "发送第" + order +"个包  ";
                sendPkgCount.setText(s);
                break;

            case UDPSenderReceiver.MSG_RECEIVE:
                s = "接收第" + order +"个包  \n收到的包的数量为："+pkgCount+" \n丢包率："+msg.obj;
                statics.setText(s);
                break;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
