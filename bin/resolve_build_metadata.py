#!/usr/bin/env python3
from __future__ import annotations

import json
import os
import re
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
GRADLE_PROPERTIES = ROOT / "gradle.properties"


def read_gradle_properties(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key.strip()] = value.strip()
    return values


def parse_csv(value: str) -> list[str]:
    return [item.strip() for item in value.split(",") if item.strip()]


def normalize_csv(value: str) -> str:
    return ",".join(parse_csv(value))


def validate_unique(values: list[str], label: str) -> None:
    if len(values) != len(set(values)):
        raise SystemExit(f"Duplicate {label} are not allowed: {values}")


def validate_subset(values: list[str], allowed: set[str], label: str) -> None:
    invalid = [value for value in values if value not in allowed]
    if invalid:
        raise SystemExit(
            f"Unsupported {label}: {invalid}. Supported now: {sorted(allowed)}"
        )


def split_tag_name(tag_name: str) -> tuple[str, str, str]:
    parts = tag_name.rsplit("-", 2)
    if len(parts) != 3:
        raise SystemExit(
            "Tag must match "
            "{minecraft version}-{mod version}-{loader1+loader2+...}. "
            f"Got: {tag_name}"
        )
    return parts[0], parts[1], parts[2]


def validate_mod_version(value: str) -> None:
    if not re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+", value):
        raise SystemExit(
            "Mod version must match x.y.z in tag parsing. "
            f"Got: {value}"
        )


def validate_loader_segment(value: str) -> None:
    if not re.fullmatch(r"[a-z0-9+]+", value):
        raise SystemExit(
            "Loader segment must contain only lowercase letters, digits, and '+'. "
            f"Got: {value}"
        )


def format_loader_label(loaders: list[str]) -> str:
    return " + ".join(loader.capitalize() for loader in loaders)


def build_publish_dependencies(loaders: list[str]) -> str:
    deps: list[str] = []

    if "fabric" in loaders:
        deps.append("fabric-api(required)")

    return "\n".join(deps)


def gha_output(key: str, value: str) -> None:
    if "\n" in value:
        print(f"{key}<<__GHA_OUTPUT__")
        print(value)
        print("__GHA_OUTPUT__")
        return

    print(f"{key}={value}")


def main() -> int:
    props = read_gradle_properties(GRADLE_PROPERTIES)

    mod_version = props["mod_version"]
    default_publishers = props.get("default_publishers", "modrinth")
    current_minecraft_version = props["minecraft_version"]

    event_name = os.getenv("GITHUB_EVENT_NAME", "")
    ref_name = os.getenv("GITHUB_REF_NAME", "")

    allowed_loaders = {"fabric"}
    allowed_publishers = {"modrinth", "sourceforge"}

    if event_name == "release":
        target_branch = os.getenv("RELEASE_TARGET_COMMITISH", "")
        tag_name = os.getenv("RELEASE_TAG_NAME", "")
        release_name = os.getenv("RELEASE_NAME", "")

        if target_branch == "main":
            raise SystemExit("Releases targeting main are not allowed.")

        branch_match = re.fullmatch(r"release/(.+)", target_branch)
        if not branch_match:
            raise SystemExit(
                f"Release target branch must match release/{{minecraft version}}. "
                f"Got: {target_branch}"
            )
        branch_minecraft_version = branch_match.group(1)

        tag_minecraft_version, tag_mod_version, tag_loaders = split_tag_name(tag_name)
        validate_mod_version(tag_mod_version)
        validate_loader_segment(tag_loaders)
        loaders_csv_raw = tag_loaders.replace("+", ",")

        if branch_minecraft_version != tag_minecraft_version:
            raise SystemExit(
                "Release target branch minecraft version "
                f"({branch_minecraft_version}) does not match tag minecraft version "
                f"({tag_minecraft_version})."
            )

        if release_name and release_name != tag_name:
            raise SystemExit(
                "Release name must match the tag exactly. "
                f"Expected '{tag_name}', got '{release_name}'."
            )

        if tag_mod_version != mod_version:
            raise SystemExit(
                f"Tag mod version ({tag_mod_version}) does not match "
                f"gradle.properties mod_version ({mod_version})."
            )

        loaders = parse_csv(loaders_csv_raw)
        loader_label = format_loader_label(loaders)

        publishers_csv_raw = default_publishers
        minecraft_version = tag_minecraft_version
        release_tag = tag_name
        version_title = tag_name
        version_description = f"[{loader_label}] {tag_minecraft_version}-{tag_mod_version}"
        should_publish = "true"
        should_update_metadata = "false"

    elif event_name == "workflow_dispatch":
        loaders_csv_raw = os.getenv("INPUT_LOADERS", "fabric")
        publishers_csv_raw = os.getenv("INPUT_PUBLISHERS", "modrinth")
        minecraft_version = current_minecraft_version
        release_tag = ""
        manual_loaders = parse_csv(loaders_csv_raw)
        loader_label = format_loader_label(manual_loaders)
        version_title = f"manual-{ref_name}"
        version_description = f"[{loader_label}] {minecraft_version}-{mod_version}"
        should_publish = os.getenv("INPUT_PUBLISH_ARTIFACTS", "true").lower()
        should_update_metadata = os.getenv("INPUT_UPDATE_METADATA", "true").lower()

    else:
        loaders_csv_raw = "fabric"
        publishers_csv_raw = "modrinth"
        minecraft_version = current_minecraft_version
        release_tag = ""
        version_title = ""
        version_description = ""
        should_publish = "false"
        should_update_metadata = (
            "true" if event_name == "push" and ref_name == "main" else "false"
        )

    loaders_csv = normalize_csv(loaders_csv_raw)
    publishers_csv = normalize_csv(publishers_csv_raw)

    loaders = parse_csv(loaders_csv)
    publishers = parse_csv(publishers_csv)
    publish_dependencies = build_publish_dependencies(loaders)

    validate_unique(loaders, "loaders")
    validate_unique(publishers, "publishers")
    validate_subset(loaders, allowed_loaders, "loaders")
    validate_subset(publishers, allowed_publishers, "publishers")

    gha_output("minecraft_version", minecraft_version)
    gha_output("mod_version", mod_version)
    gha_output("loaders_csv", loaders_csv)
    gha_output("loaders_json", json.dumps(loaders))
    gha_output("publishers_csv", publishers_csv)
    gha_output("publishers_json", json.dumps(publishers))
    gha_output("release_tag", release_tag)
    gha_output("version_title", version_title)
    gha_output("version_description", version_description)
    gha_output("publish_dependencies", publish_dependencies)
    gha_output("should_publish", should_publish)
    gha_output("should_update_metadata", should_update_metadata)

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
