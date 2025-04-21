package com.bom.ers;

import java.util.*;

public class BlackjackGame {
    private final Deck deck = new Deck();
    private final List<Hand> playerHands = new ArrayList<>();
    private Hand dealerHand;
    private int currentHandIndex = 0;
    private int balance = 1000;

    public void startNewGame() {
        playerHands.clear();
        dealerHand = new Hand();
        Hand firstHand = new Hand();
        firstHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        firstHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHands.add(firstHand);
        currentHandIndex = 0;
    }

    public void setInitialBet(int amount) {
        if (!playerHands.isEmpty()) {
            playerHands.get(0).setBet(amount);
            balance -= amount;
        }
    }

    public void hit() {
        getCurrentPlayerHand().addCard(deck.drawCard());
    }

    public void stand() {
        nextHand();
    }

    public void doubleDown() {
        Hand hand = getCurrentPlayerHand();
        hand.doubleDown();
        hand.addCard(deck.drawCard());
        nextHand();
    }

    public void surrender() {
        getCurrentPlayerHand().surrender();
        nextHand();
    }

    public void split() {
        Hand hand = getCurrentPlayerHand();
        if (!hand.canSplit()) return;
        Card splitCard = hand.getCards().remove(1);
        Hand newHand = new Hand();
        newHand.addCard(splitCard);
        newHand.addCard(deck.drawCard());
        hand.addCard(deck.drawCard());
        hand.setSplit(true);
        newHand.setBet(hand.getBet());
        playerHands.add(currentHandIndex + 1, newHand);
    }
    
    private void settleBets() {
        int dealerValue = dealerHand.getValue();
        boolean dealerBust = dealerHand.isBusted();

        for (Hand hand : playerHands) {
            int bet = hand.getBet();
            int playerValue = hand.getValue();

            if (hand.isSurrendered()) {
                balance += bet / 2; // surrender는 절반만 돌려줌
            } else if (hand.isBusted()) {
                // nothing
            } else if (dealerBust || playerValue > dealerValue) {
                balance += bet * 2; // 배팅한 돈 + 이익
            } else if (playerValue == dealerValue) {
                balance += bet; // 베팅금만 환불
            }
        }
    }


    private void nextHand() {
        if (currentHandIndex < playerHands.size() - 1) {
            currentHandIndex++;
        } else {
            dealerPlay();
            settleBets(); //이기거나 지거나 balance 정산
            currentHandIndex = playerHands.size(); // 게임 종료 처리
        }
        System.out.println("currentHandIndex: " + currentHandIndex + ", playerHands.size: " + playerHands.size());
    }

    private void dealerPlay() {
        System.out.println("---- Dealer play started ----");
        while (dealerHand.getValue() < 17) {
            Card c = deck.drawCard();
            dealerHand.addCard(c);
            System.out.println("Dealer drew: " + c.display());
        }
        System.out.println("Dealer final value: " + dealerHand.getValue());
    }

    public Hand getCurrentPlayerHand() {
        return playerHands.get(currentHandIndex);
    }

    public List<Hand> getPlayerHands() {
        return playerHands;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public boolean isGameOver() {
        return dealerHand != null && currentHandIndex >= playerHands.size();
    }

    public List<String> getResults() {
        List<String> results = new ArrayList<>();
        int dealerValue = dealerHand.getValue();
        boolean dealerBust = dealerHand.isBusted();

        for (Hand hand : playerHands) {
            int bet = hand.getBet();
            int playerValue = hand.getValue();

            if (hand.isSurrendered()) {
                results.add("Surrendered. Lost half of bet: -" + (bet / 2));
            } else if (hand.isBusted()) {
                results.add("Busted! Lost: -" + bet);
            } else if (dealerBust) {
                results.add("Dealer busted! Won: +" + (bet * 2));
            } else if (playerValue > dealerValue) {
                results.add("Win! +" + (bet * 2));
            } else if (playerValue < dealerValue) {
                results.add("Lose. -" + bet);
            } else {
                results.add("Push (draw). No change.");
            }
        }

        return results;
    }

    
    public int getBalance() {
        return balance;
    }
    

    
}