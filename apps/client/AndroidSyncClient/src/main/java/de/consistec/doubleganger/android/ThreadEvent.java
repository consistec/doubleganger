package de.consistec.doubleganger.android;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 21.03.13 09:38
 */
public class ThreadEvent {

    private final Object lock = new Object();

    public void signal() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void await() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }
}
