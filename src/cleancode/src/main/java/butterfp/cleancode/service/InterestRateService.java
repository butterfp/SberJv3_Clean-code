package butterfp.cleancode.service;

import butterfp.cleancode.model.InterestRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class InterestRateService {
    private final ClientDataService clientData;
    private final ClientTransactionHistoryService transactionHistory;

    public InterestRateService(ClientDataService clientData,
                               ClientTransactionHistoryService transactionHistory) {
        this.clientData = clientData;
        this.transactionHistory = transactionHistory;
    }

    public int calc(LocalDate openingDate, UUID clientId, LocalDate today) {
        boolean hasSberPrime = clientData.hasSberPrime(clientId);
        YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
        BigDecimal totalPurchases = transactionHistory.totalPurchases(clientId, previousMonth);
        return InterestRate.calculate(openingDate, today, hasSberPrime, totalPurchases);
    }
}
