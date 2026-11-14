package butterfp.cleancode.service;

import butterfp.cleancode.adapter.ClientDataService;
import butterfp.cleancode.adapter.ClientTransactionHistoryService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

class InterestRateServiceTest {
    @Test
    void usesClientPackageAndDecemberPurchasesForJanuaryCalculation() {
        UUID clientId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDate openingDate = LocalDate.of(2025, 1, 1);
        LocalDate today = LocalDate.of(2026, 1, 1);
        YearMonth previousMonth = YearMonth.of(2025, 12);
        ClientDataService clientData = new ClientDataService() {
            @Override
            public boolean hasSberPrime(UUID requestedClientId) {
                return clientId.equals(requestedClientId);
            }
        };
        ClientTransactionHistoryService transactionHistory = new ClientTransactionHistoryService() {
            @Override
            public BigDecimal totalPurchases(UUID requestedClientId, YearMonth month) {
                return clientId.equals(requestedClientId) && previousMonth.equals(month)
                        ? BigDecimal.valueOf(50_000) : BigDecimal.ZERO;
            }
        };
        InterestRateService service = new InterestRateService(clientData, transactionHistory);

        int rate = service.calc(openingDate, clientId, today);

        assertThat(rate).isEqualTo(16);
    }
}
