package FrequencyListWordBankProject.FrequencyListWordBank;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Integer;
import java.awt.Color;
import java.awt.Dimension;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.*;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class App {  
    public static void main(String[] args) throws IOException {     // Thrown exception for if a file is not found
        System.out.println("App: Execution has begun");
        Scanner sc = new Scanner(new File(/*The absolute path in which the text file of the lyrics is located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\lyrics.txt"));       // File object passed to a Scanner
        PrintWriter pw = new PrintWriter(/*The absolute path in which the formatted output will be located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\output.txt");         // PrintWriter creates a file with the finalized output
        PrintWriter pw2 = new PrintWriter(/*The absolute path in which the formatted output will be located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\graph.txt");
        PrintWriter wordCloudTxt = new PrintWriter("C:\\Users\\Roxas\\Desktop\\Assignment_4\\wordCloudTxt.txt");
        ArrayList<String> wordList = new ArrayList<String>();       // the list that will be populated with the words in the file
        ArrayList<String> outputList = new ArrayList<String>();     // a formatted list with the intended output structure

        while (sc.hasNext()) {      // while there are more words to be read in: each input's special characters are replaced with an empty string, numbers replaced by an empty string, and lower cased.
            String word = removeStopWords(sc.next().replaceAll("[^a-zA-Z0-9]", "").replaceAll("[0-9]", "").toLowerCase());

            if (!word.equals("null") && !word.equals(""))       // If the input is not a stopword [determined by removeStopWords(String)], and is not an empty string, it is added to the the wordList
                wordList.add(word);
        }

        sc.close();     // Scanner is closed

        for (int i = 0; i < wordList.size(); i++)       // For every element in the wordList, the outputList will be populated with a formatted output (Frequency: Word)
            outputList.add(frequency(wordList, wordList.get(i)) + ": " + wordList.get(i));

        for (int i = 0; i < sortedList(outputList).size(); i++) {     // the sortedList will be printed to the file created by the PrintWriter
            pw.println(sortedList(outputList).get(i));
            pw2.println(sortedList(outputList).get(i));
            wordCloudTxt.println(sortedList(outputList).get(i).substring(sortedList(outputList).get(i).indexOf(":") + 2, sortedList(outputList).get(i).length()));
        }
        
        wordCloudTxt.close();
        pw2.close();

        pw.print("\nThe total number of unique, non-stopwords in this file is: " + sortedList(outputList).size());      // The total number of unique, non-stop words in the file are printed
        
        pw.close();     // PrintWriter is closed

        CsvReadOptions.Builder builder = CsvReadOptions.builder("C:\\Users\\Roxas\\Desktop\\Assignment_4\\graph.txt").separator(':').header(false);

        CsvReadOptions options = builder.build();
        Table myTable = Table.read().usingOptions(options);

        System.out.println(myTable.print());

        System.out.println(myTable.columnNames());

        Layout layout = Layout.builder("Word Frequencies", "Word's Frequency", "Total Amount of Words with the Same Frequency").build();
        HistogramTrace trace = HistogramTrace.builder(myTable.nCol("C0")).build();

        Plot.show(new Figure(layout, trace));

        // From the instantiation of the FrequencyAnalyzer to the wordCloud.writeToFile() method is example code from the "https://github.com/kennycason/kumo" repository to generate a Word Cloud on top of an image        
        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(300);
        frequencyAnalyzer.setMinWordLength(1);

        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(/*The absolute path in which the generated text for the word cloud is located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\wordCloudTxt.txt");
        final Dimension dimension = new Dimension(1200, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        
        wordCloud.setPadding(2);
        wordCloud.setBackground(new PixelBoundryBackground(/*The absolute path in which your background image is located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\wordCloudBackgroundPNG.png"));     // The wordCloudBackgroundPNG is the background image that you want your wordbank output to be placed over
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(/*The absolute path in which you would want your wordcloud to be located*/"C:\\Users\\Roxas\\Desktop\\Assignment_4\\wordCloud.png");
        System.out.println("App: Excecuted successfully");
    } // end of main method

    public static int frequency(ArrayList<String> list, String word) {      // If the inputted word matches a word in the wordList, count is incremented and returned
        int count = 0;

        for (int i = 0; i < list.size(); i++)
            if (word.equals(list.get(i)))
                count++;

        return count;
    }

    public static void removeDupes(ArrayList<String> list) {       // A modified linear search algorithm that removes the element at index j from the wordList if it matches the contents of the element at index i
        for (int i = 0; i < list.size() - 1; i++)
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).equals(list.get(j)))
                    list.remove(j);
    }

    // A sortedList that is populated with the sorted inputs (sorted in descending order by frequency) of the outputList, after the sortedList's duplicate inputs are removed
    public static ArrayList<String> sortedList(ArrayList<String> list) {
        ArrayList<String> tempList = list;

        for (int i = 0; i < tempList.size() - 1; i++)
            for (int j = i + 1; j < tempList.size(); j++) {
                String tempI = tempList.get(i);     // Temporary variables that store the elements at indexes i and j
                String tempJ = tempList.get(j);

                // If the value of the substring of the temporary variable, tempI (from the first character, up to the index of a colon), as an integer is less than 
                // the value of the substring of the temporary variable, tempJ, as an integer, then the two variables' index and value are swapped
                if (Integer.valueOf(tempI.substring(0, tempI.indexOf(":"))) < Integer.valueOf(tempJ.substring(0, tempJ.indexOf(":")))) {
                    tempList.set(i, tempJ);
                    tempList.set(j, tempI);
                }
            }
            
            removeDupes(tempList);      // duplicate words and their frequency values are removed from the tempList

            return tempList;    // the tempList is returned and its contents can be accessed by calling sortedList as if it were any standard ArrayList
    }

    // , it will be replaced by "null"
    public static String removeStopWords(String input) throws IOException {
        ArrayList<String> stopWordList = new ArrayList<String>();
        Scanner sc = new Scanner(new File("C:\\Users\\Roxas\\Desktop\\Assignment_4\\stopwords.txt"));

        while (sc.hasNext()) {
            String word = sc.next().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            stopWordList.add(word);
        }

        sc.close();

        for (int i = 0; i < stopWordList.size(); i++)
            if (input.substring(0, input.length()).equals(stopWordList.get(i)))
                return "null";

        return input;
    }
}   // end of class App
