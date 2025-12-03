
# Blackjack Network Protocol

## Message Format


COMMAND [PARAMETERS]

```
- COMMAND: uppercase action or response  
- PARAMETERS: optional, space-separated  
- Messages end with `\n`  

```

## Client → Server Commands

- `JOIN <username>` : Join the game  
- `BET <value>` : Place a bet  
- `HIT` : Request a card  
- `STAND` : End turn  
- `LEAVE` : Leave the game  

**Example:**

```
JOIN Alice
BET 50
HIT
STAND
LEAVE

```

## Server → Client Responses

- `WELCOME <balance>` : Confirm player joined  
- `DEAL <card1> <card2>` : Initial cards dealt  
- `UPDATE <game_state>` : Updates during the round  
- `RESULT <outcome> <balance>` : Round result (WIN/LOSE/TIE)  
- `ERROR <message>` : Invalid command or error  

**Example:**

```
WELCOME 1000
DEAL 5H KD
UPDATE Dealer: 7S, Players: Alice: 15
RESULT WIN 1050
ERROR Invalid command
```


## Example Round

```
Client -> Server: JOIN Alice
Server -> Client: WELCOME 1000

Client -> Server: BET 50
Server -> Client: DEAL 7H 9D

Client -> Server: HIT
Server -> Client: UPDATE Player Alice: 16, Dealer: 7S

Client -> Server: STAND
Server -> Client: RESULT WIN 1050

Client -> Server: LEAVE

```
