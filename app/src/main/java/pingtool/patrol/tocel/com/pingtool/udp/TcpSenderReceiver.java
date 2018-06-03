package pingtool.patrol.tocel.com.pingtool.udp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

public class TcpSenderReceiver {

    /**
     * 本地serversocket默认端口,即对方连接时使用的端口号
     */
    public static final int LOCAL_DEFAULT_PORT = 99999;
    private ServerSocket serverSocket;
    private  Socket socket;

    public TcpSenderReceiver(String host) {
        try {
            socket = SocketFactory.getDefault().createSocket(host,LOCAL_DEFAULT_PORT);
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(LOCAL_DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
