package uk.ac.cam.jml229.logic.ui;

import javax.swing.*;
import java.awt.*;
import uk.ac.cam.jml229.logic.app.Theme;

public class FlatIcons {

  public static class CheckIcon implements Icon {
    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public int getIconHeight() {
      return 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      AbstractButton b = (AbstractButton) c;

      // Draw Box
      g2.setColor(Theme.TEXT_COLOR);
      g2.setStroke(new BasicStroke(1.5f));
      g2.drawRoundRect(x + 3, y + 3, 10, 10, 2, 2);

      if (b.isSelected()) {
        g2.setColor(Theme.WIRE_ON);
        g2.drawLine(x + 5, y + 8, x + 7, y + 10);
        g2.drawLine(x + 7, y + 10, x + 11, y + 5);
      }
      g2.dispose();
    }
  }

  public static class RadioIcon implements Icon {
    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public int getIconHeight() {
      return 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      AbstractButton b = (AbstractButton) c;

      // Draw Circle Outline
      g2.setColor(Theme.TEXT_COLOR);
      g2.setStroke(new BasicStroke(1.5f));
      g2.drawOval(x + 3, y + 3, 10, 10);

      if (b.isSelected()) {
        g2.setColor(Theme.WIRE_ON);
        g2.fillOval(x + 6, y + 6, 5, 5);
      }
      g2.dispose();
    }
  }
}
