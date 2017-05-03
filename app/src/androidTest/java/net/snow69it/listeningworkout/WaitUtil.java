package net.snow69it.listeningworkout;

/**
 * Created by raix on 2017/03/24.
 */

public class WaitUtil {

    private boolean isDone = false;

    private int timeoutSec;

    private long currentTimeMilliSec = 0;

    public WaitUtil() {
        this(10);
    }

    public WaitUtil(int timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    public boolean isDone() {
        return isDone;
    }

    public void hasDone() {
        isDone = true;
    }

    public void reset() {
        isDone = false;
    }

    public void waitForDone() {
        currentTimeMilliSec = System.currentTimeMillis();
        try {
            while (!isDone) {
                Thread.sleep(500);
                if ((System.currentTimeMillis() - currentTimeMilliSec) / 1000 > timeoutSec) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
