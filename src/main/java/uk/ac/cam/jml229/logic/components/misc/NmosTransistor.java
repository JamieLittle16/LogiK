package uk.ac.cam.jml229.logic.components.misc;

import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.core.Wire;

public class NmosTransistor extends Component {

  public NmosTransistor(String name) {
    super(name);
    setInputCount(2); // 0: Gate, 1: Source
  }

  @Override
  public void update() {
    boolean gate = getInput(0);
    boolean source = getInput(1);
    Wire w = getOutputWire();

    if (w != null) {
      if (gate) {
        // ON: Drive the wire with our source value
        w.setDrive(this, source ? Wire.State.HIGH : Wire.State.LOW);
      } else {
        // OFF: Float
        w.setDrive(this, Wire.State.FLOATING);
      }
    }
  }
}
