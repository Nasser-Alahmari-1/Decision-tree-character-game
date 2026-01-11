
- Decision-Tree-Based Character Identification Game

- Overview

This project is an interactive Java application that identifies a character by asking a sequence of intelligent yes/no questions.
It builds a decision tree from a CSV dataset and dynamically selects the most balanced questions to efficiently guess the character the user is thinking of.

- How It Works

Reads character data from a CSV file

Converts each record into a Person object

Automatically constructs a binary decision tree using character attributes

Traverses the tree through user answers (yes / no)

Identifies the character with minimal questions

- Technologies Used

Java (Java 14+ recommended)

Object-Oriented Programming (OOP)

Decision Trees

File I/O (CSV parsing)

Data Structures (Trees, Lists, Maps)

- Project Structure
.
├── Main.java        # Entry point of the program
├── GameEngine.java  # Decision tree logic and game flow
├── Person.java      # Data model for characters
├── dataset.csv      # Character dataset (CSV format)
└── README.md

- How to Run

Make sure Java 14 or higher is installed

Place the CSV dataset in the project directory

Update the CSV path in Main.java if needed

Compile and run:

javac Main.java
java Main

- Dataset Format

The CSV file must contain the following columns:

Name, Gender, Alive, AgeGroup, FamousFor, Nationality, Religion, Royalty


Each row represents a single character.

- Example Gameplay
Is the person alive? (yes/no)
Is the person famous for music? (yes/no)
Is your character Michael Jackson? (yes/no)

- Future Improvements

Add GUI support (JavaFX)

Improve CSV parsing (handle quoted fields)

Add learning capability to expand the dataset

Support fuzzy or partial answers

 - Author

Nasser Alahmari
