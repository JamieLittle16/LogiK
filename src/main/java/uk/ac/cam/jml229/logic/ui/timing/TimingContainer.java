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

    toolBar.add(Box.createHorizontalGlue());
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

    // Set explicit background on the ScrollPane itself (fills gaps)
    scrollPane.setBackground(Theme.PALETTE_BACKGROUND);

    // Attach Row Header
    scrollPane.setRowHeaderView(timingPanel.getRowHeader());

    // Set ALL 4 corners
    scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, createCorner());
    scrollPane.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, createCorner());
    scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, createCorner());
    scrollPane.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, createCorner());

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
    // Update entire tree (resets standard Swing components)
    if (getParent() != null)
      SwingUtilities.updateComponentTreeUI(this);

    // Re-apply themes to custom children
    timingPanel.updateTheme();

    // Fix ScrollPane Viewports (The "White Square" culprit is often here)
    scrollPane.setBackground(Theme.PALETTE_BACKGROUND);
    scrollPane.getViewport().setBackground(Theme.BACKGROUND);

    if (scrollPane.getRowHeader() != null) {
      scrollPane.getRowHeader().setBackground(Theme.PALETTE_BACKGROUND);
    }
    if (scrollPane.getColumnHeader() != null) {
      scrollPane.getColumnHeader().setBackground(Theme.PALETTE_BACKGROUND);
    }

    scrollPane.getVerticalScrollBar().setUI(new ThemedScrollBarUI());
    scrollPane.getHorizontalScrollBar().setUI(new ThemedScrollBarUI());

    // Force repaint of corners (Logic handled inside the createCorner class now)
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
    return new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        // Always fill with the current theme color, ignoring everything else
        g.setColor(Theme.PALETTE_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    };
  }

}
