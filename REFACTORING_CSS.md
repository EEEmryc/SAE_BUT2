# Refactoring CSS / Styles — LearnHub Frontend

## Contexte

Le frontend de LearnHub utilise **Material-UI (MUI)** comme librairie de composants.  
Contrairement à un projet CSS classique, il n'y avait pas de dizaines de fichiers `.css` séparés :  
tout le style passait par la prop `sx` de MUI, directement dans les composants TSX.

Le problème identifié après audit : des **dizaines de valeurs hex hardcodées** répétées dans 50+ fichiers,
sans aucun point de vérité commun. La moindre modification de couleur demandait de chercher-remplacer
dans tout le projet.

---

## Problèmes avant refactoring

### 1. Couleurs de bordure : 10 variantes du même gris-bleu

```tsx
// Dans DashboardStatsCards.tsx
border: "1px solid #e2e6f4"

// Dans StudentCourseCard.tsx
border: "1px solid #e0e5f3"

// Dans SummaryCard.tsx
border: "1px solid #e1e6f2"

// Dans CoursesPage.tsx (2 endroits différents)
border: "1px solid #e3e7f3"
```

→ Toutes ces valeurs représentaient la même couleur de bordure, avec de légers écarts involontaires.

---

### 2. Ombres : 9 variantes de la même ombre subtile

```tsx
"0 12px 32px rgba(62,70,130,.05)"    // DashboardStatsCards
"0 16px 42px rgba(54,64,125,0.07)"   // CoursesPage
"0 14px 34px rgba(49,61,125,.07)"    // StudentCourseCard
"0 12px 36px rgba(52,64,125,0.045)"  // CourseDetailPage
```

---

### 3. Statuts dupliqués dans chaque composant

```tsx
// Dans CoursesPage.tsx — définition locale
const statusColors = {
  DRAFT: { color: "#6b7280", bgcolor: "#eef0f4" },
  PUBLISHED: { color: "#168b5b", bgcolor: "#e6f8ef" },
  ...
};

// Dans ReportStatusChip.tsx — autre définition locale
const statusStyles = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  ...
};
```

Si on changeait la couleur d'un statut, il fallait retrouver toutes ses occurrences à la main.

---

### 4. Pattern "boîte d'icône" copié-collé partout

```tsx
// DashboardStatsCards.tsx
sx={{
  width: 50,
  height: 50,
  display: "grid",
  placeItems: "center",
  flexShrink: 0,
  color: someColor,
  bgcolor: `${someColor}14`,
  borderRadius: 2.5,
}}
```

Ce même bloc de 8 propriétés apparaissait dans chaque carte statistique, chaque page de dashboard.

---

### 5. Thème MUI minimaliste — pas de `success` / `warning`

```ts
// Avant : thème sans success/warning → valeurs MUI par défaut (vert/orange génériques)
export const muiTheme = createTheme({
  palette: {
    primary: { main: "#4f5ff7", ... },
    error: { main: "#d14343" },
    // ← pas de success, pas de warning
  },
});
```

Les composants qui avaient besoin de vert ou d'orange devaient hardcoder leurs propres hex.

---

## Ce qui a été fait

### Fichier 1 — `frontend/src/styles/tokens.ts` (nouveau)

Point de vérité unique pour tous les tokens de design.

**Couleurs fondamentales :**
```ts
export const LH_BORDER = "#e3e7f3";       // une seule couleur de bordure
export const LH_PRIMARY = "#4f5ff7";
export const LH_TEXT_SECONDARY = "#596783";
export const LH_GRADIENT_BTN = "linear-gradient(110deg,#4056f4,#7458f6)";
```

**Ombres nommées :**
```ts
export const LH_SHADOW_SM = "0 12px 32px rgba(62,70,130,.05)";
export const LH_SHADOW_MD = "0 16px 42px rgba(54,64,125,0.07)";
```

**Dégradés de cartes cours :**
```ts
export const COURSE_GRADIENTS = [
  "linear-gradient(135deg,#4056f4,#7659f6)",
  "linear-gradient(135deg,#167db7,#42b5d7)",
  "linear-gradient(135deg,#6d4bd8,#a85be5)",
] as const;

export const STUDENT_COURSE_GRADIENTS = [
  "linear-gradient(135deg,#3049a2,#5265e8)",
  "linear-gradient(135deg,#6557dc,#9386ff)",
  "linear-gradient(135deg,#2ba36d,#79d6a0)",
  "linear-gradient(135deg,#e28b2e,#ffc46f)",
] as const;
```

**Tables de statuts centralisées :**
```ts
export const COURSE_STATUS_STYLES = {
  DRAFT:     { color: "#6b7280", bgcolor: "#eef0f4" },
  PUBLISHED: { color: "#168b5b", bgcolor: "#e6f8ef" },
  VALIDE:    { color: "#2f62d9", bgcolor: "#e9f0ff" },
  ARCHIVE:   { color: "#a04e1c", bgcolor: "#fff0df" },
};

export const ENROLLMENT_STATUS_STYLES = {
  VALIDE:    { color: "#14794a", bgcolor: "#e3f7eb" },
  EN_ATTENTE:{ color: "#a35d0a", bgcolor: "#fff0d7" },
  REFUSE:    { color: "#b23c48", bgcolor: "#fdecef" },
  OUVERT:    { color: "#4556df", bgcolor: "#eef0ff" },
};

export const REPORT_STATUS_STYLES = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  EN_COURS:{ color: "#2167c7", bgcolor: "#e8f2ff", dot: "#3a86e8" },
  TRAITE:  { color: "#6350c7", bgcolor: "#eeeaff", dot: "#7864e8" },
  RESOLU:  { color: "#12834d", bgcolor: "#e5f8ed", dot: "#20b96b" },
};
```

**Patterns `sx` partagés :**
```ts
// Carte standard
export const cardSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3,
  boxShadow: LH_SHADOW_SM,
};

// Carte large (grille de cours)
export const cardLgSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3.5,
  boxShadow: LH_SHADOW_MD,
};

// Boîte d'icône — remplace 8 propriétés répétées
export const iconBoxSx = (size: number, color: string) => ({
  width: size, height: size,
  display: "grid", placeItems: "center",
  flexShrink: 0,
  color,
  bgcolor: `${color}14`,
  borderRadius: 2.5,
});

// Bouton dégradé primaire
export const gradientBtnSx = {
  color: "#fff",
  background: LH_GRADIENT_BTN,
};

// Conteneur de page
export const pageContainerSx = { maxWidth: 1540, mx: "auto" };

// Titre principal h1
export const pageTitleSx = {
  fontSize: { xs: 30, sm: 38 },
  fontWeight: 850,
  letterSpacing: "-0.04em",
};
```

---

### Fichier 2 — `frontend/src/theme/muiTheme.ts` (étendu)

**Avant :**
- Pas de `success`, pas de `warning`
- `error` sans variante `light`

**Après :**
```ts
success: {
  main: "#16864f",
  light: "#e5f8ed",   // fond vert clair pour badges
  dark: "#0f5c38",
  contrastText: "#fff",
},
warning: {
  main: "#a35d0a",
  light: "#fff0d7",   // fond orange clair pour badges
  dark: "#7a4208",
  contrastText: "#fff",
},
error: {
  main: "#d14343",
  light: "#fdecef",   // fond rouge clair pour badges
  dark: "#b23c48",
  contrastText: "#fff",
},
```

Ces couleurs correspondent exactement à celles utilisées dans les composants de statut,
ce qui permet d'utiliser `theme.palette.success.light` plutôt qu'un hex hardcodé.

**Ajout :**
```ts
MuiPaper: {
  styleOverrides: {
    root: { backgroundImage: "none" },  // supprime le gradient MUI en dark mode
  },
},
```

---

### Fichier 3 — `frontend/src/styles/global.css` (variables CSS ajoutées)

Le fichier CSS gère les pages publiques (login, demande de compte) qui ne passent pas par MUI.
Les valeurs hardcodées ont été extraites en variables CSS :

```css
:root {
  --lh-primary: #4f5ff7;
  --lh-primary-dark: #3444dc;
  --lh-border: #e3e7f3;
  --lh-border-glass: rgba(220, 225, 244, 0.9);
  --lh-text-secondary: #596783;
  --lh-shadow-login: 0 28px 75px rgba(74, 87, 145, 0.17);
  --lh-shadow-modal: 0 24px 70px rgba(69, 77, 139, 0.11);
  --lh-radius-card: 24px;
  --lh-radius-sm: 20px;
  --lh-success: #18a957;
  --lh-success-ring: rgba(24, 169, 87, 0.12);
  --lh-bg-default: #f6f8ff;
}
```

**Exemple avant / après dans le CSS :**

| Avant | Après |
|-------|-------|
| `border: 1px solid rgba(220, 225, 244, 0.9)` | `border: 1px solid var(--lh-border-glass)` |
| `color: #596783` | `color: var(--lh-text-secondary)` |
| `background: #18a957` | `background: var(--lh-success)` |
| `box-shadow: 0 28px 75px rgba(74, 87, 145, 0.17)` | `box-shadow: var(--lh-shadow-login)` |
| `border-radius: 24px` | `border-radius: var(--lh-radius-card)` |

---

### Composants mis à jour (5 fichiers)

#### `DashboardStatsCards.tsx`
```tsx
// Avant
sx={{
  border: "1px solid #e2e6f4",
  borderRadius: 3.2,
  boxShadow: "0 12px 32px rgba(62,70,130,.05)",
  // ...
}}
// + boîte icône : 8 propriétés hardcodées

// Après
sx={{ ...cardSx, p: 2.1, minHeight: 118, borderRadius: 3.2, ... }}
// + boîte icône
sx={iconBoxSx(50, color)}
```

---

#### `ReportStatusChip.tsx`
```tsx
// Avant : objet local redéfini dans ce fichier
const statusStyles = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  ...
};

// Après : import depuis tokens
import { REPORT_STATUS_STYLES } from "../../../styles/tokens";
const style = REPORT_STATUS_STYLES[status];
```

---

#### `SummaryCard.tsx`
```tsx
// Avant
sx={{ p: 2, border: "1px solid #e1e6f2", borderRadius: 2.5 }}
// couleur d'icône : "#5263e8" hardcodée

// Après
sx={{ ...cardSx, p: 2, borderRadius: 2.5 }}
// couleur d'icône : LH_PRIMARY (#4f5ff7) — la vraie couleur primaire du projet
```

---

#### `StudentCourseCard.tsx`
```tsx
// Avant
const gradients = [
  "linear-gradient(135deg,#3049a2,#5265e8)",
  ...
];
// statut: 3 ternaires imbriqués × 2 (color + bgcolor)
color: enrolled ? "#14794a" : pending ? "#a35d0a" : rejected ? "#b23c48" : "#4556df",
bgcolor: enrolled ? "#e3f7eb" : pending ? "#fff0d7" : rejected ? "#fdecef" : "#eef0ff",

// Après
import { STUDENT_COURSE_GRADIENTS, ENROLLMENT_STATUS_STYLES, cardLgSx, gradientBtnSx } from "...";
const enrollmentKey = enrolled ? "VALIDE" : pending ? "EN_ATTENTE" : rejected ? "REFUSE" : "OUVERT";
const statusStyle = ENROLLMENT_STATUS_STYLES[enrollmentKey];
// → style.color et style.bgcolor, lisibles et centralisés
```

---

#### `CoursesPage.tsx`
```tsx
// Avant : 5 valeurs dupliquées ou locales
sx={{ maxWidth: 1540, mx: "auto" }}
sx={{ fontSize: { xs: 30, sm: 38 }, fontWeight: 850, letterSpacing: "-0.04em" }}
border: "1px solid #e3e7f3"
background: "linear-gradient(110deg,#4056f4,#7458f6)"
const statusColors = { DRAFT: {...}, ... }  // objet local

// Après
import { pageContainerSx, pageTitleSx, cardSx, cardLgSx, gradientBtnSx, COURSE_STATUS_STYLES, COURSE_GRADIENTS } from "...";
```

---

## Résumé des bénéfices

| Problème | Avant | Après |
|----------|-------|-------|
| Couleur de bordure | ~10 hex différents | 1 constante `LH_BORDER` |
| Ombres | 9 variantes | 2 nommées (`LH_SHADOW_SM`, `LH_SHADOW_MD`) |
| Statuts cours | Objet redéfini dans chaque composant | `COURSE_STATUS_STYLES` centralisé |
| Statuts inscription | 6 ternaires imbriqués | `ENROLLMENT_STATUS_STYLES[key]` |
| Statuts signalement | Objet local dans ReportStatusChip | `REPORT_STATUS_STYLES` importé |
| Boîte d'icône | 8 props copiées-collées | `iconBoxSx(size, color)` |
| Bouton dégradé | Hex répétés | `gradientBtnSx` |
| Conteneur de page | `maxWidth: 1540, mx: "auto"` répété | `pageContainerSx` |
| Titre h1 | 3 props répétées | `pageTitleSx` |
| Thème MUI | Pas de success/warning | Palette complète avec variantes `light` |
| CSS global | Hex hardcodés | Variables CSS `--lh-*` |

---

## Comment migrer les autres composants

Pour tout composant encore avec des hex hardcodés, le pattern est le même :

```tsx
// 1. Importer depuis tokens
import { cardSx, iconBoxSx, LH_PRIMARY } from "../../../styles/tokens";

// 2. Remplacer les sx répétitifs
sx={{ border: "1px solid #e3e7f3", borderRadius: 3, boxShadow: "0 12px..." }}
// → sx={cardSx}

// 3. Remplacer les boîtes d'icône
sx={{ width: 48, height: 48, display: "grid", placeItems: "center", ... }}
// → sx={iconBoxSx(48, couleur)}

// 4. Remplacer les hex de statut
color: "#168b5b"  // → COURSE_STATUS_STYLES["PUBLISHED"].color
```

Les composants prioritaires restants : `CourseDetailPage.tsx`, `ReportsPage.tsx`,
`MessagingPage.tsx`, `AdminAccountRequestsPage.tsx`, `ProgressionPage.tsx`.
