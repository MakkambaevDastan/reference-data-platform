// src/config/env.ts
export const ENV = {
    CONFIG_URL: import.meta.env.VITE_CONFIG_URL || '/api/config',
    API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    APP_TITLE: import.meta.env.VITE_APP_TITLE || 'Admin Panel',

    ENABLE_DEVTOOLS: import.meta.env.VITE_ENABLE_DEVTOOLS === 'true',

    API_TIMEOUT: parseInt(import.meta.env.VITE_API_TIMEOUT || '5000', 10),

    MODE: import.meta.env.MODE,
    IS_DEV: import.meta.env.DEV,
    IS_PROD: import.meta.env.PROD,
} as const;