#!/usr/bin/env bash
# Stop hook: reminds Claude to fill the ARD REASON section when source code has changed
# without a corresponding docs/ard/ file being created or updated.

STAGED=$(git diff --name-only --cached 2>/dev/null)
UNSTAGED=$(git diff --name-only 2>/dev/null)
UNTRACKED=$(git ls-files --others --exclude-standard 2>/dev/null)
ALL_CHANGED=$(printf '%s\n%s\n%s' "$STAGED" "$UNSTAGED" "$UNTRACKED")

HAS_SRC=$(echo "$ALL_CHANGED" | grep -cE "^src/|^pom\.xml|^\.github/" || true)
HAS_ARD=$(echo "$ALL_CHANGED" | grep -cE "^docs/ard/" || true)

if [ "$HAS_SRC" -gt 0 ] && [ "$HAS_ARD" -eq 0 ]; then
  echo "ARD REQUIRED: Source code was changed but no ARD exists in docs/ard/."
  echo ""
  echo "Before committing, create docs/ard/ARD-NNNN-title.md using docs/ard/template.md."
  echo "Start with the REASON section — fill it before writing any more code."
fi
