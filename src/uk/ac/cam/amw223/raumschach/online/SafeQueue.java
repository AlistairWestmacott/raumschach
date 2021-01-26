package uk.ac.cam.amw223.raumschach.online;

import java.util.LinkedList;
import java.util.Queue;

public class SafeQueue<T> {

  private final Queue<T> queue = new LinkedList<>();

  public void add(T e) {
    synchronized (queue) {
      queue.add(e);
      queue.notify();
    }
  }

  public T get() {
    synchronized (queue) {
      while (queue.isEmpty()) { // use a loop to block thread until data is available
        try {
          queue.wait();
        } catch (InterruptedException ie) {
          // Ignored exception
          //  thrown when the thread which is running the sleep routine is stopped
          //  in this case you'd want to free any resources you're holding
          //  like semaphores or file handlers.
        }
      }
      return queue.remove();
    }
  }

  public synchronized int size() {
    return queue.size();
  }

}
