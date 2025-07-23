#!/bin/sh
echo "Script iniciado"
JLINK_VM_OPTIONS=
DIR="$(cd "$(dirname "$0")" && pwd)"
"$DIR/java" $JLINK_VM_OPTIONS -m br.com.fisica.basicas/br.com.fisica.gui.Main "$@"
