import { Box } from "@mui/material";
import AutoStoriesRoundedIcon from "@mui/icons-material/AutoStoriesRounded";
import FolderOpenRoundedIcon from "@mui/icons-material/FolderOpenRounded";
import ForumRoundedIcon from "@mui/icons-material/ForumRounded";
import MenuBookRoundedIcon from "@mui/icons-material/MenuBookRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import { DashboardChartSection } from "../components/DashboardChartSection";
import { DashboardError, DashboardLoading } from "../components/DashboardState";
import { DashboardHero } from "../components/DashboardHero";
import { DashboardProgressRing } from "../components/DashboardProgressRing";
import { DashboardRecentActivity } from "../components/DashboardRecentActivity";
import { DashboardStatsCards } from "../components/DashboardStatsCards";
import { useProfessorDashboard } from "../hooks/useDashboard";

export function ProfessorDashboard({ firstName }: { firstName: string }) {
  const dashboard = useProfessorDashboard();

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

  return (
    <Box>
      <DashboardHero
        firstName={firstName}
        description="Voici un aperçu de vos cours, de vos étudiants et de leur progression."
      />
      <DashboardStatsCards
        items={[
          {
            id: "courses",
            label: "Cours créés",
            value: data.courses,
            caption: `${data.publishedCourses} publiés`,
            icon: <AutoStoriesRoundedIcon />,
          },
          {
            id: "students",
            label: "Étudiants inscrits",
            value: data.students,
            caption: "inscriptions validées",
            icon: <PeopleAltRoundedIcon />,
            color: "#20a66a",
          },
          {
            id: "chapters",
            label: "Chapitres",
            value: data.chapters,
            caption: "dans vos cours",
            icon: <MenuBookRoundedIcon />,
            color: "#4775e8",
          },
          {
            id: "resources",
            label: "Ressources partagees",
            value: data.resources,
            caption: "documents disponibles",
            icon: <FolderOpenRoundedIcon />,
            color: "#ec8b35",
          },
          {
            id: "messages",
            label: "Messages",
            value: data.messages,
            caption: `${data.unreadMessages} non lus`,
            icon: <ForumRoundedIcon />,
            color: "#7a56e8",
          },
        ]}
      />

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1.2fr 1fr" },
          gap: 2,
        }}
      >
        <DashboardChartSection
          title="Inscriptions par cours"
          items={data.courseMetrics}
          mode="value"
          emptyMessage="Creez un cours pour commencer le suivi."
        />
        <DashboardProgressRing
          value={data.averageProgress}
          title="Progression moyenne"
          description="Moyenne pondérée des étudiants inscrits à vos cours."
          actionPath="/dashboard/progress"
        />
      </Box>

      <Box
        sx={{
          mt: 2,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "1.15fr 1fr" },
          gap: 2,
        }}
      >
        <DashboardChartSection
          title="Cours les plus actifs"
          items={data.courseMetrics.slice(0, 5)}
          mode="value"
        />
        <DashboardRecentActivity items={data.recentActivity} />
      </Box>
    </Box>
  );
}
