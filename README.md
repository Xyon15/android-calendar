# Mon Planning - Calendrier Android !

## 🚀 COMMANDES DE COMPILATION

```bash
# Configuration Java (à faire à chaque session)
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr

# Nettoyage et compilation
.\gradlew.bat clean assembleDebug

# Version rapide
.\gradlew.bat build
```

## 📁 SCRIPTS DISPONIBLES
- **`clean-gradle.bat`** → Nettoyage automatique complet
- **`gradlew.bat`** → Gradle Wrapper Windows (maintenant fonctionnel)

---

Une application de calendrier Android moderne et intuitive. Cette application permet de gérer facilement votre planning avec un système de couleurs personnalisées pour différents types d'événements.

## 🌟 Fonctionnalités

### 📅 Affichage Calendrier
- **Vue mensuelle** avec grille 7x6 jours
- **Navigation intuitive** entre les mois avec boutons fléchés
- **Indicateurs visuels** pour les jours avec événements
- **Couleurs personnalisées** selon le type d'événement
- **Affichage français** (noms des jours et mois en français)

### 🎯 Gestion des Événements
- **Ajout/modification** d'événements avec formulaire complet
- **Types d'événements personnalisables** :
  - Travail (violet)
  - Réunion (bleu)
  - Personnel (vert)
  - Vacances (cyan)
  - Formation (orange)
  - Rendez-vous médical (rouge)
  - Sport (jaune)
  - Famille (rose)
  - Et plus selon vos besoins...

### ⏰ Fonctionnalités Avancées
- **Sélection d'heure** avec NumberPicker intuitif
- **Calcul des heures travaillées** par événement
- **Système d'alertes** (15min, 30min, 1h)
- **Vue détail par jour** avec liste des rendez-vous
- **Heures supplémentaires/récupération** trackées

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
- **Entités** : Event, EventType avec relations
- **DAOs** : EventDao, EventTypeDao pour l'accès aux données
- **Migrations** automatiques et type converters

## 📂 Structure du Projet

```
app/src/main/java/com/calendar/app/
├── data/
│   ├── dao/                 # Data Access Objects
│   ├── database/           # Configuration Room
│   ├── model/              # Entités de données
│   └── repository/         # Repository pattern
├── ui/
│   ├── calendar/           # Fragment calendrier principal
│   ├── event/              # Gestion des événements
│   └── daydetail/          # Détail d'une journée
├── utils/                  # Utilitaires (dates, couleurs)
└── MainActivity.kt         # Activité principale
```

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

1. **Navigation mensuelle** : Utilisez les flèches pour naviguer entre les mois
2. **Ajouter un événement** : Cliquez sur une date puis sur le bouton "+"
3. **Modifier un événement** : Cliquez sur une date avec événement puis modifiez
4. **Types d'événements** : Sélectionnez ou créez des types avec couleurs personnalisées
5. **Vue détail** : Consultez tous les événements d'une journée spécifique

## 🔧 Personnalisation

### Ajouter de Nouveaux Types d'Événements
Les types d'événements sont stockés dans la base de données Room et peuvent être :
- Ajoutés via l'interface utilisateur
- Personnalisés avec des couleurs spécifiques
- Modifiés ou supprimés selon les besoins

### Modification des Couleurs
Les couleurs sont définies dans `colors.xml` et peuvent être personnalisées :
```xml
<color name="event_purple">#8E44AD</color>
<color name="event_blue">#3498DB</color>
<!-- Ajoutez vos couleurs personnalisées -->
```

## 📄 Licence

Ce projet est développé à des fins éducatives et personnelles.

## 🤝 Contribution

Les contributions sont les bienvenues ! N'hésitez pas à :
- Signaler des bugs
- Proposer de nouvelles fonctionnalités
- Améliorer la documentation
- Optimiser les performances

## 🔄 Mises à Jour Futures

- [ ] Export/Import de planning
- [ ] Notifications push
- [ ] Mode sombre
