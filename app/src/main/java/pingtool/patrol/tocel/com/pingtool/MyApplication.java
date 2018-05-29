package pingtool.patrol.tocel.com.pingtool;

import android.app.Application;

public class MyApplication extends Application {

    public UDPSenderReceiver udpSenderReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
