#!/bin/bash

# --- Configuration ---
INSTALL_DIR="$HOME/.local/share/logik"
BIN_DIR="$HOME/.local/bin"
DESKTOP_DIR="$HOME/.local/share/applications"
# REPLACE THIS URL WITH YOUR REAL GITHUB RELEASE LINK:
DOWNLOAD_URL="https://github.com/JamieLittle16/Logic-Simulator/releases/latest/download/logik-linux.tar.gz"
# ---------------------

echo "🚀 Installing Logik Simulator..."

# 1. Clean up old installs
rm -rf "$INSTALL_DIR"
mkdir -p "$INSTALL_DIR"

# 2. Download and Extract
# We download to a temporary directory
TEMP_DIR=$(mktemp -d)
echo "Downloading..."
curl -L "$DOWNLOAD_URL" -o "$TEMP_DIR/logik.tar.gz"

echo "Extracting..."
tar -xzf "$TEMP_DIR/logik.tar.gz" -C "$TEMP_DIR"

# Move the 'logik' folder to the install location
mv "$TEMP_DIR/logik/"* "$INSTALL_DIR/"
rm -rf "$TEMP_DIR"

# 3. Create the 'logik' terminal command
mkdir -p "$BIN_DIR"
# The jpackage binary is usually inside bin/ and has the original name
# We link it to 'logik'
ln -sf "$INSTALL_DIR/bin/Logik Simulator" "$BIN_DIR/logik"

echo "✅ Command 'logik' created."

# 4. Create Desktop Shortcut (Start Menu)
mkdir -p "$DESKTOP_DIR"
cat > "$DESKTOP_DIR/logik.desktop" <<EOF
[Desktop Entry]
Type=Application
Name=Logik Simulator
Comment=Digital Logic Circuit Simulator
Exec="$INSTALL_DIR/bin/Logik Simulator"
Icon=$INSTALL_DIR/icon.png
Terminal=false
Categories=Education;Development;
EOF

echo "✅ Desktop shortcut created."
echo "🎉 Installation Complete! restart your terminal."
echo "👉 You can now run 'logik' or find 'Logik Simulator' in your app menu."
