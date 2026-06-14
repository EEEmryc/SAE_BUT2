import { Box } from "@mui/material";
import AdminPanelSettingsRoundedIcon from "@mui/icons-material/AdminPanelSettingsRounded";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import FlagRoundedIcon from "@mui/icons-material/FlagRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import SchoolRoundedIcon from "@mui/icons-material/SchoolRounded";
import { DashboardChartSection } from "../components/DashboardChartSection";
import { DashboardError, DashboardLoading } from "../components/DashboardState";
import { DashboardHero } from "../components/DashboardHero";
import { DashboardProgressRing } from "../components/DashboardProgressRing";
import { DashboardRecentActivity } from "../components/DashboardRecentActivity";
import { DashboardStatsCards } from "../components/DashboardStatsCards";
import { useAdminDashboard } from "../hooks/useDashboard";

export function AdminDashboard({ firstName }: { firstName: string }) {
  const dashboard = useAdminDashboard();

  if (dashboard.isPending) return <DashboardLoading />;
  if (dashboard.isError) {
    return (
      <DashboardError
        error={dashboard.error}
        onRetry={() => void dashboard.refetch()}
      />
    );
  }

  const data = dashboard.data;
  const resolvedTotal =
    data.reportsNew + data.reportsInProgress + data.reportsResolved;
  const resolutionRate = resolvedTotal
    ? Math.round((data.reportsResolved * 100) / resolvedTotal)
    : 0;

  return (
    <Box>
      <DashboardHero
        firstName={firstName}
        description="Supervisez les utilisateurs, les cours et les signalements de la plateforme."
      />
      <DashboardStatsCards
        items={[
          {
            id: "users",
            label: "Utilisateurs totaux",
            value: data.totalUsers,
            caption: `${data.activeUsers} comptes actifs`,
            icon: <PeopleAltRoundedIcon />,
          },
          {
            id: "professors",
            label: "Professeurs",
            value: data.professors,
            caption: "comptes enseignants",
            icon: <SchoolRoundedIcon />,
            color: "#20a66a",
          },
          {
            id: "students",
            label: "Etudiants",
            value: data.students,
            caption: "comptes apprenants",
            icon: <AdminPanelSettingsRoundedIcon />,
            color: "#4775e8",
          },
          {
            id: "courses",
            label: "Cours actifs",
            value: data.activeCourses,
            caption: "cours publies",
            icon: <AutoStoriesRoundedIcon />,
            color: "#7a56e8",
          },
          {
            id: "reports",
            label: "Signalements ouverts",
            value: data.reportsNew + data.reportsInProgress,
            caption: `${data.reportsNew} nouveaux`,
            icon: <FlagRoundedIcon />,
            color: "#ec7135",
          },
        ]}
      />

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr 1fr" },
          gap: 2,
        }}
      >
        <DashboardChartSection
          title="Répartition des rôles"
          items={data.roleMetrics}
          mode="value"
        />
        <DashboardChartSection
          title="Suivi des signalements"
          items={data.reportMetrics}
          mode="value"
        />
        <DashboardProgressRing
          value={resolutionRate}
          title="Taux de résolution"
          description={`${data.reportsResolved} signalements résolus sur ${resolvedTotal}.`}
          actionPath="/dashboard/reports"
        />
      </Box>

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1.25fr 1fr" },
          gap: 2,
        }}
      >
        <DashboardRecentActivity items={data.recentActivity} />
        <DashboardStatsCards
          items={[
            {
              id: "admins",
              label: "Administrateurs",
              value: data.administrators,
              caption: "comptes de supervision",
              icon: <AdminPanelSettingsRoundedIcon />,
            },
            {
              id: "mailbox",
              label: "Messages de ma boîte",
              value: data.mailboxMessages,
              caption: `${data.unreadMessages} non lus`,
              icon: <ForumRoundedIcon />,
              color: "#7a56e8",
            },
          ]}
        />
      </Box>
    </Box>
  );
}
