@echo off
@rem ******************************************
@rem **** Command file to invoke build.xml ****
@rem ******************************************
setlocal
set DEVPROPFILE=C:\NCI-Projects\ncit-properties\properties\dev-upgrade.properties
set CIPROPFILE=C:\NCI-Projects\ncit-properties\properties\ci-upgrade.properties
set QAPROPFILE=C:\NCI-Projects\ncit-properties\properties\qa-upgrade.properties
set DATAQAPROPFILE=C:\NCI-Projects\ncit-properties\properties\data-qa-upgrade.properties
set DEBUG=-Denable.upgrade.debug=false
set TAG=-Danthill.build.tag_built=desktop
if "%2" == "debug" (
    set DEBUG=-Denable.upgrade.debug=true
)
cls
if "%1" == "" (
    echo.
    echo Available targets are:
    echo.
    echo   clean        -- Remove classes directory for clean build
    echo   all          -- Normal build of application
    echo   upgrade      -- Build and upgrade application
    echo   install      -- Builds, installs JBoss locally
    echo   dev          -- Builds, upgrades JBoss on DEV
    echo   ci           -- Builds, upgrades JBoss on CI
    echo   qa           -- Builds, upgrades JBoss on QA
    echo   data-qa      -- Builds, upgrades JBoss on Data QA
    echo   deploy       -- Redeploy application
    goto DONE
)
if "%1" == "all" (
    ant %TAG% build:all
    goto DONE
)
if "%1" == "upgrade" (
    ant %TAG% %DEBUG% deploy:local:upgrade
    goto DONE
)
if "%1" == "install" (
    ant %TAG% %DEBUG% deploy:local:install
    goto DONE
)
if "%1" == "deploy" (
    ant %TAG% %DEBUG% deploy:hot
    goto DONE
)
if "%1" == "clean" (
    ant clean
    if exist ..\target\*.* (
       rmdir /Q /S ..\target
    )
    goto DONE
)
if "%1" == "dev" (
    ant -Dproperties.file=%DEVPROPFILE% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)
if "%1" == "ci" (
    ant -Dproperties.file=%CIPROPFILE% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)
if "%1" == "qa" (
    ant -Dproperties.file=%QAPROPFILE% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)
if "%1" == "data-qa" (
    ant -Dproperties.file=%DATAQAPROPFILE% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)

:DONE
endlocal