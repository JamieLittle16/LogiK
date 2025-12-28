package uk.ac.cam.jml229.logic.ui.interaction.state;

import java.awt.Point;
import java.awt.event.MouseEvent;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.core.Wire;
import uk.ac.cam.jml229.logic.ui.interaction.CircuitInteraction;
import uk.ac.cam.jml229.logic.ui.render.CircuitRenderer.Pin;
import uk.ac.cam.jml229.logic.ui.render.CircuitRenderer.WireSegment;

public class WiringState implements InteractionState {

  private final CircuitInteraction ctx;

  public WiringState(CircuitInteraction ctx, Pin startPin) {
    this.ctx = ctx;
    ctx.connectionStartPin = startPin;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    ctx.currentMousePoint = ctx.getWorldPoint(e);
    ctx.getPanel().repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    Point worldPt = ctx.getWorldPoint(e);
    Pin endPin = ctx.getHitTester().findPinAt(worldPt);

    // Clicked a Pin -> Connect
    if (endPin != null) {
      Pin start = ctx.connectionStartPin;
      // Only connect Input to Output
      if (start.isInput() != endPin.isInput()) {
        ctx.saveHistory();
        Pin source = start.isInput() ? endPin : start;
        Pin dest = start.isInput() ? start : endPin;
        ctx.getCircuit().addConnection(source.component(), source.index(), dest.component(), dest.index());
      }
      finish();
      return;
    }

    // Clicked a Wire (T-Junction) -> Connect
    WireSegment seg = ctx.getHitTester().findWireAt(worldPt);
    if (seg != null && ctx.connectionStartPin.isInput()) {
      // Can only branch FROM a wire INTO an input pin
      connectTunction(seg, worldPt);
      finish();
      return;
    }

    // Clicked Empty Space -> Cancel
    finish();
  }

  private void connectTunction(WireSegment seg, Point pt) {
    Wire w = seg.wire();
    Component source = w.getSource();
    if (source == null)
      return;

    // Find source index
    int srcIdx = -1;
    for (int i = 0; i < source.getOutputCount(); i++) {
      if (source.getOutputWire(i) == w) {
        srcIdx = i;
        break;
      }
    }
    if (srcIdx == -1)
      return;

    ctx.saveHistory();

    // Add waypoint to existing wire to create "T" shape
    int idx = ctx.getHitTester().getWaypointInsertionIndex(seg, pt);
    seg.connection().waypoints.add(idx, new Point(pt));

    // Connect new pin
    boolean ok = ctx.getCircuit().addConnection(source, srcIdx, ctx.connectionStartPin.component(),
        ctx.connectionStartPin.index());

    if (ok) {
      // Copy path to new connection
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == ctx.connectionStartPin.component() && pc.inputIndex == ctx.connectionStartPin.index()) {
          for (int k = 0; k <= idx; k++)
            pc.waypoints.add(new Point(seg.connection().waypoints.get(k)));
          break;
        }
      }
    }
  }

  private void finish() {
    ctx.connectionStartPin = null;
    ctx.setState(new IdleState(ctx));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void keyPressed(java.awt.event.KeyEvent e) {
    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
      finish();
  }
}
