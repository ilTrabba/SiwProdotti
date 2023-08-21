package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    public boolean existsByNameAndAddress(String name, String address);

    public boolean existsByNameAndAddressAndEmail(String name, String address, String email);

    public List<Product> findByName(String name);

    public List<Product> findByAddress(String address);

    public List<Product> findByNameAndAddress(String name, String address);

    public List<Product> findByEmail(String email);


}
