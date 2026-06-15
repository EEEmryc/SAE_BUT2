import { useMemo, useState } from "react";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  InputAdornment,
  MenuItem,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import PeopleAltRoundedIcon from "@mui/icons-material/PeopleAltRounded";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import { getApiErrorMessage } from "../../../auth/services/apiError";
import type {
  AdminUser,
  UserStatus,
} from "../services/adminUsersApi";
import { useAdminUsers } from "../hooks/useAdminUsers";

const roleLabels = {
  ADMIN: "Administrateur",
  PROFESSEUR: "Professeur",
  ETUDIANT: "Étudiant",
} as const;

const roleStyles = {
  ADMIN: { color: "#6542d9", bgcolor: "#eee8ff" },
  PROFESSEUR: { color: "#2f62d9", bgcolor: "#e9f0ff" },
  ETUDIANT: { color: "#168b5b", bgcolor: "#e6f8ef" },
} as const;

function formatDate(value: string | null) {
  if (!value) {
    return "Non renseignée";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Non renseignée";
  }

  return new Intl.DateTimeFormat("fr-FR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
}

function UserIdentity({ user }: { user: AdminUser }) {
  const initials = `${user.prenom[0] ?? ""}${user.nom[0] ?? ""}`.toUpperCase();

  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1.25, minWidth: 0 }}>
      <Avatar
        sx={{
          width: 38,
          height: 38,
          fontSize: 13,
          fontWeight: 800,
          color: "#4f5ff7",
          bgcolor: "rgba(79,95,247,0.11)",
        }}
      >
        {initials}
      </Avatar>
      <Box sx={{ minWidth: 0 }}>
        <Typography sx={{ fontWeight: 750, lineHeight: 1.25 }}>
          {user.prenom} {user.nom}
        </Typography>
        <Typography
          color="text.secondary"
          noWrap
          sx={{ fontSize: 12.5, mt: 0.25 }}
        >
          {user.email}
        </Typography>
      </Box>
    </Box>
  );
}

function RoleChip({ user }: { user: AdminUser }) {
  return (
    <Chip
      size="small"
      label={roleLabels[user.role]}
      sx={{
        ...roleStyles[user.role],
        borderRadius: 1.5,
        fontWeight: 750,
      }}
    />
  );
}

function StatusChip({ status }: { status: UserStatus }) {
  const active = status === "ACTIF";

  return (
    <Chip
      size="small"
      label={active ? "Actif" : "Inactif"}
      sx={{
        color: active ? "#138650" : "#bf3d4c",
        bgcolor: active ? "#e7f8ef" : "#fdebed",
        borderRadius: 1.5,
        fontWeight: 750,
        "&::before": {
          content: '""',
          width: 7,
          height: 7,
          borderRadius: "50%",
          bgcolor: active ? "#20b96b" : "#e34d5c",
          ml: 0.8,
        },
      }}
    />
  );
}

type UsersListProps = {
  onCreateUser: () => void;
};

export function UsersList({ onCreateUser }: UsersListProps) {
  const usersQuery = useAdminUsers();
  const [search, setSearch] = useState("");
  const [role, setRole] = useState("TOUS");
  const [status, setStatus] = useState("TOUS");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(7);

  const filteredUsers = useMemo(() => {
    const normalizedSearch = search.trim().toLocaleLowerCase("fr");

    return [...(usersQuery.data ?? [])]
      .filter((user) => {
        const searchableValue =
          `${user.nom} ${user.prenom} ${user.email}`.toLocaleLowerCase("fr");
        return !normalizedSearch || searchableValue.includes(normalizedSearch);
      })
      .filter((user) => role === "TOUS" || user.role === role)
      .filter((user) => status === "TOUS" || user.statut === status)
      .sort((left, right) => {
        const leftDate = left.dateCreation
          ? new Date(left.dateCreation).getTime()
          : 0;
        const rightDate = right.dateCreation
          ? new Date(right.dateCreation).getTime()
          : 0;
        return rightDate - leftDate;
      });
  }, [role, search, status, usersQuery.data]);

  const visibleUsers = filteredUsers.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage,
  );

  if (usersQuery.isPending) {
    return (
      <Paper
        elevation={0}
        sx={{
          minHeight: 360,
          display: "grid",
          placeItems: "center",
          border: "1px solid #e5e8f4",
          borderRadius: 3.5,
        }}
      >
        <Box sx={{ textAlign: "center" }}>
          <CircularProgress aria-label="Chargement des utilisateurs" />
          <Typography color="text.secondary" sx={{ mt: 2 }}>
            Chargement des utilisateurs...
          </Typography>
        </Box>
      </Paper>
    );
  }

  if (usersQuery.isError) {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" onClick={() => void usersQuery.refetch()}>
            Réessayer
          </Button>
        }
      >
        Impossible de charger les utilisateurs.{" "}
        {getApiErrorMessage(usersQuery.error)}
      </Alert>
    );
  }

  return (
    <Paper
      data-testid="users-list"
      elevation={0}
      sx={{
        overflow: "hidden",
        border: "1px solid #e5e8f4",
        borderRadius: 3.5,
        boxShadow: "0 18px 50px rgba(54, 64, 125, 0.08)",
      }}
    >
      <Box
        sx={{
          p: { xs: 2, sm: 2.5 },
          display: "grid",
          gridTemplateColumns: {
            xs: "1fr",
            sm: "minmax(240px, 1fr) 180px 180px",
            lg: "minmax(280px, 1fr) 190px 190px auto auto",
          },
          gap: 1.5,
          alignItems: "center",
          borderBottom: "1px solid #eceef7",
        }}
      >
        <TextField
          size="small"
          value={search}
          onChange={(event) => {
            setSearch(event.target.value);
            setPage(0);
          }}
          placeholder="Rechercher un utilisateur..."
          slotProps={{
            htmlInput: {
              "aria-label": "Rechercher un utilisateur",
            },
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRoundedIcon fontSize="small" />
                </InputAdornment>
              ),
            },
          }}
        />
        <TextField
          select
          size="small"
          label="Rôle"
          value={role}
          onChange={(event) => {
            setRole(event.target.value);
            setPage(0);
          }}
        >
          <MenuItem value="TOUS">Tous les rôles</MenuItem>
          <MenuItem value="ADMIN">Administrateurs</MenuItem>
          <MenuItem value="PROFESSEUR">Professeurs</MenuItem>
          <MenuItem value="ETUDIANT">Étudiants</MenuItem>
        </TextField>
        <TextField
          select
          size="small"
          label="Statut"
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            setPage(0);
          }}
        >
          <MenuItem value="TOUS">Tous les statuts</MenuItem>
          <MenuItem value="ACTIF">Actifs</MenuItem>
          <MenuItem value="INACTIF">Inactifs</MenuItem>
        </TextField>
        <Button
          variant="outlined"
          startIcon={<RefreshRoundedIcon />}
          onClick={() => void usersQuery.refetch()}
          sx={{ minHeight: 40, px: 2 }}
        >
          Actualiser
        </Button>
        <Button
          variant="contained"
          startIcon={<AddRoundedIcon />}
          onClick={onCreateUser}
          sx={{
            minHeight: 40,
            px: 2,
            color: "#fff",
            background: "linear-gradient(110deg, #4056f4, #7458f6)",
          }}
        >
          Nouvel utilisateur
        </Button>
      </Box>

      {filteredUsers.length === 0 ? (
        <Box sx={{ py: 8, px: 2, textAlign: "center" }}>
          <PeopleAltRoundedIcon
            sx={{ fontSize: 52, color: "rgba(79,95,247,0.3)" }}
          />
          <Typography sx={{ mt: 1.5, fontWeight: 800 }}>
            Aucun utilisateur trouvé
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Modifiez les filtres ou créez un nouveau compte.
          </Typography>
        </Box>
      ) : (
        <>
          <TableContainer sx={{ display: { xs: "none", md: "block" } }}>
            <Table aria-label="Liste des utilisateurs">
              <TableHead>
                <TableRow sx={{ bgcolor: "#f8f9fe" }}>
                  <TableCell>Utilisateur</TableCell>
                  <TableCell>Nom</TableCell>
                  <TableCell>Prénom</TableCell>
                  <TableCell>Rôle</TableCell>
                  <TableCell>Statut</TableCell>
                  <TableCell>Date de création</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {visibleUsers.map((user) => (
                  <TableRow
                    key={user.id}
                    hover
                    sx={{ "&:last-child td": { borderBottom: 0 } }}
                  >
                    <TableCell sx={{ minWidth: 260 }}>
                      <UserIdentity user={user} />
                    </TableCell>
                    <TableCell>{user.nom}</TableCell>
                    <TableCell>{user.prenom}</TableCell>
                    <TableCell>
                      <RoleChip user={user} />
                    </TableCell>
                    <TableCell>
                      <StatusChip status={user.statut} />
                    </TableCell>
                    <TableCell sx={{ whiteSpace: "nowrap" }}>
                      {formatDate(user.dateCreation)}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box
            sx={{
              display: { xs: "grid", md: "none" },
              gap: 1.5,
              p: 2,
              bgcolor: "#f8f9fe",
            }}
          >
            {visibleUsers.map((user) => (
              <Paper
                key={user.id}
                elevation={0}
                sx={{
                  p: 2,
                  border: "1px solid #e5e8f4",
                  borderRadius: 2.5,
                }}
              >
                <UserIdentity user={user} />
                <Box
                  sx={{
                    mt: 2,
                    display: "flex",
                    flexWrap: "wrap",
                    gap: 1,
                  }}
                >
                  <RoleChip user={user} />
                  <StatusChip status={user.statut} />
                </Box>
                <Typography
                  color="text.secondary"
                  sx={{ mt: 1.5, fontSize: 12.5 }}
                >
                  Créé le {formatDate(user.dateCreation)}
                </Typography>
              </Paper>
            ))}
          </Box>

          <TablePagination
            component="div"
            count={filteredUsers.length}
            page={page}
            onPageChange={(_, nextPage) => setPage(nextPage)}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={(event) => {
              setRowsPerPage(Number(event.target.value));
              setPage(0);
            }}
            rowsPerPageOptions={[7, 10, 25]}
            labelRowsPerPage="Utilisateurs par page"
            labelDisplayedRows={({ from, to, count }) =>
              `${from}-${to} sur ${count}`
            }
            sx={{ borderTop: "1px solid #eceef7" }}
          />
        </>
      )}
    </Paper>
  );
}
