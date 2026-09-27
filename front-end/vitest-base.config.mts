import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    // The Pact specs of several HTTP adapters merge their interactions into one pact file;
    // run in parallel, they overwrite each other and interactions get lost.
    fileParallelism: false,
  },
});
