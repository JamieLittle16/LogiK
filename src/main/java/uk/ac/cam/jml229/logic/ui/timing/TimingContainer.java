package uk.ac.cam.jml229.logic.ui.timing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import uk.ac.cam.jml229.logic.app.Theme;
import uk.ac.cam.jml229.logic.ui.render.ThemedScrollBarUI;

public class TimingContainer extends JPanel {

  private final TimingPanel timingPanel;
  private final JScrollPane scrollPane;
  private final JToolBar toolBar;

  // Updated Constructor: Accepts a callback to close itself
  public TimingContainer(Runnable onClose) {
    setLayout(new BorderLayout());
    setMinimumSize(new Dimension(0, 0));

    timingPanel = new TimingPanel();

    // --- Build Toolbar ---
    toolBar = new JToolBar();
    toolBar.setFloatable(false);

    addButton("Pause", e -> {
      timingPanel.togglePause();
      ((JButton) e.getSource()).setText(timingPanel.isPaused() ? "Resume" : "Pause");
    });
    toolBar.addSeparator();
    addButton("Zoom In (+)", e -> timingPanel.zoomIn());
    addButton("Zoom Out (-)", e -> timingPanel.zoomOut());
    toolBar.addSeparator();
    addButton("Clear History", e -> timingPanel.clear());
    toolBar.addSeparator();

    JButton skipBtn = addButton("Present", e -> scrollToPresent());
    skipBtn.setToolTipText("Skip to Present");

    toolBar.add(Box.createHorizontalGlue()); // Pushes everything to the right
    JButton closeBtn = addButton(" X ", e -> onClose.run());
    closeBtn.setToolTipText("Close Timing Diagram");

    add(toolBar, BorderLayout.NORTH);

    // --- Build ScrollPane ---
    scrollPane = new JScrollPane(timingPanel);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
    scrollPane.setBorder(null);

    // Attach Row Header and Corners
    scrollPane.setRowHeaderView(timingPanel.getRowHeader());
    scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, createCorner());
    scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, createCorner());

    add(scrollPane, BorderLayout.CENTER);

    updateTheme();
  }

  // --- Public API ---

  public void addMonitor(SignalMonitor m) {
    timingPanel.addMonitor(m);
    SwingUtilities.invokeLater(this::scrollToPresent);
  }

  public void tick() {
    if (isShowing())
      timingPanel.tick();
  }

  public void clear() {
    timingPanel.clear();
  }

  public void scrollToPresent() {
    timingPanel.scrollToPresent();
  }

  public int getBufferSize() {
    return timingPanel.getBufferSize();
  }

  public void updateTheme() {
    if (getParent() != null)
      SwingUtilities.updateComponentTreeUI(this);

    timingPanel.updateTheme();

    scrollPane.getViewport().setBackground(Theme.BACKGROUND);
    scrollPane.getVerticalScrollBar().setUI(new ThemedScrollBarUI());
    scrollPane.getHorizontalScrollBar().setUI(new ThemedScrollBarUI());

    updateCorner(ScrollPaneConstants.UPPER_LEFT_CORNER);
    updateCorner(ScrollPaneConstants.LOWER_LEFT_CORNER);

    toolBar.setBackground(Theme.PALETTE_BACKGROUND);
    for (Component c : toolBar.getComponents()) {
      if (c instanceof JButton) {
        JButton btn = (JButton) c;
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBackground(Theme.BUTTON_BACKGROUND);
        btn.setForeground(Theme.TEXT_COLOR);
      }
    }
    repaint();
  }

  // --- Helpers ---

  private JButton addButton(String text, java.awt.event.ActionListener action) {
    JButton btn = new JButton(text);
    btn.addActionListener(action);
    btn.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        btn.setBackground(Theme.BUTTON_HOVER);
      }

      public void mouseExited(MouseEvent e) {
        btn.setBackground(Theme.BUTTON_BACKGROUND);
      }
    });
    toolBar.add(btn);
    return btn;
  }

  private JPanel createCorner() {
    JPanel p = new JPanel();
    p.setBackground(Theme.PALETTE_BACKGROUND);
    return p;
  }

  private void updateCorner(String key) {
    Component c = scrollPane.getCorner(key);
    if (c != null)
      c.setBackground(Theme.PALETTE_BACKGROUND);
  }
}
