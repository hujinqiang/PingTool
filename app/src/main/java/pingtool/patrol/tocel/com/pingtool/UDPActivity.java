package pingtool.patrol.tocel.com.pingtool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UDPActivity extends Activity implements View.OnClickListener ,Handler.Callback{
    UDPSenderReceiver senderReceiver ;
    private EditText remoteIP;
    private EditText remotePort;

    Handler handler;
    private EditText statics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp);
        remoteIP = findViewById(R.id.et_remote_ip);
        remotePort = findViewById(R.id.et_remote_port);

        statics = findViewById(R.id.recv_pkg_statics);

        Button start = findViewById(R.id.bt_start);

        handler = new Handler(this);

        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String ip = remoteIP.getText().toString();
        int port = Integer.parseInt(remotePort.getText().toString());

        senderReceiver = new UDPSenderReceiver(ip,port);


        new Thread(new UdpSender(senderReceiver,handler)).start();

        new Thread(new UdpReceiver(senderReceiver,handler)).start();

    }

    @Override
    public boolean handleMessage(Message msg) {
        int order = msg.arg1;
        switch (msg.what){
            case UDPSenderReceiver.MSG_SEND:
                String s = "发送第" + order +"个包";
                String newS = statics.getText().append("\n").append(s).toString();
                statics.setText(newS);
                break;

            case UDPSenderReceiver.MSG_RECEIVE:
                s = "接收第" + order +"个包";
                newS = statics.getText().append("\n").append(s).toString();
                statics.setText(newS);
                break;
        }

        return true;
    }
}
