package com.symida.authorization.service;

import com.symida.authorization.model.Account;

public interface AccountService {

    Account register(Account account) throws RuntimeException;

}
