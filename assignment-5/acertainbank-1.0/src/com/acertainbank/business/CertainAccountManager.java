package com.acertainbank.business;

import com.acertainbank.exceptions.InexistentAccountException;
import com.acertainbank.exceptions.InexistentBranchException;
import com.acertainbank.exceptions.NegativeAmountException;
import com.acertainbank.interfaces.AccountManager;

/**
 * Created by tudorgk on 6/1/15.
 */
public class CertainAccountManager implements AccountManager {


    public CertainAccountManager(){

    }

    @Override
    public synchronized void credit(int branchId, int accountId, double amount) throws InexistentBranchException, InexistentAccountException, NegativeAmountException {

    }

    @Override
    public synchronized void debit(int branchId, int accountId, double amount) throws InexistentBranchException, InexistentAccountException, NegativeAmountException {

    }

    @Override
    public synchronized void transfer(int branchId, int accountIdOrig, int accountIdDest, double amount) throws InexistentBranchException, InexistentAccountException, NegativeAmountException {

    }

    @Override
    public synchronized double calculateExposure(int branchId) throws InexistentBranchException {
        return 0;
    }
}
