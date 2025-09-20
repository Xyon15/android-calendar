# Script PowerShell pour tester l'application Android Calendar

Write-Host "Android Calendar App - Script de test" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Verification de l'environnement
Write-Host "`nVerification de l'environnement Android..." -ForegroundColor Yellow

# Verification Java
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "Java detecte: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "Java non detecte. Android Studio inclut un JDK." -ForegroundColor Yellow
}

# Verification de la structure du projet
Write-Host "`nVerification de la structure du projet..." -ForegroundColor Yellow

$requiredFiles = @(
    "build.gradle.kts",
    "settings.gradle.kts", 
    "app\build.gradle.kts",
    "app\src\main\AndroidManifest.xml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "OK: $file" -ForegroundColor Green
    } else {
        Write-Host "MANQUANT: $file" -ForegroundColor Red
    }
}

# Instructions
Write-Host "`nInstructions pour ouvrir l'application:" -ForegroundColor Cyan
Write-Host "1. Ouvrir Android Studio" -ForegroundColor White
Write-Host "2. Choisir 'Open an Existing Project'" -ForegroundColor White
Write-Host "3. Naviguer vers ce dossier: $PWD" -ForegroundColor White
Write-Host "4. Attendre la synchronisation Gradle" -ForegroundColor White
Write-Host "5. Connecter un appareil Android ou demarrer un emulateur" -ForegroundColor White
Write-Host "6. Cliquer sur le bouton 'Run' (triangle vert)" -ForegroundColor White

Write-Host "`nCommandes Gradle alternatives:" -ForegroundColor Cyan
Write-Host "Build: .\gradlew build" -ForegroundColor White
Write-Host "Install: .\gradlew installDebug" -ForegroundColor White
Write-Host "Clean: .\gradlew clean" -ForegroundColor White

Write-Host "`nBonne decouverte de votre application!" -ForegroundColor Green