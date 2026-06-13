import axios from "axios";

type ApiErrorBody = {
  error?: string;
  message?: string;
  messages?: string[];
};

export function getApiErrorMessage(error: unknown) {
  if (!axios.isAxiosError<ApiErrorBody>(error)) {
    return "Une erreur inattendue est survenue. Veuillez réessayer.";
  }

  if (!error.response) {
    return "Impossible de joindre le serveur. Vérifiez que l’API est démarrée.";
  }

  const body = error.response.data;
  return (
    body?.error ||
    body?.message ||
    body?.messages?.[0] ||
    "La requête n’a pas pu être traitée."
  );
}
