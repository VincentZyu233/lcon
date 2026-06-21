## 🚀 Quick Start

```bash
uv venv --python 3.13
uv pip install textual websocket-client
uv run python main.py
```

## 🌐 Environment Variables

| 🏷️ Variable | 📄 Default | 📝 Description |
|----------|---------|-------------|
| `LCON_HOST` | `localhost` | WebSocket server address |
| `LCON_PORT` | `58115` | WebSocket server port |
| `LCON_TOKEN` | `your_secret_token` | Authentication token |

**🐧 bash (Linux / macOS / WSL / Git Bash):**
```bash
LCON_HOST=192.168.1.100 LCON_PORT=58115 LCON_TOKEN=your_secret_token
uv run python main.py
```

**🪟 PowerShell (Windows):**
```powershell
$env:LCON_HOST="192.168.1.100"; $env:LCON_PORT="58115"; $env:LCON_TOKEN="your_secret_token"
uv run python main.py
```

**🖥️ CMD (Windows):**
```cmd
set LCON_HOST=192.168.1.100 && set LCON_PORT=58115 && set LCON_TOKEN=your_secret_token
uv run python main.py
```
