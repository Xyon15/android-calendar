# Guide Configuration Émulateur Android Studio

## 🎯 Créer un Émulateur Android

### Étapes dans Android Studio :

1. **Ouvrir AVD Manager**
   - Cliquez sur Tools > AVD Manager
   - OU cliquez sur l'icône téléphone dans la barre d'outils

2. **Créer un Virtual Device**
   - Cliquez sur "Create Virtual Device"
   - Choisissez **Pixel 6** ou **Pixel 4** (recommandé)
   - Cliquez "Next"

3. **Sélectionner System Image**
   - Choisissez **API Level 33** (Android 13) ou **API Level 34** (Android 14)
   - Si pas encore téléchargé, cliquez "Download" à côté
   - Cliquez "Next"

4. **Configuration finale**
   - Nom : "Calendar_Emulator"
   - Cliquez "Finish"

5. **Démarrer l'émulateur**
   - Cliquez sur le triangle vert ▶️ à côté de votre émulateur
   - Attendez le démarrage (2-3 minutes la première fois)

## 📋 Configuration Minimale Recommandée :
- **Device :** Pixel 6
- **API Level :** 33 ou 34
- **RAM :** 2048 MB minimum
- **Storage :** 800 MB minimum

## 🚨 Si l'émulateur ne démarre pas :
- Vérifiez que la virtualisation est activée dans le BIOS
- Essayez API Level 30 (Android 11) plus stable
- Redémarrez Android Studio

L'émulateur simule un téléphone Android réel pour tester votre app ! 📱