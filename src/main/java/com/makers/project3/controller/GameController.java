package com.makers.project3.controller;

import com.makers.project3.repository.CardRepository;
import com.makers.project3.repository.DeckRepository;
import com.makers.project3.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameController {


    @GetMapping("/newgame")
        public String newGame(){

        return "new game here";
    }

}
