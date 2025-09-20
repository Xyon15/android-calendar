# 🆘 Guide de Dépannage Android Studio

## Problèmes Courants et Solutions

### 1. 🔄 Erreurs de Synchronisation Gradle
**Symptômes :** Messages d'erreur rouge dans Android Studio
**Solutions :**
- File > Sync Project with Gradle Files
- Build > Clean Project
- Build > Rebuild Project
- Redémarrer Android Studio

### 2. 📦 SDK Android Manquant
**Symptômes :** "SDK not found" ou "API Level not installed"
**Solutions :**
- Tools > SDK Manager
- Cocher Android 13 (API 33) et Android 14 (API 34)
- Cliquer "Apply" et attendre le téléchargement

### 3. 🚀 Émulateur ne Démarre Pas
**Symptômes :** Émulateur reste bloqué au logo Android
**Solutions :**
- Activer la virtualisation dans le BIOS (VT-x/AMD-V)
- Essayer un émulateur avec API Level 30
- Augmenter la RAM allouée (2048 MB minimum)
- Redémarrer le PC si nécessaire

### 4. 🔨 Erreurs de Build
**Symptômes :** "Build failed" avec erreurs de compilation
**Solutions :**
- Vérifier que JDK 8 ou 11 est utilisé
- File > Invalidate Caches and Restart
- Tools > SDK Manager > SDK Tools > Cocher Android Build Tools

### 5. 📱 App ne s'Installe Pas
**Symptômes :** "Installation failed"
**Solutions :**
- Wipe Data de l'émulateur
- Cold Boot l'émulateur
- Créer un nouvel émulateur
- Vérifier l'espace disque disponible

### 6. 🐌 Android Studio Très Lent
**Solutions :**
- Augmenter la RAM dans Help > Edit Custom VM Options
- Fermer les autres applications
- Désactiver les plugins non utilisés
- Utiliser un SSD si possible

## 📞 Commandes de Diagnostic Utiles

### Vérifier Java
```cmd
java -version
javac -version
```

### Vérifier Android SDK
Tools > SDK Manager > Vérifier que SDK Platform 33 ou 34 est installé

### Logs de Débogage
View > Tool Windows > Logcat (pour voir les erreurs d'exécution)

## 🎯 Configuration Optimale Recommandée
- RAM : 8 GB minimum (16 GB idéal)
- SSD : Fortement recommandé
- Antivirus : Exclure les dossiers Android SDK et projets
- Windows Defender : Exclure Android Studio

Si le problème persiste, redémarrez Android Studio et votre PC ! 🔄