@echo off
echo ========================================
echo    CONFIGURATION AUTOMATIQUE GRADLE
echo ========================================

echo.
echo 1. Configuration de JAVA_HOME...
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
echo    JAVA_HOME defini: %JAVA_HOME%

echo.
echo 2. Test de Java...
"%JAVA_HOME%\bin\java.exe" -version
if %ERRORLEVEL% neq 0 (
    echo    ERREUR: Java non trouve dans Android Studio
    echo    Verifiez l'installation d'Android Studio
    pause
    exit /b 1
)

echo.
echo 3. Test de Gradle...
gradlew.bat --version
if %ERRORLEVEL% neq 0 (
    echo    ERREUR: Probleme avec Gradle
    pause
    exit /b 1
)

echo.
echo 4. Compilation du projet...
gradlew.bat clean assembleDebug

echo.
echo ========================================
echo    CONFIGURATION TERMINEE !
echo ========================================
echo.
echo Pour Android Studio:
echo 1. File ^> Invalidate Caches and Restart
echo 2. File ^> Sync Project with Gradle Files
echo 3. Build ^> Build Project
echo 4. Run
echo.
pause