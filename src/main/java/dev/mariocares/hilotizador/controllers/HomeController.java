package dev.mariocares.hilotizador.controllers;
import dev.mariocares.hilotizador.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @GetMapping("/")
    public String Home(){
        return "index";
    }

    @GetMapping("/tuit/{id}")
    public String Tuit(@PathVariable String id, Model model){
        // SE SUPONE QUE LLAMO AL FINAL DEL HILO, EL ÚLTIMO TUIT
        Twitter t = new Twitter(id);
        model.addAttribute("Usuario", t.usuario());
        model.addAttribute("Tuits", t.hilo());
        return "index";
    }
}
