# Glossary Generator

## Overview

This Java application generates a glossary of terms and their definitions as HTML pages. It reads a text file containing terms and definitions, creates an `index.html` page listing all terms, and generates individual HTML pages for each term with hyperlinked references.

## Features

- Reads terms and definitions from a text file.
- Generates an alphabetically sorted list of terms.
- Creates a main `index.html` with links to individual term pages.
- Generates individual HTML pages for each term with hyperlinked references to other terms.
- Includes basic CSS styling for the generated HTML pages.

## How to Use

### Prerequisites

- Java Development Kit (JDK) installed on your system.
- Basic knowledge of Java and HTML.

### Running the Application

1. Clone this repository or download the source code.
2. Compile the Java files using `javac Glossary.java`.
3. Run the application with `java Glossary`.
4. Follow the prompts in the console to provide the input file and the output directory.

### Input File Format

The input file should be named terms.txt and contain terms and definitions in the following format:

Term1
Definition for term1.

Term2
Definition for term2.

Definitions can span multiple lines, but terms must be separated by a blank line.

A sample terms.txt file has been included.

## Customization

You can customize the styling of the HTML pages by modifying the `style.css` file and moving it into your output directory.

## Contributing

Contributions to the Glossary Generator are welcome! Please feel free to submit pull requests with new features, fixes, or improvements.

## License

This project is open source and available under the [MIT License](LICENSE).
