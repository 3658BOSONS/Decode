#!/bin/bash
# Minimal Android SDK setup for FTC (Linux)
# Run as a normal user, not root

# 1. Set up directories
PROJECT_DIR="$(pwd)"
ANDROID_ROOT="$HOME/Android/Sdk"
CMDLINE_TOOLS="$ANDROID_ROOT/cmdline-tools"
mkdir -p "$CMDLINE_TOOLS"

# 2. Download command-line tools (latest stable)
echo "Downloading Android command-line tools..."
cd /tmp || exit
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip
unzip cmdline-tools.zip -d "$CMDLINE_TOOLS"
mv "$CMDLINE_TOOLS/cmdline-tools" "$CMDLINE_TOOLS/latest"
rm cmdline-tools.zip

# 3. Set environment variables
echo "export ANDROID_SDK_ROOT=$ANDROID_ROOT" >> ~/.bashrc
echo 'export PATH=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH' >> ~/.bashrc
source ~/.bashrc

# 4. Accept licenses automatically
yes | "$CMDLINE_TOOLS/latest/bin/sdkmanager" --licenses

# 5. Install minimal SDK components for FTC
"$CMDLINE_TOOLS/latest/bin/sdkmanager" "platform-tools" "platforms;android-33" "build-tools;33.0.2"

# 6. Setup project variables
cd "$PROJECT_DIR" || exit
echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties

# 7. Done
echo "Android SDK setup complete."
echo "SDK location: $ANDROID_ROOT"
echo "Please restart your terminal or run 'source ~/.bashrc' to apply environment variables."
