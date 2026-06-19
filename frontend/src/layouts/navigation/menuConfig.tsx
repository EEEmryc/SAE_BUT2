import type { ReactNode } from "react";
import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import AssignmentTurnedInRoundedIcon from "@mui/icons-material/AssignmentTurnedInRounded";
import TrendingUpRoundedIcon from "@mui/icons-material/TrendingUpRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import ReportProblemRoundedIcon from "@mui/icons-material/ReportProblemRounded";
import AdminPanelSettingsRoundedIcon from "@mui/icons-material/AdminPanelSettingsRounded";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import LibraryBooksRoundedIcon from "@mui/icons-material/LibraryBooksRounded";
import FolderRoundedIcon from "@mui/icons-material/FolderRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import BarChartRoundedIcon from "@mui/icons-material/BarChartRounded";
import SettingsRoundedIcon from "@mui/icons-material/SettingsRounded";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import HistoryRoundedIcon from "@mui/icons-material/HistoryRounded";
import HowToRegRoundedIcon from "@mui/icons-material/HowToRegRounded";
import type { UserRole } from "../../features/auth/services/authApi";
import LockResetRoundedIcon from "@mui/icons-material/LockResetRounded";

export type NavigationItem = {
  id: string;
  label: string;
  icon: ReactNode;
  path?: string;
  roles: UserRole[];
  children?: NavigationItem[];
};

const allRoles: UserRole[] = ["ETUDIANT", "PROFESSEUR", "ADMIN"];
const learningRoles: UserRole[] = ["ETUDIANT", "PROFESSEUR"];

export const navigationItems: NavigationItem[] = [
  {
    id: "dashboard",
    label: "Tableau de bord",
    icon: <DashboardRoundedIcon />,
    path: "/dashboard",
    roles: allRoles,
  },
  {
    id: "courses",
    label: "Cours",
    icon: <MenuBookRoundedIcon />,
    roles: allRoles,
    children: [
      {
        id: "my-courses",
        label: "Mes cours",
        icon: <AutoStoriesRoundedIcon />,
        path: "/dashboard/courses",
        roles: learningRoles,
      },
      {
        id: "catalogue",
        label: "Catalogue",
        icon: <LibraryBooksRoundedIcon />,
        path: "/dashboard/catalogue",
        roles: ["ETUDIANT", "ADMIN"],
      },
      {
        id: "chapters",
        label: "Chapitres",
        icon: <LibraryBooksRoundedIcon />,
        path: "/dashboard/chapters",
        roles: ["PROFESSEUR"],
      },
      {
        id: "resources",
        label: "Ressources",
        icon: <FolderRoundedIcon />,
        path: "/dashboard/resources",
        roles: ["PROFESSEUR"],
      },
    ],
  },
  {
    id: "enrollments",
    label: "Inscriptions",
    icon: <AssignmentTurnedInRoundedIcon />,
    path: "/dashboard/enrollments",
    roles: ["PROFESSEUR"],
  },
  {
    id: "progress",
    label: "Progression",
    icon: <TrendingUpRoundedIcon />,
    path: "/dashboard/progress",
    roles: learningRoles,
  },
  {
    id: "messaging",
    label: "Messagerie",
    icon: <ForumRoundedIcon />,
    path: "/dashboard/messages",
    roles: allRoles,
  },
  {
    id: "report-issue",
    label: "Signalements",
    icon: <ReportProblemRoundedIcon />,
    roles: learningRoles,
    children: [
      {
        id: "report-issue-create",
        label: "Signaler un problème",
        icon: <SendRoundedIcon />,
        path: "/dashboard/report-issue",
        roles: learningRoles,
      },
      {
        id: "report-issue-mine",
        label: "Mes signalements",
        icon: <HistoryRoundedIcon />,
        path: "/dashboard/my-reports",
        roles: learningRoles,
      },
    ],
  },
  {
    id: "reports",
    label: "Signalements",
    icon: <ReportProblemRoundedIcon />,
    path: "/dashboard/reports",
    roles: ["ADMIN"],
  },
  {
    id: "settings",
    label: "Paramètres",
    icon: <SettingsRoundedIcon />,
    roles: allRoles,
    children: [
      {
        id: "settings-password",
        label: "Modifier le mot de passe",
        icon: <LockResetRoundedIcon />,
        path: "/dashboard/settings",
        roles: allRoles,
      },
    ],
  },
  {
    id: "administration",
    label: "Administration",
    icon: <AdminPanelSettingsRoundedIcon />,
    roles: ["ADMIN"],
    children: [
      {
        id: "account-requests",
        label: "Demandes de compte",
        icon: <HowToRegRoundedIcon />,
        path: "/dashboard/admin/account-requests",
        roles: ["ADMIN"],
      },
      {
        id: "users",
        label: "Utilisateurs",
        icon: <PeopleAltRoundedIcon />,
        path: "/dashboard/admin/users",
        roles: ["ADMIN"],
      },
    ],
  },
];

export function getNavigationForRole(role: UserRole) {
  return navigationItems
    .filter((item) => item.roles.includes(role))
    .map((item) => ({
      ...item,
      children: item.children?.filter((child) => child.roles.includes(role)),
    }))
    .filter((item) => item.path || (item.children && item.children.length > 0));
}

export const roleLabels: Record<UserRole, string> = {
  ETUDIANT: "Étudiant",
  PROFESSEUR: "Professeur",
  ADMIN: "Administrateur",
};