@echo off
@rem ******************************************
@rem **** Command file to invoke build.xml ****
@rem ******************************************
setlocal
@rem Environment settings here...
set DEBUG=-Denable.install.debug=false
set TAG=-Danthill.build.tag_built=desktop
@rem Development servers
set DEV_SERVER1=ncias-d488-v.nci.nih.gov
set DEV_SERVER2=ncias-d499-v.nci.nih.gov
set CI_SERVER=ncias-c512-v.nci.nih.gov
set DEVPROPFILE=C:\NCI-Projects\ncit-dev-properties\dev-upgrade.properties
@rem Test is debug has been set
if "%2" == "debug" (
    set DEBUG=-Denable.install.debug=true -debug
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
    echo   dev1         -- Builds, upgrades JBoss on DEV - leg 1
    echo   dev2         -- Builds, upgrades JBoss on DEV - leg 2
    echo   deploy       -- Hot deploy application
    echo   jsp          -- Hot deploy JSP files
    echo   stop         -- Stop war file
    echo   start        -- Start war file
    echo   cissh        -- Test SSH login in CI
    echo   dev1ssh      -- Test SSH login in DEV1
    echo   dev2ssh      -- Test SSH login in DEV2
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
if "%1" == "stop" (
    ant %TAG% %DEBUG% deploy:stop
    goto DONE
)
if "%1" == "start" (
    ant %TAG% %DEBUG% deploy:start
    goto DONE
)
if "%1" == "jsp" (
    ant %DEBUG% deploy:hot:jsp
    goto DONE
)
if "%1" == "clean" (
    ant clean
    if exist ..\target\*.* (
       rmdir /Q /S ..\target
    )
    goto DONE
)
if "%1" == "dev1" (
    ant -Dproperties.file=%DEVPROPFILE% -Djboss.server.hostname=%DEV_SERVER1% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)
if "%1" == "dev2" (
    ant -Dproperties.file=%DEVPROPFILE% -Djboss.server.hostname=%DEV_SERVER2% %TAG% %DEBUG% deploy:remote:upgrade
    goto DONE
)
if "%1" == "cissh" (
    ssh jboss51a@%CI_SERVER% -i C:\NCI-Projects\ssh-keys\id_dsa_bda echo "Test connecting to CI worked!"
    goto DONE
)
if "%1" == "dev1ssh" (
    ssh jboss51c@%DEV_SERVER1% -i C:\NCI-Projects\ssh-keys\id_dsa_bda echo "Test connecting to DEV1 worked!"
    goto DONE
)
if "%1" == "dev2ssh" (
    ssh jboss51c@%DEV_SERVER2% -i C:\NCI-Projects\ssh-keys\id_dsa_bda echo "Test connecting to DEV2 worked!"
    goto DONE
)
echo Invalid target '%1'.
:DONE
endlocal