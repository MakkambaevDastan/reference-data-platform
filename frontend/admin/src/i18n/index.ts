import { createI18n } from 'vue-i18n'

const messages = {
    ru: {
        ui: {
            loading: 'Загрузка конфигурации...',
            error: 'Ошибка загрузки',
            dashboard: 'Панель управления',
            modules: 'Доступные модули',
            back: 'Назад',
            open: 'Открыть',

            error_title: 'Сервис временно недоступен',
            error_description: 'Не удалось загрузить конфигурацию системы. Пожалуйста, проверьте подключение к сети или попробуйте позже.',
            retry: 'Повторить попытку',

            empty_table: 'В этой таблице пока нет записей',
            no_data: '—',
            search: 'Поиск...',
            actions: 'Действия'
        }
    },
    en: {
        ui: {
            loading: 'Loading configuration...',
            error: 'Loading error',
            dashboard: 'Dashboard',
            modules: 'Available modules',
            back: 'Back',
            open: 'Open',

            error_title: 'Service temporarily unavailable',
            error_description: 'Failed to load system configuration. Please check your connection or try again later.',
            retry: 'Retry',

            empty_table: 'No records found in this table',
            no_data: 'N/A',
            search: 'Search...',
            actions: 'Actions'
        }
    }
}

export const i18n = createI18n({
    globalInjection: true,
    locale: 'ru',
    fallbackLocale: 'en',
    messages
})