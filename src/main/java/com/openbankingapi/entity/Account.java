package com.openbankingapi.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "account",
        indexes = {
                @Index(name = "i_account_iban", columnList = "iban")
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String iban;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;

    @ColumnDefault("0.0")
    private Long balance;

    @OneToMany(mappedBy = "accountFrom")
    @JsonManagedReference
    private List<Transaction> transactionsFrom;

    @OneToMany(mappedBy = "accountTo")
    @JsonManagedReference
    private List<Transaction> transactionsTo;

    public List<Transaction> getAllTransactions() {
        List<Transaction> all = new ArrayList<>();
        all.addAll(transactionsFrom);
        all.addAll(transactionsTo);
        return all.stream().sorted(Comparator.comparing(Transaction::getChangedAt).reversed())
                .collect(Collectors.toList());
    }
}
