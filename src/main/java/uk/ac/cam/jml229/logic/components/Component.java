package uk.ac.cam.jml229.logic.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.ac.cam.jml229.logic.core.Wire;
import uk.ac.cam.jml229.logic.core.Simulator;
import uk.ac.cam.jml229.logic.io.SettingsManager;

public abstract class Component {
  private String name;
  private int x, y;
  private int rotation = 0;

  // --- Per-Component Delay Support ---
  // null = use global default. Integer = specific override.
  private Integer customDelay = null;

  private final List<Wire> outputWires = new ArrayList<>();
  private final List<Boolean> inputs = new ArrayList<>();
  private int inputCount = 0;

  public Component(String name) {
    this.name = name;
  }

  // --- Delay Accessors ---
  public void setCustomDelay(Integer delay) {
    this.customDelay = delay;
  }

  public Integer getCustomDelay() {
    return customDelay;
  }

  public void rotate() {
    rotation = (rotation + 1) % 4;
  }

  public int getRotation() {
    return rotation;
  }

  public void setRotation(int r) {
    this.rotation = r;
  }

  public void setName(String name) {
    this.name = name;
  }

  // --- Logic with Delay ---
  public void setInput(int index, boolean state) {
    while (inputs.size() <= index) {
      inputs.add(false);
    }

    if (inputs.get(index) != state) {
      inputs.set(index, state);

      if (SettingsManager.isPropagationDelayEnabled()) {
        // Priority: Custom Delay -> Global Delay
        int d = (customDelay != null) ? customDelay : SettingsManager.getGateDelay();
        Simulator.schedule(this::update, d);
      } else {
        update();
      }
    }
  }

  public boolean getInput(int index) {
    if (index >= 0 && index < inputs.size())
      return inputs.get(index);
    return false;
  }

  protected void setInputCount(int count) {
    this.inputCount = count;
    while (inputs.size() < count)
      inputs.add(false);
  }

  public int getInputCount() {
    return inputCount;
  }

  public Wire getOutputWire() {
    return getOutputWire(0);
  }

  public void setOutputWire(Wire w) {
    setOutputWire(0, w);
  }

  public Wire getOutputWire(int index) {
    if (index >= 0 && index < outputWires.size())
      return outputWires.get(index);
    return null;
  }

  public void setOutputWire(int index, Wire w) {
    while (outputWires.size() <= index)
      outputWires.add(null);
    outputWires.set(index, w);
  }

  public List<Wire> getAllOutputs() {
    return outputWires.stream().filter(Objects::nonNull).toList();
  }

  public int getOutputCount() {
    if (outputWires.isEmpty())
      return 1;
    return Math.max(1, outputWires.size());
  }

  public String getName() {
    return name;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public abstract void update();

  public Component makeCopy() {
    try {
      Component copy = this.getClass().getConstructor(String.class).newInstance(this.name);
      copy.setInputCount(this.getInputCount());
      // Copy delay setting
      copy.setCustomDelay(this.customDelay);
      return copy;
    } catch (Exception e) {
      throw new RuntimeException("Failed to copy component: " + name, e);
    }
  }
}
