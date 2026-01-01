package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import uk.ac.cam.jml229.logic.app.Theme;
import uk.ac.cam.jml229.logic.io.SettingsManager;

public class SettingsDialog extends JDialog {

  public SettingsDialog(Frame owner) {
    super(owner, "Preferences", true);
    setLayout(new BorderLayout());
    setSize(400, 250);
    setLocationRelativeTo(owner);

    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    content.setBackground(Theme.PALETTE_BACKGROUND);

    // Title
    JLabel titleLabel = new JLabel("Simulation Settings");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
    titleLabel.setForeground(Theme.PALETTE_HEADINGS);
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.add(titleLabel);
    content.add(Box.createVerticalStrut(20));

    // Checkbox
    JCheckBox enableDelay = new JCheckBox("Enable Propagation Delay (Hazards)");
    enableDelay.setSelected(SettingsManager.isPropagationDelayEnabled());
    enableDelay.setOpaque(false);
    enableDelay.setForeground(Theme.TEXT_COLOR);
    enableDelay.setFocusPainted(false);
    enableDelay.setAlignmentX(Component.LEFT_ALIGNMENT);

    if (Theme.isDarkMode) {
      enableDelay.setIcon(new FlatIcons.CheckIcon());
    }

    content.add(enableDelay);
    content.add(Box.createVerticalStrut(15));

    // Delay Spinner
    JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    delayPanel.setOpaque(false);
    delayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel delayLabel = new JLabel("Gate Delay (Ticks): ");
    delayLabel.setForeground(Theme.TEXT_COLOR);

    JSpinner delaySpinner = new JSpinner(new SpinnerNumberModel(SettingsManager.getGateDelay(), 1, 1000000, 100));
    styleSpinner(delaySpinner);

    delayPanel.add(delayLabel);
    delayPanel.add(Box.createHorizontalStrut(10));
    delayPanel.add(delaySpinner);

    delaySpinner.setEnabled(enableDelay.isSelected());
    enableDelay.addActionListener(e -> delaySpinner.setEnabled(enableDelay.isSelected()));

    content.add(delayPanel);
    content.add(Box.createVerticalGlue());

    // Buttons
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttons.setBackground(Theme.PALETTE_BACKGROUND);

    JButton restoreBtn = createStyledButton("Restore Defaults");
    restoreBtn.addActionListener(e -> {
      enableDelay.setSelected(true);
      delaySpinner.setValue(1);
      delaySpinner.setEnabled(true);
    });

    JButton okBtn = createStyledButton("OK");
    okBtn.addActionListener(e -> {
      SettingsManager.setPropagationDelayEnabled(enableDelay.isSelected());
      SettingsManager.setGateDelay((Integer) delaySpinner.getValue());
      dispose();
    });

    buttons.add(restoreBtn);
    buttons.add(okBtn);

    add(content, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  private void styleSpinner(JSpinner spinner) {
    if (Theme.isDarkMode) {
      spinner.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER));
      JComponent editor = spinner.getEditor();
      if (editor instanceof JSpinner.DefaultEditor) {
        JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        tf.setBackground(Theme.BUTTON_BACKGROUND);
        tf.setForeground(Theme.TEXT_COLOR);
        tf.setCaretColor(Theme.TEXT_COLOR);
      }
      for (Component c : spinner.getComponents()) {
        if (c instanceof JButton) {
          ((JButton) c).setBackground(Theme.BUTTON_BACKGROUND);
          ((JButton) c).setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER));
        }
      }
    }
  }

  private JButton createStyledButton(String text) {
    JButton btn = new JButton(text);
    btn.setFocusPainted(false);
    if (Theme.isDarkMode) {
      btn.setBackground(Theme.BUTTON_BACKGROUND);
      btn.setForeground(Theme.TEXT_COLOR);
      btn.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(Theme.BUTTON_BORDER),
          BorderFactory.createEmptyBorder(5, 15, 5, 15)));
      btn.setOpaque(true);
      btn.setContentAreaFilled(true);
      btn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          btn.setBackground(Theme.BUTTON_HOVER);
        }

        public void mouseExited(MouseEvent e) {
          btn.setBackground(Theme.BUTTON_BACKGROUND);
        }
      });
    }
    return btn;
  }
}
