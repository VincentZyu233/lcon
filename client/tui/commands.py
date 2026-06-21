from textual.widgets import Static, DataTable
from textual.widget import Widget


class CommandsTab(Widget):
    DEFAULT_CSS = """
    CommandsTab {
        height: 1fr;
        padding: 1;
    }
    #commands-title {
        margin-bottom: 1;
    }
    DataTable {
        height: auto;
    }
    """

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
