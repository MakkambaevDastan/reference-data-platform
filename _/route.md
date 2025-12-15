Definition Controller
/api/v1/admin/definitions
    GET     /                                      Список всех справочников
    GET     /{code}?version={version}              Получить схему конкретного справочника
    POST    /{code}/draft                          Создает новую версию схемы `{ "type": "object",  "properties": {...}, "required":[...]}`
    PUT     /{code}/publish/{version}              Переключает трафик и валидацию на указанную версию. Старая версия архивируется
    DELETE  /{code}?version={version}              Удалить справочник

Reference Item Controller
/api/v1/admin/items
    GET     /{code}?page=0&size=20&search=...      Получить список записей
    GET     /{code}/{key}                          Получить одну запись для редактирования
    POST    /{code}/{key}                          Создать новую запись `{ ... }`
    PUT     /{code}/{key}                          Обновить запись `{ ... }`
    PUT     /{code}/{key}/activate/{version}       Активировать указанную версию
    DELETE  /{code}/{key}                          Удалить запись (Soft Delete)

Validate Item Controller
/api/v1/admin/items/validate
    GET     /{code}                                `{"{key}":boolean}`
    GET     /{code}/{key}                          `boolean`

System Controller
/api/v1/admin
    POST    /cache/evict                           Ручной сброс кэша
    POST    /cache/{code}/evict                    Ручной сброс кэша
    GET     /definitions/{code}/template           Генерирует пустой JSON-объект на основе схемы

Global Template Controller
/api/v1/admin/templates
    GET     /                                      Получить список всех шаблонов
    GET     /{code}                                Получить шаблон `{"code":"{code}","version":1,"is_active":true,"updated_at":"ISO 8601","data":[{"key":"{key}","version":1,"is_active":true,"updated_at":"2025-12-28T10:00:00Z"}]}`
    POST    /{code}                                Создать новый шаблон `{ "type": "object",  "properties": {...}, "required":[...]}`
    PUT     /{code}                                Обновить шаблон `{ "type": "object",  "properties": {...}, "required":[...]}`
    DELETE  /{code}                                Удалить шаблон

Public Lookup Controller
/api/v1/lookup
    GET     /{code}                       Получить весь справочник
    GET     /{code}/{key}                 Получить конкретное значение
    POST    /batch                        Пакетный запрос `[{ "code": "COUNTRY" }, { "code": "CURRENCY", "keys": ["USD", "EUR"] }]`


Global Template
```json
{
  "type": "object",
  "properties": {
    "i18n": {
      "type": "object",
      "title": "Мультиязычность",
      "properties": {
        "ab": { "type": "string" },
        "aa": { "type": "string" },
        "af": { "type": "string" },
        "ak": { "type": "string" },
        "sq": { "type": "string" },
        "am": { "type": "string" },
        "ar": { "type": "string" },
        "an": { "type": "string" },
        "hy": { "type": "string" },
        "as": { "type": "string" },
        "av": { "type": "string" },
        "ae": { "type": "string" },
        "ay": { "type": "string" },
        "az": { "type": "string" },
        "bm": { "type": "string" },
        "ba": { "type": "string" },
        "eu": { "type": "string" },
        "be": { "type": "string" },
        "bn": { "type": "string" },
        "bh": { "type": "string" },
        "bi": { "type": "string" },
        "bs": { "type": "string" },
        "br": { "type": "string" },
        "bg": { "type": "string" },
        "my": { "type": "string" },
        "ca": { "type": "string" },
        "ch": { "type": "string" },
        "ce": { "type": "string" },
        "ny": { "type": "string" },
        "zh": { "type": "string" },
        "cv": { "type": "string" },
        "kw": { "type": "string" },
        "co": { "type": "string" },
        "cr": { "type": "string" },
        "hr": { "type": "string" },
        "cs": { "type": "string" },
        "da": { "type": "string" },
        "dv": { "type": "string" },
        "nl": { "type": "string" },
        "dz": { "type": "string" },
        "en": { "type": "string" },
        "eo": { "type": "string" },
        "et": { "type": "string" },
        "ee": { "type": "string" },
        "fo": { "type": "string" },
        "fj": { "type": "string" },
        "fi": { "type": "string" },
        "fr": { "type": "string" },
        "ff": { "type": "string" },
        "gl": { "type": "string" },
        "ka": { "type": "string" },
        "de": { "type": "string" },
        "el": { "type": "string" },
        "gn": { "type": "string" },
        "gu": { "type": "string" },
        "ht": { "type": "string" },
        "ha": { "type": "string" },
        "he": { "type": "string" },
        "hz": { "type": "string" },
        "hi": { "type": "string" },
        "ho": { "type": "string" },
        "hu": { "type": "string" },
        "ia": { "type": "string" },
        "id": { "type": "string" },
        "ie": { "type": "string" },
        "ga": { "type": "string" },
        "ig": { "type": "string" },
        "ik": { "type": "string" },
        "io": { "type": "string" },
        "is": { "type": "string" },
        "it": { "type": "string" },
        "iu": { "type": "string" },
        "ja": { "type": "string" },
        "jv": { "type": "string" },
        "kl": { "type": "string" },
        "kn": { "type": "string" },
        "kr": { "type": "string" },
        "ks": { "type": "string" },
        "kk": { "type": "string" },
        "km": { "type": "string" },
        "ki": { "type": "string" },
        "rw": { "type": "string" },
        "ky": { "type": "string" },
        "kv": { "type": "string" },
        "kg": { "type": "string" },
        "ko": { "type": "string" },
        "ku": { "type": "string" },
        "kj": { "type": "string" },
        "la": { "type": "string" },
        "lb": { "type": "string" },
        "lg": { "type": "string" },
        "li": { "type": "string" },
        "ln": { "type": "string" },
        "lo": { "type": "string" },
        "lt": { "type": "string" },
        "lu": { "type": "string" },
        "lv": { "type": "string" },
        "gv": { "type": "string" },
        "mk": { "type": "string" },
        "mg": { "type": "string" },
        "ms": { "type": "string" },
        "ml": { "type": "string" },
        "mt": { "type": "string" },
        "mi": { "type": "string" },
        "mr": { "type": "string" },
        "mh": { "type": "string" },
        "mn": { "type": "string" },
        "na": { "type": "string" },
        "nv": { "type": "string" },
        "nd": { "type": "string" },
        "ne": { "type": "string" },
        "ng": { "type": "string" },
        "nb": { "type": "string" },
        "nn": { "type": "string" },
        "no": { "type": "string" },
        "ii": { "type": "string" },
        "nr": { "type": "string" },
        "oc": { "type": "string" },
        "oj": { "type": "string" },
        "cu": { "type": "string" },
        "om": { "type": "string" },
        "or": { "type": "string" },
        "os": { "type": "string" },
        "pa": { "type": "string" },
        "pi": { "type": "string" },
        "fa": { "type": "string" },
        "pl": { "type": "string" },
        "ps": { "type": "string" },
        "pt": { "type": "string" },
        "qu": { "type": "string" },
        "rm": { "type": "string" },
        "rn": { "type": "string" },
        "ro": { "type": "string" },
        "ru": { "type": "string" },
        "sa": { "type": "string" },
        "sc": { "type": "string" },
        "sd": { "type": "string" },
        "se": { "type": "string" },
        "sm": { "type": "string" },
        "sg": { "type": "string" },
        "sr": { "type": "string" },
        "gd": { "type": "string" },
        "sn": { "type": "string" },
        "si": { "type": "string" },
        "sk": { "type": "string" },
        "sl": { "type": "string" },
        "so": { "type": "string" },
        "st": { "type": "string" },
        "es": { "type": "string" },
        "su": { "type": "string" },
        "sw": { "type": "string" },
        "ss": { "type": "string" },
        "sv": { "type": "string" },
        "ta": { "type": "string" },
        "te": { "type": "string" },
        "tg": { "type": "string" },
        "th": { "type": "string" },
        "ti": { "type": "string" },
        "bo": { "type": "string" },
        "tk": { "type": "string" },
        "tl": { "type": "string" },
        "tn": { "type": "string" },
        "to": { "type": "string" },
        "tr": { "type": "string" },
        "ts": { "type": "string" },
        "tt": { "type": "string" },
        "tw": { "type": "string" },
        "ty": { "type": "string" },
        "ug": { "type": "string" },
        "uk": { "type": "string" },
        "ur": { "type": "string" },
        "uz": { "type": "string" },
        "ve": { "type": "string" },
        "vi": { "type": "string" },
        "vo": { "type": "string" },
        "wa": { "type": "string" },
        "cy": { "type": "string" },
        "wo": { "type": "string" },
        "xh": { "type": "string" },
        "yi": { "type": "string" },
        "yo": { "type": "string" },
        "za": { "type": "string" },
        "zu": { "type": "string" }
      },
      "additionalProperties": false,
      "required": [
        "en",
        "ru",
        "ky"
      ]
    }
  },
  "required": [
    "i18n"
  ]
}
```





































