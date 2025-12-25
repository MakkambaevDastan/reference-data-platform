import type { InjectionKey } from 'vue';

/**
 * Конфигурация конкретной колонки в таблице
 */
export interface ColumnConfig {
    key: string;
    // Поддержка i18n для заголовков колонок
    // Пример: { ru: 'Имя', en: 'Name' }
    labels: Record<string, string>;
}

/**
 * Конфигурация отдельного модуля (справочника)
 */
export interface ModuleConfig {
    apiPath: string;
    // Переводы названия самого модуля
    // Пример: { ru: { title: 'Пользователи' }, en: { title: 'Users' } }
    translations: Record<string, { title: string }>;
    columns: ColumnConfig[];
}

/**
 * Главный манифест приложения, загружаемый при старте
 */
export interface AppManifest {
    apiBaseUrl: string;
    modules: Record<string, ModuleConfig>;
}

/**
 * Ключ для безопасного внедрения (provide/inject) манифеста во Vue компоненты
 */
export const ManifestKey: InjectionKey<AppManifest> = Symbol('AppManifest');

/**
 * Функция загрузки манифеста с бэкенда
 */
export const fetchManifest = async (url: string): Promise<AppManifest> => {
    const res = await fetch(url);
    if (!res.ok) {
        throw new Error(`Failed to load manifest: ${res.statusText}`);
    }
    return res.json();
};