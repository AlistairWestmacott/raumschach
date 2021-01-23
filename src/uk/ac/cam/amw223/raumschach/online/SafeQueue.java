package uk.ac.cam.amw223.raumschach.online;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class SafeQueue<T> {

  private Queue<T> queue = new LinkedList<>();

  public synchronized void add(T e) {
    queue.add(e);
    this.notify();
  }

  public synchronized T get() {
    while (queue.isEmpty()) { // use a loop to block thread until data is available
      try {
        this.wait();
      } catch (InterruptedException ie) {
        // Ignored exception
        //  thrown when the thread which is running the sleep routine is stopped
        //  in this case you'd want to free any resources you're holding
        //  like semaphores or file handlers.
      }
    }
    return queue.remove();
  }

  public synchronized int size() {
    return queue.size();
  }

}
