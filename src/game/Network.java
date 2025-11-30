package game;

import player.Server;
import player.Client;

import piece.Piece;

import utils.Color;
import utils.NetworkMessage;
import utils.Position;

import board.Board;

import java.io.*;

import java.lang.Thread;

public class Network extends GUI {
    private int port;
    private Server server = null;
    private Client client = null;
    private boolean isHost;
    private Color myColor;
    
    // Rollback support for client side optimistic updates 
    private Board backupBoard = null;
    private Color backupTurn = null;
    
    // Network listener thread
    private Thread networkListenerThread = null;
    private volatile boolean isListening = false;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    // Constructor for HOST mode
    public Network(boolean isPvP, Color p1Color, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = true;
        this.myColor = p1Color;
        
        server = new Server(p1Color, port);
        server.acceptClient(); // Block until client connects
        
        // Send initial game state to client for sync
        Color clientColor = (p1Color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        server.sendInitialSync(this, clientColor);
        
        // Start listening for client moves
        startNetworkListener();
    }
    
    // Constructor for CLIENT mode
    public Network(boolean isPvP, Color p1Color, String serverIP, int port) {
        super(isPvP, p1Color);
        this.port = port;
        this.isHost = false;
        
        client = new Client(p1Color, serverIP, port);
        
        // Receive initial sync from server
        NetworkMessage syncMsg = client.receiveInitialSync();
        if (syncMsg != null) {
            // Update client's color based on server assignment
            this.myColor = syncMsg.clientColor;
            // Sync game state
            if (syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
        
        // Start listening for server moves
        startNetworkListener();
    }

    // ============================================================================
    // SHARED METHODS (Both host and client use)
    // ============================================================================

    @Override
    public void end(Color winner) {
        // Stop network listener
        stopNetworkListener();
        
        // Once a winner is determined, call methods to display who won in a pop up on each playes screen 
        // TODO: Show winner popup
    }



    /** 
     * Action execution (validate and apply a specific move)
     * Uses optimistic updates for client moves - immediately applies move to GUI,
     * then validates with server. Rollback occurs if server rejects.
     */
    @Override
    public boolean executeTurn(Position from, Position to, Piece piece) {
        if (WhosTurn == myColor) {
            if (isHost) {
                // Servers Local Move: validate and apply immediately
                boolean success = super.executeTurn(from, to, piece);
                
                if (success) {
                    // Send validated move to client using MOVE_UPDATE
                    server.sendMoveUpdate(from, to);
                }
                
                return success;
            } else {
                // Client local move: Backup game state and perform optimistic update
                backupBoard = deepCopyBoard(board);
                backupTurn = WhosTurn;
                
                boolean success = super.executeTurn(from, to, piece);
                
                if (success) {
                    // Send move to server for authoritative validation
                    client.sendMoveRequest(from, to);
                    // Server will respond async via handleMoveApproval() or handleMoveRejection()
                } else {
                    // Client-side validation failed, clear backup
                    backupBoard = null;
                    backupTurn = null;
                }
                return success;
            }
        } else {
            // REMOTE MOVE (opponent's turn)
            return executeRemoteMove(from, to, piece);
        }
    }

    /**
     * Executes a move received from the network opponent.
     * Applies the validated move without re-validating, then checks for game end conditions.
     * 
     * @param from Starting position of the piece
     * @param to Target position for the move
     * @param piece The piece being moved
     * @return true if move was successfully applied
     */
    private boolean executeRemoteMove(Position from, Position to, Piece piece) {
        // Apply the validated move received from network without re-validating
        Piece capturedPiece = board.getPieceAt(to);
        boolean kingCaptured = false;

        // Handle captures
        if (capturedPiece != null && capturedPiece.getColor() != piece.getColor()) {
            kingCaptured = board.capturePiece(piece, to);
        }

        // Update piece position
        board.updatePiecePosition(piece, from, to);

        // Check if game ended by King capture
        if (kingCaptured) {
            winner = WhosTurn;
            end(winner);
            return true;
        }

        // Check for checkmate using the CheckmateDetector
        player.Player opponent = getOpponentPlayer();
        if (validMoveDetector != null && validMoveDetector.isCheckmate(opponent.getColor())) {
            winner = WhosTurn;
            System.out.println("Checkmate! " + winner + " wins!");
            end(winner);
            return true;
        }

        // Switch turns
        switchTurn();
        
        return true;
    }

    /**
     * Starts a single background thread that continuously listens for network messages.
     * This thread runs for the lifetime of the game.
     */
    private void startNetworkListener() {
        isListening = true;
        networkListenerThread = new Thread(() -> {
            while (isListening) {
                try {
                    if (isHost) {
                        // Server: Listen for client move requests
                        NetworkMessage request = server.receiveMoveRequest();
                        if (request != null && request.from != null && request.to != null) {
                            handleClientMoveRequest(request);
                        }
                    } else {
                        // Client: Listen for server move updates
                        NetworkMessage update = client.receiveMoveUpdate();
                        if (update != null && update.from != null && update.to != null) {
                            handleServerMoveUpdate(update);
                        }
                    }
                } catch (Exception e) {
                    if (isListening) {
                        System.err.println("Network listener error: " + e.getMessage());
                    }
                }
            }
        }, "NetworkListener");
        networkListenerThread.setDaemon(true); // Thread dies when main program exits
        networkListenerThread.start();
    }
    
    /**
     * Handles move request from client (server side).
     * Validates the move and sends response.
     */
    private void handleClientMoveRequest(NetworkMessage request) {
        Piece piece = board.getPieceAt(request.from);
        
        // Validate the move on server side
        boolean isValid = board.attemptMove(request.to, piece);
        
        if (isValid) {
            // Execute the move on server side and notify client
            javax.swing.SwingUtilities.invokeLater(() -> {
                executeRemoteMove(request.from, request.to, piece);
                refreshBoardPanel();
            });
            server.sendMoveResponse(true);
        } else {
            server.sendMoveResponse(false);
        }
    }
    
    /**
     * Handles move update from server (client side).
     * Applies the validated move to the client's board.
     */
    private void handleServerMoveUpdate(NetworkMessage update) {
        Piece piece = board.getPieceAt(update.from);
        
        // Apply the validated move from server
        // All GUI updates must happen on EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            executeRemoteMove(update.from, update.to, piece);
            refreshBoardPanel();
        });
    }
    
    /**
     * Stops the network listener thread.
     * Call this when ending the game.
     */
    public void stopNetworkListener() {
        isListening = false;
        if (networkListenerThread != null) {
            networkListenerThread.interrupt();
        }
    } 


    // Syncs game state between host and client (for error recovery)
    public void updateGameState() {
        if (isHost) {
            // Host sends current game state to client
            Color clientColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
            server.sendInitialSync(this, clientColor);
        } else {
            // Client receives updated game state from host
            NetworkMessage syncMsg = client.receiveInitialSync();
            if (syncMsg != null && syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
            }
        }
    }
    
    public Color getMyColor() {
        return myColor;
    }
    
    /**
     * Deep copy of Board using serialization.
     * Leverages existing Serializable implementation.
     * 
     * @param original The board to copy
     * @return A deep copy of the board
     */
    private Board deepCopyBoard(Board original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bis);
            return (Board) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to deep copy board: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Called when server approves the client's optimistic move.
     * Clears the backup state as the move is confirmed valid.
     */
    public void handleMoveApproval() {
        // Move was valid, just clear backup
        backupBoard = null;
        backupTurn = null;
    }
    
    /**
     * Called when server rejects the client's optimistic move.
     * Rolls back the board state to the pre-move backup.
     * 
     * @param errorMessage Optional error message to display to user
     */
    public void handleMoveRejection(String errorMessage) {
        // Restore backup state
        this.board = backupBoard;
        this.WhosTurn = backupTurn;
        
        // Update GUI to reflect rollback
        refreshBoardPanel();
        
        // Show error to user
        if (errorMessage != null && !errorMessage.isEmpty()) {
            showInvalidMoveMessage();
        }
        
        // Clear backup
        backupBoard = null;
        backupTurn = null;
    }

    // ============================================================================
    // HOST-ONLY METHODS (Server-specific functionality)
    // ============================================================================

    // Handle incoming move request from client (only for host)
    public void handleMoveRequest() {
        if (!isHost) {
            throw new IllegalStateException("Only server can handle move requests");
        }

        NetworkMessage request = server.receiveMoveRequest();
        if (request != null) {
            // Validate Move needs to use CheckMateDetector
            
            // Send validation response back to client
            
        }
    }



    // ============================================================================
    // CLIENT-ONLY METHODS (Client-specific functionality)
    // ============================================================================


}
