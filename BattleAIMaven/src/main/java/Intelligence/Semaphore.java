package Intelligence;

public class Semaphore {

    boolean isGreen;

    public synchronized void goGreen() {
        isGreen = true;
    }

    public synchronized void goRed() {
        isGreen = false;
    }

    public synchronized boolean isGreen() {
        return isGreen;
    }

    public synchronized boolean isRed() {
        return !isGreen;
    }
}
