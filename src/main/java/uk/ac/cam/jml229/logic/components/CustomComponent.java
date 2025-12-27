package uk.ac.cam.jml229.logic.components;

import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.jml229.logic.model.Circuit;

public class CustomComponent extends Component {

  private final Circuit innerCircuit;
  private final List<Switch> internalInputs = new ArrayList<>();
  private final List<OutputProbe> internalOutputs = new ArrayList<>();

  public CustomComponent(String name, Circuit templateCircuit) {
    super(name);

    // Deep Copy the template so this chip works independently
    this.innerCircuit = templateCircuit.cloneCircuit();

    // Find the IO components inside the copy
    for (Component c : innerCircuit.getComponents()) {
      if (c instanceof Switch) {
        internalInputs.add((Switch) c);
      } else if (c instanceof OutputProbe) {
        internalOutputs.add((OutputProbe) c);
      }
    }

    // Configure External Pins
    setInputCount(internalInputs.size());

    // Note: Outputs are dynamic. The Renderer asks getOutputCount(),
    // which uses the outputWires list size. We need to ensure that list
    // is big enough.
    // We don't set wires here (the user does that), but we know we HAVE ports.
  }

  @Override
  public int getOutputCount() {
    // Override to show pins even if wires aren't connected yet
    return Math.max(1, internalOutputs.size());
  }

  @Override
  public void update() {
    // Bridge In: External Input -> Internal Switch
    for (int i = 0; i < internalInputs.size(); i++) {
      if (i < getInputCount()) {
        boolean val = getInput(i);
        internalInputs.get(i).toggle(val);
      }
    }

    // Bridge Out: Internal Probe -> External Output Wire
    for (int i = 0; i < internalOutputs.size(); i++) {
      boolean result = internalOutputs.get(i).getState();

      Wire w = getOutputWire(i);
      if (w != null) {
        w.setSignal(result);
      }
    }
  }
}
