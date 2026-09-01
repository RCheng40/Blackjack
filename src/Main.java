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
    ArrayList<Player> hands = new ArrayList<Player>();
    hands.add(us);
    us.addCard(pool.dealCard());
    dlr.addCard(pool.dealCard());
    us.addCard(pool.dealCard());
    dlr.addCard(pool.dealCard());
    System.out.println(us);
    System.out.println(dlr);
    int timesBusted = 0;
    String action;
    boolean continueAction = true;
    boolean dlrBusted = false;


    for (int i = 0; i < hands.size(); i++) {
        Player currentHand = hands.get(i);
        if (hands.size() > 1) {
            System.out.println("Hand " + (i + 1) + ": " + hands.get(i));
        }
        continueAction = true;

        while (continueAction) {
            if (currentHand.canSplit()) {
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
                    timesBusted++;
                    break;
                }
                if (currentHand.getCardValue() == 21) {
                    System.out.println("BlackJack!");
                    break;
                }
            } else if (action.equalsIgnoreCase("stand") || action.equalsIgnoreCase("s")) {
                continueAction = false;
            } else if (action.equalsIgnoreCase("split") || action.equalsIgnoreCase("p")){
                if (currentHand.canSplit()) {
                    Player newHand  = new Player(false);

                    int splitCard = currentHand.removeCardSplit();
                    newHand.addCard(splitCard);

                    currentHand.addCard(pool.dealCard());
                    newHand.addCard(pool.dealCard());

                    hands.add(newHand);
                    for (int j = 0; j < hands.size(); j++) {
                        System.out.println("Hand " + (j + 1) + ": " + hands.get(j));
                    }
                } else {
                    System.out.println("This hand cannot be split");
                }
            } else {
                System.out.println("That is not a valid action");
            }
        }
    }
    if (hands.size() == timesBusted) {
        checkShuffle(pool,factor);
        System.out.println("The hand is over, would you like to play another? Type anything to continue or type stop to finish playing: ");
        return;
    }
    dlr.setAfterUser(true);
    System.out.println(dlr);
    while (dlr.getCardValue() < 17) {
        System.out.println("The Dealer Hits!");
        dlr.addCard(pool.dealCard());
        System.out.println(dlr);
    }
    if (dlr.getCardValue() > 21) {
        System.out.println("Dealer Busted! You Win!");
        dlrBusted = true;
    } else {
        System.out.println("The Dealer Stands!");
    }
    for (int i = 0; i < hands.size(); i++) {
        String print = "";
        if (hands.size() > 1) {
            print += "Hand " + (i + 1) + ": ";
        }
        int difference = hands.get(i).getCardValue() - dlr.getCardValue();
        if (hands.get(i).getCardValue() > 21) {
            System.out.println(print + "You Lose.");
        } else if (dlrBusted) {
            System.out.println(print + "You Win!");
        } else if (difference > 0) {
            System.out.println(print + "You Win!");
        } else if (difference < 0) {
            System.out.println(print + "You Lose.");
        } else {
            System.out.println(print + "Push.");
        }
    }
    checkShuffle(pool,factor);
    dlr.setAfterUser(false);
    System.out.println("The hand is over, would you like to play another? Type anything to continue or type stop to finish playing: ");
}

public static void checkShuffle (Deck pool, int factor) {
    if (pool.getTotalCards() <= (factor * 52 * .5)) { //if there is less than half remaining, shuffle
        pool.reset(factor);
        System.out.println("The dealer has shuffled the deck.");
    }
}