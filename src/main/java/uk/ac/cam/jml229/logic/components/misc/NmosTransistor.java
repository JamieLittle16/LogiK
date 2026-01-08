package uk.ac.cam.jml229.logic.components.misc;

import uk.ac.cam.jml229.logic.components.Component;

public class NmosTransistor extends Component {

  public NmosTransistor(String name) {
    super(name);
    // Input 0: Gate (Control)
    // Input 1: Source (Input Signal)
    setInputCount(2);
  }

  @Override
  public void update() {
    boolean gate = getInput(0);
    boolean source = getInput(1);

    // NMOS Conducts (Connects Source -> Drain) when Gate is HIGH
    if (gate) {
      if (getOutputWire() != null) {
        getOutputWire().setSignal(source);
      }
    }
    // If Gate is LOW, it effectively "floats" (does nothing),
    // allowing other connected components (like a PMOS) to drive the wire.
  }
}
