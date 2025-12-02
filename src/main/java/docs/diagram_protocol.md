
# Network Protocols Diagram

---

## JOIN

```
CLIENT                                    SERVEUR
  |                                          |
  |---- JOIN <username> -------------------->|
  |                                          |
  |<--- WELCOME <username> ------------------|
  |                                          |
```

---

## BET

```
CLIENT                                    SERVEUR
  |                                          |
  |---- BET <amount> ----------------------->|
  |                                          |
  |<--- BET_OK <amount> ---------------------|
  |                                          |
```

Error if not in the game: :

```
CLIENT                                    SERVEUR
  |---- BET 100 --------------------------->|
  |                                          |
  |<--- ERROR NOT_IN_GAME --------------|
```

---

## HIT (request card)

```
CLIENT                                    SERVEUR
  |                                          |
  |---- HIT -------------------------------->|
  |                                          |
  |<--- CARD <rank> <suit> ------------------|
  |                                          |
```

If the player exceeds 21 :

```
CLIENT                                    SERVEUR
  |---- HIT -------------------------------->|
  |                                          |
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
```
In Blackjack, “BUST” means that the player has exceeded 21 points.


---

## STAND (refuse to receive other cards)

```
CLIENT                                    SERVEUR
  |                                          |
  |---- STAND ------------------------------>|
  |                                          |
  |<--- STAND_OK ----------------------------|
  |                                          |
  |<--- RESULT <WIN|LOSE|PUSH> --------------|
  |                                          |
```

---

## QUIT 

```
CLIENT                                    SERVEUR
  |                                          |
  |---- QUIT ------------------------------->|
  |                                          |
  |<--- GOODBYE -----------------------------|
  |                                          |
```

---

## All commands

```
CLIENT → SERVER
  JOIN <username>
  BET <amount>
  HIT
  STAND
  QUIT

SERVEUR → CLIENT
  WELCOME <username>
  BET_OK <amount>
  CARD <rank> <suit>
  BUST
  RESULT <WIN|LOSE|PUSH>
  ERROR <code> <message>
  GOODBYE
```

---

# Complete diagram of a round (JOIN → BET → HIT → STAND)

```
CLIENT                                    SERVEUR
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
  |<--- CARD ♚ ♠ ---------------------------|
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
  |                                          |
```

