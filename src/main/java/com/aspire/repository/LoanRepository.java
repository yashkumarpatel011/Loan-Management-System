package com.aspire.repository;

import com.aspire.model.LoanDetails;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Date;

@Repository
public interface LoanRepository extends JpaRepository<LoanDetails,Integer> {

    public static final String UPDATE_LOAN_STAGE = "UPDATE LOAN_DETAILS SET STATUS = :status" +
            " WHERE LOAN_ID = :loanId";

    public static final String UPDATE_LOAN_ASSIGNED = "UPDATE LOAN_DETAILS SET ASSIGNED = :assignee" +
            " WHERE LOAN_ID = :loanId";

    public static final String UPDATE_LOAN_REMAINING_AMOUNT = "UPDATE LOAN_DETAILS SET REMAINING_AMOUNT = :amount" +
            " WHERE LOAN_ID = :loanId";

    @Modifying
    @Query(value = UPDATE_LOAN_STAGE, nativeQuery = true)
    Integer updateStatus(@Param("status") String status, @Param("loanId") Integer loanId);

    List<LoanDetails> findAllByUserId(Integer userId);

    @Modifying
    @Query(value = UPDATE_LOAN_ASSIGNED, nativeQuery = true)
    Integer updateLoanAssignee(@Param("assignee") Integer assignee, @Param("loanId") Integer loanId);

    @Modifying
    @Query(value = UPDATE_LOAN_REMAINING_AMOUNT, nativeQuery = true)
    Integer updateRemainingAmount(@Param("amount") Double amount, @Param("loanId") Integer loanId);

}
