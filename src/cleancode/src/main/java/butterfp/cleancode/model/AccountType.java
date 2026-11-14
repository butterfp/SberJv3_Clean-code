package butterfp.cleancode.model;

public enum AccountType {
    SAVINGS {
        @Override
        public SavingsAccount createAccount() {
            return new SavingsAccount();
        }
    },
    SALARY {
        @Override
        public SavingsAccount createAccount() {
            return new SavingsAccountForSalaryClient();
        }
    };

    public abstract SavingsAccount createAccount();
}
