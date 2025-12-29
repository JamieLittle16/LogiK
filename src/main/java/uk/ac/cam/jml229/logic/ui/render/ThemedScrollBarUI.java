package uk.ac.cam.jml229.logic.ui.render;

import uk.ac.cam.jml229.logic.app.Theme;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ThemedScrollBarUI extends BasicScrollBarUI {

  @Override
  protected void configureScrollBarColors() {
    this.thumbColor = Theme.SCROLL_THUMB;
    this.trackColor = Theme.SCROLL_TRACK;
  }

  @Override
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    g.setColor(Theme.SCROLL_TRACK);
    g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
  }

  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
      return;

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(Theme.SCROLL_THUMB);
    // Draw rounded thumb
    g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
        thumbBounds.width - 4, thumbBounds.height - 4,
        6, 6);
    g2.dispose();
  }

  // Hide the ugly arrow buttons by making them zero size
  @Override
  protected JButton createDecreaseButton(int orientation) {
    return createZeroButton();
  }

  @Override
  protected JButton createIncreaseButton(int orientation) {
    return createZeroButton();
  }

  private JButton createZeroButton() {
    JButton btn = new JButton();
    btn.setPreferredSize(new Dimension(0, 0));
    btn.setMinimumSize(new Dimension(0, 0));
    btn.setMaximumSize(new Dimension(0, 0));
    return btn;
  }
}
