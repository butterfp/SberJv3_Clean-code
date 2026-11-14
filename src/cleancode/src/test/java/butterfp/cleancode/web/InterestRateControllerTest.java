package butterfp.cleancode.web;

import butterfp.cleancode.model.InterestRate;
import butterfp.cleancode.service.ClientDataService;
import butterfp.cleancode.service.ClientTransactionHistoryService;
import butterfp.cleancode.service.InterestRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class InterestRateControllerTest {
    private static final String PATH = "/api/v1/rate";
    private static final String CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    private final ClientDataService clientData = mock(ClientDataService.class);
    private final ClientTransactionHistoryService transactionHistory = mock(ClientTransactionHistoryService.class);
    private final InterestRateService service = new InterestRateService(clientData, transactionHistory);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new InterestRateController(service)).build();

    @Test
    void returnsRateFromServiceForValidRequest() throws Exception {
        String body = """
                {"date":"2025-01-01","id":"%s"}
                """.formatted(CLIENT_ID);
        UUID clientId = UUID.fromString(CLIENT_ID);
        LocalDate openingDate = LocalDate.of(2025, 1, 1);
        BigDecimal purchases = BigDecimal.valueOf(50_000);
        when(clientData.hasSberPrime(clientId)).thenReturn(false);
        when(transactionHistory.totalPurchases(eq(clientId), any(YearMonth.class))).thenReturn(purchases);

        try (MockedStatic<InterestRate> model = mockStatic(InterestRate.class, CALLS_REAL_METHODS)) {
            var response = mvc.perform(post(PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andReturn().getResponse();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentAsString()).isEqualTo("14");
            model.verify(() -> InterestRate.calculate(eq(openingDate), any(LocalDate.class),
                    eq(false), eq(purchases)), times(1));
            model.verifyNoMoreInteractions();
        }
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
            "Missing opening date | {\"id\":\"" + CLIENT_ID + "\"}",
            "Missing client ID | {\"date\":\"2025-01-01\"}",
            "Invalid opening date | {\"date\":\"2025-02-30\",\"id\":\"" + CLIENT_ID + "\"}",
            "Invalid client ID | {\"date\":\"2025-01-01\",\"id\":\"invalid\"}",
            "Future opening date | {\"date\":\"9999-01-01\",\"id\":\"" + CLIENT_ID + "\"}",
            "Malformed JSON | {"
    })
    void rejectsInvalidRequestWithoutCallingService(String scenario, String body) throws Exception {
        var request = post(PATH).contentType(MediaType.APPLICATION_JSON).content(body);

        var response = mvc.perform(request).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(clientData, transactionHistory);
    }
}
