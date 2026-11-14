package butterfp.cleancode.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;

class InterestRateTest {
    @ParameterizedTest
    @CsvSource({
            "2026-01-31, false, 0, 12",
            "2026-04-29, false, 0, 12",
            "2026-04-30, false, 0, 10",
            "2026-04-30, true, 0, 12",
            "2026-04-30, false, 29999.99, 10",
            "2026-04-30, false, 30000, 12",
            "2026-04-30, false, 49999.99, 12",
            "2026-04-30, false, 50000, 14",
            "2026-04-29, true, 50000.01, 18"
    })
    void calculatesBaseRateAndBonuses(LocalDate today, boolean hasSberPrime,
                                      BigDecimal purchases, int expectedRate) {
        LocalDate openingDate = LocalDate.of(2026, 1, 31);

        assertThat(InterestRate.calculate(openingDate, today, hasSberPrime, purchases))
                .isEqualTo(expectedRate);
    }
}
