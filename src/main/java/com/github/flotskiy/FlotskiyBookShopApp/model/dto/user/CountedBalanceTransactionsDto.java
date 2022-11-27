package com.github.flotskiy.FlotskiyBookShopApp.model.dto.user;

import java.util.List;

public class CountedBalanceTransactionsDto {

    private Integer count;
    private List<BalanceTransactionDto> transactions;

    public CountedBalanceTransactionsDto(List<BalanceTransactionDto> transactions) {
        this.transactions = transactions;
        if (transactions.isEmpty()) {
            this.count = -1;
        } else {
            this.count = transactions.size();
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<BalanceTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<BalanceTransactionDto> transactions) {
        this.transactions = transactions;
    }
}
