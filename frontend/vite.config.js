// vite.config.js
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import { fileURLToPath } from 'url'
const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: { '@': path.resolve(__dirname) },
  },
  server: {
    proxy: {
      // ✅ API만 백엔드로
      '/api': { target: 'http://localhost:8080', changeOrigin: true, secure: false },

      // ✅ OAuth2 인가 시작만 백엔드로
      //  (주의: Vite proxy 키는 정규식이 아니라 '경로 prefix' 입니다)
      '/oauth2/authorization': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },

      // ✅ 인가 완료 후 프론트 라우트로 처리
      '/oauth2/redirect': {
        bypass() {
          return '/index.html' // SPA fallback → React Router가 받음
        },
      },

      // ❌ 이 두 개는 삭제 (프론트 라우트여야 함)
      // '/login':  ...
      // '/logout': ...
    },
  },
})
