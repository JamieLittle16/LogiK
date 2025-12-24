package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import uk.ac.cam.jml229.logic.components.*;

public class GuiMain {
  public static void main(String[] args) {
    JFrame frame = new JFrame("Logic Simulator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // 1. Create the Main Circuit Panel (which holds the Controller + Renderer)
    CircuitPanel circuitPanel = new CircuitPanel();

    // 2. Extract the Controller & Renderer to give to the Palette
    // (We need to add getters to CircuitPanel for this, see note below)
    CircuitInteraction interaction = circuitPanel.getInteraction();
    CircuitRenderer renderer = circuitPanel.getRenderer();

    // 3. Create the Palette
    ComponentPalette palette = new ComponentPalette(interaction, renderer);

    // 4. Split Pane Layout
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette, circuitPanel);
    splitPane.setDividerLocation(120); // Width of sidebar
    splitPane.setContinuousLayout(true);

    frame.add(splitPane);
    frame.setSize(1000, 700);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
