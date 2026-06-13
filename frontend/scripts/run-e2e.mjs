import { spawn } from "node:child_process";
import { createServer } from "vite";

let server;

try {
  await fetch("http://127.0.0.1:5173", {
    signal: AbortSignal.timeout(1_000),
  });
} catch {
  server = await createServer({
    server: {
      host: "127.0.0.1",
      port: 5173,
      strictPort: true,
    },
  });
  await server.listen();
}

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

await server?.close();
process.exit(exitCode);
