package com.example.securityjwt.services;

import com.example.securityjwt.dtos.*;
import com.example.securityjwt.entity.AccountOperation;
import com.example.securityjwt.entity.CurrentAccount;
import com.example.securityjwt.entity.Customer;
import com.example.securityjwt.entity.SavingAccount;
import org.hibernate.query.Page;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
    CustomerDTO getCustomer(Long customerId);
    Page<CustomerDTO> searchCustomers(String keyword, int page, int size);

    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId);
    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId);
    BankAccountDTO getBankAccount(String accountId);

    void debit(String accountId, double amount, String description);
    void credit(String accountId, double amount, String description);
    void transfer(String accountIdSource, String accountIdDestination, double amount);

    Page<AccountOperationDTO> accountHistory(String accountId, int page, int size);


}
