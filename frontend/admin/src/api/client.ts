import axios from 'axios';

export const createClient = (baseUrl: string) => {
    const client = axios.create({ baseURL: baseUrl });

    client.interceptors.request.use(config => {
        config.headers['Accept-Language'] = 'ru';
        return config;
    });

    return client;
};