package com.contact.myapp.controller;

import java.security.Principal;
import java.util.Optional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.myapp.dao.AnimeRepository;
import com.contact.myapp.dao.RatingRepository;
import com.contact.myapp.dao.UserRepository;
import com.contact.myapp.entities.Anime;
import com.contact.myapp.entities.Rating;
import com.contact.myapp.entities.User;
import com.contact.myapp.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private RatingRepository ratingRepository;

    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        if(principal != null){
            String userName = principal.getName();
            System.out.println("User name " + userName);

            User user = userRepository.getUserByUserName(userName);
            System.out.println("User " + user);
            model.addAttribute("profile", user);
        }
    }

    @GetMapping("/")
    public String Home1(){
        return "redirect:/0";
    }
    
    @GetMapping("/{page}")
    public String Home(@PathVariable("page") Integer page, Model model){
        model.addAttribute("title", "Home - Smart Contact Manager");
        Pageable pageable = PageRequest.of(page, 12);
        Page<Anime> anime = this.animeRepository.findAll(pageable);
        model.addAttribute("anime", anime);        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", anime.getTotalPages());
        
        return "home"; 
    }
    
    @GetMapping("/about")
    public String About(Model model){
        model.addAttribute("title", "About Contact - Smart Contact Manager");
        return "about"; 
    }

    @GetMapping("/signup")
    public String Signup(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("title", "About Contact - Smart Contact Manager");
        return "signup"; 
    }
    
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
        BindingResult error_value,
        @RequestParam(value = "check_box", defaultValue = "false") boolean check_box, 
        Model model, HttpSession session){
        try {
            System.out.println("Check_box" + check_box);
            if(!check_box) {
                throw new Exception("You have not agreed on terms and conditions");
            }
            
            System.out.println(error_value);
            if(error_value.hasErrors()){
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(check_box);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("User " + user);
            User result = userRepository.save(user);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("User saved " + result);
            // model.addAttribute("user", result);
                
            // for blank all user attribute details
            model.addAttribute("user", user = new User());
            session.setAttribute("message", new Message("Data saved successfully", "alert-success"));        
                
            return "signup";
            

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
            
            return "signup"; 
        }
        
    }

    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title", "login page");
        return "login";
    }

    // @PostMapping("/login")
    // public String Signin(Model model){
    //     System.out.println("Login");
    //     model.addAttribute("title", "login page");
    //     return "login";
    // }

    @GetMapping("/anime/{aId}")
    public String showAnimeDetails(@PathVariable("aId") Integer aId,
        Model model, Principal principal){
        try {    
        Optional<Anime> ani = this.animeRepository.findById(aId);
        Anime anime = ani.get();

        Pageable pageable = PageRequest.of(0, 20);

        Page<Rating> ratings = this.ratingRepository.findRatingsByAnimeName(anime.getaId(), pageable);        
        
        boolean flag = false;

        if(principal != null){
            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);
            flag = anime.getUsers().contains(user);
        }

        model.addAttribute("flag", flag);
        model.addAttribute("anime", anime);
        model.addAttribute("ratings", ratings);
        model.addAttribute("fields", null);
    //     String userName = principal.getName();
    //     User user = this.userRepository.getUserByUserName(userName);

    //     if(user.getId() == contact.getUser().getId())
    //         model.addAttribute("contact", contact);
        
        return "/anime_details";
        
    } catch(Exception e) {
            e.printStackTrace();
            return "/anime_details";
        }
    }

    @PostMapping("/anime/process-rating/{aId}")
    public String processRating(@Valid @ModelAttribute Rating rating,
        BindingResult error_value,
        @PathVariable("aId") Integer aId,
        Model model,
        Principal principal, HttpSession session){

        try {
            System.out.println("--------------------------------------------------------------------------");
            if(principal == null){
                return "redirect:/signup";
            }

            Optional<Anime> ani = this.animeRepository.findById(aId);
            Anime anime = ani.get();

            if(error_value.hasErrors()){
                System.out.println("error_value" + error_value);
                model.addAttribute("anime", anime);
                model.addAttribute("rating", rating);
                model.addAttribute("fields", true);
                return "/anime_details";
            }

            model.addAttribute("fields", false);

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            float total = anime.getTotal_count();

            int size = anime.getRatings().size();
            size++;
            
            total = total + Float.parseFloat(rating.getRate());
            
            anime.setTotal_count(total);
            String r = String.format("%.2f", (total/size));
            anime.setAnime_rating(Float.parseFloat(r));

            rating.setAnime(anime);

            // add user details in ratings
            rating.setUser(user);

            anime.getRatings().add(rating);

            // add rating details in user
            user.getRatings().add(rating);

            // save data in user and rating table
            this.ratingRepository.save(rating);

            session.setAttribute("message", new Message("data saved successfully...", "success"));
            
        } catch (Exception e){
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }
        
        return "redirect:/anime/{aId}";
    }

    @PostMapping("/search")
    public String searchAnime(@Valid @ModelAttribute("search") String search, Model model){

        List<Anime> anime = this.animeRepository.getAnimeByAnimeName(search);

        // List<Anime> anime = this.animeRepository.findByAnime_NameContaining(search);
        model.addAttribute("anime", anime);
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPage", 1);

        return "home";
    }

    // Saved anime by user's profile
    @PostMapping("/anime/save/{aId}")
    public String saveAnime(@PathVariable("aId") Integer aId, Principal principal, Model model, HttpSession session){
        
        Anime anime = animeRepository.findById(aId).get();
        if(principal == null){
            session.setAttribute("message", new Message("please login", "alert-danger"));
            return "redirect:/anime/{aId}";
        }
        
        // get user
        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);

        boolean flag = false;

        if(principal != null){
            flag = anime.getUsers().contains(user);
        }

        if(flag){
            anime.getUsers().remove(user);
            flag = false;
        } else {
            anime.getUsers().add(user);
            flag = true;
        }
        
        this.animeRepository.save(anime);

        Pageable pageable = PageRequest.of(0, 20);

        Page<Rating> ratings = this.ratingRepository.findRatingsByAnimeName(anime.getaId(), pageable);

        model.addAttribute("flag", flag);

        model.addAttribute("anime", anime);
        model.addAttribute("ratings", ratings);
        model.addAttribute("fields", null);
        session.setAttribute("message", new Message("anime saved", "alert-success"));

        return "/anime_details";
    }

}
