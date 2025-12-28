package uk.ac.cam.jml229.logic.ui.interaction.state;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.core.Wire;
import uk.ac.cam.jml229.logic.ui.interaction.CircuitInteraction;
import uk.ac.cam.jml229.logic.ui.render.CircuitRenderer.WaypointRef;

public class DraggingState implements InteractionState {

  private final CircuitInteraction ctx;
  private final Point startPt;
  private final Map<Component, Point> initialPositions = new HashMap<>();
  private boolean hasDragged = false;

  public DraggingState(CircuitInteraction ctx, Point startPt) {
    this.ctx = ctx;
    this.startPt = startPt;

    for (Component c : ctx.getSelectedComponents()) {
      initialPositions.put(c, new Point(c.getX(), c.getY()));
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (!hasDragged) {
      ctx.saveHistory();
      hasDragged = true;
    }

    Point current = ctx.getWorldPoint(e);
    int dx = current.x - startPt.x;
    int dy = current.y - startPt.y;

    if (ctx.isSnapToGrid()) {
      dx = Math.round(dx / 20.0f) * 20;
      dy = Math.round(dy / 20.0f) * 20;
    }

    // Move Components
    for (Map.Entry<Component, Point> entry : initialPositions.entrySet()) {
      Point initial = entry.getValue();
      entry.getKey().setPosition(initial.x + dx, initial.y + dy);
    }

    // Move Waypoints
    for (WaypointRef wp : ctx.getSelectedWaypoints()) {
      wp.point().setLocation(current); // Absolute move for single waypoint
      // Note: Snap logic for waypoints handles itself usually, simplified here
    }

    ctx.getPanel().repaint();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    ctx.setState(new IdleState(ctx));
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void keyPressed(java.awt.event.KeyEvent e) {
  }
}
