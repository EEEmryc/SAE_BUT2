import { httpClient } from "../../../http/httpClient";
import { adminUsersApi } from "../../admin/users/services/adminUsersApi";
import { coursesApi } from "../../courses/services/coursesApi";
import { messagingApi, type Message } from "../../messaging/services/messagingApi";
import { reportsApi } from "../../reports/services/reportsApi";
import {
  studentLearningApi,
  type CourseProgress,
} from "../../student/services/studentLearningApi";

export type DashboardActivity = {
  id: string;
  title: string;
  description: string;
  date: string;
  kind: "course" | "message" | "progress" | "report" | "user";
};

export type DashboardCourseMetric = {
  id: number;
  title: string;
  value: number;
  detail: string;
};

export type StudentDashboardData = {
  courses: number;
  globalProgress: number;
  completedChapters: number;
  totalChapters: number;
  availableResources: number;
  messages: number;
  unreadMessages: number;
  courseMetrics: DashboardCourseMetric[];
  recentActivity: DashboardActivity[];
};

export type ProfessorDashboardData = {
  courses: number;
  publishedCourses: number;
  students: number;
  chapters: number;
  resources: number;
  averageProgress: number;
  messages: number;
  unreadMessages: number;
  courseMetrics: DashboardCourseMetric[];
  recentActivity: DashboardActivity[];
};

export type AdminDashboardData = {
  totalUsers: number;
  activeUsers: number;
  students: number;
  professors: number;
  administrators: number;
  activeCourses: number;
  reportsNew: number;
  reportsInProgress: number;
  reportsResolved: number;
  mailboxMessages: number;
  unreadMessages: number;
  roleMetrics: DashboardCourseMetric[];
  reportMetrics: DashboardCourseMetric[];
  recentActivity: DashboardActivity[];
};

type AdminStats = {
  totalUsers: number;
  activeCourses: number;
};

function timestamp(value: string | null | undefined) {
  if (!value) return 0;
  const parsed = Date.parse(value);
  return Number.isNaN(parsed) ? 0 : parsed;
}

function sortActivity(items: DashboardActivity[]) {
  return items
    .sort((a, b) => timestamp(b.date) - timestamp(a.date))
    .slice(0, 6);
}

function messageActivity(messages: Message[]): DashboardActivity[] {
  return messages.map((message) => ({
    id: `message-${message.id}`,
    title: message.lu ? "Message reçu" : "Nouveau message",
    description: `${message.expediteurPrenom} ${message.expediteurNom} - ${message.sujet}`,
    date: message.dateEnvoi,
    kind: "message",
  }));
}

function progressActivity(progress: CourseProgress[]): DashboardActivity[] {
  return progress.flatMap((course) =>
    course.details
      .filter((detail) => detail.statut === "TERMINE")
      .map((detail) => ({
        id: `progress-${detail.id}`,
        title: "Chapitre terminé",
        description: `${detail.chapitreTitre ?? "Chapitre"} - ${course.coursTitre}`,
        date: detail.dateFin ?? detail.dateMiseAJour ?? detail.dateDebut ?? "",
        kind: "progress" as const,
      })),
  );
}

export const dashboardApi = {
  async student(): Promise<StudentDashboardData> {
    const [progress, inbox, outbox] = await Promise.all([
      studentLearningApi.getAllProgress(),
      messagingApi.getInbox(),
      messagingApi.getOutbox(),
    ]);

    const totalChapters = progress.reduce(
      (sum, course) => sum + course.totalChapitres,
      0,
    );
    const completedChapters = progress.reduce(
      (sum, course) => sum + course.chapitresTermines,
      0,
    );

    return {
      courses: progress.length,
      globalProgress:
        totalChapters === 0
          ? 0
          : Math.round((completedChapters * 100) / totalChapters),
      completedChapters,
      totalChapters,
      availableResources: progress.reduce(
        (sum, course) => sum + course.totalRessources,
        0,
      ),
      messages: inbox.length + outbox.length,
      unreadMessages: inbox.filter((message) => !message.lu).length,
      courseMetrics: progress
        .map((course) => ({
          id: course.coursId,
          title: course.coursTitre,
          value: course.pourcentageGlobal,
          detail: `${course.chapitresTermines}/${course.totalChapitres} chapitres`,
        }))
        .sort((a, b) => b.value - a.value),
      recentActivity: sortActivity([
        ...progressActivity(progress),
        ...messageActivity(inbox),
      ]),
    };
  },

  async professor(): Promise<ProfessorDashboardData> {
    const [courses, inbox, outbox] = await Promise.all([
      coursesApi.list(),
      messagingApi.getInbox(),
      messagingApi.getOutbox(),
    ]);
    const summaries = await Promise.all(
      courses.map((course) => coursesApi.getSummary(course.id)),
    );

    const students = summaries.reduce((sum, summary) => sum + summary.students, 0);
    const chapters = summaries.reduce((sum, summary) => sum + summary.chapters, 0);
    const resources = summaries.reduce(
      (sum, summary) => sum + summary.resources,
      0,
    );
    const averageProgress = students
      ? Math.round(
          summaries.reduce(
            (sum, summary) =>
              sum + summary.averageProgress * summary.students,
            0,
          ) / students,
        )
      : 0;

    return {
      courses: courses.length,
      publishedCourses: courses.filter((course) =>
        ["PUBLISHED", "VALIDE"].includes(course.statut),
      ).length,
      students,
      chapters,
      resources,
      averageProgress,
      messages: inbox.length + outbox.length,
      unreadMessages: inbox.filter((message) => !message.lu).length,
      courseMetrics: courses
        .map((course, index) => ({
          id: course.id,
          title: course.titre,
          value: summaries[index]?.students ?? 0,
          detail: `${summaries[index]?.averageProgress ?? 0}% progression moyenne`,
        }))
        .sort((a, b) => b.value - a.value),
      recentActivity: sortActivity([
        ...messageActivity(inbox),
        ...courses.map((course) => ({
          id: `course-${course.id}`,
          title: "Cours créé",
          description: course.titre,
          date: course.dateCreation,
          kind: "course" as const,
        })),
      ]),
    };
  },

  async admin(): Promise<AdminDashboardData> {
    const [statsResponse, users, reports, inbox, outbox] = await Promise.all([
      httpClient.get<AdminStats>("/api/admin/stats"),
      adminUsersApi.list(),
      reportsApi.list(),
      messagingApi.getInbox(),
      messagingApi.getOutbox(),
    ]);

    const countRole = (role: string) =>
      users.filter((user) => user.role === role).length;
    const reportCount = (statuses: string[]) =>
      reports.filter((report) => statuses.includes(report.statut)).length;

    return {
      totalUsers: statsResponse.data.totalUsers,
      activeUsers: users.filter((user) => user.statut === "ACTIF").length,
      students: countRole("ETUDIANT"),
      professors: countRole("PROFESSEUR"),
      administrators: countRole("ADMIN"),
      activeCourses: statsResponse.data.activeCourses,
      reportsNew: reportCount(["NOUVEAU"]),
      reportsInProgress: reportCount(["EN_COURS", "TRAITE"]),
      reportsResolved: reportCount(["RESOLU"]),
      mailboxMessages: inbox.length + outbox.length,
      unreadMessages: inbox.filter((message) => !message.lu).length,
      roleMetrics: [
        {
          id: 1,
          title: "Étudiants",
          value: countRole("ETUDIANT"),
          detail: "comptes",
        },
        {
          id: 2,
          title: "Professeurs",
          value: countRole("PROFESSEUR"),
          detail: "comptes",
        },
        {
          id: 3,
          title: "Administrateurs",
          value: countRole("ADMIN"),
          detail: "comptes",
        },
      ],
      reportMetrics: [
        {
          id: 1,
          title: "Nouveaux",
          value: reportCount(["NOUVEAU"]),
          detail: "signalements",
        },
        {
          id: 2,
          title: "En traitement",
          value: reportCount(["EN_COURS", "TRAITE"]),
          detail: "signalements",
        },
        {
          id: 3,
          title: "Résolus",
          value: reportCount(["RESOLU"]),
          detail: "signalements",
        },
      ],
      recentActivity: sortActivity([
        ...users.map((user) => ({
          id: `user-${user.id}`,
          title: "Utilisateur créé",
          description: `${user.prenom} ${user.nom} - ${user.role}`,
          date: user.dateCreation ?? "",
          kind: "user" as const,
        })),
        ...reports.map((report) => ({
          id: `report-${report.id}`,
          title: `Signalement ${report.statut.toLowerCase()}`,
          description: `${report.sujet} - ${report.auteurPrenom} ${report.auteurNom}`,
          date: report.dateEnvoi,
          kind: "report" as const,
        })),
      ]),
    };
  },
};
