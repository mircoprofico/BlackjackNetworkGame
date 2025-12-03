
# Blackjack Network Game

## Introduction

This project is a networked Blackjack (21) game where multiple players can play simultaneously against a dealer (server).  
Players can join or leave at any time, place bets, and play their turns independently.  
The server manages the game state, ensures consistency, and handles the dealer logic.

### Application Protocol
The application protocol is explained [here](src/main/java/docs/protocol.md) and the diagram [here](src/main/java/docs/diagram_protocol.md).

---

# How to Launch the Game

Once the project has been built, you can start both the server and the client directly from the command line.
Navigate into the project folder:

```bash
cd BlackJackNetworkGame
```

## Starting the Server

Use the executable JAR located in the `target/` directory:

```bash
java -jar target/BlackJack-1.0-SNAPSHOT.jar server
```

By default, the server listens on its standard port.
If you want to choose a specific port:

```bash
java -jar target/BlackJack-1.0-SNAPSHOT.jar server -p 5000
# or
java -jar target/BlackJack-1.0-SNAPSHOT.jar server --port 5000
```


## Starting a Client

You can open as many clients as you want. Each instance connects to a server:

```bash
java -jar target/BlackJack-1.0-SNAPSHOT.jar client
```

To connect to a specific host and port:

```bash
java -jar target/BlackJack-1.0-SNAPSHOT.jar client -h 127.0.0.1 -p 5000
# or
java -jar target/BlackJack-1.0-SNAPSHOT.jar client --host 127.0.0.1 --port 5000
```

---

## How the Game Works

### Simple Commands with GUI

* `a` – move left
* `d` – move right
* `space` – select/confirm

### Start of the Game

* Players may join in the middle of a round and must wait for the current round to finish.
* At the start of a new round, select your **bet amount**.
* Increase/decrease with `a`/`d` and confirm with `space`.
* If time expires, a standard bet amount is applied.
* After betting, receive **two cards** and wait for your turn.

### Playing Your Turn

Options: **HIT**, **STAND** & **BET <VALUE>**

#### HIT

* Draw a new card.
* Choose again between HIT or STAND.
* If total exceeds 21 → **BUST** → `RESULT LOSE`.

#### STAND

* End your turn.
* Wait for dealer and other players to finish.
* Result sent: `RESULT WIN`, `RESULT LOSE`, or `RESULT PUSH`.

#### BET <VALUE>

* The server validates the amount and deals the opening hand.
* If the bet is accepted → you receive two cards.
* If you are not allowed to bet (wrong phase or insufficient funds) → server returns an error.

### Commands consol 

* `JOIN <username>` – Join the game
* `BET <amount>` – Place your bet
* `HIT` – Draw a card
* `STAND` – End your turn
* `QUIT` – Leave the game

Server responses:

* `WELCOME <username>` – Confirmation of join
* `OK BET <value>` – Bet accepted
* `DEAL C1 C2`
* `OK HIT CARD <rank> <suit>` – Received card
* `BUST` – Exceeded 21
* `RESULT <WIN|LOSE|PUSH>` – End of turn
* `ERROR <code> <message>` – Error message
* `GOODBYE` – Confirmation of quit

---
## Gameplay Overview

Here is a visual walkthrough of the game so you can see how to place a bet, request cards, and play a full round of Blackjack.


