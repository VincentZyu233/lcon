from pathlib import Path


def load_css(name):
    path = Path(__file__).parent / "css" / f"{name}.css"
    if path.exists():
        return path.read_text(encoding="utf-8")
    return ""
