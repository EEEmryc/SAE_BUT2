import { useState } from "react";
import {
  Box,
  Tab,
  Tabs,
  Typography,
} from "@mui/material";
import PersonAddAlt1RoundedIcon from "@mui/icons-material/PersonAddAlt1Rounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import { CreateUserPanel } from "../components/CreateUserPanel";
import { UsersList } from "../components/UsersList";

export function UsersManagementPage() {
  const [activeTab, setActiveTab] = useState(0);

  return (
    <Box sx={{ maxWidth: 1440, mx: "auto" }}>
      <Typography
        component="h1"
        sx={{
          fontSize: { xs: 29, sm: 38 },
          fontWeight: 850,
          letterSpacing: "-0.04em",
        }}
      >
        Gestion des utilisateurs
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Gérez les comptes des administrateurs, professeurs et étudiants.
      </Typography>

      <Tabs
        value={activeTab}
        onChange={(_, value: number) => setActiveTab(value)}
        aria-label="Sections de gestion des utilisateurs"
        variant="scrollable"
        scrollButtons="auto"
        sx={{
          mt: 3,
          mb: 2.5,
          minHeight: 52,
          borderBottom: "1px solid #dde1f0",
          "& .MuiTab-root": {
            minHeight: 52,
            px: { xs: 2, sm: 3 },
            border: "1px solid #dde1f0",
            borderBottom: 0,
            borderRadius: "12px 12px 0 0",
            mr: 1,
            bgcolor: "rgba(255,255,255,0.58)",
          },
          "& .Mui-selected": {
            bgcolor: "#fff",
            fontWeight: 800,
          },
        }}
      >
        <Tab
          icon={<PeopleAltRoundedIcon />}
          iconPosition="start"
          label="Liste des utilisateurs"
        />
        <Tab
          icon={<PersonAddAlt1RoundedIcon />}
          iconPosition="start"
          label="Créer un utilisateur"
        />
      </Tabs>

      <Box
        role="tabpanel"
        aria-label={
          activeTab === 0
            ? "Liste des utilisateurs"
            : "Créer un utilisateur"
        }
      >
        {activeTab === 0 ? (
          <UsersList onCreateUser={() => setActiveTab(1)} />
        ) : (
          <CreateUserPanel onCancel={() => setActiveTab(0)} />
        )}
      </Box>
    </Box>
  );
}
