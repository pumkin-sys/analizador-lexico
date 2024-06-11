import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;

public class LexicalAnalyzerGUI extends JFrame {

    // Contadores de tokens
    public static int reservedWordsCount = 0;
    public static int identifiersCount = 0;
    public static int relationalOperatorsCount = 0;
    public static int logicalOperatorsCount = 0;
    public static int arithmeticOperatorsCount = 0;
    public static int assignmentCount = 0;
    public static int integerNumbersCount = 0;
    public static int decimalNumbersCount = 0;
    public static int incrementCount = 0;
    public static int decrementCount = 0;
    public static int stringLiteralsCount = 0;
    public static int commentsCount = 0;
    public static int lineCommentsCount = 0;
    public static int parenthesesCount = 0;
    public static int bracesCount = 0;
    public static int errorsCount = 0;

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton analyzeButton;
    private JButton saveButton;
    private JButton openButton;
    private JFileChooser fileChooser;
    private File currentFile;

    public LexicalAnalyzerGUI() {
        setTitle("Analizador Léxico");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputTextArea = new JTextArea();
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        analyzeButton = new JButton("Analizar");
        saveButton = new JButton("Guardar");
        openButton = new JButton("Abrir Archivo");

        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String content = inputTextArea.getText();
                resetCounts();
                analyzeLexically(content);
                printCounts();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile != null) {
                    writeFile(currentFile.getPath(), inputTextArea.getText());
                } else {
                    JOptionPane.showMessageDialog(LexicalAnalyzerGUI.this, "No hay archivo abierto para guardar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(LexicalAnalyzerGUI.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    currentFile = fileChooser.getSelectedFile();
                    String content = readFile(currentFile.getPath());
                    inputTextArea.setText(content);
                }
            }
        });

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(analyzeButton);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputTextArea), BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void resetCounts() {
        reservedWordsCount = 0;
        identifiersCount = 0;
        relationalOperatorsCount = 0;
        logicalOperatorsCount = 0;
        arithmeticOperatorsCount = 0;
        assignmentCount = 0;
        integerNumbersCount = 0;
        decimalNumbersCount = 0;
        incrementCount = 0;
        decrementCount = 0;
        stringLiteralsCount = 0;
        commentsCount = 0;
        lineCommentsCount = 0;
        parenthesesCount = 0;
        bracesCount = 0;
        errorsCount = 0;
    }

    private void analyzeLexically(String content) {
        Automata automata = new Automata();
        automata.analyze(content);
    }

    private void printCounts() {
        StringBuilder result = new StringBuilder();
        result.append("-----------------------------------------\n");
        result.append("Palabras reservadas : ").append(reservedWordsCount).append("\n");
        result.append("Identificadores : ").append(identifiersCount).append("\n");
        result.append("Operadores Relacionales : ").append(relationalOperatorsCount).append("\n");
        result.append("Operadores Lógicos : ").append(logicalOperatorsCount).append("\n");
        result.append("Operadores Aritméticos : ").append(arithmeticOperatorsCount).append("\n");
        result.append("Asignaciones : ").append(assignmentCount).append("\n");
        result.append("Número Enteros : ").append(integerNumbersCount).append("\n");
        result.append("Números Decimales : ").append(decimalNumbersCount).append("\n");
        result.append("Incremento : ").append(incrementCount).append("\n");
        result.append("Decremento : ").append(decrementCount).append("\n");
        result.append("Cadena de Caracteres : ").append(stringLiteralsCount).append("\n");
        result.append("Comentario : ").append(commentsCount).append("\n");
        result.append("Comentario de Línea : ").append(lineCommentsCount).append("\n");
        result.append("Paréntesis : ").append(parenthesesCount).append("\n");
        result.append("Llaves : ").append(bracesCount).append("\n");
        result.append("Errores : ").append(errorsCount).append("\n");
        result.append("-----------------------------------------\n");
        outputTextArea.setText(result.toString());
    }

    private String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void writeFile(String filePath, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LexicalAnalyzerGUI().setVisible(true);
            }
        });
    }
}

class Automata {
    private static final Set<String> RESERVED_WORDS_SET = new HashSet<>(Arrays.asList(
        "if", "else", "while", "for", "switch", "case","do", "default", 
        "break", "int", "double", "String", "char", "print", "main"
    ));

    


    public void analyze(String content) {
        int length = content.length();
        int state = 0;
        StringBuilder token = new StringBuilder();
    
        for (int i = 0; i < length; i++) {
            char currentChar = content.charAt(i);
    
            switch (state) {
                case 0:
                    if (Character.isLetter(currentChar) || currentChar == '_') {
                        token.append(currentChar);
                        state = 1;
                    } else if (Character.isDigit(currentChar)) {
                        token.append(currentChar);
                        state = 2;
                    } else if (currentChar == '"') {
                        token.append(currentChar);
                        state = 3;
                    } else if (currentChar == '/' && i + 1 < length && content.charAt(i + 1) == '*') {
                        token.append("/*");
                        i++;
                        state = 4;
                    } else if (currentChar == '/' && i + 1 < length && content.charAt(i + 1) == '/') {
                        token.append("//");
                        i++;
                        state = 5;
                    } else if (currentChar == '<' || currentChar == '>' || currentChar == '=' || currentChar == '!') {
                        token.append(currentChar);
                        state = 6;
                    } else if (currentChar == '&') {
                        token.append(currentChar);
                        state = 7;
                    } else if (currentChar == '|') {
                        token.append(currentChar);
                        state = 8;
                    } else if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%') {
                        if (currentChar == '-' && i + 1 < length && Character.isDigit(content.charAt(i + 1))) {
                            token.append(currentChar);
                            state = 2;
                        } else {
                            token.append(currentChar);
                            state = 9;
                        }
                    } else if (currentChar == '(' || currentChar == ')') {
                        token.append(currentChar);
                        handleParenthesis(token.toString());
                        token.setLength(0);
                    } else if (currentChar == '{' || currentChar == '}') {
                        token.append(currentChar);
                        handleBrace(token.toString());
                        token.setLength(0);
                    } else if (Character.isWhitespace(currentChar)) {
                        // Do nothing
                    } else {
                        token.append(currentChar);
                        state = 0;
                    }
                    break;
    
                case 1:
                    if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                        token.append(currentChar);
                    } else {
                        if (Character.isWhitespace(currentChar) || currentChar == '(' || currentChar == ')' || currentChar == '{' || currentChar == '}' || currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '%' || currentChar == '<' || currentChar == '>' || currentChar == '=' || currentChar == '!' || currentChar == '&' || currentChar == '|') {
                            handleIdentifierOrReservedWord(token.toString());
                            token.setLength(0);
                            i--;
                            state = 0;
                        } else {
                            token.append(currentChar);
                            handleError(token.toString());
                            token.setLength(0);
                            state = 0;
                        }
                    }
                    break;
    
                case 2:
                    if (Character.isDigit(currentChar)) {
                        token.append(currentChar);
                    } else if (currentChar == '.') {
                        token.append(currentChar);
                        state = 14;
                    } else {
                        handleInteger(token.toString());
                        token.setLength(0);
                        i--;
                        state = 0;
                    }
                    break;
    
                case 3:
                    token.append(currentChar);
                    if (currentChar == '"') {
                        handleStringLiteral(token.toString());
                        token.setLength(0);
                        state = 0;
                    }
                    break;
    
                    case 4:
                    token.append(currentChar);
                    if (currentChar == '*' && i + 1 < length && content.charAt(i + 1) == '/') {
                        token.append("*/");
                        i++;
                        handleCommentBlock(token.toString());
                        token.setLength(0);
                        state = 0;
                    } else if (currentChar == '\n') {
                        handleError(token.toString()); 
                        token.setLength(0);
                        state = 0;
                    }
                    break;
                
    
                case 5:
                    token.append(currentChar);
                    if (currentChar == '\n') {
                        handleCommentLine(token.toString());
                        token.setLength(0);
                        state = 0;
                    }
                    break;
    
                case 6:
                    if (currentChar == '=') {
                        token.append(currentChar);
                        handleRelationalOperator(token.toString());
                        token.setLength(0);
                        state = 0;
                    } else if (Character.isWhitespace(currentChar) || Character.isLetterOrDigit(currentChar)) {
                        if (token.charAt(0) == '=') {
                            handleAssignment(token.toString());
                        } else if (token.charAt(0) == '!') {
                            handleLogicalOperator(token.toString());
                        } else {
                            handleRelationalOperator(token.toString());
                        }
                        token.setLength(0);
                        i--;
                        state = 0;
                    } else {
                        token.append(currentChar);
                        handleError(token.toString());
                        token.setLength(0);
                        state = 0;
                    }
                    break;
    
                case 7:
                    if (currentChar == '&') {
                        token.append(currentChar);
                        handleLogicalOperator(token.toString());
                        token.setLength(0);
                        state = 0;
                    } else {
                        handleError(token.toString());
                        token.setLength(0);
                        i--;
                        state = 0;
                    }
                    break;
    
                case 8:
                    if (currentChar == '|') {
                        token.append(currentChar);
                        handleLogicalOperator(token.toString());
                        token.setLength(0);
                        state = 0;
                    } else {
                        handleError(token.toString());
                        token.setLength(0);
                        i--;
                        state = 0;
                    }
                    break;
    
                    case 9:
                    if (currentChar == '+' || currentChar == '-') {
                        token.append(currentChar);
                        if (token.toString().equals("++")) {
                            LexicalAnalyzerGUI.incrementCount++;
                        } else if (token.toString().equals("--")) {
                            LexicalAnalyzerGUI.decrementCount++;
                        }
                        token.setLength(0);
                        state = 0;
                    } else {
                        handleArithmeticOperator(token.toString());
                        token.setLength(0);
                        i--;
                        state = 0;
                    }
                    break;
    
                case 12:
                    handleAssignment(token.toString());
                    token.setLength(0);
                    state = 0;
                    break;
    
                case 14:
                    if (Character.isDigit(currentChar)) {
                        token.append(currentChar);
                    } else if (currentChar == '.' && i + 1 < length) {
                        char nextChar = content.charAt(i + 1);
                        boolean isValidNextChar = Character.isDigit(nextChar) ||
                                                  Character.isLetter(nextChar) ||
                                                  Character.isWhitespace(nextChar);
                
                        if (!isValidNextChar) {
                            handleError(token.toString() + currentChar); 
                            token.setLength(0);
                            state = 13; 
                        } else {
                            token.append(currentChar);
                            state = 13; 
                        }
                    } else {
                        handleDecimal(token.toString());
                        token.setLength(0);
                        i--;
                        state = 0;
                    }
                    break;
                
    
                case 13: // Error 
                    token.append(currentChar);
                    if (Character.isWhitespace(currentChar)) {
                        handleError(token.toString());
                        token.setLength(0);
                        state = 0;
                    }
                    break;
            }
        }
    
        // Final state handling
        if (token.length() > 0) {
            switch (state) {
                case 1:
                    handleIdentifierOrReservedWord(token.toString());
                    break;
                case 2:
                    handleInteger(token.toString());
                    break;
                case 3:
                    handleError(token.toString()); 
                    break;
                case 4:
                    handleError(token.toString()); 
                    break;
                case 5:
                    handleCommentLine(token.toString());
                    break;
                case 6:
                    if (token.charAt(0) == '=') {
                        handleAssignment(token.toString());
                    } else if (token.charAt(0) == '!') {
                        handleLogicalOperator(token.toString());
                    } else {
                        handleRelationalOperator(token.toString());
                    }
                    break;
                case 7:
                    handleLogicalOperator(token.toString());
                    break;
                case 8:
                    handleLogicalOperator(token.toString());
                    break;
                case 9:
                    handleArithmeticOperator(token.toString());
                    break;
                case 10:
                    handleParenthesis(token.toString());
                    break;
                case 11:
                    handleBrace(token.toString());
                    break;
                case 12:
                    handleAssignment(token.toString());
                    break;
                case 13:
                    handleError(token.toString());
                    break;
                case 14:
                    handleDecimal(token.toString());
                    break;
            }
        }
    }
    
    
    
    
    
    
    private void handleIdentifierOrReservedWord(String token) {
        if (RESERVED_WORDS_SET.contains(token)) {
            LexicalAnalyzerGUI.reservedWordsCount++;
        } else {
            LexicalAnalyzerGUI.identifiersCount++;
        }
    }
    
    private void handleInteger(String token) {
        LexicalAnalyzerGUI.integerNumbersCount++;
    }
    
    private void handleDecimal(String token) {
        LexicalAnalyzerGUI.decimalNumbersCount++;
    }
    
    private void handleStringLiteral(String token) {
        LexicalAnalyzerGUI.stringLiteralsCount++;
    }
    
    private void handleCommentBlock(String token) {
        LexicalAnalyzerGUI.commentsCount++;
    }
    
    private void handleCommentLine(String token) {
        LexicalAnalyzerGUI.lineCommentsCount++;
    }
    
    private void handleRelationalOperator(String token) {
        LexicalAnalyzerGUI.relationalOperatorsCount++;
    }
    
    private void handleLogicalOperator(String token) {
        LexicalAnalyzerGUI.logicalOperatorsCount++;
    }
    
    private void handleArithmeticOperator(String token) {
        LexicalAnalyzerGUI.arithmeticOperatorsCount++;
    }
    
    private void handleParenthesis(String token) {
        LexicalAnalyzerGUI.parenthesesCount++;
    }
    
    private void handleBrace(String token) {
        LexicalAnalyzerGUI.bracesCount++;
    }
    
    private void handleAssignment(String token) {
        LexicalAnalyzerGUI.assignmentCount++;
    }
    
    private void handleError(String token) {
        LexicalAnalyzerGUI.errorsCount++;
    }
}
    