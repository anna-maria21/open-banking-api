package com.openbankingapi.repository;

import com.openbankingapi.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                SELECT t FROM Transaction t
                            JOIN FETCH t.accountFrom
                                        join fetch t.accountFrom.client
                                        join fetch t.accountFrom.currency
                            JOIN FETCH t.accountTo
                                        join fetch t.accountTo.client
                                        join fetch t.accountTo.currency
                            JOIN FETCH t.currencyFrom
                            JOIN FETCH t.currencyFrom
                        WHERE t.accountFrom.iban = :iban OR t.accountTo.iban = :iban
                        ORDER BY t.changedAt DESC
            """)
    List<Transaction> findAllByAccount(@Param("iban") String iban, Pageable pageable);
}
