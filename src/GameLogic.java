import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GameLogic {
    private final Deck pool;
    private final Player user;
    private final Player dealer;
    private final Scanner scanner;
    private final int numberDecks;
    private boolean printProbability;
    private ArrayList<Player> hands;
    private ArrayList<ProbabilityResult> probabilities;

    public GameLogic(Deck dck, Player us, Player dlr, Scanner scn, int multi) {
        pool = dck;
        user = us;
        dealer = dlr;
        scanner = scn;
        numberDecks = multi;
        printProbability = true;
    }

    // Public game methods
    public static void printRules() {
        System.out.println("HOUSE RULES");
        System.out.println("----------------------------------------");
        System.out.println("1. Blackjack pays 3:2");
        System.out.println("2. Dealer hits on soft 17");
        System.out.println("3. Double down is allowed after splitting");
        System.out.println("4. Maximum of 4 hands");
        System.out.println("----------------------------------------");
    }

    public void play() {
        while (true) {
            playRound();
            user.reset();
            dealer.reset();

            if (user.getMoney() <= 0) {
                System.out.println("You are out of money!\nThe house always wins!");
                break;
            }

            while (true) {
                System.out.println("You have $" + user.getMoney());
                System.out.println("The hand is over. Type anything to play another hand, \"Rules\" to view the rules, \"Probability\" to toggle the probability analysis, or \"Stop\" to finish playing: ");
                String action = scanner.next();

                if (action.equalsIgnoreCase("stop")) {
                    System.out.println("You ended with $" + user.getMoney());
                    System.out.println("Thanks for playing!");
                    return;
                } else if (action.equalsIgnoreCase("rules")) {
                    printRules();
                } else if (action.equalsIgnoreCase("probability")){
                    printProbability = !printProbability;
                    if (printProbability) {
                        System.out.println("Probability analysis is now on.");
                    } else {
                        System.out.println("Probability analysis is now off.");
                    }
                } else {
                    break;
                }
            }
        }
    }

    // Round logic
    private void playRound() {
        hands = new ArrayList<>();
        hands.add(user);
        probabilities = new ArrayList<>();

        double bet;
        while (true) {
            System.out.println("How much would you like to bet? ");
            try {
                bet = scanner.nextDouble();
                if ((bet > 0) && (bet <= user.getMoney())) {
                    break;
                }
                System.out.println("Invalid bet. You must bet more than $0 up to $" + user.getMoney() + ".");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.next();
            }
        }
        user.setBet(bet);
        user.changeMoney(-bet);

        dealInitialCards();

        boolean playerBJ = user.isBJ();
        boolean dealerBJ = dealer.isBJ();

        if (playerBJ || dealerBJ) {
            dealer.setAfterUser();
            System.out.println(user);
            System.out.println(dealer);

            if (playerBJ && dealerBJ) {
                System.out.println("Push.");
                user.changeMoney(bet);
            } else if (playerBJ) {
                System.out.println("Blackjack! You Win!");
                user.changeMoney(bet * 2.5);
            } else {
                System.out.println("Dealer Blackjack! You Lose.");
            }
            checkShuffle();
            return;
        }

        System.out.println(user);
        System.out.println(dealer);

        playUserTurns();

        boolean dealerPlay = false;
        for (Player hand : hands) {
            if (!hand.getHasBusted()) {
                dealerPlay = true;
                break;
            }
        }
        if (dealerPlay) {
            playDealerTurn();
            determineWinner();
        }

        if (printProbability) {
            System.out.println("\nProbability Analysis");
            System.out.println("--------------------");
            for (ProbabilityResult result : probabilities) {
                System.out.println(result);
            }
        }

        checkShuffle();
    }

    private void dealInitialCards() {
        user.addCard(pool.dealCard());
        dealer.addCard(pool.dealCard());
        user.addCard(pool.dealCard());
        dealer.addCard(pool.dealCard());
    }

    // Turn logic
    private void playUserTurns() {
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

                double probability = actionProbability(currentHand);
                probabilities.add(new ProbabilityResult(currentHand.getCards(), probability, currentHand));

                boolean canDoubleDown = currentHand.hasTwoCards() && currentHand.getBet() <= user.getMoney();
                boolean canSplitHand = currentHand.canSplit() && (hands.size() < maxHands) && (currentHand.getBet() <= user.getMoney());

                if (canSplitHand) {
                    System.out.println("Would you like to Hit, Stand, Double Down, or Split? (H, S, D, P)");
                } else if (canDoubleDown) {
                    System.out.println("Would you like to Hit, Stand, or Double Down? (H, S, D)");
                } else {
                    System.out.println("Would you like to Hit or Stand? (H, S)");
                }

                action = scanner.next();

                if (action.equalsIgnoreCase("hit") || action.equalsIgnoreCase("h")) {
                    currentHand.addCard(pool.dealCard());
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
                    if (currentHand.hasTwoCards() && currentHand.getBet() <= user.getMoney()) {
                        double originalBet = currentHand.getBet();

                        user.changeMoney(-originalBet);
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
                    if (currentHand.canSplit() && (hands.size() < maxHands) && (currentHand.getBet() <= user.getMoney())) {
                        Player newHand  = new Player(0);

                        double originalBet = currentHand.getBet();
                        newHand.setBet(originalBet);
                        user.changeMoney(-originalBet);

                        int splitCard = currentHand.removeCardSplit();
                        newHand.addCard(splitCard);

                        currentHand.setWasSplit();
                        newHand.setWasSplit();

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

    private void playDealerTurn() {
        dealer.setAfterUser();
        System.out.println(dealer);
        while (dealer.getCardValue() <= 17) {
            if (dealer.isSoft17() || dealer.getCardValue() < 17) {
                System.out.println("The Dealer Hits!");
                dealer.addCard(pool.dealCard());
                System.out.println(dealer);
            } else {
                break;
            }
        }
        if (dealer.getCardValue() > 21) {
            System.out.println("Dealer Busted!");
        } else {
            System.out.println("The Dealer Stands!");
        }
    }

    // Game utilities
    private void determineWinner() {
        for (int i = 0; i < hands.size(); i++) {
            double bet = hands.get(i).getBet();
            String print = "";
            if (hands.size() > 1) {
                print += "Hand " + (i + 1) + ": ";
            }
            int difference = hands.get(i).getCardValue() - dealer.getCardValue();
            if (hands.get(i).getCardValue() > 21) {
                System.out.println(print + "You Lose.");
            } else if (dealer.getCardValue() > 21) {
                System.out.println(print + "You Win!");
                user.changeMoney(bet * 2);
            } else if (difference > 0) {
                System.out.println(print + "You Win!");
                user.changeMoney(bet * 2);
            } else if (difference < 0) {
                System.out.println(print + "You Lose.");
            } else {
                System.out.println(print + "Push.");
                user.changeMoney(bet);
            }
        }
    }

    private void checkShuffle() {
        if (pool.getTotalCards() <= (numberDecks * 26)) { //if there is less than half remaining, shuffle
            pool.reset(numberDecks);
            System.out.println("The dealer has shuffled the deck.");
        }
    }

    // Probability
    private double actionProbability(Player currentHand) {
        int totalNumCardsAllowed = 0;
        for (int i = 0; i <= 12; i++) {
            if (currentHand.getValueWithCard(i) <= 21) {
                totalNumCardsAllowed += pool.getCardCount(i);
            }
        }
        return (double) totalNumCardsAllowed / pool.getTotalCards() * 100;
    }
}
