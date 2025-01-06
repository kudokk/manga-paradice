import { fileURLToPath, URL } from 'node:url'
import { resolve } from 'path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/

const root = resolve(__dirname, "src/main/resources")
export default defineConfig({
  root,
  publicDir: root,
  build: {
    outDir: resolve(__dirname, "target/classes"),
    rollupOptions: {
      input: { index:  resolve(root, "templates/index.html") },
      output: {
        assetFileNames: `static/[name]-[hash][extname]`,
        chunkFileNames: 'static/js/[name]-[hash].js',
        entryFileNames: 'static/js/[name]-[hash].js',
      }
    }
  },
  plugins: [
    vue(),
    VueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src/main/javascript', import.meta.url)),
      '@pub': fileURLToPath(new URL('./public', import.meta.url))
    }
  }
})
