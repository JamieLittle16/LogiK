package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import uk.ac.cam.jml229.logic.components.*;
import uk.ac.cam.jml229.logic.components.Component; // Explicit import

public class ComponentPalette extends JPanel {

  private final CircuitInteraction interaction;
  private final CircuitRenderer renderer;

  public ComponentPalette(CircuitInteraction interaction, CircuitRenderer renderer) {
    this.interaction = interaction;
    this.renderer = renderer;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(100, 0));
    setBackground(Color.LIGHT_GRAY);
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Add Tools
    addLabel("Inputs");
    addTool(new Switch("Switch"));

    addLabel("Outputs");
    addTool(new OutputProbe("Light"));

    addLabel("Gates");
    addTool(new AndGate("AND"));
    addTool(new OrGate("OR"));
    addTool(new NotGate("NOT"));
    addTool(new XorGate("XOR"));
  }

  private void addLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", Font.BOLD, 12));
    label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
    add(label);
  }

  private void addTool(Component prototype) {
    // Create a custom button that draws the component
    JPanel button = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Center the component visually in the button
        // We assume standard size 40x40 or 50x40
        int offsetX = (getWidth() - 50) / 2;
        int offsetY = (getHeight() - 40) / 2;

        // Temporarily move component to draw it here
        int oldX = prototype.getX();
        int oldY = prototype.getY();
        prototype.setPosition(offsetX, offsetY);

        renderer.drawComponentBody(g2, prototype, false);
        // We don't draw pins in the icon to keep it clean

        prototype.setPosition(oldX, oldY); // Restore
      }
    };

    button.setPreferredSize(new Dimension(80, 60));
    button.setMaximumSize(new Dimension(80, 60));
    button.setBackground(Color.WHITE);
    button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Interaction: Click to pick up
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        // Create a NEW instance of the component logic
        Component newComp = createNewInstance(prototype);
        interaction.startPlacing(newComp);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        button.setBackground(new Color(230, 240, 255)); // Hover effect
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setBackground(Color.WHITE);
      }
    });

    add(button);
    add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
  }

  // Factory method to create new instances based on the prototype type
  private Component createNewInstance(Component prototype) {
    if (prototype instanceof Switch)
      return new Switch("SW");
    if (prototype instanceof OutputProbe)
      return new OutputProbe("Out");
    if (prototype instanceof AndGate)
      return new AndGate("AND");
    if (prototype instanceof OrGate)
      return new OrGate("OR");
    if (prototype instanceof NotGate)
      return new NotGate("NOT");
    if (prototype instanceof XorGate)
      return new XorGate("XOR");
    return null;
  }
}
