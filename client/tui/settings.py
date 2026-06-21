from textual.widgets import Input, Button, Static
from textual.containers import Vertical, Horizontal
from textual.widget import Widget


class SettingsTab(Widget):
    DEFAULT_CSS = """
    SettingsTab {
        height: 1fr;
        padding: 1;
    }
    #settings-title {
        margin-bottom: 1;
    }
    Input {
        margin-bottom: 1;
    }
    #settings-buttons Horizontal {
        height: auto;
    }
    #connect {
        width: 1fr;
    }
    #disconnect {
        width: 1fr;
    }
    #status {
        margin-top: 1;
    }
    """

    def compose(self):
        yield Static("[bold]Connection Settings[/bold]", id="settings-title")
        yield Input(id="host", placeholder="Host", value="localhost")
        yield Input(id="port", placeholder="Port", value="58115")
        yield Input(id="token", placeholder="Token", value="your_secret_token")
        with Horizontal(id="settings-buttons"):
            yield Button("Connect", id="connect", variant="primary")
            yield Button("Disconnect", id="disconnect", variant="error")
        yield Static("Status: Disconnected", id="status")

    def on_button_pressed(self, event):
        if event.button.id == "connect":
            host = self.query_one("#host", Input).value.strip()
            port_str = self.query_one("#port", Input).value.strip()
            token = self.query_one("#token", Input).value.strip()
            try:
                port = int(port_str)
            except ValueError:
                self.query_one("#status", Static).update(
                    "Status: [red]Invalid port number[/red]"
                )
                return
            self.app.connect_ws(host, port, token)
        elif event.button.id == "disconnect":
            self.app.disconnect_ws()

    def set_fields(self, host, port, token):
        try:
            self.query_one("#host", Input).value = host
            self.query_one("#port", Input).value = port
            self.query_one("#token", Input).value = token
        except Exception:
            pass

    def update_status(self, text):
        try:
            self.query_one("#status", Static).update(text)
        except Exception:
            pass
