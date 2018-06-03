package pingtool.patrol.tocel.com.pingtool.ping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import pingtool.patrol.tocel.com.pingtool.util.Log;

public class Ping {


    /**
     * ping服务器
     * @param server 服务器IP
     * @param time 超时时间
     */
    public static void ping(String server,int time){
        try {
            InetAddress address = InetAddress.getByName(server);
            if(!address.isReachable(time)){
                Log.LOG().severe("ping :"+ server + " 服务器失败，"+time+"毫秒超时");
            }else{
                Log.LOG().info("ping 服务器:"+ server +"成功");
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
