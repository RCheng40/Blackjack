import java.util.Scanner;

void main() {
    Player user = new Player(false);
    Player dealer = new Player(true);
    Scanner scanner = new Scanner(System.in);
    System.out.println("How many decks do you want to play with? Type a number: ");
    int multiFactor = scanner.nextInt();
    Deck deck1 = new Deck(multiFactor);

    boolean play = true;
    while (play) {
        playRound(deck1, user, dealer, scanner, multiFactor);
        user.reset();
        dealer.reset();
        play = !scanner.next().equalsIgnoreCase("stop");
    }
}

public static void playRound(Deck pool, Player us, Player dlr, Scanner scn, int factor) {
    ArrayList<Player> hands = new ArrayList<>();
    hands.add(us);
    dealInitialCards(pool, us, dlr);

    boolean playerBJ = us.isBJ();
    boolean dealerBJ = dlr.isBJ();

    if (playerBJ || dealerBJ) {
        dlr.setAfterUser(true);
        System.out.println(us);
        System.out.println(dlr);

        if (playerBJ && dealerBJ) {
            System.out.println("Push.");
        } else if (playerBJ) {
            System.out.println("Blackjack! You Win!");
        } else {
            System.out.println("Dealer Blackjack! You Lose.");
        }
        checkShuffle(pool, factor);
        System.out.println("The hand is over, would you like to play another? Type anything to continue or type stop to finish playing: ");
        return;
    }

    System.out.println(us);
    System.out.println(dlr);

    playUserTurns(pool, hands, scn);

    boolean dealerPlay = false;
    for (Player hand : hands) {
        if (!hand.getHasBusted()) {
            dealerPlay = true;
            break;
        }
    }
    if (dealerPlay) {
        playDealerTurn(pool, dlr);
        determineWinner(hands, dlr);
    }
    checkShuffle(pool,factor);
    System.out.println("The hand is over, would you like to play another? Type anything to continue or type stop to finish playing: ");
}

public static void checkShuffle (Deck pool, int factor) {
    if (pool.getTotalCards() <= (factor * 26)) { //if there is less than half remaining, shuffle
        pool.reset(factor);
        System.out.println("The dealer has shuffled the deck.");
    }
}
public static void dealInitialCards (Deck pool, Player us, Player dlr) {
    us.addCard(pool.dealCard());
    dlr.addCard(pool.dealCard());
    us.addCard(pool.dealCard());
    dlr.addCard(pool.dealCard());
}
public static void playUserTurns (Deck pool, ArrayList<Player> hands, Scanner scn) {
    String action;
    boolean continueAction;
    final int maxHands = 4;

    for (int i = 0; i < hands.size(); i++) {
        Player currentHand = hands.get(i);
        continueAction = true;
        if (currentHand.getCardValue() == 21) {
            System.out.println("Hand " + (i + 1) + ": 21!");
            continue;
        }

        while (continueAction) {
            if (hands.size() > 1) {
                System.out.println("Hand " + (i + 1) + ": " + hands.get(i));
            }
            if (currentHand.canSplit() && (hands.size() < maxHands)) {
                System.out.println("Would you like to Hit, Stand, or Split? (H, S, P)");
            } else {
                System.out.println("Would you like to Hit or Stand? (H, S)");
            }

            action = scn.next();

            if (action.equalsIgnoreCase("hit") || action.equalsIgnoreCase("h")) {
                currentHand.addCard((pool.dealCard()));
                System.out.println(currentHand);
                if (currentHand.getCardValue() > 21) {
                    System.out.println("You Busted!");
                    currentHand.setHasBusted(true);
                    break;
                }
                if (currentHand.getCardValue() == 21) {
                    System.out.println("21!");
                    break;
                }
            } else if (action.equalsIgnoreCase("stand") || action.equalsIgnoreCase("s")) {
                continueAction = false;
            } else if (action.equalsIgnoreCase("split") || action.equalsIgnoreCase("p")){
                if (currentHand.canSplit() && (hands.size() < maxHands)) {
                    Player newHand  = new Player(false);

                    int splitCard = currentHand.removeCardSplit();
                    newHand.addCard(splitCard);

                    currentHand.setWasSplit(true);
                    newHand.setWasSplit(true);

                    currentHand.addCard(pool.dealCard());
                    newHand.addCard(pool.dealCard());

                    hands.add(newHand);
                    for (int j = 0; j < hands.size(); j++) {
                        System.out.println("Hand " + (j + 1) + ": " + hands.get(j));
                    }
                    if (currentHand.getCardValue() == 21) {
                        System.out.println("Hand " + (i + 1) + ": 21!");
                        break;
                    }
                } else {
                    System.out.println("This hand cannot be split");
                }
            } else {
                System.out.println("That is not a valid action");
            }
        }
    }
}
public static void playDealerTurn(Deck pool, Player dlr) {
    dlr.setAfterUser(true);
    System.out.println(dlr);
    while (dlr.getCardValue() < 17) {
        System.out.println("The Dealer Hits!");
        dlr.addCard(pool.dealCard());
        System.out.println(dlr);
    }
    if (dlr.getCardValue() > 21) {
        System.out.println("Dealer Busted!");
    } else {
        System.out.println("The Dealer Stands!");
    }
    dlr.setAfterUser(false);
}
public static void determineWinner(ArrayList<Player> hands, Player dlr) {
    for (int i = 0; i < hands.size(); i++) {
        String print = "";
        if (hands.size() > 1) {
            print += "Hand " + (i + 1) + ": ";
        }
        int difference = hands.get(i).getCardValue() - dlr.getCardValue();
        if (hands.get(i).getCardValue() > 21) {
            System.out.println(print + "You Lose.");
        } else if (dlr.getCardValue() > 21) {
            System.out.println(print + "You Win!");
        } else if (difference > 0) {
            System.out.println(print + "You Win!");
        } else if (difference < 0) {
            System.out.println(print + "You Lose.");
        } else {
            System.out.println(print + "Push.");
        }
    }
}