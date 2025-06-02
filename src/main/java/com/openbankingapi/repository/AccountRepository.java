package com.openbankingapi.repository;

import com.openbankingapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(
            """
                select a from Account a where a.iban = :iban
            """
    )
    Optional<Account> getAccountByIban(@Param("iban") String iban);
}
