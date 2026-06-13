# LearnHub Frontend

Client web léger de LearnHub, développé avec React, TypeScript, Vite et
Material UI.

## Prérequis

- Node.js LTS
- API LearnHub disponible sur `http://localhost:8081`

## Installation

```bash
npm install
copy .env.example .env.local
npm run dev
```

Le client est disponible sur `http://localhost:5173`.

## Commandes

```bash
npm run build
npm run lint
npm test
npm run test:e2e
```

## Contrat OpenAPI

Quand le backend est démarré :

```bash
npm run openapi:generate
```

Cette commande génère les types de l’API dans `src/api/generated/`.

## Authentification

- L’access token JWT reste en mémoire.
- Le refresh token est conservé dans `sessionStorage`.
- La session est restaurée au rechargement via `/api/auth/refresh`.
- Les requêtes authentifiées reçoivent automatiquement l’en-tête
  `Authorization: Bearer <token>`.
