from textual.widgets import Static, DataTable
from textual.widget import Widget
from .css import load_css


class CommandsTab(Widget):
    DEFAULT_CSS = load_css("commands")

    def compose(self):
        yield Static(
            "[bold]Send commands using these prefixes:[/bold]", id="commands-title"
        )
        yield DataTable(id="commands-table")

    def on_mount(self):
        table = self.query_one("#commands-table", DataTable)
        table.add_columns("Prefix", "Action")
        table.add_rows(
            [
                ("[chat]<message>", "Send a chat message as the player"),
                ("[message]<message>", "Display a message to the player only"),
                ("[system]<message>", "Display a system message in chat"),
                ("[client]/<command>", "Execute a [bold]client-side[/bold] command"),
                ("[server]/<command>", "Execute a [bold]server-side[/bold] command"),
            ]
        )
