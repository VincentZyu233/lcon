#!/usr/bin/env python3
import os

from tui.app import LConApp


def main():
    host = os.environ.get("LCON_HOST", "localhost")
    port = int(os.environ.get("LCON_PORT", "58115"))
    token = os.environ.get("LCON_TOKEN", "your_secret_token")
    app = LConApp(host=host, port=port, token=token)
    app.run()


if __name__ == "__main__":
    main()
