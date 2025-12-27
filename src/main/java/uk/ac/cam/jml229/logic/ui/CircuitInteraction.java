package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import uk.ac.cam.jml229.logic.components.*;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.model.Circuit;
import uk.ac.cam.jml229.logic.model.Wire;
import uk.ac.cam.jml229.logic.ui.CircuitRenderer.Pin;
import uk.ac.cam.jml229.logic.ui.CircuitRenderer.WireSegment;
import uk.ac.cam.jml229.logic.ui.CircuitRenderer.WaypointRef;

public class CircuitInteraction extends MouseAdapter implements KeyListener {

  private final Circuit circuit;
  private final CircuitPanel panel;
  private final CircuitRenderer renderer;
  private final CircuitHitTester hitTester;
  private ComponentPalette palette;

  // --- Interaction State ---
  private final List<Component> selectedComponents = new ArrayList<>();
  private WireSegment selectedWireSegment = null;
  private Pin hoveredPin = null;
  private WireSegment hoveredWire = null;
  private Pin connectionStartPin = null;
  private Point currentMousePoint = null;
  private Rectangle selectionRect;
  private Point selectionStartPt;
  private Point lastMousePt;

  private boolean isDraggingItems = false;
  private boolean isMouseInsidePanel = false;

  // Panning State
  private boolean isPanning = false;

  // Ghost component
  private Component componentToPlace = null;

  // Waypoint State
  private WaypointRef selectedWaypoint = null;
  private WaypointRef hoveredWaypoint = null;

  public CircuitInteraction(Circuit circuit, CircuitPanel panel, CircuitRenderer renderer) {
    this.circuit = circuit;
    this.panel = panel;
    this.renderer = renderer;
    this.hitTester = new CircuitHitTester(circuit, renderer);
  }

  public void setPalette(ComponentPalette palette) {
    this.palette = palette;
  }

  // --- Accessors ---
  public List<Component> getSelectedComponents() {
    return selectedComponents;
  }

  public WireSegment getSelectedWire() {
    return selectedWireSegment;
  }

  public Pin getHoveredPin() {
    return hoveredPin;
  }

  public WireSegment getHoveredWire() {
    return hoveredWire;
  }

  public Pin getConnectionStartPin() {
    return connectionStartPin;
  }

  public Point getCurrentMousePoint() {
    return currentMousePoint;
  }

  public Rectangle getSelectionRect() {
    return selectionRect;
  }

  public WaypointRef getSelectedWaypoint() {
    return selectedWaypoint;
  }

  public WaypointRef getHoveredWaypoint() {
    return hoveredWaypoint;
  }

  public Component getComponentToPlace() {
    return isMouseInsidePanel ? componentToPlace : null;
  }

  // --- Actions ---

  public void startPlacing(Component c) {
    this.componentToPlace = c;
    this.connectionStartPin = null;
    panel.repaint();
  }

  public void deleteSelection() {
    if (selectedWaypoint != null) {
      selectedWaypoint.connection().waypoints.remove(selectedWaypoint.point());
      selectedWaypoint = null;
    } else if (selectedWireSegment != null) {
      circuit.removeConnection(
          selectedWireSegment.connection().component,
          selectedWireSegment.connection().inputIndex);
      selectedWireSegment = null;
    } else if (!selectedComponents.isEmpty()) {
      for (Component c : new ArrayList<>(selectedComponents)) {
        circuit.removeComponent(c);
      }
      selectedComponents.clear();
    }
    panel.repaint();
  }

  // --- Coordinate Transformation ---
  private Point getWorldPoint(MouseEvent e) {
    return new Point(e.getX() - panel.getPanX(), e.getY() - panel.getPanY());
  }

  // --- Mouse Handling ---

  @Override
  public void mouseEntered(MouseEvent e) {
    isMouseInsidePanel = true;
    panel.requestFocusInWindow();
    panel.repaint();
  }

  @Override
  public void mouseExited(MouseEvent e) {
    isMouseInsidePanel = false;
    panel.repaint();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    currentMousePoint = getWorldPoint(e);

    // Update Ghost Component
    if (componentToPlace != null) {
      int gridX = Math.round(currentMousePoint.x / 20.0f) * 20;
      int gridY = Math.round(currentMousePoint.y / 20.0f) * 20;
      componentToPlace.setPosition(gridX, gridY);
      panel.repaint();
      return;
    }

    // Update Hover State
    Point worldPt = getWorldPoint(e);

    hoveredPin = hitTester.findPinAt(worldPt);
    hoveredWaypoint = hitTester.findWaypointAt(worldPt);
    hoveredWire = (hoveredPin == null && hoveredWaypoint == null) ? hitTester.findWireAt(worldPt) : null;

    if (hoveredPin != null || hoveredWire != null || hoveredWaypoint != null) {
      panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    } else {
      Component c = hitTester.findComponentAt(worldPt);
      panel.setCursor(c != null ? Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
          : Cursor.getDefaultCursor());
    }

    panel.repaint();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    panel.requestFocusInWindow();
    lastMousePt = e.getPoint();
    currentMousePoint = getWorldPoint(e);

    // --- Panning Logic ---
    boolean isLaptopPan = SwingUtilities.isLeftMouseButton(e) && e.isAltDown();
    if (SwingUtilities.isMiddleMouseButton(e) || isLaptopPan) {
      isPanning = true;
      panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      return;
    }

    // --- Right Click ---
    if (SwingUtilities.isRightMouseButton(e)) {
      if (componentToPlace != null || connectionStartPin != null) {
        componentToPlace = null;
        connectionStartPin = null;
      } else if (!selectedComponents.isEmpty()) {
        showContextMenu(e.getX(), e.getY());
      }
      panel.repaint();
      return;
    }

    // --- Left Click ---
    Point worldPt = getWorldPoint(e);

    // Place Component
    if (componentToPlace != null) {
      circuit.addComponent(componentToPlace);
      if (e.isControlDown()) {
        componentToPlace = componentToPlace.makeCopy();
      } else {
        componentToPlace = null;
      }
      panel.repaint();
      return;
    }

    Pin clickedPin = hitTester.findPinAt(worldPt);

    // Wiring Phase 2
    if (connectionStartPin != null) {
      if (clickedPin != null) {
        if (connectionStartPin.isInput() != clickedPin.isInput()) {
          Pin sourcePin = connectionStartPin.isInput() ? clickedPin : connectionStartPin;
          Pin destPin = connectionStartPin.isInput() ? connectionStartPin : clickedPin;
          circuit.addConnection(sourcePin.component(), sourcePin.index(), destPin.component(), destPin.index());
        }
        connectionStartPin = null;
      } else {
        connectionStartPin = null;
      }
      panel.repaint();
      return;
    }

    // Wiring Phase 1
    if (clickedPin != null) {
      connectionStartPin = clickedPin;
      selectedComponents.clear();
      selectedWireSegment = null;
      selectedWaypoint = null;
      panel.repaint();
      return;
    }

    // Waypoint Click
    WaypointRef clickedWP = hitTester.findWaypointAt(worldPt);
    if (clickedWP != null) {
      selectedWaypoint = clickedWP;
      selectedWireSegment = new WireSegment(getWireForConnection(clickedWP.connection()), clickedWP.connection());
      selectedComponents.clear();
      panel.repaint();
      return;
    }

    // Wire Click (Select or Split)
    WireSegment clickedWire = hitTester.findWireAt(worldPt);
    if (clickedWire != null) {
      if (selectedWireSegment != null &&
          clickedWire.wire() == selectedWireSegment.wire() &&
          clickedWire.connection() == selectedWireSegment.connection()) {

        // Use HitTester to find the correct index to insert
        int idx = hitTester.getWaypointInsertionIndex(clickedWire, worldPt);
        clickedWire.connection().waypoints.add(idx, worldPt);

        selectedWaypoint = new WaypointRef(clickedWire.connection(), worldPt);
      } else {
        selectedWireSegment = clickedWire;
        selectedWaypoint = null;
        selectedComponents.clear();
      }
      panel.repaint();
      return;
    } else {
      selectedWireSegment = null;
      selectedWaypoint = null;
    }

    // Component Selection
    Component clickedComp = hitTester.findComponentAt(worldPt);
    if (clickedComp != null) {
      handleComponentSelection(e, clickedComp);
    } else {
      startSelectionBox(worldPt);
    }
    panel.repaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (isPanning) {
      int dx = e.getX() - lastMousePt.x;
      int dy = e.getY() - lastMousePt.y;
      panel.setPan(panel.getPanX() + dx, panel.getPanY() + dy);
      lastMousePt = e.getPoint();
      return;
    }

    if (connectionStartPin != null) {
      currentMousePoint = getWorldPoint(e);
      panel.repaint();
      return;
    }

    // Dragging Waypoint
    if (selectedWaypoint != null) {
      Point pt = selectedWaypoint.point();
      pt.setLocation(getWorldPoint(e));

      // Snapping logic using adjacent points
      List<Point> points = selectedWaypoint.connection().waypoints;
      int index = points.indexOf(pt);
      Point prev = null;
      Point next = null;
      Wire w = getWireForConnection(selectedWaypoint.connection());

      if (index > 0) {
        prev = points.get(index - 1);
      } else if (w != null) {
        Component src = w.getSource();
        int srcIdx = 0;
        for (int i = 0; i < src.getOutputCount(); i++)
          if (src.getOutputWire(i) == w)
            srcIdx = i;
        prev = renderer.getPinLocation(src, false, srcIdx);
      }

      if (index < points.size() - 1) {
        next = points.get(index + 1);
      } else {
        next = renderer.getPinLocation(selectedWaypoint.connection().component, true,
            selectedWaypoint.connection().inputIndex);
      }

      int snapDist = 15;
      if (prev != null) {
        if (Math.abs(pt.x - prev.x) < snapDist)
          pt.x = prev.x;
        if (Math.abs(pt.y - prev.y) < snapDist)
          pt.y = prev.y;
      }
      if (next != null) {
        if (Math.abs(pt.x - next.x) < snapDist)
          pt.x = next.x;
        if (Math.abs(pt.y - next.y) < snapDist)
          pt.y = next.y;
      }

      panel.repaint();
      return;
    }

    // Dragging Components
    if (isDraggingItems) {
      int dx = e.getX() - lastMousePt.x;
      int dy = e.getY() - lastMousePt.y;
      for (Component c : selectedComponents) {
        c.setPosition(c.getX() + dx, c.getY() + dy);
      }
      lastMousePt = e.getPoint();
      panel.repaint();
      return;
    }

    if (selectionRect != null) {
      updateSelectionBox(getWorldPoint(e));
      panel.repaint();
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (isPanning) {
      isPanning = false;
      panel.setCursor(Cursor.getDefaultCursor());
    }
    if (selectionRect != null) {
      finalizeSelectionBox();
    }
    isDraggingItems = false;
    panel.repaint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (connectionStartPin == null && !isDraggingItems && componentToPlace == null) {
      Point worldPt = getWorldPoint(e);
      Component c = hitTester.findComponentAt(worldPt);
      if (c instanceof Switch) {
        ((Switch) c).toggle(!((Switch) c).getState());
        panel.repaint();
      }
    }
  }

  // --- Key Listener ---
  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      deleteSelection();
    }
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      connectionStartPin = null;
      componentToPlace = null;
      panel.repaint();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  // ==========================================
  // CUSTOM COMPONENT LOGIC
  // ==========================================

  private void showContextMenu(int x, int y) {
    JPopupMenu menu = new JPopupMenu();
    JMenuItem createItem = new JMenuItem("Create Custom Component");
    createItem.addActionListener(e -> createCustomComponentFromSelection());
    menu.add(createItem);
    JMenuItem deleteItem = new JMenuItem("Delete Selection");
    deleteItem.addActionListener(e -> deleteSelection());
    menu.add(deleteItem);
    menu.show(panel, x, y);
  }

  private void createCustomComponentFromSelection() {
    if (selectedComponents.isEmpty())
      return;
    String name = JOptionPane.showInputDialog(panel, "Enter Name (max 5 chars):", "New Component",
        JOptionPane.PLAIN_MESSAGE);
    if (name == null || name.trim().isEmpty())
      return;
    if (name.length() > 5)
      name = name.substring(0, 5);

    Circuit innerCircuit = new Circuit();
    Map<Component, Component> oldToNew = new HashMap<>();

    for (Component original : selectedComponents) {
      Component clone = original.makeCopy();
      clone.setPosition(original.getX(), original.getY());
      innerCircuit.addComponent(clone);
      oldToNew.put(original, clone);
    }
    for (Wire w : circuit.getWires()) {
      Component source = w.getSource();
      if (source == null || !selectedComponents.contains(source))
        continue;
      int sourceIndex = -1;
      for (int i = 0; i < source.getOutputCount(); i++) {
        if (source.getOutputWire(i) == w) {
          sourceIndex = i;
          break;
        }
      }
      if (sourceIndex == -1)
        continue;
      for (Wire.PortConnection pc : w.getDestinations()) {
        if (selectedComponents.contains(pc.component)) {
          Component newSource = oldToNew.get(source);
          Component newDest = oldToNew.get(pc.component);
          innerCircuit.addConnection(newSource, sourceIndex, newDest, pc.inputIndex);
        }
      }
    }
    CustomComponent newTool = new CustomComponent(name, innerCircuit);
    if (palette != null)
      palette.addCustomTool(newTool);
  }

  private void handleComponentSelection(MouseEvent e, Component clickedComp) {
    if (e.isShiftDown()) {
      if (selectedComponents.contains(clickedComp))
        selectedComponents.remove(clickedComp);
      else
        selectedComponents.add(clickedComp);
    } else {
      if (!selectedComponents.contains(clickedComp)) {
        selectedComponents.clear();
        selectedComponents.add(clickedComp);
      }
    }
    isDraggingItems = true;
  }

  private void startSelectionBox(Point p) {
    selectedComponents.clear();
    selectionStartPt = p;
    selectionRect = new Rectangle(p.x, p.y, 0, 0);
  }

  private void updateSelectionBox(Point p) {
    int x = Math.min(selectionStartPt.x, p.x);
    int y = Math.min(selectionStartPt.y, p.y);
    int w = Math.abs(p.x - selectionStartPt.x);
    int h = Math.abs(p.y - selectionStartPt.y);
    selectionRect.setBounds(x, y, w, h);
  }

  private void finalizeSelectionBox() {
    for (Component c : circuit.getComponents()) {
      if (selectionRect.contains(c.getX() + 20, c.getY() + 20)) {
        if (!selectedComponents.contains(c))
          selectedComponents.add(c);
      }
    }
    selectionRect = null;
  }

  private Wire getWireForConnection(Wire.PortConnection pc) {
    for (Wire w : circuit.getWires()) {
      if (w.getDestinations().contains(pc))
        return w;
    }
    return null;
  }
}
