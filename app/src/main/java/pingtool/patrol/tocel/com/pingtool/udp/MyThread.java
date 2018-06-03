package pingtool.patrol.tocel.com.pingtool.udp;

public class MyThread extends Thread {
    private Runnable target;
    public MyThread(Runnable target, String name) {
        super(target, name);
        this.target = target;
    }

    public Runnable getTarget() {
        return target;
    }

    public void setTarget(Runnable target) {
        this.target = target;
    }
}
