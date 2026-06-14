import { Tab, Tabs } from "@mui/material";
import AutoStoriesOutlinedIcon from "@mui/icons-material/AutoStoriesOutlined";
import FolderOutlinedIcon from "@mui/icons-material/FolderOutlined";
import ForumOutlinedIcon from "@mui/icons-material/ForumOutlined";
import GridViewRoundedIcon from "@mui/icons-material/GridViewRounded";
import GroupsOutlinedIcon from "@mui/icons-material/GroupsOutlined";

export type CourseDetailTab =
  | "overview"
  | "chapters"
  | "resources"
  | "students"
  | "messaging";

type CourseDetailTabsProps = {
  value: CourseDetailTab;
  studentsCount: number;
  onChange: (value: CourseDetailTab) => void;
};

export function CourseDetailTabs({
  value,
  studentsCount,
  onChange,
}: CourseDetailTabsProps) {
  return (
    <Tabs
      value={value}
      onChange={(_, nextValue: CourseDetailTab) => onChange(nextValue)}
      variant="scrollable"
      scrollButtons="auto"
      aria-label="Sections du cours"
      sx={{
        px: { xs: 1, sm: 2 },
        minHeight: 54,
        borderBottom: "1px solid #e5e8f3",
        "& .MuiTab-root": {
          minHeight: 54,
          px: { xs: 1.5, sm: 2.5 },
          fontSize: 13,
          fontWeight: 700,
        },
      }}
    >
      <Tab value="overview" icon={<GridViewRoundedIcon />} iconPosition="start" label="Aperçu" />
      <Tab value="chapters" icon={<AutoStoriesOutlinedIcon />} iconPosition="start" label="Chapitres" />
      <Tab value="resources" icon={<FolderOutlinedIcon />} iconPosition="start" label="Ressources" />
      <Tab
        value="students"
        icon={<GroupsOutlinedIcon />}
        iconPosition="start"
        label={`Étudiants ${studentsCount ? `(${studentsCount})` : ""}`}
      />
      <Tab value="messaging" icon={<ForumOutlinedIcon />} iconPosition="start" label="Messagerie" />
    </Tabs>
  );
}
