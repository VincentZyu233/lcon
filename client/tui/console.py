from textual.widgets import RichLog, Input, Button
from textual.containers import Horizontal
from textual.widget import Widget


class ConsoleTab(Widget):
    DEFAULT_CSS = """
    ConsoleTab {
        height: 1fr;
        layout: vertical;
    }
    #log {
        height: 1fr;
    }
    #input-row {
        height: auto;
        margin: 0 1;
        dock: bottom;
    }
    #input {
        width: 1fr;
    }
    #send {
        width: 12;
    }
    """

    def compose(self):
        yield RichLog(id="log", highlight=True, markup=True, max_lines=10000)
        with Horizontal(id="input-row"):
            yield Input(id="input", placeholder="Type a command with prefix...")
            yield Button("Send", id="send", variant="primary")

    def add_log(self, message):
        try:
            self.query_one("#log", RichLog).write(message)
        except Exception:
            pass

    def on_input_submitted(self, event):
        self._send_message()

    def on_button_pressed(self, event):
        if event.button.id == "send":
            self._send_message()

    def _send_message(self):
        if not self.app.ws or not self.app.ws.running:
            self.add_log("[red]! Not connected[/red]")
            return
        inp = self.query_one("#input", Input)
        msg = inp.value.strip()
        if not msg:
            return
        self.app.ws.send(msg)
        self.add_log(f"> {msg}")
        inp.value = ""
