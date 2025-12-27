package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GuiMain {

  // State for toggling back from Full Screen
  private static boolean isFullScreen = false;
  private static Point prevLocation = null;
  private static Dimension prevSize = null;

  public static void main(String[] args) {

    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");

    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Logic Simulator");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // --- Setup Components ---
      CircuitPanel circuitPanel = new CircuitPanel();
      CircuitInteraction interaction = circuitPanel.getInteraction();
      CircuitRenderer renderer = circuitPanel.getRenderer();

      ComponentPalette palette = new ComponentPalette(interaction, renderer);
      interaction.setPalette(palette);

      // --- Layout ---
      JScrollPane scrollPalette = new JScrollPane(palette);
      scrollPalette.setBorder(null);
      scrollPalette.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPalette, circuitPanel);
      splitPane.setDividerLocation(130);
      splitPane.setContinuousLayout(true);

      // CRITICAL: Tells SplitPane to give all extra space to the Right (Circuit)
      // panel
      // 0.0 means "give 0% of extra space to the left component"
      splitPane.setResizeWeight(0.0);

      frame.add(splitPane);

      // --- Window Setup ---
      frame.setSize(1280, 800);
      frame.setLocationRelativeTo(null); // Center on screen

      // Start maximized
      // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

      // --- F11 Full Screen Toggle ---
      circuitPanel.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_F11) {
            toggleFullScreen(frame);
          }
        }
      });

      frame.setVisible(true);
      circuitPanel.requestFocusInWindow();
    });
  }

  private static void toggleFullScreen(JFrame frame) {
    // We must dispose the frame to change decoration style (remove borders)
    frame.dispose();

    isFullScreen = !isFullScreen;

    if (isFullScreen) {
      // Save state to restore later
      prevLocation = frame.getLocation();
      prevSize = frame.getSize();

      // Enter Full Screen
      frame.setUndecorated(true);
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

      // Try to use the OS exclusive full screen mode if available
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      if (gd.isFullScreenSupported()) {
        try {
          gd.setFullScreenWindow(frame);
        } catch (Exception ex) {
          gd.setFullScreenWindow(null); // Fallback to maximized
        }
      }
    } else {
      // Exit Full Screen
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      gd.setFullScreenWindow(null);

      frame.setUndecorated(false);
      frame.setExtendedState(JFrame.NORMAL);

      // Restore previous position/size
      if (prevLocation != null)
        frame.setLocation(prevLocation);
      if (prevSize != null)
        frame.setSize(prevSize);
    }

    frame.setVisible(true);
  }
}
