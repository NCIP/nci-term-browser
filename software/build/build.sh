#!/bin/bash
# ******************************************
# **** Command file to invoke build.xml ****
# ******************************************
# Environment settings here...
DEBUG=-Denable.install.debug=false
TAG=-Danthill.build.tag_built=$USER

if [ "$2" = "debug" ]; then
   DEBUG="$DEBUG -debug"
fi
clear

if [ -z "$1" ]; then
    echo
    echo "Available targets are:"
    echo
    echo "  clean        -- Remove classes directory for clean build"
    echo "  all          -- Normal build of application"
    echo "  upgrade      -- Build and upgrade application"
    echo "  install      -- Builds, installs JBoss locally"
    echo "  deploy       -- Hot deploy application"
    echo "  jsp          -- Hot deploy JSP files"
    echo "  stop         -- Stop war file"
    echo "  start        -- Start war file"
    echo
    exit
fi

case $1 in

    clean)
        ant clean
        ;;
    *)
        echo "Invalid target $1"
        ;;
esac
