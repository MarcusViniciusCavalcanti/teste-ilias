package com.zonework.cadttee.integration.rest;

import com.github.javafaker.Faker;
import com.zonework.cadttee.application.inputs.AllotteInput;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.repository.AllotteeRepository;
import com.zonework.cadttee.domain.allottee.repository.StatusAllotteeRepository;
import com.zonework.cadttee.domain.history.repository.HistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

@ActiveProfiles("integration")
class AllotteActionRestIntegrationTest extends AbstractIntegrationTest {
    private static final String IDENTIFIER_SUCCESS = "allottee/%s/success";
    private static final String IDENTIFIER_ERROR = "allottee/%s/error/%s";

    @Autowired
    private AllotteeRepository allotteeRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private StatusAllotteeRepository statusAllotteeRepository;

    private Faker faker;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        faker = new Faker(Locale.forLanguageTag("pt-BR"));
        setUp(restDocumentationContextProvider);
    }

    @AfterEach
    void cleanDatabase() {
        historyRepository.deleteAll();
        allotteeRepository.deleteAll();
    }

    @Test
    void shoulCreateNewAllottee() {
        var fields = new ConstrainedFields(AllotteInput.class);

        var mockRequest = createMockInput();

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "create"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                requestFields(
                    fields.withPath("name").type(JsonFieldType.STRING).description("Nome do beneficiário"),
                    fields.withPath("cpf").type(JsonFieldType.STRING).description("Cpf do beneficiário"),
                    fields.withPath("email").type(JsonFieldType.STRING).description("Email do beneficiário"),
                    fields.withPath("amountYears").type(JsonFieldType.NUMBER).description("Tempo em anos do beneficio")
                ),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Nome cadastrado"),
                    fieldWithPath("cpf").type(JsonFieldType.STRING).description("Cpf cadastrado"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("E-mail cadastrado"),
                    fieldWithPath("amountYears").type(JsonFieldType.NUMBER)
                        .description("tempo em anos do benefício cadastrado"),
                    fieldWithPath("retirementBalance").type(JsonFieldType.NUMBER)
                        .description("Valor do benefício por mês pelo ano estipulado")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .post("/allottee")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .assertThat()
                .body("name", equalTo(mockRequest.getName()))
                .body("cpf", equalTo(mockRequest.getCpf()))
                .body("email", equalTo(mockRequest.getEmail()))
                .body("amountYears", equalTo(mockRequest.getAmountYears()))
                .body("retirementBalance", equalTo(0));
    }

    @Test
    void shouldActiveWhenExistInDatabaseAndStatusAllowsRegistration() {
        var fields = new ConstrainedFields(AllotteInput.class);
        var statusMock = statusAllotteeRepository.findById(StatusAllotteEnum.EXCLUDED.getStatusID()).get();
        var mockAllottee = createMockAllottee(statusMock.getId());

        var allottee = allotteeRepository.saveAndFlush(mockAllottee);

        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(15);
        mockRequest.setCpf(mockAllottee.getCpf());
        mockRequest.setEmail(faker.internet().emailAddress());
        mockRequest.setName(mockAllottee.getName());

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "active"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                requestFields(
                    fields.withPath("name").type(JsonFieldType.STRING).description("Nome do beneficiário que sera ativado ou criado"),
                    fields.withPath("cpf").type(JsonFieldType.STRING).description("Cpf do beneficiário que sera ativado ou criado"),
                    fields.withPath("email").type(JsonFieldType.STRING).description("Email do beneficiário que sera ativado ou criado"),
                    fields.withPath("amountYears").type(JsonFieldType.NUMBER).description("Tempo em anos do beneficio que sera ativado ou criado")
                ),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Novo Nome cadastrado"),
                    fieldWithPath("cpf").type(JsonFieldType.STRING).description("Novo Cpf cadastrado"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("Novo E-mail cadastrado"),
                    fieldWithPath("amountYears").type(JsonFieldType.NUMBER)
                        .description("tempo em anos do benefício cadastrado"),
                    fieldWithPath("retirementBalance").type(JsonFieldType.NUMBER)
                        .description("Valor do benefício por mês pelo ano estipulado")
                )
            ))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .post("/allottee/active")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .assertThat()
            .body("name", equalTo(mockRequest.getName()))
            .body("cpf", equalTo(mockRequest.getCpf()))
            .body("email", equalTo(mockRequest.getEmail()))
            .body("amountYears", equalTo(mockRequest.getAmountYears()))
            .body("retirementBalance", equalTo(0));
    }

    @Test
    void shouldCreateAllotteeWhenExecuteRequestToActive() {
        var mockRequest = createMockInput();
        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .post("/allottee/active")
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .assertThat()
            .body("name", equalTo(mockRequest.getName()))
            .body("cpf", equalTo(mockRequest.getCpf()))
            .body("email", equalTo(mockRequest.getEmail()))
            .body("amountYears", equalTo(mockRequest.getAmountYears()))
            .body("retirementBalance", equalTo(0));
    }

    @Test
    void shoulUpateAllotte() {
        var fields = new ConstrainedFields(AllotteInput.class);

        var statusMock = statusAllotteeRepository.findById(StatusAllotteEnum.ACTIVE.getStatusID()).get();
        var mockAllottee = createMockAllottee(statusMock.getId());

        var allottee = allotteeRepository.saveAndFlush(mockAllottee);

        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(15);
        mockRequest.setCpf("09876543212");
        mockRequest.setEmail("email_edit@email.com");
        mockRequest.setName("Name Edit");

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "update"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(parameterWithName("id").description("Id do beneficiário a ser atualizado")),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Id do beneficiário"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Nome atualizado"),
                    fieldWithPath("cpf").type(JsonFieldType.STRING).description("Cpf atualizado"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("E-mail atualizado"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("Status do beneficiário no sistema"),
                    fieldWithPath("amountYears").type(JsonFieldType.NUMBER)
                        .description("tempo em anos do benefício atualizado"),
                    fieldWithPath("retirementBalance").type(JsonFieldType.NUMBER)
                        .description("Valor do benefício por mês pelo ano estipulado")
                )
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .put("/allottee/{id}", allottee.getId())
            .then()
            .statusCode(HttpStatus.ACCEPTED.value())
            .assertThat()
            .body("id", equalTo(allottee.getId()))
            .body("name", equalTo(mockRequest.getName()))
            .body("cpf", equalTo(mockRequest.getCpf()))
            .body("email", equalTo(mockRequest.getEmail()))
            .body("amountYears", equalTo(mockRequest.getAmountYears()))
            .body("retirementBalance", equalTo(allottee.getRetirementBalance().floatValue()))
            .body("status", equalTo(allottee.getStatus().getValue()));
    }

    @Test
    void shouldDeleteAllotte() {
        var statusMock = statusAllotteeRepository.findById(StatusAllotteEnum.ACTIVE.getStatusID()).get();
        var mockAllottee = createMockAllottee(statusMock.getId());
        var allottee = allotteeRepository.saveAndFlush(mockAllottee);

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "delete"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(
                    parameterWithName("id").description("Id do beneficiário que será excluido"))
            ))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .port(port)
            .delete("/allottee/{id}", mockAllottee.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());

        var result = allotteeRepository.findById(mockAllottee.getId()).get();
        assertEquals(mockAllottee.getId(), result.getId());
        assertEquals(StatusAllotteEnum.EXCLUDED.getStatusID(), result.getStatus().getId());
    }

    @Test
    void shouldReturnPage() {
        var statusID = StatusAllotteEnum.ACTIVE.getStatusID();
        var list = IntStream.range(0, 10)
            .mapToObj(index -> createMockAllottee(statusID)).collect(Collectors.toList());

        allotteeRepository.saveAll(list);
        allotteeRepository.flush();

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "page"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                requestParameters(
                  parameterWithName("page")
                      .description("Número da página, inicia em 0, padrdão 0")
                      .optional(),
                  parameterWithName("size")
                      .description("Número número de elemetos por página, padrão é 5")
                      .optional(),
                  parameterWithName("sort")
                      .description("Ordenação que será retornado, ex. name,DESC, caso a direção da ordenação não for especificada o padrão será ASC")
                      .optional()
                ),
                responseFields(
                    fieldWithPath("content").type(JsonFieldType.ARRAY).description("conteúdo da página"),
                    fieldWithPath("content[].id").type(JsonFieldType.NUMBER).ignored(),
                    fieldWithPath("content[].name").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].cpf").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].email").type(JsonFieldType.STRING).ignored(),
                    fieldWithPath("content[].retirementBalance").type(JsonFieldType.NUMBER).ignored(),
                    fieldWithPath("content[].amountYears").type(JsonFieldType.NUMBER).ignored(),
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
            .queryParam("sort", "name,DESC")
            .get("/allottee/")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("content.size()", is(2))
            .body("totalElements", is(list.size()))
            .body("numberOfElements", is(2))
            .body("totalPages", is(list.size() / 2))
            .body("last", is(false))
            .body("first", is(true))
            .body("number", is(0))
            .body("size", is(2))
            .body("empty", is(false))
            .body("sort.sorted", is(true))
            .body("sort.unsorted", is(false))
            .body("sort.empty", is(false))
            .body("pageable.sort.sorted", is(true))
            .body("pageable.sort.unsorted", is(false))
            .body("pageable.sort.empty", is(false))
            .body("pageable.offset", is(0))
            .body("pageable.pageNumber", is(0))
            .body("pageable.pageSize", is(2))
            .body("pageable.paged", is(true))
            .body("pageable.unpaged", is(false));
    }

    @Test
    void shoulReturnAllotteById() {
        var statusMock = statusAllotteeRepository.findById(StatusAllotteEnum.ACTIVE.getStatusID()).get();
        var mockAllottee = createMockAllottee(statusMock.getId());

        var allottee = allotteeRepository.saveAndFlush(mockAllottee);

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_SUCCESS, "find-by-id"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(parameterWithName("id").description("Id para consulta")),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("Id do beneficiário"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("Nome atualizado"),
                    fieldWithPath("cpf").type(JsonFieldType.STRING).description("Cpf atualizado"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("E-mail atualizado"),
                    fieldWithPath("status").type(JsonFieldType.STRING).description("Status do beneficiário no sistema"),
                    fieldWithPath("amountYears").type(JsonFieldType.NUMBER)
                        .description("tempo em anos do benefício atualizado"),
                    fieldWithPath("retirementBalance").type(JsonFieldType.NUMBER)
                        .description("Valor do benefício por mês pelo ano estipulado")
                ))
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .port(port)
            .get("/allottee/{id}", mockAllottee.getId())
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("name", equalTo(mockAllottee.getName()))
            .body("cpf", equalTo(mockAllottee.getCpf()))
            .body("email", equalTo(mockAllottee.getEmail()))
            .body("amountYears", equalTo(mockAllottee.getAmoutYears()))
            .body("retirementBalance", equalTo(mockAllottee.getRetirementBalance().floatValue()));
    }

    @Test
    void shouldReturnConflitWhenEntityExistInDatabase() {
        var statusMock = statusAllotteeRepository.findById(StatusAllotteEnum.EXCLUDED.getStatusID()).get();
        var mockAllottee = createMockAllottee(statusMock.getId());

        var allottee = allotteeRepository.saveAndFlush(mockAllottee);

        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(10);
        mockRequest.setCpf(mockAllottee.getCpf());
        mockRequest.setEmail("email@email.com");
        mockRequest.setName(mockAllottee.getName());

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "create", "exist"),
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
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .post("/allottee")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .assertThat()
            .body("title", is("Conflict"))
            .body("statusCode", is(HttpStatus.CONFLICT.value()))
            .body("timestamp", notNullValue())
            .body("error", is("Entity exist in database with cpf or e-mail informed"));
    }

    @Test
    void shouldReturnErrorDeleteAllotteeWhenNotFound() {
        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "delete", "not-found"),
                getRequestPreprocessor(),
                getResponsePreprocessor(),
                pathParameters(parameterWithName("id").description("Id do Benficiário que será excluido")),
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
            .delete("/allottee/{id}", 1)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .assertThat()
            .body("title", is("Entity Not Found"))
            .body("statusCode", is(HttpStatus.NOT_FOUND.value()))
            .body("timestamp", notNullValue())
            .body("error", is("Allottee not found by id: 1"));
    }

    @Test
    void shouldReturnErrorWhenAttributeIsInvalid() {
        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(-1);
        mockRequest.setCpf("");
        mockRequest.setEmail("");
        mockRequest.setName("");

        given(spec)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "create", "invalid-atribute"),
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
            .body(mockRequest)
            .when()
            .port(port)
            .post("/allottee")
            .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .assertThat()
            .body("title", is("Validation Errors Invalid request body"))
            .body("statusCode", is(HttpStatus.UNPROCESSABLE_ENTITY.value()))
            .body("timestamp", notNullValue())
            .body("error.fieldErrors.message", hasItems("must not be blank", "must be greater than 0"))
            .body("error.fieldErrors.field", hasItems("amountYears", "email", "cpf", "name"))
            .body("error.fieldErrors.rejectedValue", hasItems(-1, ""))
            .body("error.fieldErrors.code", hasItems("NotBlank", "Positive"));
    }

    @Test
    void shouldReturnErrorUpdateRequestWhenNotFound() {
        var mockRequest = createMockInput();

        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(mockRequest)
            .when()
            .port(port)
            .put("/allottee/{id}", 1)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturnErrorUpdateRequestWhenAttributeIsInvalid() {
        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(-1);
        mockRequest.setCpf("");
        mockRequest.setEmail("");
        mockRequest.setName("");

        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .port(port)
            .body(mockRequest)
            .put("/allottee/{id}", 1)
            .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @ParameterizedTest
    @MethodSource("providerArgumentDataExist")
    void shouldReturnErrorUpdateRequestWhenDataExistInDatabase(String cpfRequest,
                                                               String emailRequest,
                                                               String cpfDatabase,
                                                               String emailDatabase) {

        var status = StatusAllotteEnum.ACTIVE.getStatusID();
        var mockAllottee = createMockAllottee(status);
        mockAllottee.setEmail(emailDatabase);
        mockAllottee.setCpf(cpfDatabase);

        var mockAlloteeEdit = createMockAllottee(status);

        allotteeRepository.saveAll(List.of(mockAllottee, mockAlloteeEdit));
        allotteeRepository.flush();

        var request = createMockInput();
        request.setCpf(cpfRequest);
        request.setEmail(emailRequest);

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "update", "exist"),
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
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .port(port)
            .put("/allottee/{id}", mockAlloteeEdit.getId())
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .assertThat()
            .body("title", is("Conflict"))
            .body("statusCode", is(HttpStatus.CONFLICT.value()))
            .body("timestamp", notNullValue())
            .body("error", is("Entity exist in database with cpf or e-mail informed"));
    }

    @Test
    void shouldReturnErrorActiveRequestWhenDataExistInDatabase() {

        var status = StatusAllotteEnum.EXCLUDED.getStatusID();
        var mockAllotteeExcluded = createMockAllottee(StatusAllotteEnum.EXCLUDED.getStatusID());
        var mockAllotteeActive = createMockAllottee(StatusAllotteEnum.ACTIVE.getStatusID());

        allotteeRepository.saveAll(List.of(mockAllotteeExcluded, mockAllotteeActive));
        allotteeRepository.flush();

        var request = createMockInput();
        request.setCpf(mockAllotteeExcluded.getCpf());
        request.setEmail(mockAllotteeActive.getEmail());

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "active", "exist"),
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
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .port(port)
            .post("/allottee/active")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .assertThat()
            .body("title", is("Conflict"))
            .body("statusCode", is(HttpStatus.CONFLICT.value()))
            .body("timestamp", notNullValue())
            .body("error", is("Entity exist in database with cpf or e-mail informed"));
    }

    @Test
    void shouldReturnErrorActiveRequestWhenStatusNotAllowsNewRegistration() {

        var mockAllotteeActive = createMockAllottee(StatusAllotteEnum.ACTIVE.getStatusID());

        allotteeRepository.saveAndFlush(mockAllotteeActive);

        var request = createMockInput();
        request.setCpf(mockAllotteeActive.getCpf());

        given(spec)
            .filter(RestAssuredRestDocumentation.document(
                String.format(IDENTIFIER_ERROR, "active", "not-allows"),
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
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .port(port)
            .post("/allottee/active")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .assertThat()
            .body("title", is("Bad Request"))
            .body("statusCode", is(HttpStatus.BAD_REQUEST.value()))
            .body("timestamp", notNullValue())
            .body("error", is("Allottee not permission registry"));
    }

    private static Stream<Arguments> providerArgumentDataExist() {
        return Stream.of(
            Arguments.arguments("12345678901", "email@email.com", "12345678901", "other@email.com"),
            Arguments.arguments("12345678901", "email@email.com", "09876543212", "email@email.com")
        );
    }

    private Allottee createMockAllottee(Integer statusID) {
        var statusMock = statusAllotteeRepository.findById(statusID).get();
        var mockAllottee = new Allottee();
        mockAllottee.setStatus(statusMock);
        mockAllottee.setName(faker.name().fullName());
        mockAllottee.setRetirementBalance(BigDecimal.valueOf(faker.number().randomDouble(2, 10000, 100000)));
        mockAllottee.setEmail(faker.internet().emailAddress());
        mockAllottee.setAmoutYears(faker.number().numberBetween(10, 20));
        mockAllottee.setCpf(faker.regexify("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"));

        return mockAllottee;
    }

    private AllotteInput createMockInput() {
        var mockRequest = new AllotteInput();
        mockRequest.setAmountYears(faker.number().numberBetween(10, 20));
        mockRequest.setCpf(faker.regexify("(\\d{3})(\\d{3})(\\d{3})(\\d{2})"));
        mockRequest.setEmail(faker.internet().emailAddress());
        mockRequest.setName(faker.name().fullName());

        return mockRequest;
    }

}
