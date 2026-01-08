package uk.ac.cam.jml229.logic.core;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Point;

import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.exceptions.IllegalLogicStateException;

public class Wire {
  public enum State {
    HIGH, LOW, FLOATING
  }

  private State signal = State.FLOATING;

  // Backward compatibility: The "primary" source for the renderer/UI
  private Component source;

  // Track ALL drivers to resolve Tri-State logic
  private final Map<Component, State> drivers = new HashMap<>();

  private List<PortConnection> destinations = new ArrayList<>();

  public Wire(Component source) {
    this.source = source;
    if (source != null) {
      drivers.put(source, State.FLOATING);
    }
  }

  public static class PortConnection {
    public Component component;
    public int inputIndex;
    public final List<Point> waypoints = new ArrayList<>();

    PortConnection(Component c, int i) {
      component = c;
      inputIndex = i;
    }
  }

  // --- Tri-State Driver ---
  // Transistors will call this method
  public void setDrive(Component driver, State state) {
    drivers.put(driver, state);
    recalculate();
  }

  // --- Old Boolean Driver ---
  // Standard gates call this.
  public void setSignal(boolean newSignal) {
    // Map boolean to Strong High/Low
    State newState = newSignal ? State.HIGH : State.LOW;

    // If we have a registered source, attribute this change to it
    if (source != null) {
      drivers.put(source, newState);
    }
    recalculate();
  }

  // Resolves the final state based on all drivers
  private void recalculate() {
    boolean hasHigh = false;
    boolean hasLow = false;

    for (State s : drivers.values()) {
      if (s == State.HIGH)
        hasHigh = true;
      if (s == State.LOW)
        hasLow = true;
    }

    State newState;
    if (hasHigh && hasLow) {
      // We'll treat it as LOW for safety (or could be a ERROR state).
      newState = State.LOW;
    } else if (hasHigh) {
      newState = State.HIGH;
    } else if (hasLow) {
      newState = State.LOW;
    } else {
      newState = State.FLOATING;
    }

    // Only propagate if the resolved state changes
    if (signal != newState) {
      signal = newState;
      notifyDestinations();
    }
  }

  private void notifyDestinations() {
    // Convert State -> Boolean for the components
    // FLOATING defaults to false (Pull-down behavior) to prevent crashes
    boolean logicLevel = (signal == State.HIGH);

    for (PortConnection pc : destinations) {
      Simulator.enqueue(() -> {
        pc.component.setInput(pc.inputIndex, logicLevel);
      });
    }
  }

  public boolean getSignal() {
    // Safe conversion for Renderer/Gates reading the wire
    return signal == State.HIGH;
  }

  public State getState() {
    return signal;
  }

  // --- Topology Methods ---

  public void setSource(Component c) {
    // If source changes, remove old one from drivers map
    if (source != null)
      drivers.remove(source);
    source = c;
    if (c != null)
      drivers.put(c, State.FLOATING);
  }

  public Component getSource() {
    return source;
  }

  public void removeSource(Component c) {
    drivers.remove(c);
    // If the primary source is removed, clear the reference
    if (source == c) {
      source = null;
    }
    recalculate();
  }

  public Set<Component> getSources() {
    return drivers.keySet();
  }

  // Allow adding extra drivers (for CMOS merges)
  public void addSource(Component c) {
    if (!drivers.containsKey(c)) {
      drivers.put(c, State.FLOATING);
    }
  }

  public void addDestination(Component c, int inputIndex) {
    destinations.add(new PortConnection(c, inputIndex));
  }

  public void removeDestination(Component c, int inputIndex) {
    destinations.removeIf(connection -> connection.component == c && connection.inputIndex == inputIndex);
  }

  public List<PortConnection> getDestinations() {
    return destinations;
  }
}
