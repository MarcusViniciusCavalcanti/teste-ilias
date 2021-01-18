package com.zonework.atm.integration;

import com.github.javafaker.Faker;
import com.zonework.atm.application.input.NewTransactionaInput;
import com.zonework.atm.domain.allottee.entity.Allottee;
import com.zonework.atm.domain.allottee.entity.StatusAllotteEnum;
import com.zonework.atm.domain.allottee.repository.AllotteeRepository;
import com.zonework.atm.domain.transactional.entity.StatusTransactionalEnum;
import com.zonework.atm.domain.transactional.entity.TransactionalBalance;
import com.zonework.atm.domain.transactional.entity.TypeTranctional;
import com.zonework.atm.domain.transactional.repository.TransactionalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

@ActiveProfiles("integration")
public class TransactionaActionRestIntegrationTest extends AbstractIntegrationTest {
    private static final String IDENTIFIER_SUCCESS = "transactional/%s/success";
    private static final String IDENTIFIER_ERROR = "transactional/%s/error/%s";
    private static final BigDecimal VALUE = BigDecimal.valueOf(10000.00);

    @Autowired
    private AllotteeRepository allotteeRepository;

    @Autowired
    private TransactionalRepository transactionalRepository;

    private Allottee mockAllottee;

    private Faker faker;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        faker = new Faker(Locale.forLanguageTag("pt-BR"));
        setUp(restDocumentationContextProvider);
        var amoutYears = faker.number().numberBetween(10, 20);

        var allottee = new Allottee();
        allottee.setId(1);
        allottee.setStatus(StatusAllotteEnum.ACTIVE);
        allottee.setName(faker.name().fullName());
        allottee.setRetirementBalance(VALUE);
        allottee.setRetirementValue(VALUE.divide(BigDecimal.valueOf(amoutYears), MathContext.DECIMAL32));
        allottee.setAmoutYears(amoutYears);
        allottee.setCpf(faker.regexify("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"));

        mockAllottee = allotteeRepository.saveAndFlush(allottee);
    }

    @AfterEach
    void tearDown() {
        transactionalRepository.deleteAll();
        allotteeRepository.delete(mockAllottee);
        allotteeRepository.flush();
    }

    @Test
    void shouldCreateNewTransactional() {
        var fields = new ConstrainedFields(NewTransactionaInput.class);
        var input = new NewTransactionaInput();
        input.setType("INCREMENT");
        input.setId(mockAllottee.getId());
        input.setValue(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100)));

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "create"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                requestFields(
                    fields.withPath("value").type(JsonFieldType.NUMBER).description("Valor que será processado"),
                    fields.withPath("id").type(JsonFieldType.NUMBER).description("Id do beneficiário"),
                    fields.withPath("type").type(JsonFieldType.STRING).description("Tipo de operação")
                ),
                responseFields(
                    fieldWithPath("value").type(JsonFieldType.NUMBER).description("Valor do beneficio que foi processado"),
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Id da transação"),
                    fieldWithPath("idAllottee").type(JsonFieldType.NUMBER).description("Id do beneficiário"),
                    fieldWithPath("date").type(JsonFieldType.STRING).description("Data do processamento da transação"),
                    fieldWithPath("transactionalType").type(JsonFieldType.STRING).description("Tipo da transação"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("Status da transação")
                )
            ))
            .body(input)
            .when()
            .port(port)
            .post("/transactional")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value());

    }

    @Test
    void shouldReturnAllTransactionaByAllotteeId() {
        var status = StatusTransactionalEnum.values();
        var types = TypeTranctional.values();
        var list = IntStream.range(0, 5)
            .mapToObj(index -> {
                var transactional = new TransactionalBalance();
                transactional.setAllottee(mockAllottee);
                transactional.setStatus(status[faker.number().numberBetween(0, status.length - 1)]);
                transactional.setType(types[faker.number().numberBetween(0, types.length - 1)]);
                transactional.setValue(BigDecimal.TEN);
                transactional.setDate(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 10)));

                return transactional;
            }).collect(Collectors.toList());

        transactionalRepository.saveAll(list);
        transactionalRepository.flush();

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "page"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(parameterWithName("idAllottee").description("Id do Beneficiário")),
                requestParameters(
                    parameterWithName("page")
                        .description("Número da página, inicia em 0, padrdão 0")
                        .optional(),
                    parameterWithName("size")
                        .description("Número número de elemetos por página, padrão é 5")
                        .optional(),
                    parameterWithName("sort")
                        .description("Ordenação que será retornado, ex. date,DESC, caso a direção da ordenação não for especificada o padrão será ASC")
                        .optional()
                ),
                responseFields(
                    fieldWithPath("content").type(JsonFieldType.ARRAY).description("conteúdo da página"),
                    fieldWithPath("content[].id").type(JsonFieldType.NUMBER).ignored(),
                    fieldWithPath("content[].value").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].idAllottee").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].date").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].transactionalType").type(JsonFieldType.NUMBER).ignored(),
                    fieldWithPath("content[].status").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("total de elementos"),
                    fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("total de páginas"),
                    fieldWithPath("last").type(JsonFieldType.BOOLEAN)
                        .description("Define se a página retornada é a última"),
                    fieldWithPath("number").type(JsonFieldType.NUMBER).description("Número da página"),
                    fieldWithPath("sort").type(JsonFieldType.OBJECT).description("Objeto representa a ordenação"),
                    fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request foi ordenada ou não [true] sim, [false] não"),
                    fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request não foi ordenada ou não [true] sim, [false] não"),
                    fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN)
                        .description("Define se na request foi encontrado dados de ordenação"),
                    fieldWithPath("size").type(JsonFieldType.NUMBER).description("Quantidade de elementos"),
                    fieldWithPath("first").type(JsonFieldType.BOOLEAN)
                        .description("Se a página retornada é a primeira ou não [true] sim, [false] não"),
                    fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("Número de elementos paginados"),
                    fieldWithPath("empty").type(JsonFieldType.BOOLEAN)
                        .description("Se a página retornada está vazia ou não, [true] sim, [false] não"),
                    fieldWithPath("pageable").type(JsonFieldType.OBJECT)
                        .description("Objeto que representa os dados da página"),
                    fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT)
                        .description("Objeto que representa os dados da ordenação"),
                    fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("Offset da página"),
                    fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("Número da página"),
                    fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER)
                        .description("Quantidade de elementos da página"),
                    fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request foi paginada ou não [true] sim, [false] não"),
                    fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request foi paginada ou não [true] sim, [false] não"),
                    fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request foi ordenada ou não [true] sim, [false] não"),
                    fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                        .description("Define se a request não foi ordenada ou não [true] sim, [false] não"),
                    fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                        .description("Define se na request foi encontrado dados de ordenação")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .when()
            .queryParam("size", 2)
            .queryParam("page", 0)
            .queryParam("sort", "date,DESC")
            .get("/transactional/{idAllottee}", mockAllottee.getId())
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnAllAllotteeRegisterInSystem() {
        var status = StatusTransactionalEnum.values();
        var types = TypeTranctional.values();
        var list = IntStream.range(0, 5)
            .mapToObj(index -> {
                final BigDecimal value = BigDecimal.valueOf(1000000.00);
                var transactional = new TransactionalBalance();

                transactional.setAllottee(mockAllottee);
                transactional.setStatus(status[faker.number().numberBetween(0, status.length - 1)]);
                transactional.setType(types[faker.number().numberBetween(0, types.length - 1)]);
                transactional.setValue(value);
                transactional.setDate(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 10)));

                return transactional;
            }).collect(Collectors.toList());

        transactionalRepository.saveAll(list);
        transactionalRepository.flush();

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "allottee"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(parameterWithName("id").description("Id do Beneficiário")),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Nome cadastrado"),
                    fieldWithPath("cpf").type(JsonFieldType.STRING).description("Cpf cadastrado"),
                    fieldWithPath("amountYears").type(JsonFieldType.NUMBER)
                        .description("tempo em anos do benefício cadastrado"),
                    fieldWithPath("retirementValue").type(JsonFieldType.NUMBER)
                        .description("Valor do benefício por mês pelo ano estipulado"),
                    fieldWithPath("retirement").type(JsonFieldType.NUMBER).description("Saldo do beneficio"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("Status do beneficiário")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .when()
            .get("/transactional/allottee/{id}", mockAllottee.getId())
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldReturnNotFoundWhenAllotteeNotFound() {
        var input = new NewTransactionaInput();
        input.setType("INCREMENT");
        input.setId(faker.number().numberBetween(10, 100));
        input.setValue(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100)));

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "create", "not-found"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                responseFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("Tittulo do erro"),
                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                    fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Momento de retorno do erro"),
                    fieldWithPath("path").type(JsonFieldType.STRING).description("path da request que gerou o erro"),
                    fieldWithPath("error").type(JsonFieldType.VARIES).description("Descrição do error")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .body(input)
            .when()
            .post("/transactional")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shoulReturnErrorValidate() {
        var input = new NewTransactionaInput();
        input.setType("REVERSAL");
        input.setId(0);
        input.setValue(BigDecimal.valueOf(-1));

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "create", "validation"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                responseFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("Tittulo do erro"),
                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                    fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Momento de retorno do erro"),
                    fieldWithPath("path").type(JsonFieldType.STRING).description("path da request que gerou o erro"),
                    fieldWithPath("error").type(JsonFieldType.VARIES).description("Descrição do error"),
                    fieldWithPath("error.fieldErrors[]").type(JsonFieldType.VARIES).description("Lista de campos invalidos"),
                    fieldWithPath("error.fieldErrors[].message").type(JsonFieldType.STRING).description("Descrição do error"),
                    fieldWithPath("error.fieldErrors[].field").type(JsonFieldType.STRING).description("Campo do error"),
                    fieldWithPath("error.fieldErrors[].rejectedValue").type(JsonFieldType.VARIES).description("Valor que foi rejeitado"),
                    fieldWithPath("error.fieldErrors[].code").type(JsonFieldType.VARIES).description("Código do error")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .body(input)
            .when()
            .post("/transactional")
            .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void shouldReturnBadRequestWhenAllotteeNotActive() {
        var allottee = new Allottee();
        allottee.setId(2);
        allottee.setStatus(StatusAllotteEnum.PROCESS_PENDING);
        allottee.setName(faker.name().fullName());
        allottee.setRetirementBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 10000, 100000)));
        allottee.setAmoutYears(faker.number().numberBetween(10, 20));
        allottee.setCpf(faker.regexify("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"));

        allottee = allotteeRepository.saveAndFlush(allottee);

        var input = new NewTransactionaInput();
        input.setType("INCREMENT");
        input.setId(allottee.getId());
        input.setValue(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 100)));

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "create", "bad-request"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                responseFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("Tittulo do erro"),
                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                    fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Momento de retorno do erro"),
                    fieldWithPath("path").type(JsonFieldType.STRING).description("path da request que gerou o erro"),
                    fieldWithPath("error").type(JsonFieldType.VARIES).description("Descrição do error")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .body(input)
            .when()
            .post("/transactional")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldReturnNotFoundInRequestAllotteWhenAllotteeNotFound() {
        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "allottee", "not-found"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                responseFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("Tittulo do erro"),
                    fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                    fieldWithPath("timestamp").type(JsonFieldType.NUMBER).description("Momento de retorno do erro"),
                    fieldWithPath("path").type(JsonFieldType.STRING).description("path da request que gerou o erro"),
                    fieldWithPath("error").type(JsonFieldType.VARIES).description("Descrição do error")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .port(port)
            .when()
            .get("/transactional/allottee/{id}", 1000)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
