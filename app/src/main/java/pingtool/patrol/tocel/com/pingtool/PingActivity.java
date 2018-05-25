package pingtool.patrol.tocel.com.pingtool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SERVER = "server";
    public static final String TIME = "time";
    public static final String PING_CONFIG = "config";
    protected EditText etServer;
    protected EditText etTimeOut;
    protected Button btnStart;
    protected SharedPreferences preferences;
    protected String server;
    protected String timeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        preferences = getSharedPreferences(PING_CONFIG,MODE_PRIVATE);

        findViews();
    }

    private void findViews() {
        etServer = findViewById(R.id.et_server);
        etTimeOut = findViewById(R.id.et_time_out);
        btnStart = findViewById(R.id.bt_start);

        btnStart.setOnClickListener(this);

        server = preferences.getString("server","");

        timeOut = preferences.getString("time","");

        etServer.setText(server);
        etTimeOut.setText(timeOut);

        Intent intent = new Intent(this,PingService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_start:
                server = etServer.getText().toString();
                timeOut = etTimeOut.getText().toString();
                if(TextUtils.isEmpty(server) || TextUtils.isEmpty(timeOut)){
                    Toast.makeText(this,"服务器或超时时间不能为空!",Toast.LENGTH_LONG).show();
                    return;
                }
                saveData();
                break;

            default:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SERVER,server);
        editor.putString(TIME,timeOut);
        editor.commit();
    }

}
