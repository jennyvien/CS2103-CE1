import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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
		this.outputFile = "output.txt";
		scanner = new Scanner(System.in);
		this.numItems = 0;
	}

	public TextBuddy(String outputFile) {
		super();
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
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public int getNumItems() {
		return numItems;
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

	private String[] getUserInput() {
		String[] userInput = new String[2];

		if (this.scanner.hasNextLine()) {
			userInput = this.scanner.nextLine().split(" ", 2);
		} else {
			System.exit(0);
		}

		return userInput;
	}

	private String extractCommand(String[] userInput) {
		return userInput[0];
	}

	private String extractCommandArgument(String[] userInput) {
		String commandArgument = "";

		if (userInput.length == 2) {
			commandArgument = userInput[1];
		}

		return commandArgument;
	}

	private void addItem(String arguments) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile, true));
			writer.append(++numItems + ". " + arguments);
			writer.newLine();
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void displayItems() {
		if (this.numItems == 0) {
			this.printFeedback(MSG_NO_ITEMS_TO_DISPLAY);
		} else {
			this.printItems();
		}
	}

	private String deleteItem(int itemToDelete) {
		String deletedItem = this.delete(itemToDelete);
		this.updateOutputFile();
		return deletedItem;
	}

	private void clearFileContent() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.setNumItems(0);
	}

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

	private void updateOutputFile() {
		File originalFile = new File(this.outputFile);
		File newFile = new File(TEMP_FILE);
		originalFile.delete();
		newFile.renameTo(new File(this.outputFile));
	}

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

	private void printFeedback(String feedBackMessage) {
		System.out.println(String.format(feedBackMessage, this.outputFile));
	}

	private void printFeedback(String feedBackMessage, String feedbackArg) {
		System.out.println(String.format(feedBackMessage, this.outputFile, feedbackArg));
	}

}
