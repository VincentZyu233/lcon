from textual.widgets import Static, DataTable
from textual.widget import Widget
from rich.markup import escape
from ..css import load_css


class PrefixesTab(Widget):
    DEFAULT_CSS = load_css("prefixes")

    def compose(self):
        yield Static(
            "[bold]Send commands using these prefixes:[/bold]", id="prefixes-title"
        )
        yield DataTable(id="prefixes-table")

    def on_mount(self):
        table = self.query_one("#prefixes-table", DataTable)
        table.add_columns("Prefix", "Action")
        table.add_rows(
            [
                (escape("[chat]<message>"), "Send a chat message as the player"),
                (escape("[message]<message>"), "Display a message to the player only"),
                (escape("[system]<message>"), "Display a system message in chat"),
                (escape("[client]/<command>"), "Execute a client-side command"),
                (escape("[server]/<command>"), "Execute a server-side command"),
            ]
        )
