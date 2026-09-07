import java.util.ArrayList;
public record ProbabilityResult(ArrayList<Integer> hand, double probability, Player player) {
    public ProbabilityResult(ArrayList<Integer> hand, double probability, Player player) {
        this.hand = new ArrayList<>(hand);
        this.probability = probability;
        this.player = player;
    }

    @Override
    public String toString() {
        ArrayList<String> cardNames = new ArrayList<>();
        for (int card : hand) {
            cardNames.add(player.getCardName(card));
        }
        return "Hand: " + cardNames + "\nProbability: " + String.format("%.2f%%", probability);
    }
}