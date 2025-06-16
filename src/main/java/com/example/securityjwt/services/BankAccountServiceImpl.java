package com.example.securityjwt.services;

import com.example.securityjwt.dtos.*;
import com.example.securityjwt.entity.*;
import com.example.securityjwt.enums.OperationType;
import com.example.securityjwt.repositories.AccountOperationRepository;
import com.example.securityjwt.repositories.BankAccountRepository;
import com.example.securityjwt.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = mapFromCustomerDTO(customerDTO);
        customer.setCreatedBy(getCurrentUser());
        Customer savedCustomer = customerRepository.save(customer);
        return mapFromCustomer(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(customerDTO.getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        customer.setUpdatedBy(getCurrentUser());
        Customer savedCustomer = customerRepository.save(customer);
        return mapFromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return mapFromCustomer(customer);
    }

    @Override
    public Page<CustomerDTO> searchCustomers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerRepository.findByKeyword(keyword, pageable);
        return customers.map(this::mapFromCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        currentAccount.setCreatedBy(getCurrentUser());

        CurrentAccount savedAccount = bankAccountRepository.save(currentAccount);
        return mapFromCurrentAccount(savedAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setCreatedBy(getCurrentUser());

        SavingAccount savedAccount = bankAccountRepository.save(savingAccount);
        return mapFromSavingAccount(savedAccount);
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (bankAccount instanceof SavingAccount) {
            return mapFromSavingAccount((SavingAccount) bankAccount);
        } else {
            return mapFromCurrentAccount((CurrentAccount) bankAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance not sufficient");
        }

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setPerformedBy(getCurrentUser());
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccount.setUpdatedBy(getCurrentUser());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setPerformedBy(getCurrentUser());
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccount.setUpdatedBy(getCurrentUser());
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public Page<AccountOperationDTO> accountHistory(String accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountOperation> accountOperations = accountOperationRepository
                .findByBankAccountIdOrderByOperationDateDesc(accountId, pageable);
        return accountOperations.map(this::mapFromAccountOperation);
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // Méthodes de mapping
    private CustomerDTO mapFromCustomer(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setCreatedAt(customer.getCreatedAt());
        customerDTO.setUpdatedAt(customer.getUpdatedAt());
        return customerDTO;
    }

    private Customer mapFromCustomerDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        return customer;
    }

    private SavingAccountDTO mapFromSavingAccount(SavingAccount savingAccount) {
        SavingAccountDTO accountDTO = new SavingAccountDTO();
        accountDTO.setId(savingAccount.getId());
        accountDTO.setBalance(savingAccount.getBalance());
        accountDTO.setCreatedAt(savingAccount.getCreatedAt());
        accountDTO.setInterestRate(savingAccount.getInterestRate());
        accountDTO.setCustomer(mapFromCustomer(savingAccount.getCustomer()));
        accountDTO.setType("SAVING_ACCOUNT");
        return accountDTO;
    }

    private CurrentAccountDTO mapFromCurrentAccount(CurrentAccount currentAccount) {
        CurrentAccountDTO accountDTO = new CurrentAccountDTO();
        accountDTO.setId(currentAccount.getId());
        accountDTO.setBalance(currentAccount.getBalance());
        accountDTO.setCreatedAt(currentAccount.getCreatedAt());
        accountDTO.setOverDraft(currentAccount.getOverDraft());
        accountDTO.setCustomer(mapFromCustomer(currentAccount.getCustomer()));
        accountDTO.setType("CURRENT_ACCOUNT");
        return accountDTO;
    }

    private AccountOperationDTO mapFromAccountOperation(AccountOperation accountOperation) {
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        accountOperationDTO.setId(accountOperation.getId());
        accountOperationDTO.setOperationDate(accountOperation.getOperationTime());
        accountOperationDTO.setAmount(accountOperation.getAmount());
        accountOperationDTO.setType(accountOperation.getOperationType());
        accountOperationDTO.setDescription(accountOperation.getDescription());
        accountOperationDTO.setPerformedBy(accountOperation.getPerformedBy());
        return accountOperationDTO;
    }
}
