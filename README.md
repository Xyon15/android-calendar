# Mon Planning - Calendrier Android 📅

Une application de calendrier Android moderne et intuitive avec un **système de rendez-vous simplifié** utilisant des bottom sheets. L'application permet de gérer facilement votre planning avec des rendez-vous rapides et des types de journées personnalisables.

## ✨ Fonctionnalités

### 📅 Affichage Calendrier
- **Vue mensuelle** avec grille 7x6 jours
- **Navigation intuitive** entre les mois avec boutons fléchés
- **Affichage séparé** : rendez-vous (sans couleur) vs types de journées (avec couleur)
- **Clic sur date** → Bottom sheet avec liste des rendez-vous
- **Affichage français** (noms des jours et mois en français)
- **Événements mois adjacents** : Affichage grisé des événements des jours précédents/suivants ⭐

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
- **Sélection multiple optimisée** : Interface moderne avec workflow simplifié ⭐

### ⏰ Fonctionnalités Techniques
- **Sélection d'heure** avec TimePickerDialog
- **Base de données Room** v2 avec migrations automatiques
- **Nettoyage automatique** des données parasites
- **Logging complet** pour debugging
- **Gestion timezone** corrigée
- **Gestion robuste des fragments** avec protection contre les crashes
- **Coroutines sécurisées** avec annulation automatique au cycle de vie
- **Interface adaptative** avec affichage automatique des sections
- **Récupération étendue** : Événements sur 6 semaines complètes (mois adjacents inclus) ⭐

### 🎨 Interface Utilisateur
- **Design Material** moderne et épuré
- **Thème cohérent** avec couleurs personnalisées
- **Navigation fluide** entre les écrans
- **Boutons modernes** avec coins arrondis et états visuels clairs
- **Interface de sélection multiple** avec affichage par semaines
- **Workflow simplifié** sans étapes intermédiaires inutiles
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
│   │   ├── CalendarFragment.kt                # Vue calendrier
│   │   ├── AddEventBottomSheetFragment.kt     # Bottom sheet rendez-vous ⭐
│   │   ├── DayMenuBottomSheetFragment.kt      # Menu jour avec liste ⭐
│   │   ├── DayTypeBottomSheetFragment.kt      # Types de journées
│   │   ├── MultiDaySelectionFragment.kt       # Interface sélection multiple ⭐
│   │   ├── CalendarAdapter.kt                 # Adapter avec séparation couleurs ⭐
│   │   ├── MultiSelectWeekCalendarAdapter.kt  # Adapter calendrier semaines ⭐
│   │   └── DayTypeSelectionAdapter.kt         # Adapter types avec boutons radio ⭐
│   ├── event/              # Gestion legacy
│   └── daydetail/          # Détail d'une journée
├── utils/                  # Utilitaires (dates, couleurs)
└── MainActivity.kt         # Activité avec cleanup automatique ⭐
```

### 🔄 **Fichiers Clés Modifiés**
- **`AddEventBottomSheetFragment`** → Interface de création rendez-vous
- **`DayMenuBottomSheetFragment`** → Affichage liste + distinction types
- **`CalendarAdapter`** → Séparation rendez-vous/types de journées + Affichage grisé mois adjacents ⭐
- **`CalendarViewModel`** → Récupération événements sur 6 semaines complètes ⭐
- **`MultiDaySelectionFragment`** → Interface de sélection multiple moderne ⭐
- **`MultiSelectWeekCalendarAdapter`** → Calendrier par semaines avec sélection
- **`DayTypeSelectionAdapter`** → Liste des types avec design moderne
- **`MainActivity`** → Nettoyage automatique et gestion bouton multi-sélection ⭐
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

### 🔘 **Sélection Multiple de Jours** ⭐
1. **Bouton carré avec checkmark** au centre de la barre bleue → Ouvre la sélection multiple
2. **Affichage par semaines** avec calendrier épuré et en-têtes des jours
3. **Cliquer sur les jours** à modifier → Interface des types apparaît **automatiquement**
4. **Choisir le type** dans la liste avec indicateurs colorés et boutons radio modernes
5. **Appliquer aux jours sélectionnés** → Le type est affecté à tous les jours sélectionnés

### 🗓️ **Affichage Événements Mois Adjacents** ⭐
1. **Vue complète** : Le calendrier affiche 6 semaines complètes (42 jours)
2. **Événements visibles** : Les types de journées et rendez-vous des mois précédent/suivant sont affichés
3. **Effet grisé** : Distinction visuelle claire entre mois courant et adjacents
   - **Couleurs de fond** : 30% d'opacité pour les types de journées
   - **Badges rendez-vous** : 50% de transparence
   - **Texte adaptatif** : Couleur secondaire pour les jours adjacents
4. **Récupération intelligente** : Les événements sont chargés sur toute la plage affichée
5. **Performance optimisée** : Mise à jour automatique lors du changement de mois

### 🔍 **Distinction Visuelle**
- **Rendez-vous** : Pas de couleur sur calendrier, visibles dans la liste
- **Types de journée** : Couleur de fond sur calendrier
- **Mois adjacents** : Événements visibles mais grisés (30% opacité couleur, 50% badge) ⭐

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
  - `CalendarAdapter` : Binding et couleurs + Événements mois adjacents ⭐
  - `CalendarViewModel` : Récupération plage étendue d'événements ⭐
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

### ⚙️ **Configuration Événements Mois Adjacents** ⭐
```kotlin
// CalendarViewModel.kt - Récupération étendue
val eventsForCurrentMonth = currentMonth
    .flatMapLatest { calendar ->
        val startOfDisplayedRange = getStartOfDisplayedRange(calendar) // Premier lundi affiché
        val endOfDisplayedRange = getEndOfDisplayedRange(calendar)     // Dernier dimanche affiché
        repository.getEventsWithTypeByDateRange(startOfDisplayedRange, endOfDisplayedRange)
    }

// CalendarAdapter.kt - Effet grisé
val finalColor = if (!calendarDay.isCurrentMonth) {
    Color.argb(80, Color.red(color), Color.green(color), Color.blue(color)) // 30% opacité
} else {
    color
}

// Badge semi-transparent pour jours adjacents
binding.tvAppointmentCount.alpha = if (calendarDay.isCurrentMonth) 1.0f else 0.5f
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
- **Stabilité** : ✅ Système rendez-vous fonctionnel + Gestion robuste des fragments
- **Performance** : ✅ Bottom sheets fluides + Coroutines optimisées
- **UX** : ✅ Interface intuitive et cohérente + Workflow simplifié
- **Design** : ✅ Boutons modernes + Interface de sélection multiple épurée
- **Maintenance** : ✅ Logs complets et cleanup automatique + Code défensif

### 🎯 **Dernières Améliorations Implementées** ✅
- [x] **Interface de sélection multiple** moderne avec affichage par semaines
- [x] **Boutons avec design moderne** (coins arrondis, états visuels)
- [x] **Affichage événements mois adjacents** avec effet grisé pour distinction visuelle ⭐
- [x] **Récupération étendue événements** sur plage complète de 6 semaines ⭐

## 🔄 **Roadmap Futur**

### 🎯 **Améliorations Futures**
- [ ] Mode sombre/clair
- [ ] Notifications pour rendez-vous
- [ ] Export/Import données
- [ ] Amélioration des performances de récupération événements
- [ ] Personnalisation des niveaux d'opacité pour mois adjacents

### 🚀 **Fonctionnalités Avancées**
- [ ] Recherche et filtres
- [ ] Partage de planning
- [ ] Synchronisation cloud
- [ ] Récurrence d'événements
