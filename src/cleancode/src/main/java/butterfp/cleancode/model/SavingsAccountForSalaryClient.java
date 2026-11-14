package butterfp.cleancode.model;

public final class SavingsAccountForSalaryClient extends SavingsAccount {
    private static final int INTRODUCTORY_MONTHS = 12;

    public SavingsAccountForSalaryClient() {
        super(INTRODUCTORY_MONTHS);
    }
}
