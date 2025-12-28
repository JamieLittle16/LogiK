package uk.ac.cam.jml229.logic.ui.interaction.state;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Graphics2D;

public interface InteractionState {
  void mousePressed(MouseEvent e);

  void mouseReleased(MouseEvent e);

  void mouseDragged(MouseEvent e);

  void mouseMoved(MouseEvent e);

  void mouseClicked(MouseEvent e);

  void keyPressed(KeyEvent e);

  // Render state-specific overlays (like ghost wires)
  default void render(Graphics2D g2) {
  }

  // Lifecycle hooks
  default void onEnter() {
  }

  default void onExit() {
  }
}
