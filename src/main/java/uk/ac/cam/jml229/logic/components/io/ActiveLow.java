package uk.ac.cam.jml229.logic.components.io;

import uk.ac.cam.jml229.logic.components.Component;

public class ActiveLow extends Component {

  public ActiveLow(String name) {
    super(name);
    setInputCount(0); // Source: No inputs
  }

  @Override
  public void update() {
    // Always drive the output Low (False)
    if (getOutputWire() != null) {
      getOutputWire().setSignal(false);
    }
  }
}
