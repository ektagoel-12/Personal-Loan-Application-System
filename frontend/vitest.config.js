import { fileURLToPath } from 'node:url'
import { mergeConfig } from 'vitest/config'
import viteConfig from './vite.config.js'

export default mergeConfig(viteConfig, {
  test: {
    environment: 'jsdom',
    exclude: ['node_modules', '.git', 'e2e/**'],
    root: fileURLToPath(new URL('./', import.meta.url)),
  }
})
