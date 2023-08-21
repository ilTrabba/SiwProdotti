package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Provider;
import it.uniroma3.siw.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProviderRepository extends CrudRepository<Provider,Long> {

    public boolean existsByName(String name);

    public boolean existsByNameAndAddress(String name, String address);

    public boolean existsByNameAndAddressAndEmail(String name, String address,String email);

    public List<Provider> findByName(String name);

    public List<Provider> getProvidersByStarredProductsNotContaining(Product product);

}
