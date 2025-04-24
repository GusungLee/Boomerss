package com.play;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Stream;

@Getter
@Setter
public class Player implements PublicAction, PlayerAction {
	private String name;
//	private int playerId;
	private int money;
	private int betting;
	private boolean isAlive;
	private boolean haveTurn;
	private ArrayList<String> currentCards;
	private int sumCurrentCards;
	private String status;
	private String roundResult;
	public static Cards cards_deck= new Cards();
	private HashMap<Boolean, Integer> insuranceMap;
	Scanner sc = new Scanner(System.in);
	//	생성자
	// name = 이름 (Player 1, 2, 3 ...)
	// money= 최대 소지금 (500)
	// currentCards = 손패
	// sumCurrentCards = 손패 점수 계산 결과
	// betting = 현재 라운드 베팅금액
	// isAlive = bust되거나 surrender할 경우 등 false로 전환
	// playerId = 순서를 표기하기 위해 만들었던 값인데... 사용처가 딱히 없는듯
	// status = BLACK JACK 또는 BUST 상태 표기 *아닐 경우 null
	// cards_deck = static Cards 클래스. 공용덱이라서 이렇게 표시함
	// haveTurn = 블랙잭이 되면 살아있지만 턴은 없기 때문에 bool 변수 하나 더 사용했음
	public Player (String name, int maxExpend, Cards c) {
		this.name = name;
		this.money = maxExpend;
		this.currentCards = new ArrayList<>();
		this.sumCurrentCards = 0;
		this.betting = 0;
		this.isAlive = true;
		this.haveTurn=true;
//		this.playerId=id;
		this.status=null;
		this.roundResult=null;
		this.cards_deck=c;
		this.insuranceMap= new HashMap<>();
	}

	public void putInsurance(int addBet) {
		insuranceMap.put(false, -addBet);
		insuranceMap.put(true, 2*addBet);
	}


	// isAlive 초기화 -> 새 라운드 시작 시 패배했던 플레이어 살리는데에 씀
	public void setAlive() {
		this.isAlive=true;
		setHaveTurn(true);
	}
	
	// isAlive = false -> Surrender, Bust 등의 이유로 이번 라운드에서 패배한 플레이어 표시
	public void setDead() {
		this.isAlive=false;
		setHaveTurn(false);
	}

	// getStatus -> Status는 null로 둘 때가 많은데
	// lombok이 생성해주는 getStatus는 null일 때 Exception이 발생해서
	// status가 null이면 ""을 반환하도록 별도로 만들었음
	public String getStatus() {
		if (this.status==null) return "";
		else return this.status;
	}

	public void setStatus(String stat) {
		if (stat!=null) this.status = getStatus() + stat;
		else this.status=null;
	}



	//	베팅 메서드: 인수로 들어온 금액만큼 베팅함
	// 현재 소지금에서 베팅금액만큼 빼고 베팅금액을 저장
//	기존에는 이 메서드가 setMoney였는데
//	통상적인 setMoney랑 사용법이 다르다보니
//	나중에 헷갈릴것같아서 바꿨음
	
	public void betMoney(int betAmount) {
		this.betting = 0;
		int moneyAfterBet = this.money - betAmount;
		if (moneyAfterBet < 0) {
			System.out.println("베팅 불가: 현재 잔액 $" + this.money);
		} else {
			setMoney(moneyAfterBet);
			this.betting = betAmount;
		}
	}
	
	// 카드 합계 구하기
	// 1. 카드의 수 부분만 떼서 (마지막 2글자를 substring 후 trim) 별도 ArrayList에 저장
	// 2. 카드 합계 계산: 한 칸씩 순회하면서
	//   1) J,Q,K면 10을 더함
	//   2) 정수면 parseInt해서 더함
	//   3) A면 11을 더하고, 더한 결과가 21을 넘으면 거기에서 10을 뺌 (=A를 1로 사용)
	// 3. 특수 상황 검증
	//   1) 덱이 2장이면서 합이 21이면 Black Jack
	//   2) 합이 21을 초과하면 BUST
	public int cardsCalc() {
		int finalResult=0;
		ArrayList<String> cardNums = new ArrayList<>();
		
		for (String card : this.currentCards ) {
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
		setSumCurrentCards(finalResult);

		if (this.sumCurrentCards == 21 && this.currentCards.size()==2) {
			blackJack();
		} else if (this.sumCurrentCards>21) {
			bust();
		}
//		if (getStatus()!=null) {
//			System.out.println(this.status);
//		}
		return finalResult;
	}


	// 턴마다의 플레이어 행동에 대한 메인 메서드
	public int printActions(boolean insuranceAble) {
		Scanner sc = new Scanner(System.in);
		int action=0;
		String tempAction = null;
		if (getStatus().contains("BLACK")) {
			// 보험 관련된것만 받은다음 바로 리턴해버리게 구성
			printActionOnly(insuranceAble);
			System.out.print("딜러 블랙잭에 베팅하시겠습니까? (Y/N)");
			if (sc.nextLine().equalsIgnoreCase("Y")) insurance();
		}
		printActionOnly(true);
		System.out.print("행동 선택: ");
		while (true) {
			tempAction = sc.nextLine();
			if ((tempAction.matches("^[12450]$")) ||
					(tempAction.matches("^6$") && getMoney()>=getBetting() && getCurrentCards().size()==2)) {
				break;
			} else if (tempAction.matches("^3$")) {
				if (!insuranceAble) {
					System.out.println("이미 보험에 베팅하였습니다.");
					System.out.print("다시 입력해 주세요: ");
				} else break;
			} else {
				printActionOnly(insuranceAble);
				System.out.println(tempAction + " -> 유효한 입력이 아닙니다. ");
				System.out.print("다시 입력해 주세요: ");

			}
		}
		action = Integer.parseInt(tempAction);
		switch (tempAction) {
			case "1": tempAction="1. Hit"; break;
			case "2": tempAction="2. Stand"; break;
			case "3": tempAction="3. Insurance"; break;
			case "4": tempAction="4. Surrender"; break;
			case "5": tempAction="5. Split"; break;
			case "6": tempAction="6. Double Down"; break;
		}
		System.out.println("선택한 행동 -> " + tempAction);
		return action;
	}
	// 턴마다의 플레이어 행동에 대한 메인 메서드
	// 였던것
	// printActions 에는 액션을 프린트하는 기능만 담고싶어서
	// 리뉴얼했음
//	public int printActions() {
//		Scanner sc = new Scanner(System.in);
//		boolean end = false;
//		int action=0;
//		String tempAction = null;
//		int insuranceBet;
//		boolean insuranceAble = true;
//		printActionOnly(true);
//		System.out.print("행동 선택: ");
//		while (!end) {
//			tempAction = sc.nextLine();
//			if (tempAction.matches("^[124560]$")){
//				break;
//			} else if (tempAction.matches("^3$") && insuranceAble) {
//				System.out.print("보험 베팅(0 입력으로 취소): ");
//				try {
//					insuranceBet = sc.nextInt();
//					if (insuranceBet == 0) continue;
//				} catch (InputMismatchException ime) {
//					continue;
//				}
//				System.out.println(getName() + " 님이 보험 베팅: $" + insuranceBet);
//				putInsurance(insuranceBet);
//				insuranceAble=false;
//				printActionOnly(false);
//			} else {
//				printActionOnly(insuranceAble);
//				System.out.println(tempAction + " -> 유효한 입력이 아닙니다. ");
//				System.out.print("다시 입력해 주세요: ");
//
//			}
//		}
//		action = Integer.parseInt(tempAction);
//		switch (tempAction) {
//			case "1": tempAction="1. Hit"; break;
//			case "2": tempAction="2. Stand"; break;
//			case "3": tempAction="3. Insurance"; break;
//			case "4": tempAction="4. Surrender"; break;
//			case "5": tempAction="5. Split"; break;
//			case "6": tempAction="6. Double Down"; break;
//		}
//		System.out.println("선택한 행동 -> " + tempAction);
//		return action;
//	}

	// 예외처리 하다보니까 액션 출력만 서너번 해야될것같아서 만듬
	// 오직 printActions에 넣기 위해서만 쓰임
	public void printActionOnly (boolean insuranceAble) {
		if (!getStatus().contains("BLACK")) {
			System.out.println("==== 행동목록 ====");
			System.out.println("1. Hit");
			System.out.println("2. Stand");
			if (insuranceAble && cards_deck.getDealerDeck().getFirst().contains("A"))
				System.out.println("3. Insurance");
			System.out.println("4. Surrender");
			System.out.println("5. Split 고르면에러낼거야");
			if (getMoney() >= getBetting() && getCurrentCards().size() == 2) System.out.println("6. Double Down");
			System.out.println("0. 판을 엎고 달아나기");
		} else {
			if (insuranceAble && cards_deck.getDealerDeck().getFirst().contains("A")) {
				System.out.println("보험권유");
			}
		}
	}
	
	
	// Hit 등에서 쓸 메서드
	// 뽑힌 카드를 인수로 받아서 손패에 추가함
	// Cards.drawFromDeck() 과 연계하여 사용
	public void drawCard(String drawnCard) {
		System.out.println(this.name + "님이 " + drawnCard + "카드를 받았습니다.");
		this.currentCards.add(drawnCard);
	}

	//	인터페이스 메서드	

	// 베팅
	// 베팅할 금액을 스캐너 입력으로 받고 betMoney 메서드를 호출함
	// betMoney는 입력받은 금액을 저장하고, 그만큼을 소지금에서 빼는 메서드
	// 블랙잭에서 베팅은 라운드 시작 시 1회만 이루어지므로, betMoney 내부에서 betting 을 초기화 및 Validate
	// 따라서 베팅 금액이 invalid 하면 베팅 금액 스캔을 계속 반복하게 됨
	@Override
	public void bet() {
		Scanner sc = new Scanner(System.in);
		while (this.betting == 0) {
			System.out.print("현재 소지금 $" + this.money + ", 베팅할 금액: $");
			int betAmount = sc.nextInt();
			betMoney(betAmount);			
		}
		System.out.print("Player.bet() - ");	
		System.out.println(name + "님이 $" + betting + " 베팅" );
	}
	
	@Override
	public void insurance() {
		int amount=0;
		setStatus("🛡️INSURANCE");
		while (true) {
			System.out.print("보험 베팅(0 입력으로 취소, 최대금액 $"+ getBetting()/2 +"): ");
			try {
				amount = sc.nextInt();
			} catch (InputMismatchException ime) {
				System.out.println("유효한 입력이 아닙니다.");
			}
			if (amount>getBetting()/2) {
				System.out.printf("원본 베팅액 $%s의 절반(%s)까지만 베팅할 수 있습니다.", getBetting(), getBetting()/2);
			} else	break;
		}
		if (amount!=0)	putInsurance(amount);
	}
	// 더블다운 : Hit + 2배 베팅
	// 저장된 betting 금액만큼 소지금에서 한번 더 차감하고
	// betting을 * 2 함

	@Override
	public void doubleDown() {
		System.out.println("베팅액: $" + this.betting + " -> $" + this.betting*2);
		setMoney(this.money - betting);
		setBetting(betting*2);
		drawCard(cards_deck.drawFromDeck());
		printCards();
		setHaveTurn(false);
	}
	@Override
	public void split() {
		System.out.print("player.split()");
		System.out.println();
	}

	// Hit
	// 카드 한 장을 받는다.
	@Override
	public void hit() {
		drawCard(cards_deck.drawFromDeck());
		cardsCalc();
		printCards();
	}

	@Override
	public void stand() {
		printCards();
		setHaveTurn(false);
	}



	// 다이
	// isAlive 변수를 false로 설정
	@Override
	public void surrender() {
		System.out.printf("베팅금액 %s의 50%% (%s)를 돌려받습니다.", getBetting(), getBetting()/2);
		System.out.println("하남자 행동이므로 버림합니다.");
		setStatus("🏳️SURRENDER");
		setRoundResult("SURRENDER");
		setDead();
	}

	// 게임 결과 관련 메서드
	// surrender는 행동인 동시에 결과이므로
	// 시인성을 위해 이 바로 위로 끌어내렸음
	// getMoney() << 현재 소지금, '이미 베팅액만큼이 차감된 이후'의 값임

	// 플레이어 블랙잭
	// 현재소지금 + 베팅액*2.5
	// 오로지 첫 손패로 21이 달성된 경우만 블랙잭이며
	// 즉시 턴을 비활성화하고 승리한다.
	public void blackJack() {
		setStatus("🎉BLACKJACK");
		setRoundResult("BLACKJACK");
		printResult("🎉BLACKJACK");
		setHaveTurn(false);
	}

	// 플레이어 버스트
	// 현재소지금 변동 없음
	// 모든 것에 지는 강제 패배 : 딜러가 버스트되어도 버스트된 플레이어는 여전히 패배
	public void bust() {
		setStatus("💣BUST");
		printResult("💣BUST");
		setRoundResult("LOSE");
		setDead();
	}

	// 플레이어 통상 승리
	// 베팅액 회수 + 베팅액만큼의 추가 금액 획득
	public void win() {
		printResult("🏆WIN");
		setRoundResult("WIN");
	}

	// 플레이어 통상 패배
	// 현재소지금 변동 없음
	public void lose() {
		printResult("☠️LOSE");
		setRoundResult("LOSE");
		setDead();
	}

	// DRAW
	// 베팅액만큼 돌려받음
	public void draw() {
		printResult("⚖️DRAW");
		setRoundResult("DRAW");
	}
	
	
	// 프린팅 메서드들
	
	// 기본 프린트: 이름, 라운드생존여부, 소지금, 손패, 블랙잭여부
	public void printInfo() {
		if (this.isAlive) {
			System.out.println("==== " + this.name +  " 님의 정보 ====");			
		} else {
			System.out.println("==== " + this.name +  " 님의 정보 ====");
			System.out.println("[이번 라운드에서 패배]");
		}
		System.out.println("현재 소지금: $" + this.money);
		if (!currentCards.isEmpty()) {
			System.out.println("받은 패: " + this.currentCards + " (" + this.sumCurrentCards +"점)");			
		}
		if (this.status!=null) {
			System.out.println("판정: " + this.status);
		}
	}
	
	public void printCards() {
		System.out.print(this.name + " 님의 패: " + currentCards);
		System.out.println("\n계산 결과: " + cardsCalc() + "점");
	}

	public void printResult(String judge) {
		System.out.printf("%s %s (%s)\n", name, judge, sumCurrentCards);
	}
	
	
//	테스트용 프린트
//	완성후엔 쓸 일 없을 확률이 크기때문에 나중에 삭제 고려
	
//	public void testPrintInfo() {
//		System.out.println("=== 테스트프린트 ===");
//		System.out.println("name = " + this.name);
//		System.out.println("money = " + this.money);
//		System.out.println("currentCards = " + this.currentCards);
//		System.out.println("sumCurrentCards = " + this.sumCurrentCards);
//		System.out.println("betting = " + this.betting);
//		System.out.println("isAlive = " + this.isAlive);
////		System.out.println("playerId = " + this.playerId);
//		System.out.println("status = " + this.status);
//		System.out.println("=== 테스트프린트 끝 ===");
//	}


	
	// 예전 버전 메서드
	
	// 카드 합계 구하기 예전 버전 
//	public int cardsCalc(Player p) {
//		for (String card : playerDeck) {
//			String cardNum=card.substring(card.length()-2).trim();
//			if (Stream.of("J", "Q", "K").anyMatch(card::contains)) {
//				cardNum="10";
//			}						
//			cardNums.add(cardNum);
//		}
//		if (playerDeck.size()==2 && cardNums.get(0).contains("A") && cardNums.get(1).contains("A")) {
//			finalResult = 12;
//			setSumCurrentCards(finalResult);
//			return finalResult;
//		}
//		
//		if (cardNums.contains("A")) {
//			cardNums.remove("A");
//			for (String num : cardNums) {
//				finalResult += Integer.parseInt(num);
//			}
//			finalResult += 11;
//			if (finalResult > 21) {
//				finalResult = 1;
//				cardNums.add("1");
//			} else if (finalResult==21 && playerDeck.size()==2) {
//				setSumCurrentCards(finalResult);
//				setGameResult("BLACKJACK");
//				return finalResult;
//			} else {
//				setSumCurrentCards(finalResult);
//				cardNums.add("A");
//				return finalResult;
//			}
//		}		
//		for (String num : cardNums) {
//			finalResult += Integer.parseInt(num);
//		}
//		
//		setSumCurrentCards(finalResult);
//		return finalResult;
//	}
}

