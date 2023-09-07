package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ProductValidator;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Provider;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ProviderRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductValidator productValidator;
    @Autowired
    private GlobalController globalController;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/admin/formNewProduct")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "admin/formNewProduct.html";
    }

    @PostMapping("/admin/uploadProduct")
    public String newProduct(Model model, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult,
                           @RequestParam("file") MultipartFile image) throws IOException {
        this.productValidator.validate(product, bindingResult);
        if (!bindingResult.hasErrors()) {
            /*
             * for(MultipartFile image : images){
             * Image picture = new Image(image.getBytes());
             * this.imageRepository.save(picture);
             * productImgs.add(picture);
             * }
             */


            this.productService.createProduct(product, image);
            return this.productService.function(model, product, globalController.getUser());
        } else {
            return "/admin/formNewProduct.html";
        }
    }

    @GetMapping("/admin/indexProduct")
    public String indexProduct(Model model) {
        return "admin/indexProduct.html";
    }

    @GetMapping("/products")
    public String showAllProducts(Model model) {
        model.addAttribute("products", this.productRepository.findAll());
        return "products.html";
    }

    @GetMapping("/orderedRatingProducts")
    public String showAllProductsRatingOrdered(Model model) {
        model.addAttribute("products", this.productService.getProductsOrderedByAverageRating());
        return "products.html";
    }

    @GetMapping("/orderedPriceProducts")
    public String showAllProductsPriceOrdered(Model model) {
        model.addAttribute("products", this.productService.getProductsOrderedByHighestPrice());
        return "products.html";
    }

    @GetMapping("/cartProducts")
    public String showCartProducts(Model model) {
        User user = this.credentialsService.getCredentials(this.globalController.getUser().getUsername()).getUser();
        model.addAttribute("products", this.productService.findUserProducts(user));
        return "products.html";
    }

    @GetMapping("/products/{productId}")
    public String getProduct(Model model, @PathVariable("productId") Long id) {
        Product product = this.productRepository.findById(id).get();

        return this.productService.function(model, product, this.globalController.getUser());
    }

    @PostMapping("/searchProduct")
    public String searchProduct(Model model, @RequestParam String name) {
        if (name.length() == 0) {
            model.addAttribute("products", this.productRepository.findAll());
        } else {
            model.addAttribute("products", this.productService.getSearchedProducts(name));
        }
        return "products.html";
    }

    @GetMapping("/admin/manage/{productId}")
    public String updateProduct(Model model, @PathVariable("productId") Long id) {
        Product product = this.productRepository.findById(id).get();
        model.addAttribute("product", product);
        model.addAttribute("providers", product.getProviders());
        return "admin/formUpdateProduct.html";
    }


    @GetMapping("/admin/manage/addProvider/{productId}")
    public String updateProductProvider(Model model, @PathVariable("productId") Long productId) {
        Product product = this.productRepository.findById(productId).get();
        model.addAttribute("product", product);
        model.addAttribute("choices", this.providerRepository.getProvidersByStarredProductsNotContaining(product));
        return "admin/formAddProviders.html";
    }

    @GetMapping("/admin/manage/setProvider/{productId}/{providerId}")
    public String setProductProvider(Model model, @PathVariable("productId") Long productId,
                                @PathVariable("providerId") Long providerId) {
        Product product = this.productRepository.findById(productId).get();
        this.productService.setProviderToProduct(product, providerId);

        model.addAttribute("product", product);
        model.addAttribute("providers", product.getProviders());

        return "admin/formUpdateProduct.html";
    }

    @GetMapping("/admin/manage/removeProvider/{productId}/{providerId}")
    public String removeIdProvider(Model model, @PathVariable("productId") Long productId,
                                   @PathVariable("providerId") Long providerId) {
        Product product = this.productRepository.findById(productId).get();
        this.productService.removeProviderToProduct(product, providerId);
        model.addAttribute("product", product);
        model.addAttribute("providers", product.getProviders());

        return "admin/formUpdateProduct.html";
    }

    @GetMapping("/admin/deleteProduct/{productId}")
    public String deleteProduct(Model model, @PathVariable("productId") Long productId){
        Product product = this.productRepository.findById(productId).get();

        for (Provider provider : product.getProviders()) {
            provider.getStarredProducts().remove(product);
        }

        this.productRepository.delete(product);
        return this.showAllProducts(model);
    }
}
