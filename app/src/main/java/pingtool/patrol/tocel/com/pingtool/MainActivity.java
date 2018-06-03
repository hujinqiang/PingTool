package pingtool.patrol.tocel.com.pingtool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import pingtool.patrol.tocel.com.pingtool.ping.PingActivity;
import pingtool.patrol.tocel.com.pingtool.udp.UDPActivity;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ping = findViewById(R.id.btn_ping);
        Button udp = findViewById(R.id.btn_udp);

        ping.setOnClickListener(this);
        udp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ping:
                startActivity(new Intent(this,PingActivity.class));
                break;

            case R.id.btn_udp:
                startActivity(new Intent(this,UDPActivity.class));
                break;
        }
    }
}

