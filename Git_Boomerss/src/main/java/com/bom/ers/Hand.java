package com.bom.ers;

import java.util.*;

public class Hand {
    private final List<Card> cards = new ArrayList<>();
    private boolean surrendered = false;
    private boolean doubledDown = false;
    private boolean split = false;
    private int bet = 100; // 기본 베팅 금액

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getValue() {
        int total = 0;
        int aceCount = 0;
        for (Card c : cards) {
            total += c.getValue();
            if (c.getValue() == 11) aceCount++;
        }
        while (total > 21 && aceCount-- > 0) total -= 10;
        return total;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    public boolean isSurrendered() {
        return surrendered;
    }

    public void surrender() {
        this.surrendered = true;
    }

    public void doubleDown() {
        this.doubledDown = true;
        this.bet *= 2;
    }

    public boolean isDoubledDown() {
        return doubledDown;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }
}