package it.uniroma3.siw.controller.validator;


import it.uniroma3.siw.model.Provider;
import it.uniroma3.siw.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProviderValidator implements Validator {
    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Provider.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Provider provider = (Provider) target;
        if (provider.getName() != null && provider.getAddress() != null && provider.getEmail() != null &&
                this.providerRepository.existsByNameAndAddress(provider.getName(), provider.getAddress())) {
            errors.reject("provider.duplicate");
        }
    }
}