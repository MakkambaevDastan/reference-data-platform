Ниже представлена полная документация API и спецификация для **Reference Data Platform**.

Эта документация основана на утвержденной архитектуре:
1.  **Admin API**: Для управления данными (CRUD), валидации и аудита. Использует структуру `common_content` + `translations` (List).
2.  **Lookup API**: Для публичного доступа. Максимально кэшируемый, быстрый. Использует структуру `i18n` (Map).

---

# 📚 Reference Data Platform API Documentation

## 1. Общие сведения

### Формат данных
*   Все запросы и ответы используют `application/json`.
*   Даты передаются в формате **ISO 8601** (`2025-12-17T10:00:00Z`).

### Пагинация
Методы, возвращающие списки, поддерживают параметры:
*   `page`: Номер страницы (начиная с 0).
*   `size`: Размер страницы (по умолчанию 20).
*   `sort`: Поле для сортировки (например, `createdAt,desc`).

### Версионирование
Базовый путь API: `/api/v1`

---

## 2. Модели данных (Core Models)

### `Translation` (Объект перевода)
Используется во всех запросах Admin API.
```json
{
  "locale": "ru",
  "value": "Значение на русском"
}
```

### `ItemUpsertRequest` (Создание/Обновление элемента)
```json
{
  "ref_key": "USA",
  "common_content": {
    "flag_url": "https://flags.com/usa.png",
    "iso_numeric": 840
  },
  "translations": [
    { "locale": "en", "value": "United States" },
    { "locale": "ru", "value": "США" }
  ]
}
```

---

## 3. 🛡️ Admin API (Management)
**Base URL:** `/api/v1/admin`

Предназначен для бэк-офиса. Возвращает полные данные, включая статус, аудит (`createdBy`, `updatedAt`) и историю версий.
**Контроллеры используют:** `@JsonView(Views.Internal.class)` или `Views.Audit.class`.

### 3.1. Управление Справочниками (Definitions)

#### Получить список справочников
`GET /definitions`
*   **Параметры:** `page`, `size`, `search` (по коду или названию).
*   **Ответ:** `PagedDefinitionResponse`

#### Создать справочник
`POST /definitions`
*   **Тело:**
    ```json
    {
      "code": "COUNTRY",
      "translations": [
        { "locale": "ru", "value": "Страны" },
        { "locale": "en", "value": "Countries" }
      ],
      "schema": {
        "type": "object",
        "properties": {
          "iso_numeric": { "type": "integer" }
        }
      }
    }
    ```

#### Получить детали справочника
`GET /definitions/{code}`
*   Возвращает текущую **активную** схему и метаданные.

#### Управление версиями схем
*   `GET /definitions/{code}/versions` — История изменений структуры.
*   `POST /definitions/{code}/versions` — Создать черновик новой схемы.
*   `PATCH /definitions/{code}/versions/{version}/publish` — Сделать версию активной.

---

### 3.2. Управление Данными (Items)

**Base URL:** `/api/v1/admin/definitions/{code}/items`

#### Поиск записей
`GET /`
*   **Параметры:**
    *   `q`: Поисковая строка (ищет по ключу или значению перевода).
    *   `page`, `size`.
*   **Ответ:** `PagedDefinitionResponse<ReferenceItemResponse>`
    ```json
    {
      "content": [
        {
          "code": "COUNTRY",
          "ref_key": "USA",
          "status": "ACTIVE",
          "translations": [...],
          "created_by": "admin",
          "created_at": "2025-01-01T12:00:00Z"
        }
      ],
      "totalElements": 1,
      "definition": { ... } // Метаданные справочника
    }
    ```

#### Создать / Обновить запись (Upsert)
`POST /`
*   Валидирует `common_content` по активной JSON-схеме.
*   Если `ref_key` существует — обновляет, иначе создает.
*   **Тело:** `ItemUpsertRequest`

#### Пакетная загрузка (Import)
`POST /batch`
*   Используется для начальной миграции или импорта из Excel.
*   **Тело:** Массив `[ ItemUpsertRequest, ItemUpsertRequest, ... ]`
*   **Логика:** Выполняется в одной транзакции.

#### Получить одну запись (Full)
`GET /{refKey}`
*   Возвращает полную карточку со всеми системными полями.

#### Удалить запись
`DELETE /{refKey}`
*   **Soft Delete:** Устанавливает `status = ARCHIVED` или заполняет `deleted_at`.

---

## 4. 🚀 Lookup API (Public Access)
**Base URL:** `/api/v1/lookup`

Предназначен для клиентов (Frontend, Microservices).
**Особенности:**
*   Максимальное кэширование (`ETag`, `Cache-Control`).
*   Оптимизированный формат JSON (Map вместо List).
*   Нет полей аудита.

### 4.1. Получить весь справочник
`GET /{code}`

*   **Headers:**
    *   `If-None-Match`: Хеш предыдущей версии (ETag). Если данные не менялись, вернет `304 Not Modified`.
*   **Query Params:**
    *   `lang` (опционально): Если указан, вернет `i18n` только для этого языка + fallback язык.
*   **Ответ:**
    ```json
    {
      "code": "COUNTRY",
      "i18n": {
        "ru": "Страны",
        "en": "Countries"
      },
      "content": [
        {
          "ref_key": "USA",
          "i18n": {
            "ru": "США",
            "en": "United States"
          },
          "details": {
            "flag_url": "...",
            "iso_numeric": 840
          }
        },
        {
          "ref_key": "DE",
          "i18n": { "ru": "Германия", "en": "Germany" }
        }
      ]
    }
    ```

### 4.2. Получить одну запись
`GET /{code}/{key}`

*   **Ответ:**
    ```json
    {
      "ref_key": "USA",
      "i18n": { "ru": "США", "en": "United States" },
      "details": { "iso_numeric": 840 }
    }
    ```

### 4.3. Пакетный запрос (Multi-Dictionary)
`POST /batch`

Позволяет одним вызовом загрузить все нужные справочники для инициализации UI.

*   **Тело запроса:**
    ```json
    {
      "requests": [
        { "dictionaryCode": "COUNTRY" },
        { "dictionaryCode": "LANGUAGE", "keys": ["ru", "en"] }, // Фильтр по ключам
        { "dictionaryCode": "UI_LABELS" }
      ]
    }
    ```
*   **Ответ:** Список объектов `DictionaryLookupResponse`.

---

## 5. 🛠️ System API
**Base URL:** `/api/v1/admin/system`

#### Сброс кэша
`DELETE /cache/{code}`
*   Принудительно инвалидирует кэш для указанного справочника (например, если обновили данные напрямую в БД).

#### Валидация (Maintenance)
`POST /validate/{code}`
*   Запускает фоновый процесс проверки всех записей справочника на соответствие текущей схеме. Полезно после изменения схемы.

---

## 6. Пример реализации контроллера (Spring Boot)

### LookupController.java

```java
@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    @GetMapping("/{code}")
    public ResponseEntity<DictionaryLookupResponse> getDictionary(
            @PathVariable String code,
            @RequestParam(required = false) String lang,
            WebRequest request) {

        // 1. Проверка ETag (Кэширование)
        String contentHash = lookupService.getHash(code);
        if (request.checkNotModified(contentHash)) {
            return null; // Вернет 304
        }

        // 2. Получение данных
        DictionaryLookupResponse response = lookupService.getDictionary(code, lang);

        return ResponseEntity.ok()
                .eTag(contentHash)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)) // Клиентский кэш
                .body(response);
    }

    @PostMapping("/batch")
    public List<DictionaryLookupResponse> batch(@RequestBody LookupBatchRequest request) {
        return lookupService.getBatch(request);
    }
}
```

### AdminItemController.java

```java
@RestController
@RequestMapping("/api/v1/admin/definitions/{code}/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ReferenceItemService itemService;

    @GetMapping
    @JsonView(Views.Summary.class) // Скрываем тяжелые поля, если список большой
    public PagedDefinitionResponse<ReferenceItemResponse> list(
            @PathVariable String code,
            Pageable pageable) {
        return itemService.findAll(code, pageable);
    }

    @GetMapping("/{key}")
    @JsonView(Views.Internal.class) // Показываем все поля
    public ReferenceItemResponse getDetails(@PathVariable String code, @PathVariable String key) {
        return itemService.findByKey(code, key);
    }

    @PostMapping
    public ReferenceItemResponse upsert(
            @PathVariable String code,
            @RequestBody ItemUpsertRequest request) {
        request.setCode(code);
        return itemService.upsert(request);
    }
}
```