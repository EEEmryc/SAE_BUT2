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
import type { UserRole } from "../../features/auth/services/authApi";

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
        roles: ["ETUDIANT"],
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
    label: "Signaler un problème",
    icon: <ReportProblemRoundedIcon />,
    path: "/dashboard/report-issue",
    roles: learningRoles,
  },
  {
    id: "reports",
    label: "Signalements",
    icon: <ReportProblemRoundedIcon />,
    path: "/dashboard/reports",
    roles: ["ADMIN"],
  },
  {
    id: "administration",
    label: "Administration",
    icon: <AdminPanelSettingsRoundedIcon />,
    roles: ["ADMIN"],
    children: [
      {
        id: "users",
        label: "Utilisateurs",
        icon: <PeopleAltRoundedIcon />,
        path: "/dashboard/admin/users",
        roles: ["ADMIN"],
      },
      {
        id: "statistics",
        label: "Statistiques",
        icon: <BarChartRoundedIcon />,
        path: "/dashboard/admin/statistics",
        roles: ["ADMIN"],
      },
      {
        id: "settings",
        label: "Paramètres",
        icon: <SettingsRoundedIcon />,
        path: "/dashboard/admin/settings",
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
