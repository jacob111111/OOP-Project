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
    
    /**
     * Gets the color this player is playing as in the network game.
     * 
     * @return The color this player controls (WHITE or BLACK)
     */
    public Color getMyColor() {
        return myColor;
    }

    // ============================================================================
    // HOST-ONLY METHODS (Server-specific functionality)
    // ============================================================================

    /**
     * Constructor for host with pre-created and connected server.
     * Used when the server connection is managed externally (e.g., with cancellation support).
     * 
     * @param connectedServer Already connected Server instance
     * @param hostColor The color the host wants to play as
     */
    public Network(Server connectedServer, Color hostColor) {
        super(true, hostColor);
        this.port = 0; // Port already set in server
        this.isHost = true;
        this.myColor = hostColor;
        this.server = connectedServer;

        // Send initial game state to client for sync
        Color clientColor = (hostColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        server.sendInitialSync(this, clientColor);
        
        // Start listening for client moves
        startNetworkListener();
    }

    /**
     * Executes a move on the host/server side.
     * Validates and applies immediately, then broadcasts to client.
     * 
     * @param from Starting position
     * @param to Target position
     * @param piece The piece being moved
     * @return true if move was valid and executed
     */
    private boolean executeHostTurn(Position from, Position to, Piece piece) {
        // Servers Local Move: validate and apply immediately
        boolean success = super.executeTurn(from, to, piece);

        if (success) {
            // Send validated move to client using MOVE_UPDATE
            server.sendMoveUpdate(from, to);
        }
        return success;
    }

    /**
     * Sends current game state to client for synchronization.
     */
    private void sendGameStateToClient() {
        // Host sends current game state to client
        Color clientColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        server.sendInitialSync(this, clientColor);
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

    // ============================================================================
    // CLIENT-ONLY METHODS (Client-specific functionality)
    // ============================================================================

    public Network(boolean isPvP, String serverIP, int port) {
        super(isPvP, null);
        this.port = port;
        this.isHost = false;

        client = new Client(null, serverIP, port);

        // Receive initial sync from server
        NetworkMessage syncMsg = client.receiveInitialSync();
        if (syncMsg != null) {
            // Update client's color based on server assignment
            this.myColor = syncMsg.clientColor;
            // Sync game state
            if (syncMsg.gameState != null) {
                this.board = syncMsg.gameState.getBoard();
                this.WhosTurn = syncMsg.gameState.WhosTurn;
                // Reinitialize transient fields after deserialization
                this.currentPlayer = board.getPlayer(WhosTurn);
                this.validMoveDetector = board.getCheckmateDetector();
            }
        }
        
        // Start listening for server moves
        startNetworkListener();
    }

    /**
     * Executes a move on the client side with optimistic update.
     * Applies move immediately, then sends to server for validation.
     * Will rollback if server rejects.
     * 
     * @param from Starting position
     * @param to Target position
     * @param piece The piece being moved
     * @return true if move was locally valid (may still be rejected by server)
     */
    private boolean executeClientTurn(Position from, Position to, Piece piece) {
        // Client local move: Backup game state and perform optimistic update
        backupBoard = deepCopyBoard(board);
        backupTurn = WhosTurn;
        
        boolean success = super.executeTurn(from, to, piece);

        if (success) {
            // Send move to server for authoritative validation (async)
            // Server will respond via network listener which triggers handleMoveApproval() or handleMoveRejection()
            Thread sendThread = new Thread(() -> {
                client.sendMoveRequest(from, to);
            }, "ClientMoveRequest");
            sendThread.setDaemon(true);
            sendThread.start();
        } else {
            // Client-side validation failed, clear backup
            backupBoard = null;
            backupTurn = null;
        }
        return success;
    }

    /**
     * Receives and applies game state update from server.
     */
    private void receiveGameStateFromServer() {
        // Client receives updated game state from host
        NetworkMessage syncMsg = client.receiveInitialSync();
        if (syncMsg != null && syncMsg.gameState != null) {
            this.board = syncMsg.gameState.getBoard();
            this.WhosTurn = syncMsg.gameState.WhosTurn;
        }
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
     * Handles move update from server (client side).
     * Applies the validated move to the client's board.
     */
    private void handleServerMoveUpdate(NetworkMessage update) {
        Piece piece = board.getPieceAt(update.from);
        
        // Apply the validated move from server
        javax.swing.SwingUtilities.invokeLater(() -> {
            executeRemoteMove(update.from, update.to, piece);
            refreshBoardPanel();
        });
    }

    /**
     * Handles move response from server (client side).
     * Triggers approval or rejection of the client's optimistic move.
     */
    private void handleServerMoveResponse(NetworkMessage response) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (response.isValid != null && response.isValid) {
                handleMoveApproval();
            } else {
                handleMoveRejection("Move rejected by server, ROLLBACK initiated");
            }
        });
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
     * Executes a move received from the network opponent.
     * Applies the already-validated move from the server without re-validating.
     * The server is authoritative - it has already checked all game-ending conditions.
     * 
     * @param from Starting position of the piece
     * @param to Target position for the move
     * @param piece The piece being moved
     * @return true if move was successfully applied
     */
    private boolean executeRemoteMove(Position from, Position to, Piece piece) {
        // Apply the validated move received from network
        Piece capturedPiece = board.getPieceAt(to);

        // Handle captures if any
        if (capturedPiece != null && capturedPiece.getColor() != piece.getColor()) {
            board.capturePiece(piece, to, capturedPiece);
        }

        board.updatePiecePosition(piece, from, to);

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
                        // Server: Listen for client move requests (with 1-second timeout)
                        // On null: timeout, then loop continue checking isListening
                        NetworkMessage request = server.receiveMoveRequest();
                        if (request != null && request.from != null && request.to != null) {
                            handleClientMoveRequest(request);
                        }
                    } else {
                        // Client: Listen for server messages (with 1-second timeout)
                        // On null: timeout, then loop continue checking isListening
                        NetworkMessage message = client.receiveMessage();
                        if (message != null) {
                            if (message.type == utils.NetworkMessageType.MOVE_UPDATE && message.from != null && message.to != null) {
                                handleServerMoveUpdate(message);
                            } else if (message.type == utils.NetworkMessageType.MOVE_RESPONSE) {
                                handleServerMoveResponse(message);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (isListening) {
                        System.err.println("Network listener error: " + e.getMessage());
                    }
                }
            }
        }, "NetworkListener");
        networkListenerThread.setDaemon(true);
        networkListenerThread.start();
    }
    
    /**
     * Stops the network listener thread and closes all network resources.
     * Call this when ending the game.
     */
    public void stopNetworkListener() {
        isListening = false;
        if (networkListenerThread != null) {
            networkListenerThread.interrupt();
        }
        
        // Close network resources to prevent resource leaks
        if (server != null) {
            server.close();
        }
        if (client != null) {
            client.close();
        }
    }


    // ============================================================================
    // Context-dependent (Based on isHost)
    // ============================================================================
    
    /**
     * Action execution (validate and apply a specific move)
     * Delegates to host or client specific implementation.
     * Uses optimistic updates for client moves.
     */
    @Override
    public boolean executeTurn(Position from, Position to, Piece piece) {
        // Only allow moves when it's this player's turn
        if (WhosTurn != myColor) {
            System.out.println("Not your turn! Current turn: " + WhosTurn + ", Your color: " + myColor);
            showWaitForOpponentMessage();
            return false;
        }
        
        if (WhosTurn == myColor) {
            return isHost ? executeHostTurn(from, to, piece) : executeClientTurn(from, to, piece);
        } else {
            // REMOTE MOVE (opponent's turn)
            return executeRemoteMove(from, to, piece);
        }
    }

    /**
     * Syncs game state between host and client (for error recovery).
     * Delegates to host or client specific implementation.
     */
    public void updateGameState() {
        if (isHost) {
            sendGameStateToClient();
        } else {
            receiveGameStateFromServer();
        }
    }
}
