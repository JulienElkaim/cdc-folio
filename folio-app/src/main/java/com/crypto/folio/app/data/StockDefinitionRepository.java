package com.crypto.folio.app.data;

import com.crypto.ref.data.models.StockDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockDefinitionRepository extends JpaRepository<StockDefinition, String> {
    @Override
    Optional<StockDefinition> findById(String s);
}