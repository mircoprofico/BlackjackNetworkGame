package server;
import java.util.*;

public class GameManager {

    private final List<PlayerConnection> players = new ArrayList<>();
    private final List<PlayerConnection> toRemove = new ArrayList<>();
    private final Set<PlayerConnection> bet = new HashSet<>();

    private final int numberOfPackInDeck = 3;
    private final int maxCard = numberOfPackInDeck * 52;
    private int currentCardIndex = 0;
    private final Dealer dealer = new Dealer();
    String[] colors = {"H", "D", "S", "C"}; // Heart, Diamond, Spade, Clove
    String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private final String[] cards = shuffleDeck(createDeck(numberOfPackInDeck));

    private boolean roundInProgress = false;
    private int currentPlayerIndex = -1;

    public static int totalValue(ArrayList<String> deck){
        int sum = 0;
        int aces = 0;
        for(String card : deck){
            String nextVal = card.split(" ")[0];
            switch(nextVal){
                case "K":
                case "Q":
                case "J":
                    sum += 10;
                    break;
                case "A":
                    aces++;
                    sum += 11;
                    break;
                default:
                    sum += Integer.parseInt(nextVal);
            }
            while (aces > 0 && sum > 21){
                aces--;
                sum -= 10;
            }
        }
        return sum;
    }

    private String[] createDeck(int numberOfPacks){
        String[] deck = new String[numberOfPacks * colors.length * values.length];
        for(int i = 0; i < colors.length; i++){
            for(int j = 0; j < values.length; j++){
                for(int k = 0; k < numberOfPacks; k++){
                    deck[i * values.length * numberOfPacks + j* numberOfPacks+k] =  values[j] + " " + colors[i];
                }
            }
        }
        return deck;
    }

    private String[] shuffleDeck(String[] deck){
        Collections.shuffle(Arrays.asList(deck));
        System.out.println("Array shuffled: " + Arrays.toString(deck));
        return deck;
    }

    public synchronized void playARound(){
        roundInProgress = true;
        currentPlayerIndex = 0;
        bet.clear();
        dealer.pickCard();
        notifyAll();
    }

    public synchronized String requestCard(){
        if(currentCardIndex >= cards.length-1){
            shuffleDeck(cards);
            currentCardIndex = 0;
        }
        return cards[currentCardIndex++];
    }

    public synchronized void placeBet(PlayerConnection pc){
        bet.add(pc);
        if(bet.size()>=players.size()){
            playARound();
        }
    }

    public synchronized void joinGame(PlayerConnection p) throws InterruptedException {
        while (roundInProgress) {wait();}
        players.add(p);
    }

    public synchronized int getDealerScore() {
        return totalValue(dealer.getCards());
    }


    public synchronized void endRound() {
        roundInProgress = false;

        System.out.println("Round ended");
        dealer.playLogic();
        System.out.println("Dealer made a score of " + getDealerScore());
        dealer.endRound();


        for (PlayerConnection p : players) {p.reset();}
        notifyAll();
        for(PlayerConnection p : toRemove) {players.remove(p);}
        toRemove.clear();

    }

    public synchronized void waitForNextRound() throws InterruptedException {
        while (!roundInProgress) {wait();}
    }

    public synchronized void waitForMyTurn(PlayerConnection p) throws InterruptedException {
        while (true) {
            // If the player is not in the players, then it can't wait for its turn
            if (!players.contains(p)) return;
            // If the round hasn't started, we shall wait for it
            if(!roundInProgress) wait();
            // index invalide
            if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) return;

            if (players.get(currentPlayerIndex) == p) return;
            wait();
        }
    }

    public synchronized void nextPlayer() {
        if(players.isEmpty()) return;

        currentPlayerIndex++;

        if (currentPlayerIndex >= players.size()) {
            endRound();
            currentPlayerIndex = -1;
        }

        notifyAll();
    }
    boolean isPlaying(){return roundInProgress;}

    public synchronized void removePlayer(PlayerConnection p) {
        int index = players.indexOf(p);
        boolean wasCurrent = (index == currentPlayerIndex);

        toRemove.add(p);

        // Si on retire celui qui jouait
        if (wasCurrent) {
            currentPlayerIndex++;
        }
    }

    class Dealer{
        ArrayList<String> cards = new ArrayList<>();

        String pickCard(){
            String c = requestCard();
            cards.add(c);
            return c;
        }
        void endRound(){
            roundInProgress = false;
            cards.clear();
        }
        ArrayList<String> getCards(){return cards;}
        int playLogic(){
            while(totalValue(cards)<17){
                pickCard();
            }
            return totalValue(cards);
        }
    }
}
