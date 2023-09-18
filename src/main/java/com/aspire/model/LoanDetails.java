package com.aspire.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "loan_id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "repayment_freq")
    private String rePaymentFreq;

    @Column(name = "interest_rate")
    private Double interestRate;

    @Column(name = "status")
    private String status;

    @Column(name = "assigned")
    private Integer adminId;

    @Column(name = "remaining_amount")
    private Double remainingAmount;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "loan_id", referencedColumnName = "loan_id")
    private List<RepaymentDetails> repaymentDetails;

}
