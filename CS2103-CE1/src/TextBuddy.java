import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TextBuddy {
	private String outputFile = "mytextfile.txt";
	private boolean exitProgram;
	private int itemCounter;

	public void startProgram() {
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

	public void printPromptForCommand() {
		System.out.println("command: ");
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

	public void addItem(String arguments) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile, true));
			writer.append(++itemCounter + ". " + arguments + "\n");
			writer.newLine();
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void displayItems() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.outputFile));

			String line;
			while ((line = reader.readLine()) != null) {
				//System.out.println(line.length());
				System.out.println(line);
			}
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void deleteItem(int itemToDelete) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(this.outputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter("tempfile.txt", true));
			int restartCounter = itemToDelete;
			
			String line;
		
			while ((line = reader.readLine()) != null) {
				if(line.length() != 0) {
					int currentItemNumber = Integer.parseInt(line.charAt(0)+"");
					if(currentItemNumber < itemToDelete) {
						writer.append(line);
						writer.newLine();
					} else if(currentItemNumber > itemToDelete){
						String newLine = restartCounter + line.split(".", 2)[1];
						writer.append(newLine);
						writer.newLine();
						restartCounter++;
					} else {
						continue;
					}
				}
			}
			
			reader.close();
			writer.close();
			
			File originalFile = new File(this.outputFile);
			File newFile = new File("tempfile.txt");
			
			originalFile.delete();	
			newFile.renameTo(new File(this.outputFile));
			
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
	}

	public void exitProgram() {
		this.exitProgram = true;
	}

	public void printWelcomeMessage() {
		System.out.println("Welcome to TextBuddy. " + outputFile + " is ready for use.");
	}
}
