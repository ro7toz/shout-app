/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string
  readonly VITE_INSTAGRAM_CLIENT_ID: string
  readonly VITE_INSTAGRAM_REDIRECT_URI: string
  readonly VITE_APP_NAME: string
  readonly VITE_APP_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}