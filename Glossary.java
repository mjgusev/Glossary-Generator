import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Collections;
import java.util.List;
public final class Glossary {

    private Glossary() {
    }

    public static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    public static void htmlHeader(String title, BufferedWriter out) throws IOException {
        out.write("<!DOCTYPE html>\n");
        out.write("<html>\n");
        out.write("<head>\n");
        out.write("<meta charset=\"utf-8\">\n");
        out.write("<title>" + title + "</title>\n");
        out.write("<link rel=\"stylesheet\" href=\"style.css\">\n");
        out.write("</head>\n");
    }

    public static Map<String, String> generateTermsAndDefinitionsMap(String inputFile) {
        Map<String, String> termsAndDefinitions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String term;

            // read the file line by line
            while ((term = reader.readLine()) != null) {
                StringBuilder def = new StringBuilder();
                String nextLine = reader.readLine();
                while (nextLine != null && !nextLine.isEmpty()) {
                    // read the definition until an empty line is found
                    def.append(nextLine).append(System.lineSeparator());
                    nextLine = reader.readLine();
                }
                // add the term and definition to the map
                termsAndDefinitions.put(term, def.toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return termsAndDefinitions;
    }

    public static Queue<String> getSortedTerms(Map<String, String> termsAndDefinitions) {
        List<String> terms = new LinkedList<>(termsAndDefinitions.keySet());
        Collections.sort(terms, String.CASE_INSENSITIVE_ORDER);
        return new LinkedList<>(terms);
    }

    public static void glossaryList(Queue<String> terms, BufferedWriter out) throws IOException {
        out.write("<ul>\n");

        // creates a <li> for every term in the list
        for (String term : terms) {
            out.write("<li><a id=\"link\" href=\"" + term + ".html\">" + term + "</a></li>\n");
        }

        out.write("</ul>\n");
    }

    public static void printDefinitionWithHyperlinks(Map<String, String> termsAndDefinitions, String definition, BufferedWriter fileOut) throws IOException {
        int position = 0;
        Set<Character> separators = generateSeparators();

        fileOut.write("<blockquote>");
        while (position < definition.length()) {
            String currentTerm = nextWordOrSeparator(definition, position, separators);

            // check if the current word is a term
            if (termsAndDefinitions.containsKey(currentTerm)) {
                // if it is, provide a hyperlink
                fileOut.write("<a id=\"link\" href=\"" + currentTerm + ".html\">" + currentTerm + "</a>");
            } else {
                // otherwise, just print the term
                fileOut.write(currentTerm);
            }
            position += currentTerm.length();
        }
        fileOut.write("</blockquote>");
    }

    private static Set<Character> generateSeparators() {
        Set<Character> separators = new HashSet<>();

        // add all the separators to the set
        for (char c : ", .?!-\n\t".toCharArray()) {
            separators.add(c);
        }
        return separators;
    }


    public static String nextWordOrSeparator(String text, int position, Set<Character> separators) {
        StringBuilder token = new StringBuilder();
        char currentChar = text.charAt(position);
        boolean isSeparator = separators.contains(currentChar);

        while (position < text.length()) {
            currentChar = text.charAt(position);
            boolean currentIsSeparator = separators.contains(currentChar);

            if (currentIsSeparator != isSeparator) {
                // When the current character does not match the type (separator or not) of the token we're building, stop.
                break;
            }

            token.append(currentChar);
            position++;
        }

        return token.toString();
    }
    
    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter indexOut = null;

        try {
            String inputFileName = "terms.txt"; // The name of the input file
            String outputDir = "output/"; // The directory where the output files will be written

            // Check if the output directory exists, and create it if it doesn't
            File directory = new File(outputDir);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Unable to create output directory");
            }

            in = new BufferedReader(new FileReader(inputFileName));
            indexOut = new BufferedWriter(new FileWriter(outputDir + "index.html"));

            // Generate the terms and definitions map from the input file
            Map<String, String> termsAndDefinitions = generateTermsAndDefinitionsMap(inputFileName);

            // Get sorted terms
            Queue<String> sortedTerms = getSortedTerms(termsAndDefinitions);

            // Write the header for the index file
            htmlHeader("Glossary", indexOut);
            indexOut.write("<body>\n");
            indexOut.write("<h2>Glossary</h2>\n");
            indexOut.write("<hr />\n");

            // Generate list with hyperlinks to each term page in the index file
            glossaryList(sortedTerms, indexOut);

            // Iterate over each term to create individual term pages
            for (String term : sortedTerms) {
                try (BufferedWriter termOut = new BufferedWriter(new FileWriter(outputDir + term + ".html"))) {
                    String definition = termsAndDefinitions.get(term);

                    // Create a page for the term
                    htmlHeader(term, termOut);
                    termOut.write("<body>\n");
                    termOut.write("<h2>" + term + "</h2>\n");

                    // Print the term definition including hyperlinks
                    printDefinitionWithHyperlinks(termsAndDefinitions, definition, termOut);
                    termOut.write("<hr />\n");
                    termOut.write("<p>Return to <a id=\"link\" href=\"index.html\">index</a>.</p>\n");
                    termOut.write("</body>\n");
                    termOut.write("</html>\n");
                } catch (IOException e) {
                    System.err.println("Error writing the term file for: " + term);
                    e.printStackTrace();
                }
            }

            // Finish the index file
            indexOut.write("<p>Created by <a id=\"link\" href=\"https://github.com/mjgusev\">Misha Gusev</a></p>\n");

            indexOut.write("</body>\n");
            indexOut.write("</html>\n");

            // Files created message
            System.out.println("Files created.");

        } catch (IOException e) {
            System.err.println("Error processing the files.");
            e.printStackTrace();
        } finally {
            // Close the streams
            try {
                if (in != null) {
                    in.close();
                }
                if (indexOut != null) {
                    indexOut.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing the files.");
                e.printStackTrace();
            }
        }
    }

    
}
