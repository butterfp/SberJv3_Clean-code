package butterfp.cleancode.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterestRateServiceTest {
    @Test
    void usesClientPackageAndDecemberPurchasesForJanuaryCalculation() {
        UUID clientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDate openingDate = LocalDate.of(2025, 1, 1);
        LocalDate today = LocalDate.of(2026, 1, 1);
        YearMonth previousMonth = YearMonth.of(2025, 12);
        ClientDataService clientData = mock(ClientDataService.class);
        ClientTransactionHistoryService transactionHistory = mock(ClientTransactionHistoryService.class);
        when(clientData.hasSberPrime(clientId)).thenReturn(true);
        when(transactionHistory.totalPurchases(clientId, previousMonth)).thenReturn(BigDecimal.valueOf(50_000));
        InterestRateService service = new InterestRateService(clientData, transactionHistory);

        int rate = service.calculateRate(openingDate, clientId, today);

        assertThat(rate).isEqualTo(16);
    }
}
