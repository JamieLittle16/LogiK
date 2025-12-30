package uk.ac.cam.jml229.logic.core;

import java.util.PriorityQueue;
import java.util.Queue;

public class Simulator {

  private static class SimEvent implements Comparable<SimEvent> {
    long tickTime;
    Runnable action;

    SimEvent(long tickTime, Runnable action) {
      this.tickTime = tickTime;
      this.action = action;
    }

    @Override
    public int compareTo(SimEvent other) {
      return Long.compare(this.tickTime, other.tickTime);
    }
  }

  private static final Queue<SimEvent> eventQueue = new PriorityQueue<>();
  private static long currentTick = 0;

  public static void enqueue(Runnable event) {
    schedule(event, 0);
  }

  public static void schedule(Runnable event, int delayTicks) {
    eventQueue.add(new SimEvent(currentTick + delayTicks, event));
  }

  public static void run(int maxTicks) {
    for (int i = 0; i < maxTicks; i++) {
      while (!eventQueue.isEmpty() && eventQueue.peek().tickTime <= currentTick) {
        eventQueue.poll().action.run();
      }
      currentTick++;
    }
  }

  public static void clear() {
    eventQueue.clear();
    currentTick = 0;
  }

  public static boolean isStable() {
    return eventQueue.isEmpty();
  }

  public static long getTick() {
    return currentTick;
  }
}
