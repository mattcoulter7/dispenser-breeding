#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path


def main() -> int:
    readme = Path("README.md").read_text(encoding="utf-8")
    print(json.dumps({"body": readme}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
