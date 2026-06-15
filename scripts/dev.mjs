import { spawn } from 'node:child_process';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const rootDir = join(__dirname, '..');
const webDir = join(rootDir, 'web');
const serverDir = join(rootDir, 'server');

const target = process.argv[2];

const tasks = {
  'admin-web': {
    cwd: webDir,
    command: 'pnpm dev',
    url: 'http://localhost:5174',
  },
  'admin-server': {
    cwd: serverDir,
    command: [
      'mvn -pl console -am -DskipTests package',
      'if ($LASTEXITCODE -eq 0) { java -jar .\\console\\target\\smallAuthPlatform-1.0.0.jar }',
    ].join('; '),
    url: 'http://localhost:18080/api',
  },
  'h5-web': {
    cwd: webDir,
    command: 'pnpm dev:h5',
    url: 'http://localhost:5173',
  },
  'h5-server': {
    cwd: serverDir,
    command: [
      'mvn -pl auth -am -DskipTests package',
      'if ($LASTEXITCODE -eq 0) { java -jar .\\auth\\target\\smallauthplatform-auth-1.0.0.jar }',
    ].join('; '),
    url: 'http://localhost:18081/auth',
  },
};

const task = tasks[target];

if (!task) {
  console.error('Usage: node ./scripts/dev.mjs <admin-web|admin-server|h5-web|h5-server>');
  process.exit(1);
}

console.log(`[${target}] ${task.command}`);
console.log(`[${target}] ${task.url}`);

const child = spawn('powershell.exe', [
  '-NoProfile',
  '-ExecutionPolicy',
  'Bypass',
  '-Command',
  task.command,
], {
  cwd: task.cwd,
  stdio: 'inherit',
  windowsHide: false,
});

child.on('exit', (code, signal) => {
  if (signal) {
    console.log(`[${target}] stopped by ${signal}`);
    return;
  }
  process.exitCode = code ?? 0;
});
