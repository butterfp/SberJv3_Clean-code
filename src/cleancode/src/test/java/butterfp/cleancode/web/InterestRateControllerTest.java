package butterfp.cleancode.web;

import butterfp.cleancode.service.ClientDataService;
import butterfp.cleancode.service.ClientTransactionHistoryService;
import butterfp.cleancode.service.InterestRateService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.mockito.Mockito.mock;
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

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "'' | 14",
        "', \"accountType\": \"SAVINGS\"' | 14",
        "', \"accountType\": \"SALARY\"' | 16"
    })
    void returnsRateForRequestedAccountType(String accountTypeField, int expectedRate) throws Exception {
        LocalDate openingDate = LocalDate.now().minusMonths(6);
        String body = """
                {"date":"%s","id":"%s"%s}
                """.formatted(openingDate, CLIENT_ID, accountTypeField);
        UUID clientId = UUID.fromString(CLIENT_ID);
        when(clientData.hasSberPrime(clientId)).thenReturn(false);
        when(transactionHistory.totalPurchases(eq(clientId), any(YearMonth.class)))
                .thenReturn(BigDecimal.valueOf(50_000));

        var response = mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo(Integer.toString(expectedRate));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', value = {
        "Missing opening date | {\"id\":\"" + CLIENT_ID + "\"}",
        "Missing client ID | {\"date\":\"2025-01-01\"}",
        "Invalid opening date | {\"date\":\"2025-02-30\",\"id\":\"" + CLIENT_ID + "\"}",
        "Invalid client ID | {\"date\":\"2025-01-01\",\"id\":\"invalid\"}",
        "Future opening date | {\"date\":\"9999-01-01\",\"id\":\"" + CLIENT_ID + "\"}",
        "Unknown account type | {\"date\":\"2025-01-01\",\"id\":\"" + CLIENT_ID + "\",\"accountType\":\"UNKNOWN\"}",
        "Malformed JSON | {"
    })
    void rejectsInvalidRequestWithoutCallingService(String scenario, String body) throws Exception {
        var request = post(PATH).contentType(MediaType.APPLICATION_JSON).content(body);

        var response = mvc.perform(request).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(clientData, transactionHistory);
    }
}
