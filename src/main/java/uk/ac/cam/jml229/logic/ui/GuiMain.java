package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import uk.ac.cam.jml229.logic.components.*;

public class GuiMain {
  public static void main(String[] args) {
    JFrame frame = new JFrame("Logic Simulator");
    CircuitPanel panel = new CircuitPanel();

    // Create a simple test circuit
    AndGate and = new AndGate("AND-1");
    and.setPosition(100, 100);

    panel.addComponent(and);

    frame.add(panel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
