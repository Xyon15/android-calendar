# 🧪 Tests de la Sélection Multiple

## ✅ Checklist de Test

### 🔘 Bouton de Sélection Multiple
- [ ] Le bouton carré est visible au centre de la barre bleue
- [ ] Le bouton a l'icône carré avec checkmark
- [ ] Cliquer sur le bouton ouvre l'interface de sélection multiple
- [ ] Le bouton disparaît sur les autres écrans (types de journées, etc.)

### 📅 Interface de Sélection Multiple
- [ ] Les en-têtes des jours (Lun, Mar, Mer, etc.) sont visibles
- [ ] Le calendrier s'affiche par semaines (lignes horizontales)
- [ ] Les jours ont une forme rectangulaire avec coins arrondis (plus de cercles)
- [ ] Les jours précédents et suivants sont masqués/invisibles
- [ ] Cliquer sur un jour le sélectionne (fond bleu, texte blanc)
- [ ] Cliquer à nouveau désélectionne le jour
- [ ] Plusieurs jours peuvent être sélectionnés simultanément
- [ ] Les weekends (Sam/Dim) ont une couleur de texte légèrement différente
- [ ] Le bouton "Valider" est désactivé si aucun jour sélectionné
- [ ] Le bouton "Valider" s'active dès qu'un jour est sélectionné

### 🎨 Sélection du Type de Journée
- [ ] La section bas apparaît après validation de la sélection
- [ ] La liste des types de journées s'affiche avec couleurs
- [ ] Radio buttons fonctionnent (un seul sélectionnable)
- [ ] Descriptions des types sont visibles
- [ ] Bouton "Appliquer" désactivé sans sélection de type
- [ ] Bouton "Appliquer" s'active avec type et jours sélectionnés

### 💾 Application et Sauvegarde
- [ ] Les événements sont créés en base de données
- [ ] Retour au calendrier principal après application
- [ ] Message de confirmation affiché
- [ ] Les jours modifiés montrent la couleur du type choisi
- [ ] Les événements apparaissent dans le bottom sheet des jours

### 🔙 Navigation et UX
- [ ] Bouton retour fonctionne depuis l'interface de sélection
- [ ] Navigation back Android fonctionne
- [ ] L'interface se ferme proprement
- [ ] Pas de crashes ou d'erreurs

## 🎯 Scénarios de Test

### Scénario 1 : Sélection Simple
1. Ouvrir la sélection multiple
2. Sélectionner 3 jours non consécutifs
3. Valider → Interface types apparaît
4. Choisir "Congé"
5. Appliquer → Vérifier les 3 jours sont en couleur sur le calendrier

### Scénario 2 : Modification Sélection
1. Sélectionner 5 jours
2. Déselectionner 2 jours
3. Valider avec 3 jours restants
4. Appliquer un type → Vérifier que seuls les 3 jours finaux sont modifiés

### Scénario 3 : Annulation
1. Sélectionner des jours
2. Utiliser le bouton retour sans valider
3. Vérifier que rien n'est modifié au calendrier
4. Répéter avec validation mais sans application

## 🐛 Points d'Attention

- **Performance** : Vérifier la fluidité avec beaucoup de sélections
- **Mémoire** : S'assurer que les listes se libèrent correctement
- **États** : Validation des états désactivé/activé des boutons
- **Couleurs** : Cohérence des couleurs avec le reste de l'app
- **Textes** : Tous les textes sont en français
- **Accessibilité** : Content descriptions présentes