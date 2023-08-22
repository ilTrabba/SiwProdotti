package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReviewController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewValidator reviewValidator;
    @Autowired
    private GlobalController globalController;

    @PostMapping("/user/uploadReview/{productId}")
    public String newReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("productId") Long id) {
        this.reviewValidator.validate(review,bindingResult);
        Product product = this.productRepository.findById(id).get();
        if(!bindingResult.hasErrors()){
            if(this.globalController.getUser() != null && !product.getReviews().contains(review)){
                review.setAuthor(this.globalController.getUser().getUsername());
                review.setReviewedPlayer(productRepository.findById(id).orElse(null));
                this.reviewRepository.save(review);
                product.getReviews().add(review);

            }
        }
        this.productRepository.save(product);

        return this.productService.function(model, product, this.globalController.getUser());

    }

    @GetMapping("/admin/deleteReview/{productId}/{reviewId}")
    public String removeReview(Model model, @PathVariable("productId") Long playerId,@PathVariable("reviewId") Long reviewId){
        Product product = this.productRepository.findById(playerId).get();
        Review review = this.reviewRepository.findById(reviewId).get();

        product.getReviews().remove(review);
        this.reviewRepository.delete(review);
        this.productRepository.save(product);
        return this.productService.function(model, product, this.globalController.getUser());
    }


    @GetMapping("/user/modifyingReview/{productId}/{reviewId}")
    public String updateReview(Model model, @PathVariable("productId") Long productId,@PathVariable("reviewId") Long reviewId){
        Product product = this.productRepository.findById(productId).get();
        Review review = this.reviewRepository.findById(reviewId).get();

        model.addAttribute("review", review);
        model.addAttribute("product", product);
        model.addAttribute("username", this.globalController.getUser().getUsername());

        return "formUpdateReview.html";
    }

    @PostMapping("/user/updateReview/{productId}/{reviewId}")
    public String updateReview(Model model, @Valid @ModelAttribute("review") Review updatedReview, BindingResult bindingResult,@PathVariable("productId") Long productId, @PathVariable("reviewId") Long reviewId) {
        this.reviewValidator.validate(updatedReview, bindingResult);

        if (!bindingResult.hasErrors()) {
            Review existingReview = this.reviewRepository.findById(reviewId).orElse(null);

            if (existingReview != null) {
                existingReview.setTitle(updatedReview.getTitle());
                existingReview.setText(updatedReview.getText());
                existingReview.setRating(updatedReview.getRating());

                this.reviewRepository.save(existingReview);

            }
        }

        Product product = this.productRepository.findById(productId).orElse(null);
        return this.productService.function(model, product, this.globalController.getUser());
    }
}
