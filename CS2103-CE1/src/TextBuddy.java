import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * TextBuddy is a program that takes user inputs and records it into a text file.
 * User can view and delete items from the file as s/he wants.
 * If user does not specify while file s/he would like inputs to get written out to, 
 * program will store it into "mytextfile.txt" by default.
 * 
 * @author Jenny Vien
 */
public class TextBuddy {

	private static final String MSG_ADDED_ITEM = "added to %s: \"%s\"";
	private static final String MSG_DELETED_ITEM = "deleted from %s: \"%s\"";
	private static final String MSG_CLEARED_CONTENT = "all content deleted from %s";
	private static final String MSG_NO_ITEMS_TO_DISPLAY = "%s is empty";
	private static final String MSG_INVALID_COMMAND = "Please enter a valid command.";
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %s is ready for use.";
	private static final String PROMPT_FOR_COMMAND = "command:";
	private static final String TEMP_FILE = "tempfile.txt";

	private String outputFile;
	private int numItems;
	private Scanner scanner;
	
	public TextBuddy() {
		this.outputFile = "mytextfile.txt";
		scanner = new Scanner(System.in);
		this.numItems = 0;
	}

	public TextBuddy(String outputFile) {
		this.outputFile = outputFile;
		this.scanner = new Scanner(System.in);
		this.numItems = 0;

	}
	
	public static void main(String[] args) {
		TextBuddy tb;
		// if user did not specify output file
		if (args.length == 0) {
			tb = new TextBuddy();
		// else user specified output file
		} else {
			tb = new TextBuddy(args[0]);
		}
		tb.startProgram();
	}
	
	public String getOutputFile() {
		return this.outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public int getNumItems() {
		return this.numItems;
	}

	public void setNumItems(int itemCounter) {
		this.numItems = itemCounter;
	}

	public void startProgram() {
		this.printFeedback(WELCOME_MESSAGE);
		this.handleUserCommands();
	}

	private void handleUserCommands() {
		while (true) {
			this.printFeedback(PROMPT_FOR_COMMAND);
			this.executeUserCommands();
		}
	}

	private void executeUserCommands() {
		String[] input = this.getUserInput();
		String command = this.extractCommand(input);
		String commandArgument = this.extractCommandArgument(input);

		switch (command) {
		case "add":
			this.addItem(commandArgument);
			printFeedback(MSG_ADDED_ITEM, commandArgument);
			break;
		case "display":
			this.displayItems();
			break;
		case "delete":
			String deletedItem = this.deleteItem(Integer.parseInt(commandArgument));
			printFeedback(MSG_DELETED_ITEM, deletedItem);
			break;
		case "clear":
			this.clearFileContent();
			printFeedback(MSG_CLEARED_CONTENT);
			break;
		case "exit":
			System.exit(0);
		default:
			this.printFeedback(MSG_INVALID_COMMAND);
		}
	}
	/**
	 * Helper method that reads the user input from System.in.
	 * @return a string array with two elements
	 * userInput[0] contains the command (add, display, etc.)
	 * userInput[1] contains the arguments for the command. 
	 * 		(i.e. for "add item,"  userInput[1] contains "item")
	 */
	private String[] getUserInput() {
		String[] userInput = new String[2];

		if (this.scanner.hasNextLine()) {
			userInput = this.scanner.nextLine().split(" ", 2);
		} else {
			System.exit(0);
		}

		return userInput;
	}

	/**
	 * @param userInput
	 * @return the command that the user would like to execute.
	 * 		(i.e. for "add item," this method will return "add")
	 */
	private String extractCommand(String[] userInput) {
		return userInput[0];
	}

	/**
	 * @param userInput
	 * @return the argument for a given command.
	 * 		(i.e. for "add item," this method will return "item")
	 */
	private String extractCommandArgument(String[] userInput) {
		String commandArgument = "";

		if (userInput.length == 2) {
			commandArgument = userInput[1];
		}

		return commandArgument;
	}

	/**
	 * Adds argument into the output file.
	 * @param argument is the item to be added.
	 */
	private void addItem(String argument) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile, true));
			writer.append(++numItems + ". " + argument);
			writer.newLine();
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Displays all the items in the output file.
	 */
	private void displayItems() {
		if (this.numItems == 0) {
			this.printFeedback(MSG_NO_ITEMS_TO_DISPLAY);
		} else {
			this.printItems();
		}
	}

	/**
	 * @param itemToDelete an integer that specifies the position of the item in the file. 
	 * @return The item that was deleted from the file.
	 */
	private String deleteItem(int itemToDelete) {
		String deletedItem = this.delete(itemToDelete);
		this.updateOutputFile();
		return deletedItem;
	}

	/**
	 * Deletes all the entry from the file.
	 */
	private void clearFileContent() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.setNumItems(0);
	}
	
	/**
	 * Prints out all the items in the file to System.out
	 */
	private void printItems() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.outputFile));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Helper method to update the output file after deleting an item.
	 */
	private void updateOutputFile() {
		File originalFile = new File(this.outputFile);
		File newFile = new File(TEMP_FILE);
		originalFile.delete();
		newFile.renameTo(new File(this.outputFile));
	}

	/**
	 * Helper method to delete the item from the file.
	 * @param itemToDelete
	 * @return The item to be deleted.
	 */
	private String delete(int itemToDelete) {
		String itemDeleted = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.outputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_FILE, true));

			String line;
			int updatedCounter = itemToDelete;

			while ((line = reader.readLine()) != null) {
				if (line.length() != 0) {
					int currentItemNumber = Integer.parseInt(line.charAt(0) + "");
					if (currentItemNumber < itemToDelete) {
						writer.append(line);
						writer.newLine();
					} else if (currentItemNumber > itemToDelete) {
						String newLine = updatedCounter + line.split(".", 2)[1];
						writer.append(newLine);
						writer.newLine();
						updatedCounter++;
					} else {
						itemDeleted = line.split(" ", 2)[1];
					}
				}
			}

			writer.flush();
			reader.close();
			writer.close();

			this.setNumItems(this.getNumItems() - 1);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return itemDeleted;
	}
	
	/**
	 * Print messages to System.out
	 * @param feedBackMessage
	 */
	private void printFeedback(String feedBackMessage) {
		System.out.println(String.format(feedBackMessage, this.outputFile));
	}

	/**
	 * Print messages to System.out
	 * @param feedBackMessage
	 */

	private void printFeedback(String feedBackMessage, String feedbackArg) {
		System.out.println(String.format(feedBackMessage, this.outputFile, feedbackArg));
	}

}
