import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import tanstackRouter from '@tanstack/router-plugin/vite';

export default defineConfig({
    plugins: [
        tanstackRouter({
            target: 'vue',
            addExtensions: true,
        }),
        vue(),
    ],
    resolve: {
        alias: {
            '@': '/src',
        },
    },
    define: {
        __VUE_I18N_LEGACY_API__: false,
        __VUE_I18N_FULL_INSTALL__: true,
        __INTLIFY_PROD_DEVTOOLS__: false,
    },
});
