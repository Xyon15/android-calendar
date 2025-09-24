# Mon Planning - Calendrier Android 📅

Une application de calendrier Android moderne et intuitive avec un **système de rendez-vous simplifié** utilisant des bottom sheets. L'application permet de gérer facilement votre planning avec des rendez-vous rapides et des types de journées personnalisables.

## ✨ Fonctionnalités

### 📅 Affichage Calendrier
- **Vue mensuelle** avec grille 7x6 jours
- **Navigation intuitive** entre les mois avec boutons fléchés
- **Affichage séparé** : rendez-vous (sans couleur) vs types de journées (avec couleur)
- **Clic sur date** → Bottom sheet avec liste des rendez-vous
- **Affichage français** (noms des jours et mois en français)

### 🎯 Gestion des Rendez-vous 
- **Bottom Sheet de création** rapide et intuitive
- **Rendez-vous simples** avec `eventTypeId = null`
- **Support heure optionnelle** (switch activable)
- **Édition en place** depuis la liste des rendez-vous
- **Pas de couleurs** sur le calendrier (distinction claire avec types de journées)

### 📋 Types de Journées 
- **Types personnalisables** avec couleurs :
  - Travail, Congé, Formation, etc.
- **Application à une journée complète**
- **Couleurs visibles** sur le calendrier
- **Gestion via bottom sheets** dédiés

### ⏰ Fonctionnalités Techniques
- **Sélection d'heure** avec TimePickerDialog
- **Base de données Room** v2 avec migrations automatiques
- **Nettoyage automatique** des données parasites
- **Logging complet** pour debugging
- **Gestion timezone** corrigée

### 🎨 Interface Utilisateur
- **Design Material** moderne et épuré
- **Thème cohérent** avec couleurs personnalisées
- **Navigation fluide** entre les écrans
- **Responsive design** adapté aux différentes tailles d'écran

## 🏗️ Architecture Technique

### 🛠️ Technologies Utilisées
- **Kotlin** - Langage principal
- **Android Architecture Components** (Room, ViewModel, LiveData)
- **Navigation Component** - Navigation entre fragments
- **View Binding** - Liaison des vues
- **Material Design 3** - Interface utilisateur moderne
- **Coroutines** - Programmation asynchrone

### 📱 Architecture MVVM
- **Model** : Room database avec entités Event et EventType
- **View** : Fragments avec layouts XML
- **ViewModel** : Logique métier et gestion des états
- **Repository** : Couche d'abstraction pour l'accès aux données

### 🗄️ Base de Données
- **Room Database** pour la persistance locale
- **Entités améliorées** :
  - `Event` avec `eventTypeId` nullable (pour les rendez-vous)
  - `EventType` pour les types de journées
  - `EventWithType` relation avec `eventType` nullable
- **DAOs** : EventDao, EventTypeDao pour l'accès aux données
- **Migration v1→v2** : Support des rendez-vous sans type
- **Cleanup automatique** des EventTypes parasites

## 📂 Structure du Projet

```
app/src/main/java/com/calendar/app/
├── data/
│   ├── dao/                # Data Access Objects
│   ├── database/           # Configuration Room v2
│   ├── model/              # Entités Event, EventType, EventWithType
│   └── repository/         # Repository pattern avec cleanup
├── ui/
│   ├── calendar/           # Fragments principaux
│   │   ├── CalendarFragment.kt           # Vue calendrier
│   │   ├── AddEventBottomSheetFragment.kt  # Bottom sheet rendez-vous ⭐
│   │   ├── DayMenuBottomSheetFragment.kt   # Menu jour avec liste ⭐
│   │   ├── DayTypeBottomSheetFragment.kt   # Types de journées
│   │   └── CalendarAdapter.kt            # Adapter avec séparation couleurs ⭐
│   ├── event/              # Gestion legacy
│   └── daydetail/          # Détail d'une journée
├── utils/                  # Utilitaires (dates, couleurs)
└── MainActivity.kt         # Activité avec cleanup automatique ⭐
```

### 🔄 **Fichiers Clés Modifiés**
- **`AddEventBottomSheetFragment`** → Interface de création rendez-vous
- **`DayMenuBottomSheetFragment`** → Affichage liste + distinction types
- **`CalendarAdapter`** → Séparation rendez-vous/types de journées  
- **`MainActivity`** → Nettoyage automatique au démarrage
- **`CalendarDatabase`** → Migration v2 pour support `eventTypeId` nullable

## 🚀 Installation et Configuration

### Prérequis
- Android Studio Arctic Fox ou plus récent
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.22

### Build du Projet
```bash
# Cloner le repository
git clone [url-du-repo]
cd android-calendar

# Build avec Gradle
./gradlew build

# Installation sur device/émulateur
./gradlew installDebug
```

## 🎮 Utilisation

### 📅 **Navigation Calendrier**
1. **Navigation mensuelle** : Flèches pour changer de mois
2. **Clic sur date** → Ouverture automatique du bottom sheet jour

### 📝 **Créer un Rendez-vous**
1. **Clic sur date** → Bottom sheet s'ouvre
2. **Bouton "Nouveau rendez-vous"** → Formulaire de création
3. **Remplir titre** (obligatoire)
4. **Activer/désactiver l'heure** avec le switch
5. **Sauvegarder** → Rendez-vous créé avec `eventTypeId = null`

### ✏️ **Modifier un Rendez-vous**
1. **Clic sur date** → Liste des rendez-vous apparaît
2. **Clic sur rendez-vous** → Ouverture en mode édition
3. **Modifier** titre, description, heure
4. **Sauvegarder** les modifications

### 🎨 **Gérer les Types de Journées**
1. **Section "Type de Journée"** dans le bottom sheet
2. **Sélectionner type** → Application à toute la journée
3. **Couleur visible** sur le calendrier (contrairement aux rendez-vous)

### 🔍 **Distinction Visuelle**
- **Rendez-vous** : Pas de couleur sur calendrier, visibles dans la liste
- **Types de journée** : Couleur de fond sur calendrier

## 🔧 Personnalisation et Debugging

### 🎨 Types de Journées Personnalisés
- **Ajout via interface** : Bottom sheet → Menu → Nouveau type
- **Couleurs spécifiques** pour chaque type
- **Stockage en base** Room avec gestion complète

### 🐛 **Système de Debugging Intégré**
- **Logs complets** avec tags spécifiques :
  - `AddEventBottomSheet` : Création/édition rendez-vous
  - `DayMenuBottomSheet` : Chargement et affichage
  - `MainActivity` : Cleanup et nettoyage
  - `CalendarAdapter` : Binding et couleurs
- **Timestamps détaillés** pour debugging timezone
- **Informations EventType** pour traçage parasites

### 🧹 **Nettoyage Automatique**
```kotlin
// Dans MainActivity.cleanupParasiteEventTypes()
// Supprime automatiquement les EventTypes "Type de journée" parasites
// Corrige les eventTypeId incorrects avant suppression
// Préserve les rendez-vous en les convertissant à eventTypeId = null
```

### 🔧 **Configuration Avancée**
```xml
<!-- colors.xml -->
<color name="event_purple">#8E44AD</color>
<color name="event_blue">#3498DB</color>
<!-- Ajoutez vos couleurs personnalisées -->
```

## 📄 Licence

Ce projet est développé à des fins éducatives et personnelles.

## 🛠️ **Maintenance et Support**

### 🐛 **Résolution de Problèmes**
Si vous rencontrez des issues :

1. **Vérifiez les logs** avec les tags spécifiques
2. **Nettoyage manuel** si nécessaire :
   ```kotlin
   // Le cleanup automatique se lance au démarrage
   // Mais peut être forcé via MainActivity.cleanupParasiteEventTypes()
   ```
3. **Reset base données** en cas de corruption :
   ```bash
   # Supprimer les données app dans Android Settings
   # Ou incrémenter DATABASE_VERSION dans CalendarDatabase
   ```

### 🤝 **Contribution**

Les contributions sont les bienvenues ! N'hésitez pas à :
- **Signaler des bugs** avec logs détaillés
- **Proposer fonctionnalités** avec cas d'usage
- **Améliorer documentation** et exemples
- **Optimiser performances** avec profiling

### 📊 **Métriques Projet**
- **Stabilité** : ✅ Système rendez-vous fonctionnel
- **Performance** : ✅ Bottom sheets fluides  
- **UX** : ✅ Interface intuitive et cohérente
- **Maintenance** : ✅ Logs complets et cleanup automatique

## 🔄 **Roadmap Futur**

### 🎯 **Améliorations Immédiates**
- [ ] Mode sombre/clair
- [ ] Notifications pour rendez-vous
- [ ] Export/Import données

### 🚀 **Fonctionnalités Avancées**
- [ ] Recherche et filtres
- [ ] Partage de planning
