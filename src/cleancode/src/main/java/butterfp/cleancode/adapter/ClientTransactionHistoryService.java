package butterfp.cleancode.adapter;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class ClientTransactionHistoryService {
    public BigDecimal totalPurchases(UUID clientId, YearMonth month) {
        return BigDecimal.ZERO;
    }
}
