# Script PowerShell pour tester l'application Android Calendar
# Exécutez ce script depuis le répertoire racine du projet

Write-Host "🚀 Script de test pour l'application Android Calendar" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Vérifier si Android Studio est installé
Write-Host "`n📱 Vérification de l'environnement Android..." -ForegroundColor Yellow

# Fonction pour vérifier si un chemin existe
function Test-AndroidStudio {
    $androidStudioPaths = @(
        "$env:LOCALAPPDATA\Google\AndroidStudio*",
        "$env:PROGRAMFILES\Android\Android Studio*",
        "C:\Program Files\Android\Android Studio*"
    )
    
    foreach ($path in $androidStudioPaths) {
        if (Test-Path $path) {
            return $true
        }
    }
    return $false
}

# Vérifier Android Studio
if (Test-AndroidStudio) {
    Write-Host "✅ Android Studio détecté" -ForegroundColor Green
} else {
    Write-Host "❌ Android Studio non détecté. Veuillez l'installer depuis:" -ForegroundColor Red
    Write-Host "   https://developer.android.com/studio" -ForegroundColor White
}

# Vérifier Java/JDK
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Java détecté: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java non détecté. Android Studio inclut un JDK." -ForegroundColor Yellow
}

# Vérifier la structure du projet
Write-Host "`n📂 Vérification de la structure du projet..." -ForegroundColor Yellow

$requiredFiles = @(
    "build.gradle.kts",
    "settings.gradle.kts", 
    "app\build.gradle.kts",
    "app\src\main\AndroidManifest.xml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "✅ $file" -ForegroundColor Green
    } else {
        Write-Host "❌ $file manquant" -ForegroundColor Red
    }
}

# Instructions d'ouverture
Write-Host "`n🔧 Instructions pour ouvrir et tester l'application:" -ForegroundColor Cyan
Write-Host "1. Ouvrir Android Studio" -ForegroundColor White
Write-Host "2. Choisir 'Open an Existing Project'" -ForegroundColor White
Write-Host "3. Naviguer vers: $PWD" -ForegroundColor White
Write-Host "4. Attendre la synchronisation Gradle" -ForegroundColor White
Write-Host "5. Connecter un appareil Android ou démarrer un émulateur" -ForegroundColor White
Write-Host "6. Cliquer sur le bouton 'Run' (triangle vert)" -ForegroundColor White

# Instructions pour l'émulateur
Write-Host "`n📱 Configuration de l'émulateur (si nécessaire):" -ForegroundColor Cyan
Write-Host "1. Tools > AVD Manager" -ForegroundColor White
Write-Host "2. Create Virtual Device" -ForegroundColor White
Write-Host "3. Choisir Pixel 6 ou similaire" -ForegroundColor White
Write-Host "4. API Level 33 ou 34 (Android 13/14)" -ForegroundColor White
Write-Host "5. Finish et Start" -ForegroundColor White

# Instructions de débogage
Write-Host "`n🐛 En cas de problème:" -ForegroundColor Cyan
Write-Host "1. File > Sync Project with Gradle Files" -ForegroundColor White
Write-Host "2. Build > Clean Project puis Build > Rebuild Project" -ForegroundColor White
Write-Host "3. Vérifier les erreurs dans l'onglet 'Build'" -ForegroundColor White
Write-Host "4. Vérifier que le SDK Android 33/34 est installé" -ForegroundColor White

# Commandes Gradle alternatives
Write-Host "`n⚡ Commandes Gradle alternatives (ligne de commande):" -ForegroundColor Cyan
Write-Host "Pour build: .\gradlew build" -ForegroundColor White
Write-Host "Pour installer: .\gradlew installDebug" -ForegroundColor White
Write-Host "Pour nettoyer: .\gradlew clean" -ForegroundColor White

Write-Host "`n🎯 L'application propose:" -ForegroundColor Magenta
Write-Host "- Calendrier mensuel avec navigation" -ForegroundColor White
Write-Host "- Ajout d'événements avec types colorés" -ForegroundColor White
Write-Host "- Vue détail par jour" -ForegroundColor White
Write-Host "- Gestion des heures de travail" -ForegroundColor White

Write-Host "`n✨ Bonne decouverte de votre application calendrier!" -ForegroundColor Green