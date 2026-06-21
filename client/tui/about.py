from textual.widgets import Markdown
from textual.containers import Vertical
from textual.widget import Widget

ABOUT_MD = """# LCon

**Version:** 1.0.3

WebSocket remote control for Minecraft client.

A Forge mod that runs a WebSocket server on the Minecraft **client** (single-player / LAN), allowing external applications to execute commands and interact with the game in real time.

---

**Repository:** [github.com/VincentZyu233/lcon](https://github.com/VincentZyu233/lcon)

**Original Author:** [ZigTheHedge](https://github.com/ZigTheHedge)
"""


class AboutTab(Widget):
    DEFAULT_CSS = """
    AboutTab {
        height: 1fr;
        padding: 1;
    }
    Markdown {
        height: auto;
    }
    """

    def compose(self):
        with Vertical():
            yield Markdown(ABOUT_MD)
