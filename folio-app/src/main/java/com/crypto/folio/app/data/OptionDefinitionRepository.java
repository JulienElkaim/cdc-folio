package com.crypto.folio.app.data;

import com.crypto.ref.data.models.OptionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionDefinitionRepository extends JpaRepository<OptionDefinition, String> {
    @Override
    Optional<OptionDefinition> findById(String s);
}
