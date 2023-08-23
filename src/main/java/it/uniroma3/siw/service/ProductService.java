package it.uniroma3.siw.service;


import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Provider;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ProviderRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ProductService {
    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ImageRepository imageRepository;

    @Transactional
    public void createProduct(Product product, MultipartFile image) throws IOException {
        Image productImg = new Image(image.getBytes());
        this.imageRepository.save(productImg);

        product.setFoto(productImg);
        this.productRepository.save(product);
    }

   /* @Transactional
    public List<Player> getSearchedPlayers(String name) {
        return this.playerRepository.findByName(name);
    }*/

    @Transactional
    public List<Product> getSearchedProducts(String name) {
        List<Product> result = this.productRepository.findByName(name);
        return result;
    }

    @Transactional
    public void setProviderToProduct(Product product, Long providerId) {
        Provider provider = this.providerRepository.findById(providerId).get();

        provider.getStarredProducts().add(product);
        product.getProviders().add(provider);

        this.providerRepository.save(provider);
        this.productRepository.save(product);
    }

    @Transactional
    public void removeProviderToProduct(Product product, Long providerId) {
        Provider provider = this.providerRepository.findById(providerId).get();

        provider.getStarredProducts().remove(product);
        product.getProviders().remove(provider);

        this.providerRepository.save(provider);
        this.productRepository.save(product);
    }

    public String function(Model model,Product product,UserDetails user){
        Set<Provider> productProviders = new HashSet<>();                 //product providers intendo tipo movieCast
        if(product.getProviders() != null)
            productProviders.addAll(product.getProviders());
        productProviders.remove(null);
        model.addAttribute("productProviders", productProviders);
        model.addAttribute("product", product);
        if(user != null && this.alreadyReviewed(product.getReviews(),user.getUsername()))
            model.addAttribute("hasComment", true);
        else {
            model.addAttribute("hasComment", false);
        }
        model.addAttribute("review", new Review());
        model.addAttribute("reviews", product.getReviews());


        return "product.html";
    }

    @Transactional
    public Float getAvgRating(Long productId) {
        Product product = this.productRepository.findById(productId).orElse(null);

        return productRepository.getAvgRatingByProduct(product);
    }

    @Transactional
    public Collection<Product> getProductsOrderedByAverageRating() {

        Collection<Product> products = this.productRepository.findProductsOrderByAverageRating();

        Collection<Product> orderedProducts = new ArrayList<>();

        for (Product product : products) {

            orderedProducts.add(product);

        }

        return orderedProducts;
    }

    @Transactional
    public boolean alreadyReviewed(Set<Review> reviews,String author){
        if(reviews != null)
            for(Review rev : reviews)
                if(rev.getAuthor().equals(author))
                    return true;
        return false;
    }
}
