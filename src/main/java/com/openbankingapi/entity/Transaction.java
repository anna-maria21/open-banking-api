package com.openbankingapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(
        name = "transaction",
        indexes = {
                @Index(name = "i_transaction_acc_from", columnList = "accountFrom"),
                @Index(name = "i_transaction_acc_to", columnList = "accountTo"),
                @Index(name = "i_transaction_changedAt", columnList = "changedAt")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_from")
    private Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "currency_id_from")
    private Currency currencyFrom;

    @ManyToOne
    @JoinColumn(name = "account_to")
    private Account accountTo;

    @ManyToOne
    @JoinColumn(name = "currency_id_to")
    private Currency currencyTo;

    @Column(nullable = false)
    private Long sum;

    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime changedAt;

    @Enumerated(EnumType.STRING)
    private Status status;
}
