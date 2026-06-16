# Refactoring CSS / Styles — LearnHub Frontend

## Contexte

Le frontend LearnHub utilise principalement **Material UI (MUI)**. La majorité des styles ne se trouve donc pas dans des fichiers `.css` classiques, mais directement dans les composants via la prop `sx`.

Avant cette refactorisation, beaucoup de valeurs visuelles étaient répétées dans plusieurs composants :

- couleurs hexadécimales ;
- bordures ;
- ombres ;
- rayons de cartes ;
- dégradés ;
- badges de statut ;
- boîtes d'icônes ;
- styles de cartes dashboard ;
- styles de tableaux et listes.

Le problème principal était l'absence d'un point de vérité commun. Modifier une couleur ou une ombre demandait de rechercher les mêmes valeurs dans plusieurs fichiers.

## Stratégie retenue

La refactorisation ne consiste pas à tout déplacer dans un seul fichier CSS, car le projet repose déjà sur MUI et `sx`.

La stratégie retenue est donc mixte :

- `frontend/src/styles/global.css` garde les styles globaux et les pages publiques qui utilisent des classes CSS classiques ;
- `frontend/src/styles/tokens.ts` centralise les tokens de design réutilisables par les composants MUI ;
- `frontend/src/theme/muiTheme.ts` réutilise les tokens principaux pour garder le thème MUI aligné ;
- les styles vraiment spécifiques à un composant peuvent rester dans ce composant.

Cette approche correspond mieux à l'architecture actuelle du front.

## Fichiers principaux

### `frontend/src/styles/global.css`

Ce fichier contient les styles globaux CSS et les variables CSS utilisées surtout par les pages publiques :

- page de login ;
- page de demande de création de compte ;
- variables `--lh-*` ;
- reset global léger ;
- responsive des pages publiques.

Exemples de variables :

```css
:root {
  --lh-primary: #4f5ff7;
  --lh-primary-dark: #3444dc;
  --lh-primary-light: #7f75ff;
  --lh-text-primary: #17203b;
  --lh-text-secondary: #596783;
  --lh-border: #e3e7f3;
  --lh-bg-default: #f6f8ff;
  --lh-radius-card: 24px;
  --lh-radius-sm: 20px;
}
```

### `frontend/src/styles/tokens.ts`

Ce fichier est le point de vérité principal pour les composants MUI.

Il contient maintenant :

- couleurs de base ;
- couleurs de surface ;
- couleurs d'état ;
- dégradés ;
- ombres ;
- styles de statuts ;
- patterns `sx` réutilisables.

Exemples :

```ts
export const LH_BORDER = "#e3e7f3";
export const LH_BORDER_LIGHT = "#edf0f7";
export const LH_PRIMARY = "#4f5ff7";
export const LH_PRIMARY_ACCENT = "#5966ef";
export const LH_PRIMARY_SOFT = "#eef0ff";
export const LH_TEXT_SECONDARY = "#596783";
export const LH_SURFACE = "#ffffff";
export const LH_PROGRESS_TRACK = "#edf0f8";
```

Dégradés centralisés :

```ts
export const LH_GRADIENT_BTN =
  "linear-gradient(110deg,#4056f4,#7458f6)";

export const LH_GRADIENT_HERO_ICON =
  "linear-gradient(135deg,#5265f5,#7554ee)";

export const LH_GRADIENT_HERO_CARD =
  "radial-gradient(circle at 88% 35%,rgba(99,88,238,.14),transparent 22%),linear-gradient(135deg,#fff,#fbfbff)";
```

Patterns `sx` partagés :

```ts
export const cardSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3,
  boxShadow: LH_SHADOW_SM,
};

export const cardLgSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3.5,
  boxShadow: LH_SHADOW_MD,
};

export const dashboardCardSx = {
  ...cardSx,
  borderRadius: 3.4,
};

export const iconBoxSx = (size: number, color: string) => ({
  width: size,
  height: size,
  display: "grid",
  placeItems: "center",
  flexShrink: 0,
  color,
  bgcolor: `${color}14`,
  borderRadius: 2.5,
});
```

### `frontend/src/theme/muiTheme.ts`

Le thème MUI a été relié aux tokens principaux.

Avant, les couleurs du thème étaient redéfinies directement dans `muiTheme.ts`.

Maintenant, le thème importe les valeurs centrales :

```ts
import {
  LH_ERROR,
  LH_ERROR_LIGHT,
  LH_PRIMARY,
  LH_PRIMARY_DARK,
  LH_PRIMARY_LIGHT,
  LH_SUCCESS,
  LH_SUCCESS_LIGHT,
  LH_TEXT_SECONDARY,
  LH_WARNING,
  LH_WARNING_LIGHT,
} from "../styles/tokens";
```

Cela évite de maintenir deux palettes différentes.

## Statuts centralisés

Les badges de statuts sont maintenant regroupés dans `tokens.ts`.

### Cours

```ts
export const COURSE_STATUS_STYLES = {
  DRAFT: { color: "#6b7280", bgcolor: "#eef0f4" },
  PUBLISHED: { color: "#168b5b", bgcolor: "#e6f8ef" },
  VALIDE: { color: "#2f62d9", bgcolor: "#e9f0ff" },
  ARCHIVE: { color: "#a04e1c", bgcolor: "#fff0df" },
};
```

### Inscriptions

```ts
export const ENROLLMENT_STATUS_STYLES = {
  VALIDE: { color: "#14794a", bgcolor: "#e3f7eb" },
  EN_ATTENTE: { color: "#a35d0a", bgcolor: "#fff0d7" },
  REFUSE: { color: "#b23c48", bgcolor: "#fdecef" },
  OUVERT: { color: "#4556df", bgcolor: "#eef0ff" },
};
```

### Signalements

```ts
export const REPORT_STATUS_STYLES = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  EN_COURS: { color: "#2167c7", bgcolor: "#e8f2ff", dot: "#3a86e8" },
  TRAITE: { color: "#6350c7", bgcolor: "#eeeaff", dot: "#7864e8" },
  RESOLU: { color: "#12834d", bgcolor: "#e5f8ed", dot: "#20b96b" },
};
```

### Progression

```ts
export const PROGRESSION_STATUS_STYLES = {
  SANS_CONTENU: {
    label: "Sans contenu",
    color: "#596174",
    bgcolor: "#edf0f5",
  },
  FAIBLE: { label: "Faible", color: "#c23e4b", bgcolor: LH_ERROR_LIGHT },
  MOYEN: { label: "Moyen", color: "#a96308", bgcolor: "#fff1d9" },
  BON: { label: "Bon", color: "#16834f", bgcolor: "#e4f7ec" },
  TERMINE: { label: "Terminé", color: "#147545", bgcolor: "#dff5e8" },
};
```

## Composants déjà migrés

### Dashboard

Les composants communs du dashboard utilisent maintenant les tokens partagés :

- `DashboardStatsCards.tsx`
- `DashboardChartSection.tsx`
- `DashboardRecentActivity.tsx`
- `DashboardHero.tsx`
- `DashboardProgressRing.tsx`

Exemples de remplacements :

```tsx
// Avant
border: "1px solid #e2e6f4";
boxShadow: "0 12px 32px rgba(62,70,130,.05)";

// Après
sx={{ ...dashboardCardSx, p: 2.4 }}
```

```tsx
// Avant
sx={{
  width: 36,
  height: 36,
  display: "grid",
  placeItems: "center",
  color: "#5966ef",
  bgcolor: "#eef0ff",
}}

// Après
sx={{
  ...iconBoxSx(36, LH_PRIMARY_ACCENT),
  borderRadius: "50%",
}}
```

### Progression

Les composants de l'écran progression professeur utilisent aussi les tokens :

- `ProgressionStats.tsx`
- `ProgressionFilters.tsx`
- `ProgressionTable.tsx`

Exemples :

```tsx
// Avant
border: "1px solid #e2e6f4";

// Après
sx={{ ...cardSx, mt: 2 }}
```

```tsx
// Avant
const styles = {
  FAIBLE: { label: "Faible", color: "#c23e4b", background: "#fdecef" },
  ...
};

// Après
const style = PROGRESSION_STATUS_STYLES[level];
```

### Cours et catalogue étudiant

Plusieurs composants liés aux cours ont été migrés :

- `CoursesPage.tsx`
- `StudentCourseCard.tsx`
- `SummaryCard.tsx`

Exemples :

```tsx
// Avant
background: "linear-gradient(110deg,#4056f4,#7458f6)";

// Après
sx={gradientBtnSx}
```

```tsx
// Avant
const gradients = ["linear-gradient(...)", ...];

// Après
background: STUDENT_COURSE_GRADIENTS[index % STUDENT_COURSE_GRADIENTS.length]
```

### Signalements

Le composant `ReportStatusChip.tsx` utilise désormais `REPORT_STATUS_STYLES`.

Cela évite de redéfinir localement les couleurs de `NOUVEAU`, `EN_COURS`, `TRAITE` et `RESOLU`.

## Résultat actuel

La refactorisation est **bien engagée**, mais elle n'est pas encore totale.

Ce qui est propre maintenant :

- un fichier central de tokens existe ;
- le thème MUI dépend des tokens principaux ;
- les cartes dashboard partagent le même style ;
- les cartes progression partagent le même style ;
- plusieurs statuts sont centralisés ;
- les dégradés principaux ne sont plus éparpillés dans les composants déjà migrés ;
- les pages publiques utilisent des variables CSS.

Ce qui reste encore à migrer :

- `CourseDetailPage.tsx`
- `CourseDetailHeader.tsx`
- `ChapterList.tsx`
- `ResourceList.tsx`
- `StudentsPanel.tsx`
- `ResourcesManagementPage.tsx`
- `EnrollmentsManagementPage.tsx`
- `UsersList.tsx`
- `AdminAccountRequestsPage.tsx`
- `ReportsPage.tsx`
- `ReportIssuePage.tsx`
- `StudentProgressPage.tsx`
- `StudentCourseDetailPage.tsx`
- `AppLayout.tsx`
- `SidebarNavigation.tsx`

Ces fichiers contiennent encore des couleurs, bordures ou ombres codées en dur. Ils peuvent être migrés progressivement sans casser l'application.

## Règles pour les prochaines migrations

### 1. Carte blanche standard

Utiliser :

```tsx
sx={{ ...cardSx, p: 2 }}
```

au lieu de :

```tsx
sx={{
  border: "1px solid #e3e7f3",
  borderRadius: 3,
  boxShadow: "0 12px 32px rgba(...)",
}}
```

### 2. Carte dashboard

Utiliser :

```tsx
sx={{ ...dashboardCardSx, p: 2.4 }}
```

### 3. Carte large

Utiliser :

```tsx
sx={{ ...cardLgSx, p: 2.5 }}
```

### 4. Icône dans une carte

Utiliser :

```tsx
sx={iconBoxSx(48, LH_PRIMARY)}
```

ou :

```tsx
sx={{ ...iconBoxSx(48, LH_PRIMARY), borderRadius: "50%" }}
```

### 5. Bouton principal dégradé

Utiliser :

```tsx
sx={gradientBtnSx}
```

ou :

```tsx
sx={{ ...gradientBtnSx, minHeight: 44 }}
```

### 6. Badge de statut

Utiliser une table centralisée :

```tsx
const style = COURSE_STATUS_STYLES[course.statut];

<Chip
  label={statusLabels[course.statut]}
  sx={{
    color: style.color,
    bgcolor: style.bgcolor,
    fontWeight: 750,
  }}
/>
```

## Vérifications effectuées

Après la migration actuelle, les vérifications suivantes passent :

```bash
npm run lint
npm run build
npm test -- --run src/features/progression/components/progressionLevel.test.ts src/features/progression/components/progressionStats.test.ts
npm run test:e2e -- dashboard.spec.ts --grep "affiche le tableau de bord professeur" --project=desktop
```

## Conclusion

La base de refactorisation CSS est maintenant correcte.

L'application ne dépend plus uniquement de styles dispersés dans chaque composant. Les éléments les plus réutilisés commencent à être centralisés dans `tokens.ts`, tandis que `global.css` reste utile pour les pages publiques.

La suite logique est de migrer progressivement les gros écrans restants, en commençant par les composants les plus réutilisés : détails de cours, listes de ressources, utilisateurs, signalements et layout principal.
