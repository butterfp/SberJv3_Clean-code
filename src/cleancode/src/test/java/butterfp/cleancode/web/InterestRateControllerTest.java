package butterfp.cleancode.web;

import butterfp.cleancode.service.InterestRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class InterestRateControllerTest {
    private static final String PATH = "/api/v1/rate";
    private static final String CLIENT_ID = "550e8400-e29b-41d4-a716-446655440000";

    private final InterestRateService service = mock(InterestRateService.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new InterestRateController(service)).build();

    @Test
    void returnsRateFromService() throws Exception {
        when(service.calc(eq(LocalDate.of(2025, 1, 1)), eq(UUID.fromString(CLIENT_ID)), any(LocalDate.class)))
                .thenReturn(14);

        var response = mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2025-01-01","id":"%s"}
                                """.formatted(CLIENT_ID)))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("14");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"id\":\"" + CLIENT_ID + "\"}",
            "{\"date\":\"2025-01-01\"}",
            "{\"date\":\"2025-02-30\",\"id\":\"" + CLIENT_ID + "\"}",
            "{\"date\":\"2025-01-01\",\"id\":\"invalid\"}",
            "{\"date\":\"9999-01-01\",\"id\":\"" + CLIENT_ID + "\"}",
            "{"
    })
    void rejectsInvalidRequestWithoutCallingService(String body) throws Exception {
        var response = mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
        verifyNoInteractions(service);
    }
}
