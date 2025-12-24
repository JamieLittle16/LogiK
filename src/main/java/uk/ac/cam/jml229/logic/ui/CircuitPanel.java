package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.model.Circuit;

public class CircuitPanel extends JPanel {

  // The MVC Trio
  private final Circuit circuit;
  private final CircuitRenderer renderer;
  private final CircuitInteraction interaction;

  public CircuitPanel() {
    setPreferredSize(new Dimension(800, 600));
    setBackground(Color.WHITE);
    setFocusable(true);

    // 1. Initialise Model
    this.circuit = new Circuit();

    // 2. Initialise View
    this.renderer = new CircuitRenderer();

    // 3. Initialise Controller
    this.interaction = new CircuitInteraction(circuit, this, renderer);

    // 4. Wire them up
    addMouseListener(interaction);
    addMouseMotionListener(interaction);
    addKeyListener(interaction);
  }

  public void addComponent(Component c) {
    circuit.addComponent(c);
    repaint();
  }

  public CircuitInteraction getInteraction() {
    return interaction;
  }

  public CircuitRenderer getRenderer() {
    return renderer;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // The Panel asks the Renderer to draw, passing data from Model and Controller
    renderer.render(
        (Graphics2D) g,
        circuit.getComponents(), // Data from Model
        circuit.getWires(), // Data from Model
        interaction.getSelectedComponents(), // State from Controller
        interaction.getSelectedWire(), // State from Controller
        interaction.getDragStartPin(), // State from Controller
        interaction.getDragCurrentPoint(), // State from Controller
        interaction.getSelectionRect(), // State from Controller
        interaction.getComponentToPlace());
  }
}
