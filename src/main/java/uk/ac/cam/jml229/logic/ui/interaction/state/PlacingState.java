package uk.ac.cam.jml229.logic.ui.interaction.state;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.ui.interaction.CircuitInteraction;
import uk.ac.cam.jml229.logic.ui.interaction.state.IdleState;
import uk.ac.cam.jml229.logic.ui.interaction.state.InteractionState;

public class PlacingState implements InteractionState {

  private final CircuitInteraction ctx;
  private Component ghost;

  public PlacingState(CircuitInteraction ctx, Component template) {
    this.ctx = ctx;
    // Create a detached copy for the "Ghost"
    this.ghost = template.makeCopy();
    // Link it to the Context so the Renderer can see it
    ctx.componentToPlace = this.ghost;
  }

  @Override
  public void onExit() {
    // Clean up when leaving this state
    ctx.componentToPlace = null;
    ctx.getPanel().repaint();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    updateGhostPosition(e);
    ctx.getPanel().repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    updateGhostPosition(e);
    ctx.getPanel().repaint();
  }

  private void updateGhostPosition(MouseEvent e) {
    Point p = ctx.getWorldPoint(e);
    if (ctx.isSnapToGrid()) {
      p.x = Math.round(p.x / 20.0f) * 20;
      p.y = Math.round(p.y / 20.0f) * 20;
    }
    ghost.setPosition(p.x, p.y);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    // Place the component
    ctx.saveHistory();

    // We add the *current* ghost instance to the circuit
    ctx.getCircuit().addComponent(ghost);

    // If CTRL is held, we stay in PlacingState but need a NEW ghost
    if (e.isControlDown()) {
      ghost = ghost.makeCopy(); // Create new ghost for next placement
      ctx.componentToPlace = ghost; // Update context
      updateGhostPosition(e); // Move to current mouse pos
    } else {
      // Otherwise, return to Idle
      ctx.setState(new IdleState(ctx));
    }
    ctx.getPanel().repaint();
  }

  public void rotate() {
    ghost.rotate();
    ctx.getPanel().repaint();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      ctx.setState(new IdleState(ctx));
    }
    if (e.getKeyCode() == KeyEvent.VK_R) {
      rotate();
    }
  }

  // Unused events
  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  // REMOVED: keyReleased and keyTyped (not in InteractionState interface)
}
