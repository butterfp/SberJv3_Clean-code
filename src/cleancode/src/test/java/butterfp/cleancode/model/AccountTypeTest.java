package butterfp.cleancode.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;

class AccountTypeTest {
    @Test
    void createsSavingsAccountWithThreeMonthBonusPeriod() {
        SavingsAccount account = AccountType.SAVINGS.createAccount();
        LocalDate openingDate = LocalDate.of(2025, 1, 31);

        assertThat(account.getClass()).isEqualTo(SavingsAccount.class);
        assertThat(account.calculate(openingDate, openingDate.plusMonths(3).minusDays(1),
                false, BigDecimal.ZERO)).isEqualTo(12);
        assertThat(account.calculate(openingDate, openingDate.plusMonths(3),
                false, BigDecimal.ZERO)).isEqualTo(10);
    }

    @Test
    void createsSalarySavingsAccountWithTwelveMonthBonusPeriod() {
        SavingsAccount account = AccountType.SALARY.createAccount();
        LocalDate openingDate = LocalDate.of(2025, 1, 31);

        assertThat(account.getClass()).isEqualTo(SavingsAccountForSalaryClient.class);
        assertThat(account.calculate(openingDate, openingDate.plusMonths(12).minusDays(1),
                false, BigDecimal.ZERO)).isEqualTo(12);
        assertThat(account.calculate(openingDate, openingDate.plusMonths(12),
                false, BigDecimal.ZERO)).isEqualTo(10);
    }
}
