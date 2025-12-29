#!/bin/bash

# --- Configuration ---
# These must match the paths used in install.sh
INSTALL_DIR="$HOME/.local/share/logik"
BIN_LINK="$HOME/.local/bin/logik"
DESKTOP_FILE="$HOME/.local/share/applications/logik.desktop"
# ---------------------

echo "🗑️  Uninstalling Logik Simulator..."

# 1. Remove the main application files
if [ -d "$INSTALL_DIR" ]; then
    rm -rf "$INSTALL_DIR"
    echo "✅ Removed application files ($INSTALL_DIR)"
else
    echo "⚠️  Application files not found (already removed?)"
fi

# 2. Remove the terminal command 'logik'
if [ -L "$BIN_LINK" ] || [ -f "$BIN_LINK" ]; then
    rm "$BIN_LINK"
    echo "✅ Removed terminal command 'logik'"
else
    echo "⚠️  Terminal command not found"
fi

# 3. Remove the Desktop shortcut
if [ -f "$DESKTOP_FILE" ]; then
    rm "$DESKTOP_FILE"
    echo "✅ Removed Desktop shortcut"
else
    echo "⚠️  Desktop shortcut not found"
fi

# 4. Refresh desktop database (optional but good practice for Linux UI)
if command -v update-desktop-database &> /dev/null; then
    update-desktop-database "$HOME/.local/share/applications"
fi

echo "🎉 Uninstallation Complete."
