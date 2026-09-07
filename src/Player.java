import java.util.HashMap;
import java.util.ArrayList;

public class Player {
    private final ArrayList<Integer> cards = new ArrayList<>();
    private final HashMap<Integer, String> toCards = new HashMap<>();
    private final boolean isDealer;
    private double money;
    private double bet;
    private boolean afterUser;
    private boolean hasBusted;
    private boolean wasSplit;

    // Constructors
    public Player(double mny) {
        money = mny;
        isDealer = false;
        initializeHashmap();
    }
    public Player() {
        isDealer = true;
        initializeHashmap();
    }

    //Initialization
    private void initializeHashmap() {
        toCards.put(0, "Ace");
        toCards.put(1, "2");
        toCards.put(2, "3");
        toCards.put(3, "4");
        toCards.put(4, "5");
        toCards.put(5, "6");
        toCards.put(6, "7");
        toCards.put(7, "8");
        toCards.put(8, "9");
        toCards.put(9, "10");
        toCards.put(10, "Jack");
        toCards.put(11, "Queen");
        toCards.put(12, "King");
    }

    // Card methods
    public void addCard(int value) {
        cards.add(value);
    }

    public int removeCardSplit() {
        return cards.remove(1);
    }

    // Value calculations
    public int getCardValue() {
        int total = 0;
        int aces = 0;

        for (int card : cards) {
            int value = card + 1;
            if (value >= 10) {
                value = 10;
            }
            if (card == 0) { //ace
                value = 11;
                aces++;
            }
            total += value;
        }
        while (total > 21 && aces > 0) { //would bust but have ace
            total -= 10;
            aces--;
        }
        return total;
    }

    public int getValueWithCard(int newCard) {
        int total = 0;
        int aces = 0;

        for (int card : cards) {
            int value = card + 1;
            if (value >= 10) {
                value = 10;
            }
            if (card == 0) {
                value = 11;
                aces++;
            }
            total += value;
        }

        int value = newCard + 1;
        if (value >= 10) {
            value = 10;
        }
        if (newCard  == 0) {
            value = 11;
            aces++;
        }
        total += value;

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }

    // Hand state
    public boolean isSoft17() {
        if (getCardValue() != 17) {
            return false;
        }
        int totalAce11 = 0; //total if ace is 11, must be for soft 17
        boolean hasAce = false;
        for (int card : cards) {
            int value = card + 1;
            if (value >= 10) {
                value = 10;
            }
            if (card == 0) {
                value = 11;
                hasAce = true;
            }
            totalAce11 += value;
        }
        return hasAce && totalAce11 == 17;
    }

    public boolean hasTwoCards() {
        return cards.size() == 2;
    }

    public boolean canSplit() {
        return hasTwoCards() && cards.get(0).equals(cards.get(1)); //2 of the same card
    }

    public boolean isBJ() {
        return !wasSplit && cards.size() == 2 && getCardValue() == 21;
    }

    public void setWasSplit() {
        wasSplit = true;
    }

    public void setHasBusted() {
        hasBusted = true;
    }

    public boolean getHasBusted() {
        return hasBusted;
    }

    public void setAfterUser() {
        afterUser = true;
    }

    public void reset() {
        cards.clear();
        hasBusted = false;
        wasSplit = false;
        afterUser = false;
        bet = 0;
    }

    // Betting
    public void changeMoney(double mny) {
        money += mny;
    }

    public void setBet(double amount) {
        bet = amount;
    }

    // Getters
    public double getMoney() {
        return money;
    }

    public double getBet() {
        return bet;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }

    public String getCardName(int card) {
        return toCards.get(card);
    }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();
        if (!isDealer) {
            print.append("You have $").append(money).append(" | Your bet: $").append(bet).append("\n");
        }

        if (!isDealer) {
            print.append("Card Value: ").append(getCardValue()).append(" | Your Cards: ").append(toCards.get(cards.getFirst())).append(", ");
        } else if (!afterUser) {
            print.append("Dealer's Cards: Hole Card, ");
        } else {
            print.append("Dealer's Value: ").append(getCardValue()).append(" | Dealer's Cards: ").append(toCards.get(cards.getFirst())).append(", ");
        }
        for (int i = 1; i < cards.size() - 1; i++) {
            print.append(toCards.get(cards.get(i))).append(", ");
        }
        print.append(toCards.get(cards.getLast()));
        return print.toString();
    }
}
