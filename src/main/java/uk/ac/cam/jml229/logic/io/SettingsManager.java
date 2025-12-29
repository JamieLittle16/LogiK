package uk.ac.cam.jml229.logic.io;

import java.util.prefs.Preferences;

public class SettingsManager {

  // Unique node for app settings
  private static final Preferences prefs = Preferences.userRoot().node("logik");

  // Keys
  private static final String KEY_DARK_MODE = "dark_mode";
  private static final String KEY_SNAP_GRID = "snap_grid";
  private static final String KEY_WIN_WIDTH = "win_width";
  private static final String KEY_WIN_HEIGHT = "win_height";
  private static final String KEY_WIN_X = "win_x";
  private static final String KEY_WIN_Y = "win_y";
  private static final String KEY_MAXIMIZED = "win_maximized";
  private static final String KEY_THEME_NAME = "theme_name";

  // --- Getters (Load) ---

  public static boolean isDarkMode() {
    return prefs.getBoolean(KEY_DARK_MODE, false);
  }

  public static boolean isSnapToGrid() {
    return prefs.getBoolean(KEY_SNAP_GRID, false);
  }

  public static int getWindowWidth() {
    return prefs.getInt(KEY_WIN_WIDTH, 1280);
  }

  public static int getWindowHeight() {
    return prefs.getInt(KEY_WIN_HEIGHT, 800);
  }

  public static int getWindowX() {
    return prefs.getInt(KEY_WIN_X, -1);
  }

  public static int getWindowY() {
    return prefs.getInt(KEY_WIN_Y, -1);
  }

  public static boolean isMaximized() {
    return prefs.getBoolean(KEY_MAXIMIZED, false);
  }

  public static String getThemeName() {
    return prefs.get(KEY_THEME_NAME, "Default Light");
  }

  // --- Setters (Save) ---

  // CRITICAL: Forces the OS to write the file immediately
  private static void save() {
    try {
      prefs.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void setDarkMode(boolean dark) {
    prefs.putBoolean(KEY_DARK_MODE, dark);
    save();
  }

  public static void setSnapToGrid(boolean snap) {
    prefs.putBoolean(KEY_SNAP_GRID, snap);
    save();
  }

  public static void setWindowBounds(int x, int y, int w, int h, boolean maximized) {
    prefs.putInt(KEY_WIN_X, x);
    prefs.putInt(KEY_WIN_Y, y);
    prefs.putInt(KEY_WIN_WIDTH, w);
    prefs.putInt(KEY_WIN_HEIGHT, h);
    prefs.putBoolean(KEY_MAXIMIZED, maximized);
    save();
  }

  public static void setThemeName(String name) {
    prefs.put(KEY_THEME_NAME, name);
    save();
  }
}
