package uk.ac.cam.jml229.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.components.seq.Clock;

public class Circuit {
  private final List<Component> components = new ArrayList<>();
  private final List<Wire> wires = new ArrayList<>();

  public void tick() {
    for (Component c : components) {
      if (c instanceof Clock)
        ((Clock) c).tick();
    }
  }

  public void addComponent(Component c) {
    components.add(c);
    for (Wire w : c.getAllOutputs()) {
      if (!wires.contains(w))
        wires.add(w);
    }
  }

  public void removeComponent(Component c) {
    // Remove wires driven BY this component
    for (int i = 0; i < c.getOutputCount(); i++) {
      Wire w = c.getOutputWire(i);
      if (w != null) {
        w.removeSource(c);
        if (w.getSources().isEmpty()) {
          // Wire is dead, clear destinations
          for (Wire.PortConnection pc : w.getDestinations()) {
            pc.component.setInput(pc.inputIndex, false);
            pc.component.update();
          }
          wires.remove(w);
        }
      }
    }
    // Remove wires driving INTO this component
    for (Wire w : wires) {
      w.getDestinations().removeIf(pc -> pc.component == c);
    }
    components.remove(c);
  }

  public boolean addConnection(Component source, Component dest, int inputIndex) {
    return addConnection(source, 0, dest, inputIndex);
  }

  public boolean addConnection(Component source, int sourceOutputIndex, Component dest, int inputIndex) {
    if (source == dest)
      return false;

    // --- MERGE LOGIC START ---
    // Check if the destination pin is already occupied
    for (Wire w : wires) {
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == dest && pc.inputIndex == inputIndex) {

          // Add the new source to the EXISTING wire object.
          w.addSource(source);
          source.setOutputWire(sourceOutputIndex, w);

          // Force an update so the new driver contributes its state immediately
          source.update();
          return true;
        }
      }
    }
    // --- MERGE LOGIC END ---

    // Standard Connection
    Wire w = source.getOutputWire(sourceOutputIndex);
    boolean isNewWire = false;

    if (w == null) {
      w = new Wire(source);
      source.setOutputWire(sourceOutputIndex, w);
      wires.add(w);
      isNewWire = true;
    }

    // Avoid adding the exact same destination twice
    for (Wire.PortConnection pc : w.getDestinations()) {
      if (pc.component == dest && pc.inputIndex == inputIndex)
        return true;
    }

    w.addDestination(dest, inputIndex);
    dest.setInput(inputIndex, w.getSignal());
    dest.update();

    if (isNewWire)
      source.update();

    return true;
  }

  public void removeConnection(Component dest, int inputIndex) {
    for (Wire w : wires) {
      boolean wasConnected = false;
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == dest && pc.inputIndex == inputIndex) {
          wasConnected = true;
          break;
        }
      }
      if (wasConnected) {
        dest.setInput(inputIndex, false);
        dest.update();
        w.removeDestination(dest, inputIndex);
        return;
      }
    }
  }

  public Circuit cloneCircuit() {
    Circuit copy = new Circuit();
    java.util.Map<Component, Component> oldToNew = new java.util.HashMap<>();

    for (Component original : this.components) {
      Component clone = original.makeCopy();
      clone.setPosition(original.getX(), original.getY());
      copy.addComponent(clone);
      oldToNew.put(original, clone);
    }

    for (Wire originalWire : this.wires) {
      // Re-link all drivers
      for (Component oldSource : originalWire.getSources()) {
        int sourceIndex = -1;
        for (int i = 0; i < oldSource.getOutputCount(); i++) {
          if (oldSource.getOutputWire(i) == originalWire) {
            sourceIndex = i;
            break;
          }
        }
        if (sourceIndex == -1)
          continue;

        Component newSource = oldToNew.get(oldSource);
        for (Wire.PortConnection pc : originalWire.getDestinations()) {
          Component newDest = oldToNew.get(pc.component);
          copy.addConnection(newSource, sourceIndex, newDest, pc.inputIndex);
        }
      }
    }
    return copy;
  }

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
