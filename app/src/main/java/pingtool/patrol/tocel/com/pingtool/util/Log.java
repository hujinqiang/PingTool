package pingtool.patrol.tocel.com.pingtool.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    static Logger LOG;

    /**
     * 单个文件大小
     */
    public static final int FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 文件数量
     */
    public static final int FILE_COUNT = 2;

    static {
        LOG = Logger.getLogger("Ping");
        try {
            File pingFile = new File(Environment.getExternalStorageDirectory().getPath() + "/ping");
            if(!pingFile.exists()){
                pingFile.mkdir();
            }
            String path = pingFile.getPath() + "/tocel_ping";
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            Handler handler = new FileHandler(path, FILE_SIZE, FILE_COUNT,true);
            handler.setFormatter(new SimpleFormatter());
            LOG.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取log的实例
     * @return
     */
    public static Logger LOG(){
        return LOG;
    }

}
