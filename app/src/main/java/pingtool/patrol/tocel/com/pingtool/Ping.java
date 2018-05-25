package pingtool.patrol.tocel.com.pingtool;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Ping {
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
     * ping服务器
     * @param server 服务器IP
     * @param time 超时时间
     */
    public static void ping(String server,int time){
        try {
            InetAddress address = InetAddress.getByName(server);
            if(!address.isReachable(time)){
                LOG.severe("ping :"+ server + " 服务器失败，"+time+"毫秒超时");
            }else{
                LOG.info("ping 服务器:"+ server +"成功");
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
