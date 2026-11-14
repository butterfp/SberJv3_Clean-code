package butterfp.cleancode.web;

import butterfp.cleancode.service.InterestRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class InterestRateControllerTest {
    private static final String PATH = "/api/v1/rate";
    private static final String CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    private final RecordingInterestRateService service = new RecordingInterestRateService();
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new InterestRateController(service)).build();

    @Test
    void returnsRateFromServiceForValidRequest() throws Exception {
        String body = """
                {"date":"2025-01-01","id":"%s"}
                """.formatted(CLIENT_ID);
        var expectedRequest = new InterestRateController.RateRequest(
                LocalDate.of(2025, 1, 1), UUID.fromString(CLIENT_ID));

        var response = mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("14");
        assertThat(service.request).isEqualTo(expectedRequest);
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
        assertThat(service.request).isNull();
    }

    private static final class RecordingInterestRateService extends InterestRateService {
        private InterestRateController.RateRequest request;

        private RecordingInterestRateService() {
            super(null, null);
        }

        @Override
        public int calc(LocalDate openingDate, UUID clientId, LocalDate today) {
            request = new InterestRateController.RateRequest(openingDate, clientId);
            return 14;
        }
    }
}
