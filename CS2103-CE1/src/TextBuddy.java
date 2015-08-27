import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TextBuddy {
	// Feedback from commands
	private static final String MSG_ADDED_ITEM = "added to %s: \"%s\"";
	private static final String MSG_DELETED_ITEM = "deleted from %s: \"%s\"";
	private static final String MSG_CLEARED_CONTENT = "all content deleted from %s";
	private static final String MSG_NO_ITEMS_TO_DISPLAY = "%s is empty";

	// Error messages
	private static final String MSG_INVALID_COMMAND = "Please enter a valid command.";
	private static final String MSG_INVALID_ARGUMENT = "Please enter valid argument. ";
	
	private static final String MSG_WELCOME = "Welcome to TextBuddy. %s is ready for use";
	private static final String MSG_PROMPT_FOR_COMMAND = "command: ";
	
	private String outputFile;
	private boolean exitProgram;
	private int itemCounter;

	public void startProgram() {
		// this.setOutputFile(outputFileName);
		this.printWelcomeMessage();
		this.handleUserCommands();
	}

	public void handleUserCommands() {
		while (!exitProgram) {
			this.printPromptForCommand();
			this.executeUserCommands();
		}
	}

	public String[] getUserInput() {
		Scanner scanner = new Scanner(System.in);

		String[] userInput = new String[2];
		userInput = scanner.nextLine().split(" ", 2);

		return userInput;
	}

	public String extractCommand(String[] userInput) {
		return userInput[0];
	}

	public String extractCommandArguments(String[] userInput) {
		String commandArguments = "";

		if (userInput.length == 2) {
			commandArguments = userInput[1];
		}

		return commandArguments;
	}

	public void executeUserCommands() {
		String[] input = this.getUserInput();
		String command = this.extractCommand(input);
		String commandArguments = this.extractCommandArguments(input);

		switch (command) {
		case "add":
			addItem(commandArguments);
			break;
		case "display":
			displayItems();
			break;
		case "delete":
			deleteItem(Integer.parseInt(commandArguments));
			break;
		case "clear":
			clearFileContent();
			break;
		case "exit":
			exitProgram();
			break;
		default:
			System.out.println("Invalid Command.");
		}
	}

	public void printFeedback(String message, String fileName, String commandArgs) {
		System.out.println(String.format(message, fileName, commandArgs));
	}
	
	public void addItem(String arguments) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile, true));
			writer.append(++itemCounter + ". " + arguments + "\n");
			writer.newLine();
			writer.close();
			printFeedback(MSG_ADDED_ITEM, this.outputFile, arguments);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void displayItems() {
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
		if(itemCounter == 0) {
			printFeedback(MSG_NO_ITEMS_TO_DISPLAY, this.outputFile, "");
		}
	}

	public void deleteItem(int itemToDelete) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.outputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter("tempfile.txt", true));
			int restartCounter = itemToDelete;
			String deletedItem = "";
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.length() != 0) {
					int currentItemNumber = Integer.parseInt(line.charAt(0) + "");
					if (currentItemNumber < itemToDelete) {
						writer.append(line);
						writer.newLine();
					} else if (currentItemNumber > itemToDelete) {
						String newLine = restartCounter + line.split(".", 2)[1];
						writer.append(newLine);
						writer.newLine();
						restartCounter++;
					} else {
						deletedItem = line.split(" ", 2)[1];
					}
				}
			}
			writer.flush();
			reader.close();
			writer.close();

			File originalFile = new File(this.outputFile);
			File newFile = new File("tempfile.txt");

			originalFile.delete();
			newFile.renameTo(new File(this.outputFile));
			printFeedback(MSG_DELETED_ITEM, this.outputFile, deletedItem);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void clearFileContent() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		this.itemCounter = 0;
		printFeedback(MSG_CLEARED_CONTENT, this.outputFile, "");

	}

	public void exitProgram() {
		this.exitProgram = true;
	}

	public void printWelcomeMessage() {
		System.out.println("Welcome to TextBuddy. " + outputFile + " is ready for use.");
	}
	
	public void printPromptForCommand() {
		System.out.println("command: ");
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isExitProgram() {
		return exitProgram;
	}

	public void setExitProgram(boolean exitProgram) {
		this.exitProgram = exitProgram;
	}

	public int getItemCounter() {
		return itemCounter;
	}

	public void setItemCounter(int itemCounter) {
		this.itemCounter = itemCounter;
	}
}
