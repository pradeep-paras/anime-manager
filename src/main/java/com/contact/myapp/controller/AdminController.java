package com.contact.myapp.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contact.myapp.dao.AnimeRepository;
import com.contact.myapp.dao.UserRepository;
import com.contact.myapp.entities.Anime;
import com.contact.myapp.entities.User;
import com.contact.myapp.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimeRepository animeRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("User name " + userName);

        User user = userRepository.getUserByUserName(userName);
        System.out.println("User " + user);
        model.addAttribute("user", user);

    }

    @GetMapping("/admin")
    public String dashboard1() {
        return "redirect:/admin/0";
    }

    @RequestMapping("/admin/{page}")
    public String dashboard(@PathVariable("page") Integer page, Model model,Principal principal){

        Pageable pageable = PageRequest.of(page, 10);
        Page<Anime> anime = this.animeRepository.findAll(pageable);
        model.addAttribute("anime", anime);
        model.addAttribute("anime1", null);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", anime.getTotalPages());

        return "admin/admin_home";
    }

    @RequestMapping("/admin/process-anime")
    public String saveAnime(@Valid @ModelAttribute("anime") Anime anime,
        BindingResult error_value, Model model,
        Principal principal, HttpSession session){

            try {
                System.out.println(error_value);
                if(error_value.hasErrors()){
                    model.addAttribute("anime", anime);
                    return "redirect:/admin";
                }
                System.out.println("Anime "+ anime);

                this.animeRepository.save(anime);
                session.setAttribute("message", new Message("data saved successfully...", "success"));   
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("anime", anime);
                session.setAttribute("message", new Message("Something went wrong", "danger"));
            }
            return "redirect:/admin";
        }

    // deleting anime
    @GetMapping("admin/delete/{aId}")
    public String deleteAnime(@PathVariable("aId") Integer aId, 
        Principal principal, HttpSession session){
            
            try {
                System.out.println("before anime deleted");
                this.animeRepository.deleteById(aId);
                System.out.println("after anime deleted");
                session.setAttribute("message", new Message("anime deleted successfully...", "success"));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
                session.setAttribute("message", new Message("Something went wrong", "danger"));
                return "redirect:/admin";
            }
            return "redirect:/admin";
    }

    // redirect to update anime page
    @PostMapping("admin/update-anime/{aId}")
    public String updateAnime(@PathVariable("aId") Integer aId, 
        Principal principal, HttpSession session, Model model){

        Anime anime = animeRepository.findById(aId).get();

        model.addAttribute("anime", anime);
        
        return "admin/update-anime";
    }

    // update anime details
    @PostMapping("admin/process-update-anime")
    public String updateAnimeDetails(@ModelAttribute Anime anime, 
        Principal principal, HttpSession session) {
            // , anime.getImageUrl(), anime.getStatus(), anime.getDescription()
            // this.animeRepository.updateAnime(anime.getaId(), anime.getAnime_name());

            this.animeRepository.save(anime);
            return "redirect:/admin";
        }
}
