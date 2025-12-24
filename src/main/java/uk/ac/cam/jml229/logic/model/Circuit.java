package uk.ac.cam.jml229.logic.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.ac.cam.jml229.logic.components.*;

public class Circuit {
  // The core data
  private final List<Component> components = new ArrayList<>();
  private final List<Wire> wires = new ArrayList<>();

  /**
   * Adds a component to the circuit.
   * If the component already has wires attached (e.g. from a copy-paste),
   * it ensures those wires are tracked too.
   */
  public void addComponent(Component c) {
    components.add(c);
    if (c.getOutputWire() != null && !wires.contains(c.getOutputWire())) {
      wires.add(c.getOutputWire());
    }
  }

  /**
   * Removes a component and safely cleans up all connected wires.
   */
  public void removeComponent(Component c) {
    // 1. Remove wires driven BY this component (Outputs)
    List<Wire> outputWires = new ArrayList<>();
    for (Wire w : wires) {
      if (w.getSource() == c) {
        outputWires.add(w);
      }
    }

    // Remove these wires from the circuit
    wires.removeAll(outputWires);
    // (Optional: You could also set c.setOutputWire(null) here)

    // 2. Remove wires driving INTO this component (Inputs)
    for (Wire w : wires) {
      // We remove any connection that points to 'c'
      // Using removeIf is safe while iterating the outer list
      w.getDestinations().removeIf(pc -> pc.component == c);
    }

    // 3. Finally remove the component itself
    components.remove(c);
  }

  /**
   * Attempts to connect source -> dest.
   * 
   * @return true if successful, false if invalid (e.g. loop or input occupied)
   */
  public boolean addConnection(Component source, Component dest, int inputIndex) {
    if (source == dest)
      return false; // Prevent self-loops

    // Check if this specific input pin is already occupied
    for (Wire w : wires) {
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == dest && pc.inputIndex == inputIndex) {
          return false; // Input already has a wire!
        }
      }
    }

    // Get or Create the wire
    Wire w = source.getOutputWire();
    if (w == null) {
      w = new Wire(source);
      wires.add(w);
    }

    w.addDestination(dest, inputIndex);
    return true;
  }

  /**
   * Removes a specific connection (Wire segment).
   * IMPORTANT: Resets the destination input to FALSE to prevent "stuck" signals.
   */
  public void removeConnection(Component dest, int inputIndex) {
    // Find the wire connected to this specific input
    for (Wire w : wires) {
      boolean wasConnected = false;

      // Check if this wire hits the target
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == dest && pc.inputIndex == inputIndex) {
          wasConnected = true;
          break;
        }
      }

      if (wasConnected) {
        // Reset signal to FALSE (The Bug Fix from earlier!)
        dest.setInput(inputIndex, false);

        // Remove the physical connection
        w.removeDestination(dest, inputIndex);

        // TODO (Optional) If wire has no more destinations, we could delete the wire
        // entirely.
        // For now, we leave it dangling
        return;
      }
    }
  }

  // --- Accessors ---

  public List<Component> getComponents() {
    return Collections.unmodifiableList(components);
  }

  public List<Wire> getWires() {
    return Collections.unmodifiableList(wires);
  }

  public void clear() {
    components.clear();
    wires.clear();
  }
}
