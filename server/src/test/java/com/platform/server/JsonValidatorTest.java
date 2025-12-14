package com.platform.server;

import com.fasterxml.jackson.core.JsonLocation;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SchemaRegistryConfig;
import com.networknt.schema.SpecificationVersion;
import com.networknt.schema.dialect.Dialects;
import com.networknt.schema.regex.JoniRegularExpressionFactory;
import com.networknt.schema.serialization.DefaultNodeReader;
import com.networknt.schema.utils.JsonNodes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class JsonValidatorTest {

    static void main(String[] args) throws Exception {

        System.out.println("--- Валидация корректных данных ---");
        String validJson = "{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"age\": 30}";

        System.out.println("\n--- Валидация некорректных данных (отсутствует обязательное поле 'email') ---");
        String invalidJsonMissingField = "{\"id\": 2, \"name\": \"Jane\", \"age\": 25}";

        System.out.println("\n--- Валидация некорректных данных (неверный формат email) ---");
        String invalidJsonBadFormat = "{\"id\": 3, \"name\": \"Peter\", \"email\": \"not-an-email\"}";
    }

    private static void example1(){
        /*
         * SchemaRegistryConfig можно опционально использовать для настройки определённых аспектов
         * выполнения валидации.
         *
         * По умолчанию используется реализация регулярных выражений из JDK, которая не полностью
         * соответствует стандарту ECMA 262. GraalJSRegularExpressionFactory.getInstance() предлагает
         * наилучшую совместимость, за ней следует JoniRegularExpressionFactory.getInstance(),
         * но обе требуют дополнительных опциональных зависимостей.
         */
        SchemaRegistryConfig schemaRegistryConfig = SchemaRegistryConfig.builder()
                .regularExpressionFactory(JoniRegularExpressionFactory.getInstance()).build();

        /*
         * Это создаёт реестр схем, который поддерживает все стандартные диалекты для
         * кросс-диалектной валидации и будет использовать Draft 2020-12 по умолчанию,
         * если в данных схемы не указан $schema. Если в данных схемы указан $schema,
         * то будет использован диалект этой схемы, а указанная здесь версия будет проигнорирована.
         */
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder.schemaRegistryConfig(schemaRegistryConfig)
                        /*
                         * Это создаёт сопоставление, по которому $id, начинающийся с
                         * https://www.example.org/schema, будет перенаправляться на IRI classpath:schema.
                         */
                        .schemaIdResolvers(schemaIdResolvers -> schemaIdResolvers
                                .mapPrefix("https://www.example.com/schema", "classpath:schema")));

        /*
         * Благодаря этому сопоставлению схема будет загружена из classpath по пути
         * classpath:schema/example-main.json. Если в данных схемы не указан $id,
         * то в качестве $id будет использован абсолютный IRI места расположения схемы.
         * Если в данных схемы не указан диалект через $schema, то будет использован
         * диалект по умолчанию, заданный при создании реестра схем.
         */
        Schema schema = schemaRegistry.getSchema(SchemaLocation.of("https://www.example.com/schema/example-main.json"));
        String input = "{\r\n"
                + "  \"main\": {\r\n"
                + "    \"common\": {\r\n"
                + "      \"field\": \"invalidfield\"\r\n"
                + "    }\r\n"
                + "  }\r\n"
                + "}";

        List<Error> errors = schema.validate(input, InputFormat.JSON, executionContext -> {
            /*
             * По умолчанию, начиная с Draft 2019-09, ключевое слово format генерирует только аннотации,
             * а не утверждения (assertions).
             */
            executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true));
        });

    }

    private static void example2(){
        SchemaRegistry schemaRegistry = SchemaRegistry.withDialect(Dialects.getDraft202012());
        /*
         * Благодаря сопоставлению мета-схема для данного диалекта будет загружена
         * из classpath по пути classpath:draft/2020-12/schema.
         */
        Schema schema = schemaRegistry.getSchema(SchemaLocation.of(Dialects.getDraft202012().getId()));
        String input = "{\r\n"
                + "  \"type\": \"object\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"key\": {\r\n"
                + "      \"title\" : \"My key\",\r\n"
                + "      \"type\": \"invalidtype\"\r\n"
                + "    }\r\n"
                + "  }\r\n"
                + "}";
        List<Error> errors = schema.validate(input, InputFormat.JSON, executionContext -> {
            /*
             * По умолчанию, начиная с Draft 2019-09, ключевое слово format генерирует только аннотации,
             * а не утверждения (assertions).
             */
            executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true));
        });
    }

    private static void example3(){
        String schemaData = "{\r\n"
                + "  \"$id\": \"https://schema/myschema\",\r\n"
                + "  \"properties\": {\r\n"
                + "    \"startDate\": {\r\n"
                + "      \"format\": \"date\",\r\n"
                + "      \"minLength\": 6\r\n"
                + "    }\r\n"
                + "  }\r\n"
                + "}";
        String inputData = "{\r\n"
                + "  \"startDate\": \"1\"\r\n"
                + "}";
        SchemaRegistry schemaRegistry = SchemaRegistry.withDialect(Dialects.getDraft202012(),
                builder -> builder.nodeReader(DefaultNodeReader.Builder::locationAware));

        Schema schema = schemaRegistry.getSchema(schemaData, InputFormat.JSON);
        List<Error> errors = schema.validate(inputData, InputFormat.JSON, executionContext ->
            executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        Error format = errors.getFirst();
        JsonLocation formatInstanceNodeTokenLocation = JsonNodes.tokenStreamLocationOf(format.getInstanceNode());
        JsonLocation formatSchemaNodeTokenLocation = JsonNodes.tokenStreamLocationOf(format.getSchemaNode());
        Error minLength = errors.get(1);
        JsonLocation minLengthInstanceNodeTokenLocation = JsonNodes.tokenStreamLocationOf(minLength.getInstanceNode());
        JsonLocation minLengthSchemaNodeTokenLocation = JsonNodes.tokenStreamLocationOf(minLength.getSchemaNode());

        assertEquals("format", format.getKeyword());
        assertEquals("date", format.getSchemaNode().asText());
        assertEquals(5, formatSchemaNodeTokenLocation.getLineNr());
        assertEquals(17, formatSchemaNodeTokenLocation.getColumnNr());
        assertEquals("1", format.getInstanceNode().asText());
        assertEquals(2, formatInstanceNodeTokenLocation.getLineNr());
        assertEquals(16, formatInstanceNodeTokenLocation.getColumnNr());
        assertEquals("minLength", minLength.getKeyword());
        assertEquals("6", minLength.getSchemaNode().asText());
        assertEquals(6, minLengthSchemaNodeTokenLocation.getLineNr());
        assertEquals(20, minLengthSchemaNodeTokenLocation.getColumnNr());
        assertEquals("1", minLength.getInstanceNode().asText());
        assertEquals(2, minLengthInstanceNodeTokenLocation.getLineNr());
        assertEquals(16, minLengthInstanceNodeTokenLocation.getColumnNr());
        assertEquals(16, minLengthInstanceNodeTokenLocation.getColumnNr());
    }
}
