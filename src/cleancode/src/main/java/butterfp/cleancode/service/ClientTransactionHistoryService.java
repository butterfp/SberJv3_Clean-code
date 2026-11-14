package butterfp.cleancode.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public interface ClientTransactionHistoryService {
    BigDecimal totalPurchases(UUID clientId, YearMonth month);
}
