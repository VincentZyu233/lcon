from textual.app import App
from textual.widgets import Header, Footer, TabbedContent, TabPane
from textual.binding import Binding

from .console import ConsoleTab
from .commands import CommandsTab
from .settings import SettingsTab
from .about import AboutTab
from ..ws_client import WSClient


class LConApp(App):
    TITLE = "LCon Client"
    BINDINGS = [
        Binding("ctrl+q", "quit", "Quit"),
        Binding("ctrl+tab", "next_tab", "Next Tab"),
    ]

    def __init__(self, host="localhost", port=58115, token="your_secret_token"):
        super().__init__()
        self._default_host = host
        self._default_port = port
        self._default_token = token
        self.ws: WSClient | None = None

    def compose(self):
        yield Header()
        with TabbedContent():
            with TabPane("Console", id="console-pane"):
                yield ConsoleTab()
            with TabPane("Commands", id="commands-pane"):
                yield CommandsTab()
            with TabPane("Settings", id="settings-pane"):
                yield SettingsTab()
            with TabPane("About", id="about-pane"):
                yield AboutTab()
        yield Footer()

    def action_next_tab(self):
        tabs = self.query_one(TabbedContent)
        tabs.active = (
            tabs.active[1:] + tabs.active[:1] if len(tabs.active) > 1 else tabs.active
        )

    def on_mount(self):
        self.call_from_thread(self._update_settings_fields)
        self.ws = WSClient(
            host=self._default_host,
            port=self._default_port,
            token=self._default_token,
            on_message=self._on_ws_message,
            on_error=self._on_ws_error,
            on_open=self._on_ws_open,
            on_close=self._on_ws_close,
        )
        self.ws.connect()

    def _on_ws_message(self, message):
        self.call_from_thread(self._add_console_log, f"[white]< {message}[/white]")

    def _on_ws_error(self, error):
        self.call_from_thread(self._add_console_log, f"[red]! Error: {error}[/red]")

    def _on_ws_open(self):
        self.call_from_thread(self._add_console_log, "[green]! Connected[/green]")
        self.call_from_thread(
            self._update_settings_status, "Status: [green]Connected[/green]"
        )

    def _on_ws_close(self):
        self.call_from_thread(self._add_console_log, "[yellow]! Disconnected[/yellow]")
        self.call_from_thread(
            self._update_settings_status, "Status: [yellow]Disconnected[/yellow]"
        )

    def _update_settings_fields(self):
        try:
            settings = self.query_one(SettingsTab)
            settings.set_fields(
                self._default_host, str(self._default_port), self._default_token
            )
        except Exception:
            pass

    def _add_console_log(self, message):
        try:
            self.query_one(ConsoleTab).add_log(message)
        except Exception:
            pass

    def _update_settings_status(self, text):
        try:
            self.query_one(SettingsTab).update_status(text)
        except Exception:
            pass

    def connect_ws(self, host, port, token):
        self.disconnect_ws()
        self.ws = WSClient(
            host=host,
            port=port,
            token=token,
            on_message=self._on_ws_message,
            on_error=self._on_ws_error,
            on_open=self._on_ws_open,
            on_close=self._on_ws_close,
        )
        self.ws.connect()

    def disconnect_ws(self):
        if self.ws:
            self.ws.close()

    def on_exit(self):
        self.disconnect_ws()
