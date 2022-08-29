package com.borodulin.moneytransferservice.dao;

import com.borodulin.moneytransferservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDao extends JpaRepository<Card, String> {
}
