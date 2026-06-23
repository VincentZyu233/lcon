#!/usr/bin/env python3
import base64
import re
import sys
import tempfile
from pathlib import Path

import vtracer
from PIL import Image

LABEL = "Forge"
TEXT = "1.20.1"
COLOR = "FF6600"
MAX_SIZE = 64


def main():
    if len(sys.argv) < 2:
        print(
            f"Usage: uv run python {sys.argv[0]} <relative_path_to_png>",
            file=sys.stderr,
        )
        print(
            f"  Label/text/color are hardcoded in the script (currently: {LABEL}/{TEXT}/{COLOR})",
            file=sys.stderr,
        )
        sys.exit(1)

    img_path = Path(sys.argv[1])
    if not img_path.exists():
        print(f"File not found: {img_path}", file=sys.stderr)
        sys.exit(1)

    print(f"Processing {img_path}...")

    img = Image.open(img_path).convert("L")
    w, h = img.size
    if w > MAX_SIZE or h > MAX_SIZE:
        ratio = MAX_SIZE / max(w, h)
        img = img.resize((int(w * ratio), int(h * ratio)), Image.LANCZOS)
        print(f"  Resized to {img.size[0]}x{img.size[1]}")

    img = img.point(lambda x: 255 if x > 128 else 0)

    with tempfile.NamedTemporaryFile(suffix=".png", delete=False) as tmp:
        tmp_path = tmp.name
        img.save(tmp_path, format="PNG")

    svg_path = img_path.with_suffix(".svg")
    vtracer.convert_image_to_svg_py(
        tmp_path,
        str(svg_path),
        colormode="binary",
        mode="polygon",
        filter_speckle=8,
        color_precision=1,
        path_precision=4,
    )
    Path(tmp_path).unlink()

    svg = svg_path.read_text(encoding="utf-8")

    m = re.search(r'<path[^>]*d="([^"]+)"', svg)
    if m:
        d = m.group(1)
        parts = d.split("M")
        main_rect = "M" + parts[1].strip() if len(parts) > 1 else ""
        sub_paths = []
        for p in parts[2:]:
            sub_paths.append("M" + p.strip())
        if sub_paths:
            new_d = " ".join(sub_paths)
            # svg = f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64"><path d="{new_d}" fill="#fff" transform="matrix(1.82 0 0 1.82 -27 -26)"/></svg>'
            svg = f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64"><path d="{new_d}" fill="#fff" transform="matrix(1.4 0 0 1.4 -13 -12)"/></svg>'

    svg_path.write_text(svg, encoding="utf-8")

    b64 = base64.b64encode(svg.encode()).decode()

    b64_path = img_path.with_suffix(".b64.txt")
    b64_path.write_text(b64, encoding="utf-8")

    url = f"https://img.shields.io/badge/{LABEL}-{TEXT}-{COLOR}?style=for-the-badge&logo=data:image/svg%2bxml;base64,{b64}"

    print(f"\nSVG saved:    {svg_path}")
    print(f"Base64 saved: {b64_path}")
    print(f"B64 length:   {len(b64)} chars")
    print(f"\nShield.io Badge URL:")
    print(url)


if __name__ == "__main__":
    main()
