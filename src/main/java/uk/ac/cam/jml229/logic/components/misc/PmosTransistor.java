package uk.ac.cam.jml229.logic.components.misc;

import uk.ac.cam.jml229.logic.components.Component;

public class PmosTransistor extends Component {

  public PmosTransistor(String name) {
    super(name);
    // Input 0: Gate (Control)
    // Input 1: Source (Input Signal)
    setInputCount(2);
  }

  @Override
  public void update() {
    boolean gate = getInput(0);
    boolean source = getInput(1);

    // PMOS Conducts (Connects Source -> Drain) when Gate is LOW
    if (!gate) {
      if (getOutputWire() != null) {
        getOutputWire().setSignal(source);
      }
    }
  }
}
