export type ProgressLevel =
  | "SANS_CONTENU"
  | "FAIBLE"
  | "MOYEN"
  | "BON"
  | "TERMINE";

export function getProgressLevel(
  percentage: number,
  totalChapters?: number,
): ProgressLevel {
  if (totalChapters === 0) return "SANS_CONTENU";
  if (percentage >= 100) return "TERMINE";
  if (percentage >= 70) return "BON";
  if (percentage >= 40) return "MOYEN";
  return "FAIBLE";
}
