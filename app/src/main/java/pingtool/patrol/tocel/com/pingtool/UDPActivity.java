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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public class UDPActivity extends Activity implements View.OnClickListener ,Handler.Callback{
    public static final String IS_START = "is_start";
    public static final String THREAD_NAME_SENDER = "UDP_Sender";
    public static final String THREAD_NAME_RECEIVER = "UDP_Receiver";
    UDPSenderReceiver senderReceiver ;
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
        remoteIP.setText(preferences.getString("udp_server","127.0.0.1"));
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
                start.setEnabled(false);
                UDP target = (UDP) ((MyThread) t).getTarget();
                target.setHandler(handler);
            }else if (THREAD_NAME_RECEIVER.equals(t.getName())){
                receiverT = (MyThread) t;
                start.setEnabled(false);
                UDP target = (UDP) ((MyThread) t).getTarget();
                target.setHandler(handler);
            }
        }

        if(start.isEnabled()){
            //启动udp  Socket
            startUdpSocket();
//            start.setText("停止发包");
        }


        WifiManager manager = (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("test wifi");
        lock.acquire();

        /*Intent intent = new Intent(this,UdpService.class);

        ServiceConnection conn  = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mMessenger = new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(intent,conn,BIND_AUTO_CREATE);*/

    }

    @Override
    public void onClick(View v) {

        isStart = preferences.getBoolean(IS_START,false);
        String ip = remoteIP.getText().toString();
        int port = Integer.parseInt(remotePort.getText().toString());

        editor.putString("udp_server",ip);
        editor.putInt("udp_port",port);
        editor.putBoolean(IS_START,true);
        editor.commit();

        sender = new UdpSender(senderReceiver,handler);
        sendT = new MyThread(sender, THREAD_NAME_SENDER);
        sendT.start();

        receiver = new UdpReceiver(senderReceiver,handler);
        receiverT = new MyThread(receiver, THREAD_NAME_RECEIVER);
        receiverT.start();

    }

    public void stopSend(){
        if(lock != null && lock.isHeld()){
            lock.release();
        }
        senderReceiver.release();

        if (sender != null || receiver != null) {
            sender.setStart(false);
            receiver.setStart(false);
        }
    }

    private void startUdpSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = remoteIP.getText().toString();
                int port = Integer.parseInt(remotePort.getText().toString());

                senderReceiver = new UDPSenderReceiver();
                try {
                    senderReceiver.init(ip,port);
                    showToast("连接到"+ip +"成功!");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    showToast("连接到"+ip+"失败，"+e.getMessage());
                } catch (SocketException e) {
                    e.printStackTrace();
                    showToast("连接到"+ip+"失败，"+e.getMessage());
                }
            }
        }).start();
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
                start.setEnabled(false);
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
