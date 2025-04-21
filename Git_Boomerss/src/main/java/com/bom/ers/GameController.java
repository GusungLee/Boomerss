package com.bom.ers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
@SessionAttributes("game")
public class GameController {

    @ModelAttribute("game")
    public BlackjackGame createGame() {
        return new BlackjackGame();
    }

    @GetMapping("/start")
    public String startGameForm(@ModelAttribute("game") BlackjackGame game, Model model) {
        if (game == null) {
            game = new BlackjackGame(); // 예외 방지
        }
        model.addAttribute("balance", game.getBalance());
        return "bet";
    }



    @PostMapping("/start")
    public String startGame(@RequestParam("bet") int bet, @ModelAttribute("game") BlackjackGame game, Model model) {
        game.startNewGame();
        game.setInitialBet(bet);

        model.addAttribute("playerHands", game.getPlayerHands());
        model.addAttribute("dealerHand", game.getDealerHand());
        model.addAttribute("currentHand", game.getCurrentPlayerHand());
        model.addAttribute("gameOver", game.isGameOver());
        model.addAttribute("balance", game.getBalance());

        if (game.isGameOver()) {
            model.addAttribute("results", game.getResults());
        }

        return "game"; // 전체 game.html 렌더링
    }

    @PostMapping("/hit")
    public String hit(@ModelAttribute("game") BlackjackGame game, Model model) {
        game.hit();
        return reloadGame(model, game);
    }

    @PostMapping("/stand")
    public String stand(@ModelAttribute("game") BlackjackGame game, Model model) {
        game.stand();
        return reloadGame(model, game);
    }

    @PostMapping("/double")
    public String doubleDown(@ModelAttribute("game") BlackjackGame game, Model model) {
        game.doubleDown();
        return reloadGame(model, game);
    }

    @PostMapping("/surrender")
    public String surrender(@ModelAttribute("game") BlackjackGame game, Model model) {
        game.surrender();
        return reloadGame(model, game);
    }

    @PostMapping("/split")
    public String split(@ModelAttribute("game") BlackjackGame game, Model model) {
        game.split();
        return reloadGame(model, game);
    }

    private String reloadGame(Model model, BlackjackGame game) {
        boolean isOver = game.isGameOver();

        model.addAttribute("playerHands", game.getPlayerHands());
        model.addAttribute("dealerHand", game.getDealerHand());
        model.addAttribute("currentHand", isOver ? null : game.getCurrentPlayerHand());
        model.addAttribute("gameOver", isOver);
        model.addAttribute("balance", game.getBalance());

        if (isOver) {
            model.addAttribute("results", game.getResults());
        }

        return "game"; 
    }


}

