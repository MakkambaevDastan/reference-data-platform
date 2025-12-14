```json
{
  "code": "LANGUAGE",
  "version": 1,
  "is_active": true,
  "valid": true,
  "updated_at": "2025-10-20T10:00:00Z",
  "description": "All languages",
  "i18n": {
    "en": "Languages",
    "ru": "Языки",
    "ky": "Тилдер"
  },
  "data": [
    {
      "key": "ky",
      "version": 1,
      "is_active": true,
      "valid": true,
      "updated_at": "2025-12-28T10:00:00Z",
      "iso_639_1": "ky",
      "iso_639_2": "kir",
      "iso_639_3": "kir",
      "i18n": {
        "en": "Kyrgyz language",
        "ru": "Киргизский язык",
        "ky": "Кыргыз тили"
      }
    }
  ]
}
```
```java

public class Reference {
    @JsonUnwrapped
    private State state;
    private List<ReferenceData> data;
}

public class ReferenceData {
    @JsonUnwrapped
    private State state;
}

@Embeddable
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private String code;
    private int version;
    private boolean active;
    private boolean valid;
    private Instant updatedAt;
}

```
