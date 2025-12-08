package gui.menu;

import java.awt.Component;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Manages game save and load operations.
 * 
 * This class handles file operations for saving and loading chess games,
 * including file chooser dialogs and serialization/deserialization.
 */
public class SaveLoadManager {
    
    /**
     * Handles saving the current game state to a file.
     * 
     * Opens a file chooser dialog and serializes the current game
     * object to the selected file. Shows error messages if save fails.
     * 
     * @param parent The parent component for dialogs
     * @param currentGame The current game to save
     */
    public static void handleSaveGame(Component parent, game.GUI currentGame) {
        if (currentGame == null) {
            JOptionPane.showMessageDialog(parent, "No game to save!", "Save Game", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));
        fileChooser.setSelectedFile(new File("game.chess"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".chess")) {
                file = new File(file.getAbsolutePath() + ".chess");
            }

            try {
                saveGameToFile(file, currentGame);
                JOptionPane.showMessageDialog(parent, "Game saved successfully!", "Save Game",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Failed to save game: " + e.getMessage(), "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles loading a game state from a file.
     * 
     * Opens a file chooser dialog and deserializes a game object
     * from the selected file. Returns the loaded game or null if cancelled/failed.
     * 
     * @param parent The parent component for dialogs
     * @param currentGame The current game (to check if it should be replaced)
     * @return The loaded game, or null if operation was cancelled or failed
     */
    public static game.GUI handleLoadGame(Component parent, game.GUI currentGame) {
        if (currentGame != null) {
            int result = JOptionPane.showConfirmDialog(
                    parent,
                    "This will replace the current game. Are you sure?",
                    "Load Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                game.GUI loadedGame = loadGameFromFile(file);
                JOptionPane.showMessageDialog(parent, "Game loaded successfully!", "Load Game",
                        JOptionPane.INFORMATION_MESSAGE);
                return loadedGame;
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(parent, "Failed to load game: " + e.getMessage(), "Load Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        
        return null;
    }

    /**
     * Serializes and saves the game to the specified file.
     * 
     * @param file The file to save the game state to
     * @param game The game to save
     * @throws IOException if file writing fails
     */
    private static void saveGameToFile(File file, game.GUI game) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(game);
        }
    }

    /**
     * Deserializes and loads a game from the specified file.
     * 
     * @param file The file to load the game state from
     * @return The loaded game object
     * @throws IOException if file reading fails
     * @throws ClassNotFoundException if game class cannot be found
     */
    private static game.GUI loadGameFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (game.GUI) ois.readObject();
        }
    }
}
