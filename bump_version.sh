#!/usr/bin/env bash
set -e

if [ -z "$1" ]; then
  echo "Usage: ./bump_version.sh <new_version> [optional_notes]"
  echo "Example: ./bump_version.sh 1.0.1 'Bug fixes and performance improvements'"
  exit 1
fi

NEW_VERSION="$1"
NOTES="${2:-Patch release $NEW_VERSION}"

# Update gradle.properties
sed -i "s/^mod_version=.*/mod_version=$NEW_VERSION/" gradle.properties

# Update RELEASE_NOTES.md header
sed -i "s/^# CatPay v.* — Release & Patch Notes/# CatPay v$NEW_VERSION — Release & Patch Notes/" RELEASE_NOTES.md

git add gradle.properties RELEASE_NOTES.md
git commit -m "chore(release): bump version to v$NEW_VERSION"
git tag "v$NEW_VERSION"

echo "Version bumped to v$NEW_VERSION and tagged."
echo "Push with: git push origin main --tags"
