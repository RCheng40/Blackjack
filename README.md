# Blackjack Probability Analyzer
Java-based Blackjack command-line game. It gives a list of the exact probabilities of busting given another card at each turn after each hand.

# About the Project
It was developed for fun and as an introduction to the implementation of Statistics and Probability in code
The main purpose is the stat/probability, but the game does blackjack was designed and implemented by me.

# Features
Blackjack Rules:
1. Blackjack pays 3:2
2. Dealer hits on soft 17
3. Double down is allowed after split
4. Maximum of 4 hands

Allows user to choose how many decks to play with

Calculates and stores the probability of busting at each action available to the player
Implements aces change values depending on the total value of the hand
Tracks decks dynamically, which allows for counting cards and accurate probability based on the exact number of remaining cards in the deck

# Calculationa
Finds difference between 21 and the current hand value
Finds number of total cards that would lead to NOT BUSTING
Divide that number by the total cards left in the deck 

# License
This project is open-source and available under the [MIT License](License).
