package com.borodulin.moneytransferservice.dao;

import com.borodulin.moneytransferservice.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTransferDao extends JpaRepository<Transfer, Long> {
}
