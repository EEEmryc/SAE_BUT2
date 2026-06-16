// Couleurs de base
export const LH_BORDER = "#e3e7f3";
export const LH_BORDER_LIGHT = "#edf0f7";
export const LH_PRIMARY = "#4f5ff7";
export const LH_PRIMARY_DARK = "#3444dc";
export const LH_PRIMARY_LIGHT = "#7f75ff";
export const LH_PRIMARY_ACCENT = "#5966ef";
export const LH_PRIMARY_SOFT = "#eef0ff";
export const LH_TEXT_SECONDARY = "#596783";
export const LH_SURFACE = "#ffffff";
export const LH_SURFACE_SOFT = "#f8f9ff";
export const LH_PROGRESS_TRACK = "#edf0f8";
export const LH_SUCCESS = "#16864f";
export const LH_SUCCESS_LIGHT = "#e5f8ed";
export const LH_WARNING = "#a35d0a";
export const LH_WARNING_LIGHT = "#fff0d7";
export const LH_ERROR = "#d14343";
export const LH_ERROR_LIGHT = "#fdecef";

// Degrades
export const LH_GRADIENT_BTN = "linear-gradient(110deg,#4056f4,#7458f6)";
export const LH_GRADIENT_APP_SIDEBAR =
  "linear-gradient(165deg, #5364f4 0%, #554bd8 58%, #4438bd 100%)";
export const LH_GRADIENT_HERO_ICON = "linear-gradient(135deg,#5265f5,#7554ee)";
export const LH_GRADIENT_HERO_CARD =
  "radial-gradient(circle at 88% 35%,rgba(99,88,238,.14),transparent 22%),linear-gradient(135deg,#fff,#fbfbff)";

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

// Ombres
export const LH_SHADOW_SM = "0 12px 32px rgba(62,70,130,.05)";
export const LH_SHADOW_MD = "0 16px 42px rgba(54,64,125,0.07)";
export const LH_SHADOW_HERO = "0 18px 44px rgba(62,70,130,.06)";
export const LH_SHADOW_ICON = "0 16px 30px rgba(84,86,232,.28)";

// Statuts : Cours
export const COURSE_STATUS_STYLES = {
  DRAFT: { color: "#6b7280", bgcolor: "#eef0f4" },
  PUBLISHED: { color: "#168b5b", bgcolor: "#e6f8ef" },
  VALIDE: { color: "#2f62d9", bgcolor: "#e9f0ff" },
  ARCHIVE: { color: "#a04e1c", bgcolor: "#fff0df" },
} satisfies Record<string, { color: string; bgcolor: string }>;

// Statuts : Inscription etudiant
export const ENROLLMENT_STATUS_STYLES = {
  VALIDE: { color: "#14794a", bgcolor: "#e3f7eb" },
  EN_ATTENTE: { color: "#a35d0a", bgcolor: "#fff0d7" },
  REFUSE: { color: "#b23c48", bgcolor: "#fdecef" },
  OUVERT: { color: "#4556df", bgcolor: "#eef0ff" },
} satisfies Record<string, { color: string; bgcolor: string }>;

// Statuts : Signalement
export const REPORT_STATUS_STYLES = {
  NOUVEAU: { color: "#c4610b", bgcolor: "#fff0df", dot: "#f08a24" },
  EN_COURS: { color: "#2167c7", bgcolor: "#e8f2ff", dot: "#3a86e8" },
  TRAITE: { color: "#6350c7", bgcolor: "#eeeaff", dot: "#7864e8" },
  RESOLU: { color: "#12834d", bgcolor: "#e5f8ed", dot: "#20b96b" },
} satisfies Record<string, { color: string; bgcolor: string; dot: string }>;

// Statuts : Progression
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
} satisfies Record<string, { label: string; color: string; bgcolor: string }>;

// Couleurs metier pour graphiques/statistiques
export const LH_STAT_COLORS = {
  green: "#20a66a",
  blue: "#4775e8",
  violet: "#7a56e8",
  orange: "#ec8b35",
  warning: "#e58b25",
} as const;

// Patterns sx partages

/** Carte standard : bordure légère + ombre subtile */
export const cardSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3,
  boxShadow: LH_SHADOW_SM,
};

/** Carte large (grille de cours, catalogue) */
export const cardLgSx = {
  border: `1px solid ${LH_BORDER}`,
  borderRadius: 3.5,
  boxShadow: LH_SHADOW_MD,
};

/** Boîte d'icône colorée (taille × taille, fond en teinte légère) */
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

/** Bouton dégradé primaire */
export const gradientBtnSx = {
  color: LH_SURFACE,
  background: LH_GRADIENT_BTN,
};

/** Conteneur de page centré avec max-width */
export const pageContainerSx = {
  maxWidth: 1540,
  mx: "auto",
};

/** Titre principal de page */
export const pageTitleSx = {
  fontSize: { xs: 30, sm: 38 },
  fontWeight: 850,
  letterSpacing: "-0.04em",
};

/** Carte dashboard standard */
export const dashboardCardSx = {
  ...cardSx,
  borderRadius: 3.4,
};

/** Separateur de liste discret */
export const listDividerSx = `1px solid ${LH_BORDER_LIGHT}`;
