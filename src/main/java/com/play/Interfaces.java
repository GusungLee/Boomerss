package com.play;

import java.util.ArrayList;

interface PlayerAction {
	public void bet();
	public void surrender();
	public void insurance();
	public void split();
	public void doubleDown();
}

interface DealerAction {
	public ArrayList<ArrayList<String>> dealCards(int playerNum);
	public void giveHitCard(Player p);
}

interface PublicAction {
	public void hit();
	public void stand();
}


public class Interfaces {

}
