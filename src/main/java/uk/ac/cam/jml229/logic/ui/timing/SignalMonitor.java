package uk.ac.cam.jml229.logic.ui.timing;

import uk.ac.cam.jml229.logic.core.Wire;
import java.awt.Color;

public class SignalMonitor {
  private final String name;
  private final Wire wire;
  private final boolean[] history;
  private int head = 0;
  private final Color color;

  public SignalMonitor(String name, Wire wire, Color color, int bufferSize) {
    this.name = name;
    this.wire = wire;
    this.color = color;
    this.history = new boolean[bufferSize];
  }

  public void tick() {
    history[head] = wire.getSignal();
    head = (head + 1) % history.length;
  }

  public boolean getStateAt(int timeOffset) {
    int index = (head + timeOffset) % history.length;
    if (index < 0)
      index += history.length;
    return history[index];
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public boolean getCurrentState() {
    return wire.getSignal();
  }
}
