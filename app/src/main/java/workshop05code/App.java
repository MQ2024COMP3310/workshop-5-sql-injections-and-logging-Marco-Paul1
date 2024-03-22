package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */

public class App {
    // Start code for logging exercise
    private static final Logger logger = Logger.getLogger(App.class.getName());

    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            // e1.printStackTrace();
            logger.log(Level.WARNING, "Issue with resources file", e1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            // System.out.println("Wordle created and connected."); // remove, not game
            // related -> logger
            logger.log(Level.OFF, "Wordle created and connected.");
        } else {
            // System.out.println("Not able to connect. Sorry!"); // remove, not game
            // related -> logger
            logger.log(Level.WARNING, "Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            // System.out.println("Wordle structures in place."); // remove, not game
            // related -> logger
            logger.log(Level.OFF, "Wordle structures in place.");
        } else {
            // System.out.println("Not able to launch. Sorry!"); // remove, not game related
            // -> logger
            logger.log(Level.WARNING, "Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                // System.out.println(line); remove -> prints invalid words too
                if (line.matches("^[a-z]{4}$")) {
                    logger.log(Level.OFF, line);
                    ;
                    wordleDatabaseConnection.addValidWord(i, line);
                } else {
                    logger.log(Level.SEVERE, line);
                    // System.out.println("Ignored Unacceptable input"); // remove, not game related
                    // -> logger
                }
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Not able to load. Sorry!", e);
            // System.out.println("Not able to load . Sorry!"); // remove, not game related
            // -> logger
            // System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {

            String guess = "";
            boolean valid = false;

            while (!valid && !guess.equals("q")) {
                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
                if (guess.matches("^[a-z]{4}$")) {
                    valid = true;
                } else {
                    logger.log(Level.FINE, guess);
                    System.out.println("Invalid Guess");
                }
            }

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess + "'.");

                if (wordleDatabaseConnection.isValidWord(guess)) {
                    System.out.println("Success! It is in the the list.\n");
                } else {
                    System.out.println("Sorry. This word is NOT in the the list.\n");
                }
                valid = false;
                while (!valid) {
                    System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                    guess = scanner.nextLine();
                    if (guess.matches("^[a-z]{4}$")) {
                        valid = true;
                    } else {
                        logger.log(Level.FINE, guess);
                        System.out.println("Invalid Guess"); // -> logged
                    }
                }
            }

        } catch (NoSuchElementException |

                IllegalStateException e) {
            logger.log(Level.WARNING, "An Error Occurred", e);
            // e.printStackTrace();
        }

    }
}