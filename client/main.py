#!/usr/bin/env python3
import os
import sys

_client_dir = os.path.dirname(os.path.abspath(__file__))
if _client_dir not in sys.path:
    sys.path.insert(0, _client_dir)

from tui.app import LConApp


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
    host = os.environ.get("LCON_HOST", "localhost")
    port = int(os.environ.get("LCON_PORT", "58115"))
    token = os.environ.get("LCON_TOKEN", "your_secret_token")
    version = _read_mod_version()
    app = LConApp(host=host, port=port, token=token, mod_version=version)
    app.run()


if __name__ == "__main__":
    main()
