(echo "[Desktop Entry]" &&
echo "Name=SimplePlanner" &&
echo "Comment=For Simple Planning"
echo "Exec="$HOME"/Documents/GitHub/simple-planner/planner.sh"
echo "Icon="$HOME"/Documents/GitHub/simple-planner/icon.png"
echo "Terminal=false"
echo "Type=Application") > planner.desktop

mv planner.desktop $HOME/Desktop
