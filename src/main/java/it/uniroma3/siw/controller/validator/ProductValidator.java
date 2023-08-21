package it.uniroma3.siw.controller.validator;


import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProductValidator implements Validator {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;
        if(product.getName() != null && product.getPrice() != null
                && productRepository.existsByNameAndPrice(product.getName(),product.getPrice())){
            errors.reject("player.duplicate");
        }
    }
}
