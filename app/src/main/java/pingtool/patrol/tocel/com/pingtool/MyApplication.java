package pingtool.patrol.tocel.com.pingtool;

import android.app.Application;

import pingtool.patrol.tocel.com.pingtool.udp.UDPSenderReceiver;

public class MyApplication extends Application {

    public UDPSenderReceiver udpSenderReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
