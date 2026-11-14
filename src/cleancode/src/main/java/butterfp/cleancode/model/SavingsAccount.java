package butterfp.cleancode.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsAccount {
    private static final int BASE_RATE = 10;
    private static final int INTRODUCTORY_BONUS = 2;
    private static final int INTRODUCTORY_MONTHS = 3;
    private static final int SBER_PRIME_BONUS = 2;
    private static final BigDecimal LOWER_PURCHASE_THRESHOLD = BigDecimal.valueOf(30_000);
    private static final BigDecimal UPPER_PURCHASE_THRESHOLD = BigDecimal.valueOf(50_000);

    private final int introductoryMonths;

    public SavingsAccount() {
        this(INTRODUCTORY_MONTHS);
    }

    protected SavingsAccount(int introductoryMonths) {
        this.introductoryMonths = introductoryMonths;
    }

    public int calculate(LocalDate openingDate, LocalDate today, boolean hasSberPrime,
                         BigDecimal totalPurchases) {
        int introductoryBonus = today.isBefore(openingDate.plusMonths(introductoryMonths))
                ? INTRODUCTORY_BONUS : 0;
        int packageBonus = hasSberPrime ? SBER_PRIME_BONUS : 0;
        return BASE_RATE + introductoryBonus + packageBonus + purchaseBonus(totalPurchases);
    }

    private static int purchaseBonus(BigDecimal totalPurchases) {
        if (totalPurchases.compareTo(UPPER_PURCHASE_THRESHOLD) >= 0) {
            return 4;
        }
        if (totalPurchases.compareTo(LOWER_PURCHASE_THRESHOLD) >= 0) {
            return 2;
        }
        return 0;
    }
}
