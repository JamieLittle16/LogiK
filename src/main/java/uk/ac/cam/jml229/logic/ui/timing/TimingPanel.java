package uk.ac.cam.jml229.logic.ui.timing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.jml229.logic.app.Theme;

public class TimingPanel extends JPanel implements Scrollable {

  private final List<SignalMonitor> monitors = new ArrayList<>();
  private final int bufferSize = 2000;
  private int timeStep = 5;
  private boolean paused = false;

  private static final int ROW_HEIGHT = 60;
  private static final int LABEL_WIDTH = 140;

  public TimingPanel() {
    setBackground(Theme.BACKGROUND);
    setAutoscrolls(true);
  }

  public void addMonitor(SignalMonitor m) {
    monitors.add(m);
    revalidate();
    repaint();
  }

  public void clear() {
    monitors.clear();
    revalidate();
    repaint();
  }

  public void tick() {
    if (paused)
      return;
    for (SignalMonitor m : monitors)
      m.tick();

    Rectangle visible = getVisibleRect();
    int width = getWidth();
    if (visible.x + visible.width >= width - timeStep * 2) {
      scrollRectToVisible(new Rectangle(width - 1, 0, 1, 1));
    } else {
      repaint();
    }
  }

  public void togglePause() {
    paused = !paused;
  }

  public boolean isPaused() {
    return paused;
  }

  public void zoomIn() {
    timeStep = Math.min(timeStep + 1, 50);
    revalidate();
    repaint();
  }

  public void zoomOut() {
    timeStep = Math.max(timeStep - 1, 1);
    revalidate();
    repaint();
  }

  public int getBufferSize() {
    return bufferSize;
  }

  @Override
  public Dimension getPreferredSize() {
    int w = LABEL_WIDTH + (bufferSize * timeStep);
    int h = Math.max(400, monitors.size() * ROW_HEIGHT + 20);
    return new Dimension(w, h);
  }

  @Override
  protected void paintComponent(Graphics g) {
    setBackground(Theme.BACKGROUND);
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    Rectangle viewRect = getVisibleRect();
    int labelX = viewRect.x;
    int w = getWidth();
    int y = 10;

    for (SignalMonitor m : monitors) {
      drawSignalRow(g2, m, y, w);
      y += ROW_HEIGHT;
    }

    y = 10;
    for (SignalMonitor m : monitors) {
      drawLabelOverlay(g2, m, labelX, y);
      y += ROW_HEIGHT;
    }

    g2.setColor(Theme.GRID_MAJOR);
    g2.setStroke(new BasicStroke(2));
    g2.drawLine(labelX + LABEL_WIDTH, viewRect.y, labelX + LABEL_WIDTH, viewRect.y + viewRect.height);
  }

  private void drawSignalRow(Graphics2D g2, SignalMonitor m, int y, int totalWidth) {
    int rowBot = y + ROW_HEIGHT;
    int graphTop = y + 10;
    int graphBot = rowBot - 10;

    g2.setColor(Theme.GRID_MINOR);
    g2.drawLine(0, rowBot, totalWidth, rowBot);

    g2.setColor(m.getColor());
    g2.setStroke(new BasicStroke(2));

    Rectangle view = getVisibleRect();
    int minI = Math.max(0, (totalWidth - (view.x + view.width)) / timeStep);
    int maxI = Math.min(bufferSize, (totalWidth - view.x) / timeStep + 1);

    int prevX = -1;
    int prevY = -1;

    for (int i = minI; i < maxI; i++) {
      boolean signal = m.getStateAt(bufferSize - 1 - i);
      int px = totalWidth - (i * timeStep);
      int py = signal ? graphTop : graphBot;

      if (prevX != -1) {
        g2.drawLine(prevX, prevY, px, prevY);
        if (prevY != py)
          g2.drawLine(px, prevY, px, py);
      }
      prevX = px;
      prevY = py;
    }
  }

  private void drawLabelOverlay(Graphics2D g2, SignalMonitor m, int x, int y) {
    g2.setColor(Theme.PANEL_BACKGROUND);
    g2.fillRect(x, y, LABEL_WIDTH, ROW_HEIGHT);

    g2.setColor(Theme.GRID_MAJOR);
    g2.drawLine(x, y + ROW_HEIGHT, x + LABEL_WIDTH, y + ROW_HEIGHT);

    g2.setColor(m.getCurrentState() ? Theme.WIRE_ON : Theme.WIRE_OFF);
    g2.fillOval(x + 10, y + ROW_HEIGHT / 2 - 6, 12, 12);

    g2.setColor(Theme.TEXT_COLOR);
    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.drawString(m.getName(), x + 30, y + ROW_HEIGHT / 2 + 5);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return new Dimension(800, 400);
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
    return o == SwingConstants.HORIZONTAL ? timeStep * 10 : ROW_HEIGHT;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
    return o == SwingConstants.HORIZONTAL ? r.width / 2 : ROW_HEIGHT * 3;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
}
