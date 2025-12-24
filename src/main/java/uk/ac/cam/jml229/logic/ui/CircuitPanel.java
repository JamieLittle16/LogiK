package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.components.Wire;

public class CircuitPanel extends JPanel {
  private List<Component> components = new ArrayList<>();
  private List<Wire> wires = new ArrayList<>();

  public CircuitPanel() {
    setPreferredSize(new Dimension(800, 600));
    setBackground(Color.WHITE);
  }

  public void addComponent(Component c) {
    components.add(c);
    repaint(); // Request a redraw
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // Draw Components
    for (Component c : components) {
      // Placeholder: Draw a blue box for every component
      // You will eventually want distinct icons for AND, OR, etc.
      g2.setColor(Color.BLUE);
      g2.fillRect(50, 50, 60, 40); // Hardcoded position for now
      g2.setColor(Color.BLACK);
      g2.drawString(c.getName(), 55, 75);
    }
  }
}
