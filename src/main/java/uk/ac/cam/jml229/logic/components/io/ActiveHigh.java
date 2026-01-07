package uk.ac.cam.jml229.logic.components.io;

import uk.ac.cam.jml229.logic.components.Component;

public class ActiveHigh extends Component {
  public ActiveHigh(String name) {
    super(name);
    setInputCount(0); // Source: No inputs
  }

  @Override
  public void update() {
    // Always drive the output High (True)
    if (getOutputWire() != null) {
      getOutputWire().setSignal(true);
    }
  }
}
