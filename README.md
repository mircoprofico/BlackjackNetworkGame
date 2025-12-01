# BlackjackNetworkGame

## Introduction

This project is a networked Blackjack (21) game where multiple players can play simultaneously against a dealer (server).  
Players can join or leave at any time, place bets, and play their turns independently.  
The server manages the game state, ensures consistency, and handles the dealer logic.

### Application Protocol
The application protocol is explained [here]([protocol.md](src/main/java/docs/protocol.md)).


#### How to run the server
To run the server, you can use docker with the provided

#### How to run the client


### How does this game works

### Simple commands
To play this game, you only have to use 3 keys : the 'a' key is used to go left, the 'd' one to go right, and
the space key to select.
#### Start of the game
The first thing to know is that you might join the game in the middle of a round, and you will therefore
have to wait for the round to end.
Once a new round start, you will first have to select the amount you are willing to bet for the round.  

You can select the amount by increasing or decreasing your current bet amount. Once you're satisfied with
the amount, you may proceed to play. Keep in mind that you have a limited amount of time to select your amount, and
if you did not confirm your betting amount after this time, the standard amount will be used.

Once your bet has been made, you'll receive 2 cards,and you'll have to wait for your turn.

When it's your turn to play, you have 2 options : HIT or STAND.

##### If you choose HIT
you'll be granted with a new card, and you'll be able to make another choice between HIT and STAND.
Keep in mind that the goal of the game is to go the closest to 21 in total, without overshooting it.

##### If you choose STAND
Your turn end here.
