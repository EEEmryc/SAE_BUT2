import { httpClient } from "../../../../http/httpClient";

export type AppSettings = {
  requestableRoles: string[];
  inscriptionAutoValidation: boolean;
};

export type UpdateAppSettingsPayload = {
  requestableRoles?: string[];
  inscriptionAutoValidation?: boolean;
};

export const adminSettingsApi = {
  async getSettings() {
    const response = await httpClient.get<AppSettings>("/api/admin/settings");
    return response.data;
  },

  async updateSettings(payload: UpdateAppSettingsPayload) {
    const response = await httpClient.put<AppSettings>(
      "/api/admin/settings",
      payload,
    );
    return response.data;
  },
};
