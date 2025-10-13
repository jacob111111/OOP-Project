Info Flow:

 --> Board and Player



player --> piece



User --> Game:

board square (string e4 converted to location)



Game --> Player

find what piece is at that location (go through asking players current pieces)

now know specific piece ID to work with



Game --> Board

Will moving piece put that players king in check



If king will not be checked:

Board --> Player

Can this piece move to this location



If yes:

Board <-- Player

player moves piece and returns true



 	Player --> Piece

 	Update your position to this new one







Board:

run checkmate function



Game:

Switch current turn





Class Breakdown:

Game

calls functions from board



Handles:

all user interaction

user validation(random shit or not, and is this piece yours)

switching turns



board

Calls functions from player



On receive move from game(aka user):

Does your move put yourself in check (Some other function)

2\. Try to make move (Player.MakeMove() )



Other actions:

Captures (Updating capture and current piece list) (Deconstruct captured piece)

Check (are there any pieces you can move to stop check, can the king move))

Checkmate (If check conditions are false)

Display board



player

Calls functions from piece

Gets requests for color and pieces from board (e.g board ask for piece at E4 --> CurrentPieces)



Handles:

initializing Piece list



Board asks player to make a move: (at this point, we know the piece is in currentPieces of correct player)

check if a asked for move is valid (Looks through the asked for piece's possible moves)

True --> update found pieces position

(on piece.move(), run the piece.findPossibleMoves() w/ new position)





piece

Has Current position

Has list of possible moves



Handles:

Updating its own possible moves (based on current location and logic)

Updating its own location (move() )

