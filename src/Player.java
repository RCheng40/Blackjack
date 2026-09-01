import java.util.HashMap;
import java.util.ArrayList;

public class Player {
    private final ArrayList<Integer> cards = new ArrayList<>();
    private final HashMap<Integer, String> toCards = new HashMap<>();
    private final boolean isDealer;
    private boolean afterUser;

    public Player(boolean isDlr) {
        isDealer = isDlr;
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

    public void addCard(int value) {
        cards.add(value);
    }
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
            total+= value;
        }
        while (total > 21 && aces > 0) { //would bust but have ace
            total -= 10;
            aces--;
        }
        return total;
    }

    public void reset() {
        //totalCardValue = 0;
        cards.clear();
    }
    public void setAfterUser(boolean tf) {
        afterUser = tf;
    }
    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).equals(cards.get(1)); //2 of the same card
    }
    public int removeCardSplit() {
        return cards.remove(1);
    }


    public String toString() {
        String print;
        if(!isDealer) {
            print = "Card Value: " + getCardValue() + " | Your Cards: " + toCards.get(cards.getFirst()) + ", ";
        } else if (!afterUser){
            print = "Dealer's Cards: Hole Card, ";
        } else {
            print = "Dealer's Value: " + getCardValue() + " | Dealer's Cards: " + toCards.get(cards.getFirst()) + ", ";
        }
        for (int i = 1; i < cards.size() - 1; i++) {
            print += toCards.get(cards.get(i)) + ", ";
        }
        print += toCards.get(cards.getLast());
        return print;
    }
}
