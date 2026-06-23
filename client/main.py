#!/usr/bin/env python3
import os
import sys

_client_dir = os.path.dirname(os.path.abspath(__file__))
if _client_dir not in sys.path:
    sys.path.insert(0, _client_dir)

from tui.app import LConApp


def _load_dotenv():
    root = os.path.dirname(_client_dir)
    dotenv_path = os.path.join(root, "config", ".env")
    try:
        with open(dotenv_path, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#") or "=" not in line:
                    continue
                key, _, val = line.partition("=")
                key, val = key.strip(), val.strip().strip("\"'")
                if key not in os.environ:
                    os.environ[key] = val
    except Exception:
        pass


def _read_mod_version():
    root = os.path.dirname(_client_dir)
    props_path = os.path.join(root, "gradle.properties")
    try:
        with open(props_path, encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if line.startswith("mod_version="):
                    return line.split("=", 1)[1].strip()
    except Exception:
        pass
    return "unknown"


def main():
    _load_dotenv()

    host = os.environ.get("LCON_HOST", "localhost")
    port = int(os.environ.get("LCON_PORT", "58115"))
    token = os.environ.get("LCON_TOKEN", "your_secret_token")
    soft_wrap = os.environ.get("LCON_SOFT_WRAP", "true").lower() in ("1", "true", "yes")
    log_buffer = int(os.environ.get("LCON_LOG_BUFFER", "1000"))
    auto_mode = os.environ.get("LCON_AUTO_MODE", "true").lower() in ("1", "true", "yes")
    version = _read_mod_version()
    app = LConApp(
        host=host,
        port=port,
        token=token,
        mod_version=version,
        soft_wrap=soft_wrap,
        log_buffer=log_buffer,
        auto_mode=auto_mode,
    )
    app.run()


if __name__ == "__main__":
    main()
