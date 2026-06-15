import { useMemo, useState } from "react";
import {
  Alert,
  Avatar,
  Box,
  Button,
  Chip,
  CircularProgress,
  Divider,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import CheckRoundedIcon from "@mui/icons-material/CheckRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import PersonAddAlt1RoundedIcon from "@mui/icons-material/PersonAddAlt1Rounded";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../../auth/services/apiError";
import {
  useAccountRequests,
  useDecideAccountRequest,
} from "../hooks/useAccountRequests";
import type {
  AccountRequest,
  AccountRequestStatus,
} from "../services/accountRequestsApi";

const statusLabels: Record<AccountRequestStatus, string> = {
  EN_ATTENTE: "En attente",
  ACCEPTEE: "Acceptée",
  REFUSEE: "Refusée",
};

export function AdminAccountRequestsPage() {
  const navigate = useNavigate();
  const requestsQuery = useAccountRequests();
  const decision = useDecideAccountRequest();
  const [status, setStatus] = useState<AccountRequestStatus | "TOUTES">(
    "EN_ATTENTE",
  );
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [message, setMessage] = useState("");

  const requests = useMemo(
    () =>
      (requestsQuery.data ?? []).filter(
        (request) => status === "TOUTES" || request.statut === status,
      ),
    [requestsQuery.data, status],
  );
  const selected =
    requests.find((request) => request.id === selectedId) ?? requests[0] ?? null;

  const accept = async (request: AccountRequest) => {
    const accepted = await decision.mutateAsync({
      id: request.id,
      statut: "ACCEPTEE",
    });
    navigate(
      `/dashboard/admin/users?tab=create&requestId=${accepted.id}`,
    );
  };

  const reject = async (request: AccountRequest) => {
    await decision.mutateAsync({ id: request.id, statut: "REFUSEE" });
    setMessage("La demande a été refusée.");
  };

  return (
    <Box sx={{ maxWidth: 1440, mx: "auto" }}>
      <Typography component="h1" sx={{ fontSize: { xs: 30, sm: 38 }, fontWeight: 850 }}>
        Demandes de création de compte
      </Typography>
      <Typography color="text.secondary" sx={{ mt: 0.5 }}>
        Consultez et traitez les demandes envoyées depuis la page de connexion.
      </Typography>

      {message && <Alert severity="success" sx={{ mt: 2.5 }}>{message}</Alert>}
      {decision.isError && (
        <Alert severity="error" sx={{ mt: 2.5 }}>
          {getApiErrorMessage(decision.error)}
        </Alert>
      )}

      <Box
        sx={{
          mt: 3,
          display: "grid",
          gridTemplateColumns: { xs: "1fr", lg: "minmax(0, 1fr) 430px" },
          gap: 2.5,
          alignItems: "start",
        }}
      >
        <Paper elevation={0} sx={{ border: "1px solid #e4e7f4", borderRadius: 3.5, overflow: "hidden" }}>
          <Box sx={{ p: 2.25, display: "flex", gap: 1.5, flexWrap: "wrap" }}>
            <TextField
              select
              size="small"
              label="Statut"
              value={status}
              onChange={(event) => {
                setStatus(event.target.value as AccountRequestStatus | "TOUTES");
                setSelectedId(null);
              }}
              sx={{ maxWidth: 220 }}
            >
              <MenuItem value="TOUTES">Toutes les demandes</MenuItem>
              <MenuItem value="EN_ATTENTE">En attente</MenuItem>
              <MenuItem value="ACCEPTEE">Acceptées</MenuItem>
              <MenuItem value="REFUSEE">Refusées</MenuItem>
            </TextField>
            <Button
              variant="outlined"
              startIcon={<RefreshRoundedIcon />}
              onClick={() => void requestsQuery.refetch()}
              sx={{ minHeight: 40 }}
            >
              Actualiser
            </Button>
          </Box>
          <Divider />

          {requestsQuery.isPending ? (
            <Box sx={{ minHeight: 320, display: "grid", placeItems: "center" }}>
              <CircularProgress aria-label="Chargement des demandes" />
            </Box>
          ) : requestsQuery.isError ? (
            <Alert severity="error" sx={{ m: 2 }}>
              {getApiErrorMessage(requestsQuery.error)}
            </Alert>
          ) : requests.length === 0 ? (
            <Box sx={{ py: 8, textAlign: "center" }}>
              <PersonAddAlt1RoundedIcon sx={{ fontSize: 50, color: "rgba(79,95,247,.3)" }} />
              <Typography sx={{ mt: 1, fontWeight: 800 }}>
                Aucune demande dans cette catégorie
              </Typography>
            </Box>
          ) : (
            <Box>
              {requests.map((request) => (
                <Box
                  key={request.id}
                  component="button"
                  type="button"
                  onClick={() => setSelectedId(request.id)}
                  sx={{
                    width: "100%",
                    p: 2,
                    display: "flex",
                    gap: 1.5,
                    alignItems: "center",
                    textAlign: "left",
                    border: 0,
                    borderBottom: "1px solid #edf0f7",
                    bgcolor: selected?.id === request.id ? "rgba(79,95,247,.07)" : "#fff",
                    cursor: "pointer",
                  }}
                >
                  <Avatar sx={{ bgcolor: "rgba(79,95,247,.12)", color: "primary.main", fontWeight: 800 }}>
                    {request.prenom[0]}{request.nom[0]}
                  </Avatar>
                  <Box sx={{ minWidth: 0, flex: 1 }}>
                    <Typography sx={{ fontWeight: 800 }}>
                      {request.prenom} {request.nom}
                    </Typography>
                    <Typography noWrap color="text.secondary" sx={{ fontSize: 13 }}>
                      {request.email} · {request.requestedRole === "ETUDIANT" ? "Étudiant" : "Professeur"}
                    </Typography>
                  </Box>
                  <StatusChip status={request.statut} />
                </Box>
              ))}
            </Box>
          )}
        </Paper>

        <Paper elevation={0} sx={{ p: 3, border: "1px solid #e4e7f4", borderRadius: 3.5 }}>
          {selected ? (
            <>
              <Typography sx={{ fontSize: 20, fontWeight: 850 }}>
                {selected.prenom} {selected.nom}
              </Typography>
              <Typography color="text.secondary">{selected.email}</Typography>
              <Box sx={{ mt: 2 }}><StatusChip status={selected.statut} /></Box>
              <Detail label="Type de compte" value={selected.requestedRole === "ETUDIANT" ? "Étudiant" : "Professeur"} />
              <Detail label="Diplôme / Formation" value={selected.formation} />
              <Detail label="Motif de la demande" value={selected.commentaire} />

              {selected.statut === "EN_ATTENTE" && (
                <Box sx={{ mt: 3, display: "flex", gap: 1.25, flexWrap: "wrap" }}>
                  <Button
                    variant="contained"
                    startIcon={<CheckRoundedIcon />}
                    loading={decision.isPending}
                    onClick={() => void accept(selected)}
                    sx={{ color: "#fff" }}
                  >
                    Accepter et créer le compte
                  </Button>
                  <Button
                    variant="outlined"
                    color="error"
                    startIcon={<CloseRoundedIcon />}
                    disabled={decision.isPending}
                    onClick={() => void reject(selected)}
                  >
                    Refuser
                  </Button>
                </Box>
              )}

              {selected.statut === "ACCEPTEE" && (
                <Button
                  variant="contained"
                  startIcon={<PersonAddAlt1RoundedIcon />}
                  onClick={() =>
                    navigate(
                      `/dashboard/admin/users?tab=create&requestId=${selected.id}`,
                    )
                  }
                  sx={{ mt: 3, color: "#fff" }}
                >
                  Ouvrir le formulaire de création
                </Button>
              )}
            </>
          ) : (
            <Typography color="text.secondary">
              Sélectionnez une demande pour afficher son détail.
            </Typography>
          )}
        </Paper>
      </Box>
    </Box>
  );
}

function StatusChip({ status }: { status: AccountRequestStatus }) {
  const styles = {
    EN_ATTENTE: { color: "#a96308", bgcolor: "#fff1d9" },
    ACCEPTEE: { color: "#16834f", bgcolor: "#e4f7ec" },
    REFUSEE: { color: "#b23c48", bgcolor: "#fdecef" },
  }[status];
  return <Chip size="small" label={statusLabels[status]} sx={{ ...styles, fontWeight: 750 }} />;
}

function Detail({ label, value }: { label: string; value: string }) {
  return (
    <Box sx={{ mt: 2.5 }}>
      <Typography color="text.secondary" sx={{ fontSize: 12, fontWeight: 700 }}>
        {label}
      </Typography>
      <Typography sx={{ mt: 0.5, whiteSpace: "pre-wrap", lineHeight: 1.55 }}>
        {value}
      </Typography>
    </Box>
  );
}
