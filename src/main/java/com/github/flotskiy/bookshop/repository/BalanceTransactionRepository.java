package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.payments.BalanceTransactionEntity;
import com.github.flotskiy.bookshop.model.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, Integer> {

    @Query(value = "SELECT * FROM balance_transaction bt WHERE bt.user_id = :userId ORDER BY bt.time DESC", nativeQuery = true)
    Page<BalanceTransactionEntity> getSortedBalanceTransactionsForUserOrderByTimeDesc(
            @Param("userId") Integer userId, Pageable nextPage
    );

    @Query(value = "SELECT * FROM balance_transaction bt WHERE bt.user_id = :userId ORDER BY bt.time ASC", nativeQuery = true)
    Page<BalanceTransactionEntity> getSortedBalanceTransactionsForUserOrderByTimeAsc(
            @Param("userId") Integer userId, Pageable nextPage
    );

    Integer countBalanceTransactionEntitiesByUserId(UserEntity userEntity);
}
