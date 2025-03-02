package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.repository.AccountRepository;
import com.example.entity.Account;

@Service
@Transactional
public class AccountService {

    AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Account> addAccount (Account account) {
        if (!account.getUsername().isBlank() &&
            account.getPassword().length() >= 4) {
                if (accountRepository.findAccountByUsername(account.getUsername()) != null) {
                    // if username already exists, return conflict status
                    return new ResponseEntity<Account>(account, HttpStatus.CONFLICT);
                }
                // if username and password valid and username does NOT exist, create account and respond ok
                return new ResponseEntity<Account>(accountRepository.save(account), HttpStatus.OK);
        }
        // if username or password invalid, return bad request status
        return new ResponseEntity<Account>(account, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Account> loginAccount (Account account) {
        // get account from database by username
        Account matchingAccount = accountRepository.findAccountByUsername(account.getUsername());
        if (matchingAccount != null && matchingAccount.getPassword().equals(account.getPassword())) {
            // if account exists and passwords match, return database account and ok status
            return new ResponseEntity<Account>(matchingAccount, HttpStatus.OK);
        }
        // if username or password incorrect, return unauthorized status
        return new ResponseEntity<Account>(account, HttpStatus.UNAUTHORIZED);
    }
}
