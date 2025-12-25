import {createApp, h} from 'vue'
import {VueQueryPlugin} from '@tanstack/vue-query'
import App from './App.vue'
import ConfigError from './components/ConfigError.vue'
import {i18n} from "./i18n"
import {ENV} from './config/env'
import {fetchManifest, ManifestKey} from './config/manifest'
import {queryClient} from './api/queryClient'
import {createRouter} from './router'

async function bootstrap() {
    try {
        const manifest = await fetchManifest(ENV.CONFIG_URL)

        const routerInstance = createRouter({manifest, queryClient})

        const app = createApp(App)
        app.provide(ManifestKey, manifest)
        app.use(i18n)

        app.use(routerInstance as any)

        app.use(VueQueryPlugin, {queryClient})
        app.mount('#app')

    } catch (error) {
        console.error('[Bootstrap] Critical Error:', error)
        mountErrorApp()
    }
}


function mountErrorApp() {
    const rootEl = document.getElementById('app')
    if (rootEl) rootEl.innerHTML = ''

    const errorApp = createApp({
        render: () => h(ConfigError)
    })
    errorApp.use(i18n)
    errorApp.mount('#app')
}

bootstrap().then(r => r);
