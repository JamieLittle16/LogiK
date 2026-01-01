package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import uk.ac.cam.jml229.logic.app.Theme;
import uk.ac.cam.jml229.logic.io.SettingsManager;
import uk.ac.cam.jml229.logic.ui.panels.CircuitPanel;
import uk.ac.cam.jml229.logic.components.Component;
import uk.ac.cam.jml229.logic.core.Wire;
import uk.ac.cam.jml229.logic.ui.timing.SignalMonitor;
import uk.ac.cam.jml229.logic.ui.timing.TimingContainer;

public class AppMenuBar extends JMenuBar {

  private final JFrame parentFrame;
  private final CircuitPanel circuitPanel;
  private final SimulationController simController;
  private final TimingContainer timingContainer;

  private final Consumer<String> themeCallback;
  private final Runnable settingsCallback;
  private final Runnable saveCallback;
  private final Runnable loadCallback;
  private final Runnable toggleTimingCallback;

  private final JLabel zoomStatusLabel;

  public AppMenuBar(JFrame parentFrame,
      CircuitPanel circuitPanel,
      SimulationController simController,
      TimingContainer timingContainer,
      Consumer<String> themeCallback,
      Runnable settingsCallback,
      Runnable saveCallback,
      Runnable loadCallback,
      Runnable toggleTimingCallback) {

    this.parentFrame = parentFrame;
    this.circuitPanel = circuitPanel;
    this.simController = simController;
    this.timingContainer = timingContainer;
    this.themeCallback = themeCallback;
    this.settingsCallback = settingsCallback;
    this.saveCallback = saveCallback;
    this.loadCallback = loadCallback;
    this.toggleTimingCallback = toggleTimingCallback;

    this.zoomStatusLabel = new JLabel("Zoom: 100%  ");
    this.zoomStatusLabel.setForeground(Color.GRAY);

    initMenus();
    updateTheme();
  }

  private void initMenus() {
    // --- FILE ---
    JMenu fileMenu = new JMenu("File");
    addItem(fileMenu, "Save...", KeyEvent.VK_S, e -> saveCallback.run());
    addItem(fileMenu, "Load...", KeyEvent.VK_O, e -> loadCallback.run());
    fileMenu.addSeparator();
    addItem(fileMenu, "Settings...", 0, e -> settingsCallback.run());
    add(fileMenu);

    // --- EDIT ---
    JMenu editMenu = new JMenu("Edit");
    addItem(editMenu, "Undo", KeyEvent.VK_Z, e -> circuitPanel.undo());
    addItem(editMenu, "Redo", KeyEvent.VK_Y, e -> circuitPanel.redo());
    editMenu.addSeparator();
    addItem(editMenu, "Cut", KeyEvent.VK_X, e -> circuitPanel.cut());
    addItem(editMenu, "Copy", KeyEvent.VK_C, e -> circuitPanel.copy());
    addItem(editMenu, "Paste", KeyEvent.VK_V, e -> circuitPanel.paste());
    editMenu.addSeparator();
    addItem(editMenu, "Rotate", KeyEvent.VK_R, e -> circuitPanel.rotateSelection());
    JMenuItem deleteItem = new JMenuItem("Delete");
    deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    deleteItem.addActionListener(e -> circuitPanel.deleteSelection());
    editMenu.add(deleteItem);
    add(editMenu);

    // --- VIEW ---
    JMenu viewMenu = new JMenu("View");

    addItem(viewMenu, "Toggle Timing Diagram", KeyEvent.VK_D, e -> toggleTimingCallback.run());

    viewMenu.addSeparator();

    JMenu themeMenu = new JMenu("Theme");
    buildThemeMenu(themeMenu);
    viewMenu.add(themeMenu);

    addItem(viewMenu, "Zoom In", KeyEvent.VK_EQUALS, e -> circuitPanel.zoomIn());
    addItem(viewMenu, "Zoom Out", KeyEvent.VK_MINUS, e -> circuitPanel.zoomOut());
    viewMenu.addSeparator();
    addItem(viewMenu, "Reset Zoom", KeyEvent.VK_0, e -> circuitPanel.resetZoom());
    viewMenu.addSeparator();

    JCheckBoxMenuItem snapGridItem = new JCheckBoxMenuItem("Snap to Grid");
    snapGridItem.setSelected(SettingsManager.isSnapToGrid());
    snapGridItem.addActionListener(e -> {
      boolean val = snapGridItem.isSelected();
      circuitPanel.getInteraction().setSnapToGrid(val);
      SettingsManager.setSnapToGrid(val);
    });
    viewMenu.add(snapGridItem);
    add(viewMenu);

    // --- TOOLS ---
    JMenu toolsMenu = new JMenu("Tools");
    addItem(toolsMenu, "Auto-Organise Circuit", KeyEvent.VK_L, e -> {
      AutoLayout.organise(circuitPanel.getCircuit());
      circuitPanel.centerCircuit();
      circuitPanel.repaint();
      circuitPanel.getInteraction().saveHistory();
    });
    addItem(toolsMenu, "Add Selected to Timing Diagram", KeyEvent.VK_M, e -> addSelectionToTiming());
    add(toolsMenu);

    // --- SIMULATION ---
    JMenu simMenu = new JMenu("Simulation");
    JMenuItem startItem = new JMenuItem("Start");
    startItem.addActionListener(e -> simController.start());
    JMenuItem stopItem = new JMenuItem("Stop");
    stopItem.addActionListener(e -> simController.stop());
    JMenuItem stepItem = new JMenuItem("Step (Manual Tick)");
    stepItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0));
    stepItem.addActionListener(e -> simController.step());

    JMenu clockSpeedMenu = new JMenu("Clock Speed");
    ButtonGroup clockGroup = new ButtonGroup();
    addSpeedItem(clockSpeedMenu, clockGroup, "0.5 Hz (Slow)", 2000, false);
    addSpeedItem(clockSpeedMenu, clockGroup, "1 Hz", 1000, false);
    addSpeedItem(clockSpeedMenu, clockGroup, "2 Hz (Default)", 500, true);
    addSpeedItem(clockSpeedMenu, clockGroup, "5 Hz", 200, false);
    addSpeedItem(clockSpeedMenu, clockGroup, "10 Hz", 100, false);
    addSpeedItem(clockSpeedMenu, clockGroup, "50 Hz", 20, false);

    JMenu logicSpeedMenu = new JMenu("Logic Speed (Propagation)");
    ButtonGroup logicGroup = new ButtonGroup();
    addLogicSpeedItem(logicSpeedMenu, logicGroup, "Instant (1000 updates/frame)", 1000, true);
    addLogicSpeedItem(logicSpeedMenu, logicGroup, "Fast (50 updates/frame)", 50, false);
    addLogicSpeedItem(logicSpeedMenu, logicGroup, "Visible (5 updates/frame)", 5, false);
    addLogicSpeedItem(logicSpeedMenu, logicGroup, "Slow Motion (1 update/frame)", 1, false);

    simMenu.add(startItem);
    simMenu.add(stopItem);
    simMenu.add(stepItem);
    simMenu.addSeparator();
    simMenu.add(clockSpeedMenu);
    simMenu.add(logicSpeedMenu);
    add(simMenu);

    add(Box.createHorizontalGlue());
    add(zoomStatusLabel);
  }

  private void addItem(JMenu menu, String label, int key, java.awt.event.ActionListener action) {
    JMenuItem item = new JMenuItem(label);
    if (key != 0) {
      item.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
    }
    item.addActionListener(action);
    menu.add(item);
  }

  private void buildThemeMenu(JMenu themeMenu) {
    ButtonGroup themeGroup = new ButtonGroup();
    String[] builtIns = {
        "Default Light", "Default Dark", "One Dark", "Catppuccin Mocha",
        "High Contrast", "Dracula", "Monokai", "Nord",
        "Solarized Light", "GitHub Light", "Blueprint",
        "Cyberpunk", "Gruvbox Dark"
    };
    List<String> allThemes = new ArrayList<>(List.of(builtIns));

    File userThemeDir = new File(System.getProperty("user.home") + "/.logik/themes");
    if (userThemeDir.exists() && userThemeDir.isDirectory()) {
      for (File f : userThemeDir.listFiles()) {
        if (f.getName().endsWith(".properties")) {
          String name = f.getName().replace(".properties", "");
          if (!allThemes.contains(name))
            allThemes.add(name);
        }
      }
    }

    String currentTheme = SettingsManager.getThemeName();
    for (String t : allThemes) {
      JRadioButtonMenuItem item = new JRadioButtonMenuItem(t);
      if (t.equals(currentTheme))
        item.setSelected(true);
      item.addActionListener(e -> themeCallback.accept(t));
      themeGroup.add(item);
      themeMenu.add(item);
    }
  }

  private void addSpeedItem(JMenu menu, ButtonGroup group, String label, int delayMs, boolean selected) {
    JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
    item.setSelected(selected);
    item.addActionListener(e -> simController.setClockDelayMs(delayMs));
    group.add(item);
    menu.add(item);
  }

  private void addLogicSpeedItem(JMenu menu, ButtonGroup group, String label, int steps, boolean selected) {
    JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
    item.setSelected(selected);
    item.addActionListener(e -> simController.setLogicStepsPerFrame(steps));
    group.add(item);
    menu.add(item);
  }

  private void addSelectionToTiming() {
    List<Component> selection = circuitPanel.getInteraction().getSelectedComponents();
    if (selection.isEmpty()) {
      JOptionPane.showMessageDialog(parentFrame, "Please select a component (Gate/Switch) to monitor.");
      return;
    }
    boolean added = false;
    for (Component c : selection) {
      if (c.getOutputCount() > 0) {
        Wire w = c.getOutputWire(0);
        if (w != null) {
          timingContainer.addMonitor(new SignalMonitor(c.getName(), w, Theme.WIRE_ON, timingContainer.getBufferSize()));
          added = true;
        }
      }
    }
    if (added) {
      // Logic for auto-showing the docked panel
      if (!timingContainer.isShowing()) {
        toggleTimingCallback.run();
      }
      SwingUtilities.invokeLater(timingContainer::scrollToPresent);
    } else {
      JOptionPane.showMessageDialog(parentFrame, "Selected components have no outputs to monitor.");
    }
  }

  public void updateZoomLabel(double scale) {
    int pct = (int) (scale * 100);
    zoomStatusLabel.setText("Zoom: " + pct + "%  ");
  }

  public void updateTheme() {
    setBackground(Theme.isDarkMode ? Theme.PALETTE_BACKGROUND : null);

    if (Theme.isDarkMode) {
      setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BUTTON_BORDER));
    } else {
      setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
    }

    for (int i = 0; i < getMenuCount(); i++) {
      JMenu m = getMenu(i);
      if (m != null)
        styleMenu(m);
    }

    if (zoomStatusLabel != null)
      zoomStatusLabel.setForeground(Theme.PALETTE_HEADINGS);

    SwingUtilities.updateComponentTreeUI(this);
  }

  private void styleMenu(JComponent item) {
    if (Theme.isDarkMode) {
      item.setBackground(Theme.PALETTE_BACKGROUND);
      item.setForeground(Theme.TEXT_COLOR);
      item.setOpaque(true);

      if (item instanceof JPopupMenu) {
        item.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER));
        item.setBackground(Theme.PALETTE_BACKGROUND);
      }
      if (item instanceof JMenuItem) {
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      }
      if (item instanceof JMenu) {
        JPopupMenu popup = ((JMenu) item).getPopupMenu();
        popup.setBackground(Theme.PALETTE_BACKGROUND);
        popup.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER));
      }
      if (item instanceof JPopupMenu.Separator) {
        item.setBackground(Theme.PALETTE_BACKGROUND);
        item.setForeground(Theme.GRID_MAJOR);
      }
    } else {
      item.setBackground(null);
      item.setForeground(Color.BLACK);
      item.setOpaque(false);
      if (item instanceof JMenu) {
        JPopupMenu popup = ((JMenu) item).getPopupMenu();
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
      }
    }

    if (item instanceof JMenu) {
      for (java.awt.Component c : ((JMenu) item).getMenuComponents()) {
        if (c instanceof JComponent)
          styleMenu((JComponent) c);
      }
    }
  }
}
