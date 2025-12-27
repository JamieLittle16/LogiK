package uk.ac.cam.jml229.logic.ui;

import java.awt.*;
import java.awt.geom.*;
import uk.ac.cam.jml229.logic.components.*;
import uk.ac.cam.jml229.logic.components.Component;

public class ComponentPainter {

  // Styling
  private static final Color PIN_COLOR = new Color(50, 50, 50);
  private static final Color STUB_COLOR = new Color(0, 0, 0);
  private static final Color SELECTION_BORDER = new Color(0, 180, 255);
  private static final Color HOVER_COLOR = new Color(255, 180, 0);
  private static final int PIN_SIZE = 8;

  public void drawComponent(Graphics2D g2, Component c, boolean isSelected, boolean showLabel) {
    int x = c.getX();
    int y = c.getY();

    // Draw the body
    if (c instanceof Switch)
      drawSwitch(g2, (Switch) c, x, y, isSelected);
    else if (c instanceof OutputProbe)
      drawLight(g2, (OutputProbe) c, x, y, isSelected);
    else if (c instanceof AndGate)
      drawAndGate(g2, c, x, y, isSelected);
    else if (c instanceof OrGate)
      drawOrGate(g2, c, x, y, isSelected);
    else if (c instanceof XorGate)
      drawXorGate(g2, c, x, y, isSelected);
    else if (c instanceof NotGate)
      drawNotGate(g2, c, x, y, isSelected);
    else if (c instanceof NandGate)
      drawNandGate(g2, c, x, y, isSelected);
    else if (c instanceof NorGate)
      drawNorGate(g2, c, x, y, isSelected);
    else if (c instanceof BufferGate)
      drawBufferGate(g2, c, x, y, isSelected);
    else
      drawGenericBox(g2, c, x, y, isSelected);

    // Draw Label (Generic text below component)
    if (showLabel && !(c instanceof CustomComponent)) {
      g2.setColor(Color.BLACK);
      g2.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
      FontMetrics fm = g2.getFontMetrics();
      int tw = fm.stringWidth(c.getName());
      g2.drawString(c.getName(), x + (50 - tw) / 2, y - 5);
    }
  }

  public void drawStubs(Graphics2D g2, Component c) {
    if (c instanceof OutputProbe)
      return;
    g2.setColor(STUB_COLOR);
    g2.setStroke(new BasicStroke(3));

    int x = c.getX();

    // Output Stubs
    int outCount = c.getOutputCount();
    boolean hasBubbleOutput = (c instanceof NandGate || c instanceof NorGate);
    for (int i = 0; i < outCount; i++) {
      if (outCount == 1) {
        if (!hasBubbleOutput) {
          if (c instanceof Switch)
            g2.drawLine(x + 40, c.getY() + 20, x + 60, c.getY() + 20);
          else
            g2.drawLine(x + 50, c.getY() + 20, x + 60, c.getY() + 20);
        } else {
          g2.drawLine(x + 55, c.getY() + 20, x + 60, c.getY() + 20);
        }
      } else {
        Point p = getPinLocation(c, false, i);
        g2.drawLine(x + 50, p.y, p.x, p.y);
      }
    }

    // Input Stubs
    int inputCount = getInputCount(c);
    for (int i = 0; i < inputCount; i++) {
      Point p = getPinLocation(c, true, i);
      int endX = x;
      if (c instanceof OrGate || c instanceof NorGate || c instanceof XorGate)
        endX = x + 8;
      g2.drawLine(p.x, p.y, endX, p.y);
    }
  }

  public void drawPinCircle(Graphics2D g2, Point p, boolean isHovered, boolean isActive) {
    if (isActive) {
      g2.setColor(SELECTION_BORDER);
      g2.fillOval(p.x - 6, p.y - 6, 12, 12);
    } else if (isHovered) {
      g2.setColor(HOVER_COLOR);
      g2.drawOval(p.x - 6, p.y - 6, 12, 12);
    }
    g2.setColor(PIN_COLOR);
    g2.fillOval(p.x - PIN_SIZE / 2, p.y - PIN_SIZE / 2, PIN_SIZE, PIN_SIZE);
  }

  // --- Math / Geometry Helpers ---

  public Point getPinLocation(Component c, boolean isInput, int index) {
    if (!isInput) {
      int outCount = c.getOutputCount();
      if (outCount <= 1)
        return new Point(c.getX() + 60, c.getY() + 20);
      else
        return new Point(c.getX() + 60, c.getY() + 10 + (index * 20));
    } else {
      int count = getInputCount(c);
      if (count == 1)
        return new Point(c.getX() - 10, c.getY() + 20);
      else
        return new Point(c.getX() - 10, c.getY() + 10 + (index * 20));
    }
  }

  public int getInputCount(Component c) {
    if (c instanceof Switch)
      return 0;
    if (c instanceof UnaryGate || c instanceof OutputProbe)
      return 1;
    return c.getInputCount();
  }

  // --- Private Drawing Primitives (Moved from CircuitRenderer) ---

  private void drawSwitch(Graphics2D g2, Switch s, int x, int y, boolean sel) {
    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.drawRoundRect(x, y + 5, 40, 30, 15, 15);
    }
    g2.setColor(Color.DARK_GRAY);
    g2.fillRoundRect(x, y + 5, 40, 30, 30, 30);
    boolean on = s.getOutputWire() != null && s.getOutputWire().getSignal();
    int circleX = on ? x + 22 : x + 2;
    Color c = on ? new Color(100, 255, 100) : new Color(200, 200, 200);
    g2.setColor(c);
    g2.fillOval(circleX, y + 7, 26, 26);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(1));
    g2.drawOval(circleX, y + 7, 26, 26);
  }

  private void drawLight(Graphics2D g2, OutputProbe p, int x, int y, boolean sel) {
    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.drawOval(x, y, 40, 40);
    }
    boolean on = p.getState();
    Color core = on ? new Color(255, 220, 0) : new Color(50, 50, 50);
    if (on) {
      float[] dist = { 0.0f, 0.7f, 1.0f };
      Color[] colors = { new Color(255, 255, 200, 200), new Color(255, 220, 0, 100), new Color(0, 0, 0, 0) };
      RadialGradientPaint glow = new RadialGradientPaint(new Point2D.Float(x + 20, y + 20), 35, dist, colors);
      g2.setPaint(glow);
      g2.fillOval(x - 15, y - 15, 70, 70);
    }
    GradientPaint gp = new GradientPaint(x, y, core.brighter(), x + 30, y + 30, core.darker());
    g2.setPaint(gp);
    g2.fillOval(x, y, 40, 40);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2));
    g2.drawOval(x, y, 40, 40);
    g2.setColor(new Color(255, 255, 255, 100));
    g2.fillOval(x + 10, y + 8, 12, 8);
  }

  private void drawGenericBox(Graphics2D g2, Component c, int x, int y, boolean sel) {
    int inputCount = getInputCount(c);
    int outputCount = c.getOutputCount();
    int maxPins = Math.max(inputCount, outputCount);
    int h = Math.max(40, maxPins * 20);
    int w = 50;

    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.drawRect(x, y, w, h);
    }
    g2.setColor(new Color(220, 220, 220));
    g2.fillRect(x, y, w, h);
    g2.setColor(new Color(60, 60, 60));
    g2.fillRect(x, y, w, 16);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2));
    g2.drawRect(x, y, w, h);

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("SansSerif", Font.BOLD, 10));
    FontMetrics fm = g2.getFontMetrics();
    String name = c.getName();
    if (fm.stringWidth(name) > 46) {
      while (name.length() > 0 && fm.stringWidth(name + "..") > 46)
        name = name.substring(0, name.length() - 1);
      name += "..";
    }
    int textWidth = fm.stringWidth(name);
    g2.drawString(name, x + (w - textWidth) / 2, y + 12);
  }

  private void drawAndGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    Path2D p = new Path2D.Double();
    p.moveTo(x, y);
    p.lineTo(x + 25, y);
    p.curveTo(x + 57, y, x + 57, y + 40, x + 25, y + 40);
    p.lineTo(x, y + 40);
    p.closePath();
    fillGate(g2, p, sel);
  }

  private void drawOrGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    Path2D p = new Path2D.Double();
    p.moveTo(x, y);
    p.quadTo(x + 15, y + 20, x, y + 40);
    p.quadTo(x + 35, y + 40, x + 50, y + 20);
    p.quadTo(x + 35, y, x, y);
    p.closePath();
    fillGate(g2, p, sel);
  }

  private void drawXorGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    Path2D b = new Path2D.Double();
    b.moveTo(x - 4, y);
    b.quadTo(x + 11, y + 20, x - 4, y + 40);
    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.draw(b);
    }
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2));
    g2.draw(b);
    drawOrGate(g2, c, x + 5, y, sel);
  }

  private void drawBufferGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    Path2D p = new Path2D.Double();
    p.moveTo(x, y);
    p.lineTo(x + 40, y + 20);
    p.lineTo(x, y + 40);
    p.closePath();
    fillGate(g2, p, sel);
  }

  private void drawNotGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    drawBufferGate(g2, c, x, y, sel);
    drawBubble(g2, x + 40, y + 15, sel);
  }

  private void drawNandGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    drawAndGate(g2, c, x, y, sel);
    drawBubble(g2, x + 45, y + 15, sel);
  }

  private void drawNorGate(Graphics2D g2, Component c, int x, int y, boolean sel) {
    drawOrGate(g2, c, x, y, sel);
    drawBubble(g2, x + 45, y + 15, sel);
  }

  private void fillGate(Graphics2D g2, Path2D p, boolean sel) {
    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.draw(p);
    }
    GradientPaint gp = new GradientPaint(0, 0, new Color(70, 120, 200), 0, 40, new Color(120, 160, 240));
    g2.setPaint(gp);
    g2.fill(p);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2));
    g2.draw(p);
  }

  private void drawBubble(Graphics2D g2, int x, int y, boolean sel) {
    if (sel) {
      g2.setColor(SELECTION_BORDER);
      g2.setStroke(new BasicStroke(5));
      g2.drawOval(x, y, 10, 10);
    }
    g2.setColor(Color.WHITE);
    g2.fillOval(x, y, 10, 10);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2));
    g2.drawOval(x, y, 10, 10);
  }
}
