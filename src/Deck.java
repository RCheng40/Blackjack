public class Deck {
    private final int[] cards = new int[13];
    private int totalCards = 0;

    public Deck(int multiplicationFactor) {
        reset(multiplicationFactor);
    }

    public void reset(int factor) {
        totalCards = 0;
        for (int i = 0; i < 13; i++) {
            cards[i] = 4 * factor;
            totalCards += 4 * factor;
        }
    }
    public boolean hasCard(int index) {
        return cards[index] > 0;
    }
    public void useCard(int index) {
        cards[index]--;
        totalCards--;
    }
    public int getTotalCards() {
        return totalCards;
    }

    public int dealCard() { //deal random number between 0 and 12
        int randomIndex = (int) (Math.random() * 13);
        while (true) {
            if (hasCard(randomIndex)) {
                useCard(randomIndex);
                return randomIndex;
            }
            randomIndex = (int) (Math.random() * 13);
        }
    }
}
