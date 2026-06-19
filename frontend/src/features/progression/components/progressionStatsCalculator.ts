import type { ProfessorStudentProgress } from "../services/progressionApi";

export type ProgressionStatsSummary = {
  averageProgress: number;
  trackedStudents: number;
  activeCourses: number;
  completedPaths: number;
  supportPaths: number;
  completedChapters: number;
  totalChapters: number;
};

export function calculateProgressionStats(
  progressions: ProfessorStudentProgress[],
): ProgressionStatsSummary {
  const measurable = progressions.filter((item) => item.totalChapitres > 0);
  const completedChapters = measurable.reduce(
    (sum, item) =>
      sum + Math.min(item.chapitresTermines, item.totalChapitres),
    0,
  );
  const totalChapters = measurable.reduce(
    (sum, item) => sum + item.totalChapitres,
    0,
  );

  return {
    averageProgress:
      totalChapters === 0
        ? 0
        : Math.round((completedChapters * 100) / totalChapters),
    trackedStudents: new Set(progressions.map((item) => item.eleveId)).size,
    activeCourses: new Set(progressions.map((item) => item.coursId)).size,
    completedPaths: measurable.filter(
      (item) => item.chapitresTermines >= item.totalChapitres,
    ).length,
    supportPaths: measurable.filter((item) => item.pourcentage < 40).length,
    completedChapters,
    totalChapters,
  };
}
