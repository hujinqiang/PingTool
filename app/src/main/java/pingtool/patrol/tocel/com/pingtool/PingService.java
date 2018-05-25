package pingtool.patrol.tocel.com.pingtool;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

public class PingService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String DEF_SERVER = "192.168.1.95";
    public static final String DEF_TIME = "1000";
    protected HandlerThread handlerThread;

    Handler handler;
    protected SharedPreferences preferences;
    protected String pingServer;
    protected String timeOut;

    public PingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences(PingActivity.PING_CONFIG,MODE_PRIVATE);

        preferences.registerOnSharedPreferenceChangeListener(this);

        startPing();
    }

    /**
     * 启动Ping
     *
     */
    private void startPing() {
        handlerThread = new HandlerThread("Ping loop thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        pingServer = preferences.getString(PingActivity.SERVER, DEF_SERVER);
        timeOut = preferences.getString(PingActivity.TIME, DEF_TIME);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Ping.ping(pingServer,Integer.parseInt(timeOut));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(this);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PingActivity.SERVER.equals(key)){
            pingServer = sharedPreferences.getString(key,DEF_SERVER);
        }else if(PingActivity.TIME.equals(key)){
            timeOut = sharedPreferences.getString(key,DEF_TIME);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        handlerThread.quit();
    }
}
