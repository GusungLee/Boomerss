package com.play;

import java.util.ArrayList;
import java.util.Scanner;


public class MainGamePlay {

	public static void main(String[] args) {
//		알게된 사실
//		내부 메서드에서 sc.close(); 가 있으면
//		그 안에서만 닫히는게 아니라
//		스캐너 자체가 아예 닫히는 식으로 동작하는듯함
//		따라서 메서드 내부에선 sc.close() 하지 말고
//		메인함수의 마지막에서만 닫게 해야할듯함
		Scanner sc = new Scanner(System.in);
		Cards cardsDeck = new Cards();
		BasicRulesAndUtils game = new BasicRulesAndUtils();
		ArrayList<Player> players = new ArrayList<>();
		Dealer d = new Dealer(cardsDeck);
		ArrayList<ArrayList<String>> initialCards;


		int playersNum = game.welcomePlayers();

		for (int i=1; i<=playersNum; i++) {
			Player p = new Player("Player" + i, 500, cardsDeck);
			p.printInfo();
			players.add(p);
		}

		while (!players.isEmpty()) {
		game.roundStart(d, players);


		// 베팅 프로세스
		for (Player plyr : players) {
			System.out.print(plyr.getName() + " - ");
			plyr.bet();
			System.out.println(plyr.getName() + " 님의 잔액: $" +  plyr.getMoney());
		}

		// 카드 뿌리기
		initialCards = d.dealCards(playersNum);
		System.out.println("딜러 오픈 카드: " + d.getDealerHand().getFirst());
		int playerId = 0;

		for (Player plyr : players) {
			plyr.setCurrentCards(initialCards.get(playerId));
			plyr.cardsCalc();
			plyr.printInfo();
			playerId++;
		}

		// 플레이어 행동 페이즈

		for (Player plyr : players) {
			int action = -1;
			if (plyr.isHaveTurn()) {
				System.out.println("\n" + plyr.getName() + " 님의 턴");
				System.out.println("딜러 오픈 카드: " + d.getDealerHand().getFirst());
				plyr.printCards();
				while (plyr.isHaveTurn()) {
					action = plyr.printActions(true);
					if (action==3 && !d.getDealerHand().getFirst().contains("A")) {
						System.out.println("보험은 딜러 오픈 카드가 A일 때만 가능합니다.");
						action=-1;
					} else if (action==1 && plyr.getSumCurrentCards()==21) {
						System.out.printf("현재 점수 %s, Hit을 권장하지 않습니다.\n", plyr.getCurrentCards());
						System.out.print("계속하려면 y를 입력해 주세요: ");
						String forceHit = sc.nextLine();
						if (forceHit.equalsIgnoreCase("y")) action=1;
						else {
							System.out.println("2. Stand");
							action=2;
						}
					}
					switch (action) {
						case 1: plyr.hit(); break;
						case 2: plyr.stand(); break;
						case 3: plyr.insurance(); break;
						case 4: plyr.surrender(); break;
						case 6: plyr.doubleDown(); break;
						case -1: break;
						case 0:
							System.out.println(plyr.getName() + " 님이 판을 엎고 도망칩니다.."); return;
                        default:
							System.out.println("아직 없는 기능을 누르려고 하지 마십시오");
                    }
					plyr.cardsCalc();
				}
			} else if (plyr.getStatus().contains("BLACK")) {
				System.out.println(plyr.getName() + ": "+ plyr.getStatus() + "!!");
				if (d.getDealerHand().getFirst().contains("A")) {

				}
			}

		}

		// 딜러 행동 페이즈
		while (d.cardsCalc()<17) {
			d.hit();
		} d.stand();
			System.out.print(d.getDealerHand() +"\n딜러 점수 : "+d.cardsCalc());
		if (!d.getStatus().isEmpty()) {
			System.out.printf(" (%s)\n",d.getStatus());
		}
		
		
//		if (d.cardsCalc()<17) d.hit(); else d.stand();
//		System.out.print(d.getDealerHand() +"\n딜러 점수 : "+d.cardsCalc());
//		if (!d.getStatus().isEmpty()) System.out.printf(" (%s)\n",d.getStatus()); else System.out.println();

		// 정산 페이즈
		ArrayList<Player> allInPlayers = new ArrayList<>();
		for (Player plyr : players) {
			if (plyr.getStatus().contains("BUST")) {
				plyr.bust();
			} else if (plyr.getStatus().contains("BLACK")) {
				if (d.getStatus().contains("BLACK")) plyr.draw();
				else plyr.blackJack();
			} else if (plyr.getStatus().contains("SURRENDER")) {
				plyr.printResult("🏳️SURRENDER");
			} else if (d.getScore()!= plyr.getSumCurrentCards()) {
				if (d.getScore() > plyr.getSumCurrentCards() && !d.getStatus().contains("BUST")) {
					plyr.lose();
				} else {
					plyr.win();
				}
			} else plyr.draw();

		}
		game.roundEnd(d, players);


		for (Player p : players) {
			if (p.getMoney()<=0) {
				playersNum--;
				allInPlayers.add(p);
				System.out.println(p.getName() + " 님이 전당포로 갔습니다...");
			}
		}
		players.removeAll(allInPlayers);

		}
		System.out.println("빅스타랜드: ㅋㅋ전원따잇 빠이");
		sc.close();
	}

}
