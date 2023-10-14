package com.contact.myapp.controller;

// import java.util.List;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.myapp.dao.AnimeRepository;
import com.contact.myapp.dao.ContactRepository;
import com.contact.myapp.dao.RatingRepository;
import com.contact.myapp.dao.UserRepository;
import com.contact.myapp.entities.Anime;
import com.contact.myapp.entities.Contact;
import com.contact.myapp.entities.Rating;
import com.contact.myapp.entities.User;
import com.contact.myapp.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private RatingRepository ratingRepository;

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

    @GetMapping("/index")
    public String dashboard1() {
        return "redirect:/user/index/0";
    }

    @RequestMapping("/index/{page}")
    public String dashboard(@PathVariable("page") Integer page,Model model,Principal principal){

        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);

        Pageable pageable = PageRequest.of(page, 10);

        Page<Anime> anime = this.animeRepository.findByUsers(user, pageable);
        System.out.println("Animes User: "+anime.getNumber());
        
        System.out.println("Total Pages: "+anime.getTotalPages());
        System.out.println("Anime saved: "+anime.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", anime.getTotalPages());
        model.addAttribute("anime", anime);

        return "user/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        
        return "user/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
        @RequestParam("profileImage") MultipartFile file, 
        Principal principal, HttpSession session){

        try {
            System.out.println("--------------------------------------------------------------------------");
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if(file.isEmpty()){
                System.out.println("File is empty");
                contact.setImage("contact.jpg");
            } else {
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");
            }

            // add user details in contacts
            contact.setUser(user);

            // add contact details in user
            user.getContacts().add(contact);

            // save data in user and contact table
            this.userRepository.save(user);

            System.out.println("Data " + contact);
            session.setAttribute("message", new Message("data saved successfully...", "success"));
            
        } catch (Exception e){
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }
        
        return "/user/add_contact_form";
    }

    @GetMapping("/show-reviews/{page}")
    public String showReviews(@PathVariable("page") Integer page, 
        Model model, Principal principal) {

        String userName = principal.getName();
        User user  = this.userRepository.getUserByUserName(userName);
        // current page
        // per page size = 5
        Pageable pageable = PageRequest.of(page, 10);

        Page<Rating> ratings = this.ratingRepository.findRatingsByUser(user.getId(), pageable);

        System.out.println("Total Pages: "+ratings.getTotalPages());
        System.out.println("Rating saved: "+ratings.getTotalElements());
        model.addAttribute("ratings", ratings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", ratings.getTotalPages());

        return "/user/show_ratings";
    }

    @GetMapping("/show-contact/{cId}")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal){

        Optional<Contact> con = this.contactRepository.findById(cId);
        Contact contact = con.get();
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId() == contact.getUser().getId())
            model.addAttribute("contact", contact);
        
        return "user/contact_details";
    }

    @GetMapping("/delete/{rId}")
    public String deleteReview(@PathVariable("rId") Integer rId, Principal principal, HttpSession session){
        Optional<Rating> rat = this.ratingRepository.findById(rId);
        Rating rating = rat.get();

        System.out.println("Anime Total count"+rating.getAnime().getTotal_count());

        float newtotal = rating.getAnime().getTotal_count() - Float.parseFloat(rating.getRate());

        rating.getAnime().setTotal_count(newtotal);

        int total = rating.getAnime().getRatings().size();

        if(total > 1){
            rating.getAnime().setAnime_rating(newtotal/total);
        } else {
            rating.getAnime().setAnime_rating(newtotal);
        } 

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        //
        if(user.getId() == rating.getUser().getId()){
            // Unlink contact from user before deleting
            rating.setUser(null);
            rating.setAnime(null);
            
            this.ratingRepository.delete(rating);
            session.setAttribute("message", new Message("Rating deleted successfully", "success"));
        } else {
            System.out.println("You don't have permission to delete this review....");
        }

        return "redirect:/user/show-reviews/0";
    }

    // open update form
    @PostMapping("/update-contact/{cId}")
    public String updateContact(@PathVariable("cId") Integer cid, Model model){
        
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
 
        return "user/update_contact";
    }

    // update contacts
    @PostMapping("/process-update")
    public String updateContactDetails(@ModelAttribute Contact contact,
        @RequestParam("profileImage") MultipartFile file, 
        Principal principal, HttpSession session){
        try {
            System.out.println("Fetching contact details---------------------------------------");
            System.out.println(contact);
            System.out.println("contact.getcId() " + contact.getcId());
            System.out.println("contact.getName() " + contact.getName());
            System.out.println("contact.getImage() " + contact.getImage());
            Contact old_contact = this.contactRepository.findById(contact.getcId()).get();
            System.out.println("Old Contact " + old_contact);
            if(!file.isEmpty()){
                // image
                // 1. delete old image
                File deleteFile = new ClassPathResource("static/image").getFile();
                File oldfile = new File(deleteFile, old_contact.getImage());
                oldfile.delete();

                // 2. upload new image
                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage("contact.jpg");
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/show-contacts/0";
    }

    @GetMapping("/your-profile")
    public String yourProfile(Principal principal, Model model){

        User user = userRepository.getUserByUserName(principal.getName());
        model.addAttribute("user", user);

        return "user/update_user";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid @ModelAttribute User user, 
        BindingResult error_value, Model model, Principal principal,
        HttpSession session){
        try {
            if(error_value.hasErrors()){
                model.addAttribute("user", user);
                return "user/update_user";
            }

            User result = userRepository.save(user);
            model.addAttribute("user", result);
            session.setAttribute("message", new Message("User profile updated successfully!", "alert-success"));

            return "user/update_user";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !!" + e.getMessage(), "alert-danger"));
            
            return "user/update_user";
        }
    }

    @GetMapping("/settings")
    public String openSettings(){
        return "user/settings";
    }

    // change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, 
        @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session){
        
        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        if(this.passwordEncoder.matches(oldPassword, currentUser.getPassword())){
            
            currentUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Password changed successfully", "success"));
        } else {
            session.setAttribute("message", new Message("Incorrect old password", "danger"));
            return "redirect:/user/settings";
        }
        return "redirect:/user/settings";
    }

    // remove anime by user's profile
    @PostMapping("/remove-anime/{aId}")
    public String removeAnime(@PathVariable("aId") Integer aId, Principal principal, Model model, HttpSession session){
        
        Anime anime = animeRepository.findById(aId).get();
        if(principal == null){
            session.setAttribute("message", new Message("please login", "alert-danger"));
            return "redirect:/0";
        }
        
        // get user
        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);

        anime.getUsers().remove(user);
        
        this.animeRepository.save(anime);

        session.setAttribute("message", new Message("anime removed", "alert-success"));

        return "redirect:/user/index/0";
    }
}
