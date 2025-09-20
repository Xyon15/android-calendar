@echo off
echo ==========================================
echo    NETTOYAGE RADICAL GRADLE ANDROID
echo ==========================================

echo.
echo 1. Arret de tous les processus Java/Gradle...
taskkill /F /IM java.exe /T 2>nul
taskkill /F /IM gradle.exe /T 2>nul

echo.
echo 2. Attente de 3 secondes...
timeout /t 3 /nobreak >nul

echo.
echo 3. Suppression cache Gradle global...
if exist "%USERPROFILE%\.gradle" (
    rmdir /s /q "%USERPROFILE%\.gradle"
    echo    Cache global supprime
) else (
    echo    Pas de cache global trouve
)

echo.
echo 4. Suppression cache projet local...
if exist ".gradle" (
    rmdir /s /q ".gradle"
    echo    Cache local supprime
)

if exist "build" (
    rmdir /s /q "build"
    echo    Dossier build supprime
)

if exist "app\build" (
    rmdir /s /q "app\build"
    echo    Dossier app\build supprime
)

echo.
echo 5. Recreation de l'environnement Gradle...
gradlew --no-daemon --refresh-dependencies clean

echo.
echo 6. Test de compilation...
gradlew --no-daemon assembleDebug

echo.
echo ==========================================
echo    NETTOYAGE TERMINE
echo ==========================================
pause