package com.play;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dealer extends Cards implements PublicAction, DealerAction {
	
	private String name = "빅스타랜드";
	private int casinoMoney = 0;
	private static Cards cards_deck = new Cards();
	private static ArrayList<String> dealerHand;
	private int score;
	private String status=null;
	
	
	public Dealer(Cards c) {
		this.cards_deck=c;

	}

	// getStatus -> Status는 null로 둘 때가 많은데
	// lombok이 생성해주는 getStatus는 null일 때 Exception이 발생해서
	// status가 null이면 ""을 반환하도록 별도로 만들었음
	public String getStatus() {
		if (this.status==null) return "";
		else return this.status;
	}

	public static ArrayList<String> getDealerHand() {
		return dealerHand;
	}
	public static void setDealerHand(ArrayList<String> cardsDrawn) {
		dealerHand = cardsDrawn;
		cards_deck.setDealerDeck(cardsDrawn);
	}
	
//	public void giveCard(Player p, String targetCard) {
//		p.drawCard(targetCard);
//	}
//
//	public static Cards getCardsDeck() {
//		return cards_deck;
//	}

	public int cardsCalc() {
		int finalResult=0;
		ArrayList<String> cardNums = new ArrayList<>();

		for (String card : dealerHand) {
			String cardNum = card.substring(card.length()-2).trim();
			cardNums.add(cardNum);
		}

		for (String cardScore : cardNums) {
			if (Stream.of("J", "Q", "K").anyMatch(cardScore::contains)) {
				finalResult += 10;
			} else if (cardScore.equals("A")) {
				finalResult += 11;
				if (finalResult>21) {
					finalResult -= 10;
				}
			} else {
				finalResult += Integer.parseInt(cardScore);
			}
		}
		setScore(finalResult);

		if (finalResult == 21 && dealerHand.size()==2) {
			setStatus("🎉BLACK JACK");

		} else if (finalResult>21) {
			setStatus("💣BUST");
		}

		return finalResult;
	}


	// 카드 딜링 메서드
	// 2*(플레이어수+1) 장만큼 카드를 뽑고
	// 0번째 + (1+플레이어수)번째, 1번째 + (2+플레이어수)번째, ... 와 같이 서브 리스트를 만듬
	// 예를들어 플레이어수가 3이라면
	// 카드 리스트 0 1 2 3 4 5 6 7
	// 플레이어 순 1 2 3   1 2 3
	// 이렇게 가져가고 3번째, 7번째는 딜러 패가 되는 것
	// (카드리스트.get(플레이어수)) 번째 리스트는 미리 딜러 덱으로 할당한다
	@Override
	public ArrayList<ArrayList<String>> dealCards(int playerNum) {
		ArrayList<String> drawnCards = new ArrayList<>();

		System.out.println("저와 " + playerNum + "명의 플레이어에게 총 " + 2*(playerNum+1) + "장의 카드를 배분합니다.");
		
		for (int i=1; i<=2*(playerNum+1); i++) {
			drawnCards.add(cards_deck.drawFromDeck());
		}
		
		ArrayList<ArrayList<String>> cardsForEachPlayer = new ArrayList<>();
		for (int j=1; j<=playerNum+1; j++) {
			cardsForEachPlayer.add(new ArrayList<>(List.of(drawnCards.get(j-1),drawnCards.get(j+playerNum))));
		}
		setDealerHand(cardsForEachPlayer.get(playerNum));
		return cardsForEachPlayer;
	}
	@Override
	public void giveHitCard(Player p) {
		String drawnCard = drawFromDeck();
		p.drawCard(drawnCard);
	}
	@Override
	public void hit() {
		System.out.println("딜러의 현재 점수: " + this.score + "점, Hit합니다.");
		dealerHand.add(cards_deck.drawFromDeck());
		setDealerDeck(dealerHand);

	}
	@Override
	public void stand() {
		System.out.println("딜러의 현재 점수: " + this.score + "점, Stand합니다.");
	}
}
