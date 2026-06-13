import { spawn } from "node:child_process";
import { createServer } from "vite";

const server = await createServer({
  server: {
    host: "127.0.0.1",
    port: 5173,
  },
});

await server.listen();

const playwright = spawn(
  process.execPath,
  ["node_modules/@playwright/test/cli.js", "test"],
  {
    cwd: process.cwd(),
    stdio: "inherit",
  },
);

const exitCode = await new Promise((resolve) => {
  playwright.on("exit", (code) => resolve(code ?? 1));
});

await server.close();
process.exit(exitCode);
