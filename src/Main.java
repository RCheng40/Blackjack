import java.util.Scanner;
import java.util.InputMismatchException;

void main() {
    printRules();
    Scanner scanner = new Scanner(System.in);

    double initialMoney;
    while (true) {
        System.out.println("How much money do you want to start with? ");
        try {
            initialMoney = scanner.nextDouble();
            if (initialMoney > 0) {
                break;
            }
            System.out.println("Starting money must be greater than 0.");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a positive number.");
            scanner.next();
        }
    }

    Player user = new Player(initialMoney);
    Player dealer = new Player();

    int multiFactor;
    while (true) {
        System.out.println("How many decks do you want to play with? Type a number: ");
        try {
            multiFactor = scanner.nextInt();
            if (multiFactor > 0) {
                break;
            }
            System.out.println("Number of decks must be greater than 0.");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a positive integer.");
            scanner.next();
        }
    }


    Deck deck1 = new Deck(multiFactor);

    while (true) {
        playRound(deck1, user, dealer, scanner, multiFactor);
        user.reset();
        dealer.reset();

        if (user.getMoney() <= 0) {
            System.out.println("You are out of money!\nThe house always wins!");
            break;
        }

        while (true) {
            System.out.println("The hand is over. Type anything to play another hand, \"Rules\" to view the rules, or \"Stop\" to finish playing: ");
            String action = scanner.next();

            if (action.equalsIgnoreCase("stop")) {
                System.out.println("You ended with $" + user.getMoney());
                System.out.println("Thanks for playing!");
                return;
            } else if (action.equalsIgnoreCase("rules")) {
                printRules();
            } else {
                break;
            }
        }
    }
}

public static void playRound(Deck pool, Player us, Player dlr, Scanner scn, int factor) {
    ArrayList<Player> hands = new ArrayList<>();
    hands.add(us);

    double bet;
    while (true) {
        System.out.println("How much would you like to bet? ");
        try {
            bet = scn.nextDouble();
            if ((bet > 0) && (bet <= us.getMoney())) {
                break;
            }
            System.out.println("Invalid bet. You must bet more than $0 up to $" + us.getMoney() + ".");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            scn.next();
        }
    }
    us.setBet(bet);
    us.changeMoney(-bet);

    dealInitialCards(pool, us, dlr);

    boolean playerBJ = us.isBJ();
    boolean dealerBJ = dlr.isBJ();

    if (playerBJ || dealerBJ) {
        dlr.setAfterUser();
        System.out.println(us);
        System.out.println(dlr);

        if (playerBJ && dealerBJ) {
            System.out.println("Push.");
            us.changeMoney(bet);
        } else if (playerBJ) {
            System.out.println("Blackjack! You Win!");
            us.changeMoney(bet * 2.5);
        } else {
            System.out.println("Dealer Blackjack! You Lose.");
        }
        checkShuffle(pool, factor);
        return;
    }

    System.out.println(us);
    System.out.println(dlr);

    playUserTurns(pool, hands, scn, us);

    boolean dealerPlay = false;
    for (Player hand : hands) {
        if (!hand.getHasBusted()) {
            dealerPlay = true;
            break;
        }
    }
    if (dealerPlay) {
        playDealerTurn(pool, dlr);
        determineWinner(hands, dlr, us);
    }
    checkShuffle(pool,factor);
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
public static void playUserTurns (Deck pool, ArrayList<Player> hands, Scanner scn, Player us) {
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

            boolean canDoubleDown = currentHand.hasTwoCards() && currentHand.getBet() <= us.getMoney();
            boolean canSplitHand = currentHand.canSplit() && (hands.size() < maxHands) && (currentHand.getBet() <= us.getMoney());

            if (canSplitHand) {
                System.out.println("Would you like to Hit, Stand, Double Down, or Split? (H, S, D, P)");
            } else if (canDoubleDown) {
                System.out.println("Would you like to Hit, Stand, or Double Down? (H, S, D)");
            } else {
                System.out.println("Would you like to Hit or Stand? (H, S)");
            }

            action = scn.next();

            if (action.equalsIgnoreCase("hit") || action.equalsIgnoreCase("h")) {
                currentHand.addCard((pool.dealCard()));
                System.out.println(currentHand);
                if (currentHand.getCardValue() > 21) {
                    System.out.println("You Busted!");
                    currentHand.setHasBusted();
                    break;
                }
                if (currentHand.getCardValue() == 21) {
                    System.out.println("21!");
                    break;
                }
            } else if (action.equalsIgnoreCase("stand") || action.equalsIgnoreCase("s")) {
                continueAction = false;
            } else if (action.equalsIgnoreCase("double down") || action.equalsIgnoreCase("d")) {
                if (currentHand.hasTwoCards() && currentHand.getBet() <= us.getMoney()) {
                    double originalBet = currentHand.getBet();

                    us.changeMoney(-originalBet);
                    currentHand.setBet(originalBet * 2);

                    currentHand.addCard(pool.dealCard());
                    System.out.println(currentHand);

                    if (currentHand.getCardValue() > 21) {
                        System.out.println("You Busted!");
                        currentHand.setHasBusted();
                    } else if (currentHand.getCardValue() == 21) {
                        System.out.println("21!");
                    }
                    break;
                } else {
                    System.out.println("You can not double down on this hand.");
                }
            } else if (action.equalsIgnoreCase("split") || action.equalsIgnoreCase("p")){
                if (currentHand.canSplit() && (hands.size() < maxHands) && (currentHand.getBet() <= us.getMoney())) {
                    Player newHand  = new Player(0);

                    double originalBet = currentHand.getBet();
                    newHand.setBet(originalBet);
                    us.changeMoney(-originalBet);

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
    dlr.setAfterUser();
    System.out.println(dlr);
    while (dlr.getCardValue() <= 17) {
        if (dlr.isSoft17() || dlr.getCardValue() < 17) {
            System.out.println("The Dealer Hits!");
            dlr.addCard(pool.dealCard());
            System.out.println(dlr);
        } else {
            break;
        }
    }
    if (dlr.getCardValue() > 21) {
        System.out.println("Dealer Busted!");
    } else {
        System.out.println("The Dealer Stands!");
    }
}
public static void determineWinner(ArrayList<Player> hands, Player dlr, Player us) {
    for (int i = 0; i < hands.size(); i++) {
        double bet = hands.get(i).getBet();
        String print = "";
        if (hands.size() > 1) {
            print += "Hand " + (i + 1) + ": ";
        }
        int difference = hands.get(i).getCardValue() - dlr.getCardValue();
        if (hands.get(i).getCardValue() > 21) {
            System.out.println(print + "You Lose.");
        } else if (dlr.getCardValue() > 21) {
            System.out.println(print + "You Win!");
            us.changeMoney(bet * 2);
        } else if (difference > 0) {
            System.out.println(print + "You Win!");
            us.changeMoney(bet * 2);
        } else if (difference < 0) {
            System.out.println(print + "You Lose.");
        } else {
            System.out.println(print + "Push.");
            us.changeMoney(bet);
        }
    }
}
public static void printRules() {
    System.out.println("HOUSE RULES");
    System.out.println("----------------------------------------");
    System.out.println("1. Blackjack pays 3:2");
    System.out.println("2. Dealer hits on soft 17");
    System.out.println("3. Double down is allowed after splitting");
    System.out.println("4. Maximum of 4 hands");
    System.out.println("----------------------------------------");
}