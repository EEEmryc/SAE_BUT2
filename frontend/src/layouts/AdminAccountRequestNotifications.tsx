import { useState } from "react";
import {
  Alert,
  Badge,
  Box,
  CircularProgress,
  IconButton,
  Menu,
  MenuItem,
  Typography,
} from "@mui/material";
import NotificationsNoneRoundedIcon from "@mui/icons-material/NotificationsNoneRounded";
import { useNavigate } from "react-router-dom";
import { getApiErrorMessage } from "../features/auth/services/apiError";
import { usePendingAccountRequests } from "../features/accountRequests/hooks/useAccountRequests";

export function AdminAccountRequestNotifications() {
  const navigate = useNavigate();
  const requests = usePendingAccountRequests();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const items = requests.data ?? [];

  return (
    <>
      <IconButton
        aria-label={`${items.length} demande(s) de compte en attente`}
        onClick={(event) => {
          setAnchor(event.currentTarget);
          void requests.refetch();
        }}
      >
        <Badge color="error" badgeContent={items.length} invisible={items.length === 0}>
          <NotificationsNoneRoundedIcon />
        </Badge>
      </IconButton>
      <Menu
        anchorEl={anchor}
        open={Boolean(anchor)}
        onClose={() => setAnchor(null)}
        slotProps={{ paper: { sx: { mt: 1, width: 380, maxWidth: "calc(100vw - 24px)" } } }}
      >
        <Box sx={{ px: 2, py: 1 }}>
          <Typography sx={{ fontWeight: 850 }}>Demandes de compte</Typography>
          <Typography color="text.secondary" sx={{ fontSize: 12 }}>
            {items.length} demande{items.length > 1 ? "s" : ""} en attente
          </Typography>
        </Box>
        {requests.isFetching && items.length === 0 ? (
          <Box sx={{ display: "grid", placeItems: "center", py: 2 }}>
            <CircularProgress size={24} />
          </Box>
        ) : requests.isError ? (
          <Alert severity="error" sx={{ mx: 1.5, mb: 1.5 }}>
            {getApiErrorMessage(requests.error)}
          </Alert>
        ) : items.length === 0 ? (
          <MenuItem disabled>Aucune nouvelle demande</MenuItem>
        ) : (
          items.slice(0, 5).map((request) => (
            <MenuItem
              key={request.id}
              onClick={() => {
                setAnchor(null);
                navigate("/dashboard/admin/account-requests");
              }}
              sx={{ py: 1.2 }}
            >
              <Box sx={{ minWidth: 0 }}>
                <Typography noWrap sx={{ fontSize: 13, fontWeight: 800 }}>
                  {request.prenom} {request.nom}
                </Typography>
                <Typography noWrap color="text.secondary" sx={{ fontSize: 11.5 }}>
                  {request.email} · {request.requestedRole}
                </Typography>
              </Box>
            </MenuItem>
          ))
        )}
      </Menu>
    </>
  );
}
