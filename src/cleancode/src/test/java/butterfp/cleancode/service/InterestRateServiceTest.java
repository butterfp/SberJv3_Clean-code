package butterfp.cleancode.service;

import butterfp.cleancode.adapter.ClientDataService;
import butterfp.cleancode.adapter.ClientTransactionHistoryService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

class InterestRateServiceTest {
    @ParameterizedTest
    @CsvSource({
            "2025-10-01, false, 29999.99, 10",
            "2025-10-02, false, 0, 12",
            "2025-10-01, false, 30000, 12",
            "2025-10-01, false, 49999.99, 12",
            "2025-10-01, false, 50000, 14",
            "2025-10-01, true, 0, 12",
            "2025-10-02, true, 50000, 18"
    })
    void calculatesRateUsingPreviousCalendarMonth(LocalDate openingDate, boolean hasSberPrime,
                                                   BigDecimal totalPurchases, int expectedRate) {
        UUID clientId = UUID.randomUUID();
        LocalDate today = LocalDate.of(2026, 1, 1);
        YearMonth previousMonth = YearMonth.of(2025, 12);
        ClientDataService clientData = mock(ClientDataService.class);
        ClientTransactionHistoryService transactionHistory = mock(ClientTransactionHistoryService.class);
        when(clientData.hasSberPrime(clientId)).thenReturn(hasSberPrime);
        when(transactionHistory.totalPurchases(clientId, previousMonth)).thenReturn(totalPurchases);
        InterestRateService service = new InterestRateService(clientData, transactionHistory);

        assertThat(service.calc(openingDate, clientId, today)).isEqualTo(expectedRate);
        verify(clientData).hasSberPrime(clientId);
        verify(transactionHistory).totalPurchases(clientId, previousMonth);
    }
}
