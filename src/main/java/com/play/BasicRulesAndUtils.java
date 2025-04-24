package com.play;

import java.security.SecureRandom;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Cards {
	SecureRandom srd = new SecureRandom();
	private static ArrayList<String> deck = new ArrayList<>();
	private static ArrayList<String> dealerDeck = new ArrayList<>();
	private final String[] CARD_TYPES = {"♠️Spade","♥Heart","◆Diamond","♣Clover"};
	private final String[] CARD_NUMS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

	// 생성자
	// 스,하,다,클 A~K 다 갖다넣고 섞음
	public Cards() {
		for (String types : CARD_TYPES) {
			for (String nums : CARD_NUMS) {
				deck.add(types+" "+nums);
			}
		}
		shuffleDeck();
	}

	public static ArrayList<String> getDealerDeck() {
		return dealerDeck;
	}
	public static void setDealerDeck(ArrayList<String> cards) {
		dealerDeck = cards;
	}
	
	public String drawFromDeck() {
		String drawnCard = deck.get(0);
		deck.remove(drawnCard);		
		return drawnCard;
	}
	
	public void resetDeck() {
		deck.clear();
		for (String types : CARD_TYPES) {
			for (String nums : CARD_NUMS) {
				deck.add(types + " " + nums);
			}
		}

		shuffleDeck();		
	}

	public static ArrayList<String> getDeck() {
		return deck;
	}
	
	public void shuffleDeck() {
		Collections.shuffle(deck, srd);
	}
}


public class BasicRulesAndUtils {
	Scanner sc = new Scanner(System.in);
	
	public void roundStart(Dealer d, ArrayList<Player> players) {
		System.out.println("라운드를 시작합니다... ");
		for (Player p : players) {
			p.setAlive();
			p.setCurrentCards(null);
			p.setSumCurrentCards(0);
			p.setBetting(0);
			p.setStatus(null);
			p.setRoundResult(null);
			p.setInsuranceMap(new HashMap<>());
		}
		d.setDealerHand(null);
		d.setScore(0);
		d.setStatus(null);
	}

	public void roundEnd(Dealer d, ArrayList<Player> players) {
		for (Player p : players) {
			int playerEarn = 0;
			int dealerLoss = 0;

			if (p.getRoundResult().contains("BLACKJACK")) {
				if (p.getBetting()%10<5) playerEarn=p.getBetting()*25/10; else playerEarn=p.getBetting()*25/10 + 1;
				dealerLoss = playerEarn-p.getBetting();
				d.setCasinoMoney(d.getCasinoMoney()-dealerLoss);
				p.setMoney(p.getMoney()+playerEarn);
			} else if (p.getRoundResult().contains("SURRENDER")) {
				p.setMoney(p.getMoney()+p.getBetting()/2);
			} else if (p.getRoundResult().contains("WIN")) {
				playerEarn=p.getBetting()*2;
				dealerLoss=p.getBetting();
				d.setCasinoMoney(d.getCasinoMoney()-dealerLoss);
				p.setMoney(p.getMoney()+playerEarn);
			} else if (p.getRoundResult().contains("DRAW")) {
				p.setMoney(p.getMoney()+p.getBetting());
			}

			if (!p.getInsuranceMap().isEmpty()) {
				if (d.getStatus().contains("BLACK")) {
					System.out.println(p.getName() + " 딜러 블랙잭 적중!!");
					playerEarn=p.getInsuranceMap().get(true);
				} else {
					System.out.println(p.getName() + " 보험금 따잇!!");
					playerEarn=p.getInsuranceMap().get(false);
				}
				dealerLoss=-playerEarn;
				d.setCasinoMoney(d.getCasinoMoney()+dealerLoss);
				p.setMoney(p.getMoney()+playerEarn);
			}

		}
	}
	
	public int welcomePlayers() {
		boolean validInput = false;
		int playersNum = 0;
		
		while (!validInput){
			System.out.print("빅스타랜드 : 빅랙잭 도박장에 어세요서오. 몇 분이신가요? (1~7): ");
			playersNum = sc.nextInt();
			if (playersNum<=7 && playersNum>0) {
				validInput=true;
			}
		}
		return playersNum;
	}

//	public int printActions() {
//		int action=0;
//		String tempAction = null;
//		System.out.println("==== 행동목록 ====");
//		System.out.println("1. Hit");
//		System.out.println("2. Stand");
//		System.out.println("3. Double Down");
//		System.out.println("4. Surrender");
//		System.out.println("5. Split 고르면에러낼거야");
//		System.out.println("0. 판을 엎고 달아나기");
//		System.out.print("행동 선택: ");
//		while (true) {
//			tempAction = sc.nextLine();
//			if (tempAction.matches("^[123450]$")){
//				break;
//			} else {
//				System.out.println("==== 행동목록 ====");
//				System.out.println("1. Hit");
//				System.out.println("2. Stand");
//				System.out.println("3. Double Down");
//				System.out.println("4. Surrender");
//				System.out.println("5. Split 고르면에러낼거야");
//				System.out.println("0. 판을 엎고 달아나기");
//				System.out.println(tempAction + " -> 유효한 입력이 아닙니다. ");
//				System.out.print("다시 입력해 주세요: ");
//
//			}
//		}
//		action = Integer.parseInt(tempAction);
//		switch (tempAction) {
//			case "1": tempAction="1. Hit"; break;
//			case "2": tempAction="2. Stand"; break;
//			case "3": tempAction="3. Double Down"; break;
//			case "4": tempAction="4. Surrender"; break;
//			case "5": tempAction="5. Split"; break;
//		}
//        System.out.println("선택한 행동 -> " + tempAction);
//		return action;
//	}
	
	// 버스트 
	
	public void bust(Player p) {
				
	}
	
	// 블랙잭
	
	public void blackJack(Player p) {
//		System.out.println(p.getName() + " WINS!");
//		p.setMoney((int) (p.getMoney()+p.getBetting()+1.5*p.getBetting()));
//		p.printMoney();
	}

	public void playerWin(Player p) {

	}
	
	// 딜러버스트 -> 플레이어 승리
	
	public void dealerBust(Dealer d) {
		
	}
	
	// 딜러블랙잭 -> 동점이면 베팅반환, 아니면패배, 보험 구현할거면 보험 관련 로직 구성
	
	public void dealerBlackJack(Dealer d) {
		
	}

}


