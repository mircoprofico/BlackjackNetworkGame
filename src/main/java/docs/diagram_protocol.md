
# Network Protocols Diagram

---

## JOIN

```
CLIENT                                    SERVER
  |                                          |
  |---- JOIN <username> -------------------->|
  |                                          |
  |<--- WELCOME <balance> -------------------|
  |                                          |
```

---

## BET

```
CLIENT                                    SERVER
  |                                          |
  |---- BET <value> ------------------------>|
  |                                          |
  |<--- OK BET  -----------------------------|
  |<--- DEAL C1 C2  -------------------------|
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
  |<--- OK HIT <rank> <suit> ------------------|
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
  |                                          |
  |<--- RESULT <WIN|LOSE|TIE> ---------------|
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
  WELCOME <value>
  OK BET
  DEAL C1 C2
  OK HIT <rank> <suit>
  BUST
  RESULT <WIN|LOSE|TIE>
  ERROR <code> <message>
  GOODBYE
```

---

# Complete diagram of a round (JOIN → BET → HIT → STAND)

```
CLIENT                                    SERVER
  |                                          |
  |---- JOIN Alan -------------------------->|
  |<--- WELCOME 50 --------------------------|

  |---- BET 10 ----------------------------->|
  |<--- OK BET  -----------------------------|
  |<--- DEAL C1 C2  -------------------------|
  |                                          |
  |---- HIT -------------------------------->|
  |<--- OK HIT CARD 7 H ---------------------|                               
  |                                          |
  |---- HIT -------------------------------->|
  |<--- OK HIT CARD K S ---------------------|
  |<--- BUST --------------------------------|
  |<--- RESULT LOSE -------------------------|
  |                                          |
```



