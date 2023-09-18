package com.aspire.repository;

import com.aspire.model.RepaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentRepository extends JpaRepository<RepaymentDetails,Integer> {

    List<RepaymentDetails> findByLoanIdAndStatus(Integer loanId,String status);
}
