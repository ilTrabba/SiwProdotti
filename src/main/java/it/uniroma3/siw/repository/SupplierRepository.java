package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Supplier;
import it.uniroma3.siw.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SupplierRepository extends CrudRepository<Supplier,Long> {

    public boolean existsByName(String name);

    public boolean existsByNameAndAddress(String name, String address);

    public List<Supplier> findByName(String name);

    public List<Supplier> getSuppliersByStarredProductsNotContaining(Product product);

}
