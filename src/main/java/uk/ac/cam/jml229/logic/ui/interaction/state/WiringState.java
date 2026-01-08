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

  public WiringState(CircuitInteraction ctx, Pin startPin, Point initialMousePt) {
    this.ctx = ctx;
    ctx.connectionStartPin = startPin;
    ctx.currentMousePoint = initialMousePt;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    ctx.updateHoverState(e);
    ctx.currentMousePoint = ctx.getWorldPoint(e);
    ctx.getPanel().repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    handleConnection(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    handleConnection(e);
  }

  private void handleConnection(MouseEvent e) {
    Point worldPt = ctx.getWorldPoint(e);

    // Try Connecting to Pin
    Pin endPin = ctx.getHitTester().findPinAt(worldPt);
    if (endPin != null && endPin != ctx.connectionStartPin) {
      if (connectToPin(endPin))
        finish();
      return;
    }

    // Try Connecting to Wire (Merge or T-Junction)
    WireSegment seg = ctx.getHitTester().findWireAt(worldPt);
    if (seg != null) {
      if (ctx.connectionStartPin.isInput()) {
        // Input -> Wire (Read Tap / T-Junction)
        connectTunction(seg, worldPt);
      } else {
        // Output -> Wire (Merge Source) - THIS WAS MISSING
        connectMerge(seg);
      }
      finish();
      return;
    }

    // Clicked empty space -> Cancel
    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      finish();
    }
  }

  // --- Helper Methods ---

  private boolean connectToPin(Pin endPin) {
    Pin start = ctx.connectionStartPin;
    // Only connect Input <-> Output
    if (start.isInput() != endPin.isInput()) {
      ctx.saveHistory();
      Pin source = start.isInput() ? endPin : start;
      Pin dest = start.isInput() ? start : endPin;
      ctx.getCircuit().addConnection(source.component(), source.index(), dest.component(), dest.index());
      return true;
    }
    return false;
  }

  private void connectTunction(WireSegment seg, Point pt) {
    Wire w = seg.wire();
    Component source = w.getSource();
    if (source == null)
      return;

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

    // Insert waypoint
    int idx = ctx.getHitTester().getWaypointInsertionIndex(seg, pt);
    seg.connection().waypoints.add(idx, new Point(pt));

    boolean ok = ctx.getCircuit().addConnection(source, srcIdx, ctx.connectionStartPin.component(),
        ctx.connectionStartPin.index());

    if (ok) {
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (pc.component == ctx.connectionStartPin.component() && pc.inputIndex == ctx.connectionStartPin.index()) {
          for (int k = 0; k <= idx; k++)
            pc.waypoints.add(new Point(seg.connection().waypoints.get(k)));
          break;
        }
      }
    }
  }

  private void connectMerge(WireSegment seg) {
    // Logic: Output Pin -> Existing Wire
    Wire w = seg.wire();
    if (w.getDestinations().isEmpty())
      return;

    ctx.saveHistory();

    Wire.PortConnection target = w.getDestinations().get(0);

    ctx.getCircuit().addConnection(
        ctx.connectionStartPin.component(),
        ctx.connectionStartPin.index(),
        target.component,
        target.inputIndex);
  }

  private void finish() {
    ctx.connectionStartPin = null;
    ctx.setPreventNextClick(true);
    ctx.setState(new IdleState(ctx));
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
