package pingtool.patrol.tocel.com.pingtool;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

public class UdpService extends Service{

    public static final int START = 0;
    public static final int STOP = 1;

    public static final int IS_STARTED = 2;


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START:


                    break;
                case STOP:


                    break;
                case IS_STARTED:


                    break;
            }
        }
    };
    protected Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();

        messenger = new Messenger(mHandler);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public void startSend(){

    }


}
