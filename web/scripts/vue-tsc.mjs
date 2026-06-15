#!/usr/bin/env node
import { createRequire } from 'node:module';
import { pathToFileURL } from 'node:url';

const require = createRequire(import.meta.url);

await import(pathToFileURL(require.resolve('vue-tsc/bin/vue-tsc.js')).href);
