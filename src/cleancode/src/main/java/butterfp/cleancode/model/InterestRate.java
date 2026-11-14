package butterfp.cleancode.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class InterestRate {
    private InterestRate() {
    }

    public static int calculate(LocalDate openingDate, LocalDate today, boolean hasSberPrime,
                                BigDecimal totalPurchases) {
        return AccountType.SAVINGS.createAccount().calculate(openingDate, today, hasSberPrime, totalPurchases);
    }
}
