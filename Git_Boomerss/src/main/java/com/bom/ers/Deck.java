package com.bom.ers;

import java.util.*;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private final String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    public Deck() {
        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                int value = i < 9 ? i + 2 : (ranks[i].equals("A") ? 11 : 10);
                cards.add(new Card(suit, ranks[i], value));
            }
        }
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty. No more cards to draw.");
        }
        return cards.remove(0);
    }
}