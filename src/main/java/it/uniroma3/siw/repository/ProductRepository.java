package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    public boolean existsByNameAndPrice(String name, Float price);

    public boolean existsByNameAndPriceAndDescription(String name, Float price, String description);

    public List<Product> findByName(String name);

    public List<Product> findByNameAndPrice(String name, Float price);

    public List<Product> findByPrice(Float price);


}
