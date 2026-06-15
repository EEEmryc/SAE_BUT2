export type ProgressLevel = "FAIBLE" | "MOYEN" | "BON" | "TERMINE";

export function getProgressLevel(percentage: number): ProgressLevel {
  if (percentage >= 100) return "TERMINE";
  if (percentage >= 70) return "BON";
  if (percentage >= 40) return "MOYEN";
  return "FAIBLE";
}
