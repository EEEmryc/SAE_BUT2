import type {
  ReportCategory,
  ReportStatus,
} from "../api/reportsApi";

export const reportStatusLabels: Record<ReportStatus, string> = {
  NOUVEAU: "Nouveau",
  EN_COURS: "En cours",
  TRAITE: "Traité",
  RESOLU: "Résolu",
};

export const categoryLabels: Record<ReportCategory, string> = {
  CONTENU: "Contenu",
  ACCES: "Accès",
  COMPORTEMENT: "Comportement",
  EVALUATION: "Évaluation",
  TECHNIQUE: "Technique",
  COMPTE: "Compte",
  MESSAGERIE: "Messagerie",
  AUTRE: "Autre",
};
