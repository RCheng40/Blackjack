import java.util.Scanner;
import java.util.InputMismatchException;

void main() {
    GameLogic.printRules();
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

    //initialize deck, user, dealer based on above input
    Deck deck = new Deck(multiFactor);
    Player user = new Player(initialMoney);
    Player dealer = new Player();

    GameLogic game = new GameLogic(deck, user, dealer, scanner, multiFactor);
    game.play();
}