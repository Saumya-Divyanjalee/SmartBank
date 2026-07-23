package lk.saumiz.banking.bo.custom;

import lk.saumiz.banking.bo.AccountBO;
import lk.saumiz.banking.bo.CustomerBO;
import lk.saumiz.banking.bo.DashboardBO;
import lk.saumiz.banking.bo.UserBO;
import lk.saumiz.banking.bo.impl.AccountBOImpl;
import lk.saumiz.banking.bo.impl.CustomerBOImpl;
import lk.saumiz.banking.bo.impl.DashboardBOImpl;
import lk.saumiz.banking.bo.impl.UserBOImpl;

/** Factory pattern: Controllers ask this for BOs instead of `new`-ing impl classes directly. */
public class BOFactory {

    private static final BOFactory INSTANCE = new BOFactory();

    private BOFactory() {}

    public static BOFactory getInstance() {
        return INSTANCE;
    }

    public UserBO getUserBO() {
        return new UserBOImpl();
    }

    public CustomerBO getCustomerBO() {
        return new CustomerBOImpl();
    }

    public AccountBO getAccountBO() {
        return new AccountBOImpl();
    }

    public DashboardBO getDashboardBO() {
        return new DashboardBOImpl();
    }
}
