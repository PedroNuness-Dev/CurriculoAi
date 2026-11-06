package br.com.CurriculoAi.repositories.address;

import br.com.CurriculoAi.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    boolean existsByZipCode(String zipCode);

    Optional<Address> findByZipCode(String zipCode);
}