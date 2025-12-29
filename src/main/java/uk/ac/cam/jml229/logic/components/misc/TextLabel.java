package uk.ac.cam.jml229.logic.components.misc;

import uk.ac.cam.jml229.logic.components.Component;

public class TextLabel extends Component {

  public TextLabel() {
    this("Label");
  }

  public TextLabel(String name) {
    super(name);
    setInputCount(0);
  }

  @Override
  public void update() {
    // Passive component
  }

  @Override
  public int getOutputCount() {
    return 0;
  }
}
