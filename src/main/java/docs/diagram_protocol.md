
# Network Protocols Diagram

---

## JOIN

```
CLIENT                                    SERVER
  |                                          |
  |---- JOIN <username> -------------------->|
  |                                          |
  |<--- WELCOME <username> ------------------|
  |                                          |
```

---

## BET

```
CLIENT                                    SERVER
  |                                          |
  |---- BET <amount> ----------------------->|
  |                                          |
  |<--- BET_OK <amount> ---------------------|
  |                                          |
```

Error if not in the game: :

```
CLIENT                                    SERVER
  |---- BET 100 ----------------------------->|
  |                                           |
  |<--- ERROR NOT_IN_GAME --------------------|
```

---

## HIT (request card)

```
CLIENT                                    SERVER
  |                                          |
  |---- HIT -------------------------------->|
  |                                          |
  |<--- CARD <rank> <suit> ------------------|
  |                                          |
```

If the player exceeds 21 :

```
CLIENT                                    SERVER
  |---- HIT -------------------------------->|
  |                                          |
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
```
In Blackjack, “BUST” means that the player has exceeded 21 points.


---

## STAND (refuse to receive other cards)

```
CLIENT                                    SERVER
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
CLIENT                                    SERVER
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

SERVER → CLIENT
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
  |<--- CARD ♚ ♠ ---------------------------|
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
  |                                          |
```

