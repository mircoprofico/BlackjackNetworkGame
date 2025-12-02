
# Blackjack Network Game

## Introduction

This project is a networked Blackjack (21) game where multiple players can play simultaneously against a dealer (server).  
Players can join or leave at any time, place bets, and play their turns independently.  
The server manages the game state, ensures consistency, and handles the dealer logic.

### Application Protocol
The application protocol is explained [here](src/main/java/docs/protocol.md) and the diagram [here](src/main/java/docs/diagram_protocol.md).

---

## Running the Server and Client

### Running the Server
The server can be run using Docker or directly via Java.

**With Docker:**

```bash
# Build the Docker image
docker build -t blackjack-network-game .

# Run the server on port 1234
docker run -p 1234:1234 blackjack-network-game server -p 1234
````

**Directly with Java:**

```bash
java -jar target/blackjack-network-game-1.0-SNAPSHOT.jar server -p 1234
```

### Running the Client

The client can connect to the server via Docker or Java.

**With Docker:**

```bash
docker run -it blackjack-network-game client -h <server-ip> -p 1234
```

**Directly with Java:**

```bash
java -jar target/blackjack-network-game-1.0-SNAPSHOT.jar client -h <server-ip> -p 1234
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

Options: **HIT** or **STAND**

#### HIT

* Draw a new card.
* Choose again between HIT or STAND.
* If total exceeds 21 → **BUST** → `RESULT LOSE`.

#### STAND

* End your turn.
* Wait for dealer and other players to finish.
* Result sent: `RESULT WIN`, `RESULT LOSE`, or `RESULT PUSH`.

### Commands consol 

* `JOIN <username>` – Join the game
* `BET <amount>` – Place your bet
* `HIT` – Draw a card
* `STAND` – End your turn
* `QUIT` – Leave the game

Server responses:

* `WELCOME <username>` – Confirmation of join
* `BET_OK <amount>` – Bet accepted
* `CARD <rank> <suit>` – Received card
* `BUST` – Exceeded 21
* `RESULT <WIN|LOSE|PUSH>` – End of turn
* `ERROR <code> <message>` – Error message
* `GOODBYE` – Confirmation of quit

---

### Example Turn (JOIN → BET → HIT → STAND)

```text
CLIENT                                    SERVER
  |                                          |
  |---- JOIN Mirco ------------------------->|
  |<--- WELCOME Mirco -----------------------|
  |                                          |
  |---- BET 50 ----------------------------->|
  |<--- BET_OK 50 ---------------------------|
  |                                          |
  |---- HIT -------------------------------->|
  |<--- CARD 7 HEARTS -----------------------|
  |                                          |
  |---- HIT -------------------------------->|
  |<--- CARD KING SPADES --------------------|
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
```

