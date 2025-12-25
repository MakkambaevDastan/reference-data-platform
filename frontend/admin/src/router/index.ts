import { createRouter as createTanstackRouter, createBrowserHistory } from '@tanstack/vue-router'
import { routeTree } from '../routeTree.gen'
import type { AppManifest } from '../config/manifest'
import type { QueryClient } from '@tanstack/vue-query'

export interface RouterContext {
    manifest: AppManifest
    queryClient: QueryClient
}

export function createRouter(context: RouterContext) {
    return createTanstackRouter({
        routeTree,
        history: createBrowserHistory(),
        context,
        defaultPreload: 'intent',
    })
}

declare module '@tanstack/vue-router' {
    interface Register {
        router: ReturnType<typeof createRouter>
    }
}