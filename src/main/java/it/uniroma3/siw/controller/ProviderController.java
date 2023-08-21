package it.uniroma3.siw.controller;


import it.uniroma3.siw.controller.validator.ProviderValidator;
import it.uniroma3.siw.model.Provider;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ProviderRepository;
import it.uniroma3.siw.repository.ImageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ProviderController {
    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProviderValidator providerValidator;

    @GetMapping("/admin/indexProvider")
    public String indexProvider(){
        return "/admin/indexProvider.html";
    }

    @GetMapping("/admin/formNewProvider")
    public String newProvider(Model model){
        model.addAttribute("provider",new Provider());
        return "/admin/formNewProvider.html";
    }

    @PostMapping("/admin/uploadProvider")
    public String newprovider(Model model, @Valid @ModelAttribute("provider") Provider provider, BindingResult bindingResult, @RequestParam("file") MultipartFile image) throws IOException {
        this.providerValidator.validate(provider,bindingResult);
        if(!bindingResult.hasErrors()){
            Image picture = new Image(image.getBytes());
            this.imageRepository.save(picture);
            provider.setLogo(picture);

            this.providerRepository.save(provider);

            model.addAttribute("provider",provider);
            return "provider.html";
        } else {
            return "/admin/formNewProvider.html";
        }
    }

    @GetMapping("/providers")
    public String showAllProviders(Model model){
        model.addAttribute("providers",this.providerRepository.findAll());
        return "providers.html";
    }

    @GetMapping("/providers/{providerId}")
    public String getProviders(Model model,@PathVariable("providerId") Long id){
        Provider provider = this.providerRepository.findById(id).get();
        model.addAttribute("provider", this.providerRepository.findById(id).get());

        model.addAttribute("starredProducts", provider.getStarredProducts());
        return "provider.html";
    }

    @PostMapping("/searchProvider")
    public String searchProvider(Model model, @RequestParam String name) {
        if (name.length() == 0) {
            model.addAttribute("providers", this.providerRepository.findAll());
        } else {
            model.addAttribute("providers", this.providerRepository.findByName(name));
        }
        return "providers.html";
    }
}
