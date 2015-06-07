import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import java.util.Random;
import java.util.Arrays;


public class MainAppLayout extends JFrame implements KeyListener, ActionListener {
    
    
    public static final String ZOMBIE_FILE = "images/zombie.png";
    public static final String HUMAN_FILE = "images/human.png";
    public static final String DESTINATION_FILE = "images/destination.png";
    public static final String VACANT_FILE = "images/vacant.png";
    public static final String ALERT_ZONE_FILE = "images/alert.png";
    
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 10;
    
    static ImageIcon zombieImage = new ImageIcon(ZOMBIE_FILE);
    static ImageIcon humanImage = new ImageIcon(HUMAN_FILE);
    static ImageIcon destinationImage = new ImageIcon(DESTINATION_FILE);
    static ImageIcon vacantImage = new ImageIcon(VACANT_FILE);
    static ImageIcon alertImage = new ImageIcon(ALERT_ZONE_FILE);
    
    private int[][] gameBoard;
    private JPanel CenterPanel;
    private JPanel topPanelA;
    private JPanel buttomPanel;
    private int humanLocationX = 0;
    private int humanLocationY = 9;
    private int destinationX = 10;
    private int destinationY = 0;
    private int zombieALocationX = 0;
    private int zombieALocationY = 0;
    private int zombieBLocationX = 0;
    private int zombieBLocationY = 0;
    private boolean zombieAAlert = false;
    private boolean zombieBAlert = false;
    private boolean playerWon = false;
    private boolean playerLost = false;
    
    private JButton NewGameButton;
    
    public MainAppLayout(){
        
        // To close the application when clicking the close button of a window.
        // On MS Windows, it's the top right hand corner white on red cross
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Setting up how component will be organised in the main window
        this.setLayout(new BorderLayout(10, 20));
        
        // Setting up the background colour of the main window
        this.getContentPane().setBackground(Color.white);
        
        this.setTitle("Apocalypse - Zombies are coming!");
        
        initialise();
        
        this.addKeyListener(this);
        this.setFocusable(true);
        this.getContentPane().add(topPanelA, BorderLayout.NORTH);
        this.getContentPane().add(CenterPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttomPanel, BorderLayout.SOUTH);
        this.setSize(700, 700);
        
    }
    
    public void initialise(){
        
        //Initialise the interface.
        
        // TOP PANEL, series of button using FlowLayout
        topPanelA = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        topPanelA.setBackground(Color.RED);
        JTextArea TopPanelAMessage = new JTextArea("Zombies are flooding the city, the military has given up retaliation."
                + "\nYou must find your way towards the extraction point with arrow keys. Avoid the undead!"
                + "\nYellow areas represent zombies' line of sight. Avoid them if you can");
        TopPanelAMessage.setBackground(Color.red);
        topPanelA.add(TopPanelAMessage);
        
        // CENTER PANEL, main game map grid.
        CenterPanel = new JPanel(new GridLayout(BOARD_WIDTH,BOARD_HEIGHT,0,0));
        gameBoard = new int[BOARD_WIDTH][BOARD_HEIGHT];
        
        //Gameboard Encoding: 0 = vacant, 1 = zombie, 2 = human, 3 = destination, 4,5 = zombieA and zombie B's alert zone
        for(int i=0; i<gameBoard.length; i++){
            for(int j=0; j<gameBoard.length; j++){
                gameBoard[i][j] = 0;
            }
        }
        
        //Build the initial board.
        
        Random randomInt = new Random();
        humanLocationX = randomInt.nextInt(BOARD_WIDTH);
        destinationX = randomInt.nextInt(BOARD_WIDTH);
        gameBoard[gameBoard.length-1][humanLocationX] = 2;
        gameBoard[0][destinationX] = 3;
        
        zombieALocationX = randomInt.nextInt((BOARD_WIDTH/2-1)-0+1)+0;
        zombieALocationY = randomInt.nextInt((BOARD_HEIGHT-3)-2+1)+2;
        zombieBLocationX = randomInt.nextInt((BOARD_WIDTH-1)-(BOARD_WIDTH/2)+1)+(BOARD_WIDTH/2);
        zombieBLocationY = randomInt.nextInt((BOARD_HEIGHT-3)-2+1)+2;
        gameBoard[zombieALocationY][zombieALocationX] = 1;
        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
        updateBoard();
        
        // BOTTOM PANEL, use the default FlowLayout, containing two buttons.
        buttomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        NewGameButton = new JButton("New Game");
        NewGameButton.addActionListener( this );
        buttomPanel.add(NewGameButton);
    }
    
    public void updateBoard() {
        
        //Let the AI move the zombies.
        zombieMove();
        
        //update the board after keyboard move
        CenterPanel.removeAll();
        for(int i=0; i<gameBoard.length; i++){
            for(int j=0; j<gameBoard.length; j++){
                JLabel addedImage = new JLabel(vacantImage);
                if (gameBoard[i][j] == 4 || gameBoard[i][j] == 5) {
                    addedImage = new JLabel(alertImage);
                }
                else if (gameBoard[i][j] == 3) {
                    addedImage = new JLabel(destinationImage);
                }
                else if (gameBoard[i][j] == 2) {
                    addedImage = new JLabel(humanImage);
                }
                else if (gameBoard[i][j] == 1) {
                    addedImage = new JLabel(zombieImage);
                }
                else {
                    addedImage = new JLabel(vacantImage);
                }
                CenterPanel.add(addedImage);
            }
        }
        CenterPanel.repaint();
        CenterPanel.updateUI();
        
        if ((humanLocationX == zombieALocationX) & (humanLocationY == zombieALocationY)) {
            //Losing conditions check for zombie A.
            System.out.println("Player lost to Zombie A.");
            lost();
        }
        
        if ((humanLocationX == zombieBLocationX) & (humanLocationY == zombieBLocationY)) {
            //Losing conditions check for zombie B.
            System.out.println("Player lost to Zombie B.");
            lost();
        }
        
        if ((humanLocationX == destinationX) & (humanLocationY == destinationY)) {
            //Winning conditions check.
            winning();
        }
    }
    
    public void zombieMove(){
        
        boolean zombieAMoved = false;
        boolean zombieBMoved = false;
        
        if (zombieAAlert == false) {
            
            //Remove previous alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieALocationX] == 1 || gameBoard[i][zombieALocationX] == 4 || gameBoard[i][zombieALocationX] == 5) {
                    gameBoard[i][zombieALocationX] = 0;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieALocationY][i] == 1 || gameBoard[zombieALocationY][i] == 4 || gameBoard[zombieALocationY][i] == 5) {
                    gameBoard[zombieALocationY][i] = 0;
                }
            }
            
            //In case human moved to zombie's old location.
            if (gameBoard[zombieALocationY][zombieALocationX] == 1 || gameBoard[zombieALocationY][zombieALocationX] == 4 || gameBoard[zombieALocationY][zombieALocationX] == 5) {
                gameBoard[zombieALocationY][zombieALocationX] = 0;
            }
            
            boolean[] moveArray = moveValidator(zombieALocationX, zombieALocationY);
            System.out.println("========Zombie Move Starts========");
            System.out.print("Original Y for A:");
            System.out.println(zombieALocationY);
            System.out.print("Original X for A:");
            System.out.println(zombieALocationX);
            System.out.print("MoveArray for A: ");
            System.out.println(Arrays.toString(moveArray));
            
            Random randomMoveInt = new Random();
            while ((zombieAMoved == false) & (!areAllFalse(moveArray))) {
                int zombieAMove = randomMoveInt.nextInt(3-0+1)+0;
                if (moveArray[zombieAMove] == true) {
                    if (zombieAMove == 0) {
                        zombieALocationY -= 1;
                    }
                    else if (zombieAMove == 1){
                        zombieALocationY += 1;
                    }
                    else if (zombieAMove == 2){
                        zombieALocationX -= 1;
                    }
                    else if (zombieAMove == 3){
                        zombieALocationX += 1;
                    }
                    zombieAMoved = true;
                }
            }
            gameBoard[zombieALocationY][zombieALocationX] = 1;
            //Add alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieALocationX] == 0) {
                    gameBoard[i][zombieALocationX] = 4;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieALocationY][i] == 0) {
                    gameBoard[zombieALocationY][i] = 4;
                }
            }
            
        }
        else {

            //Remove previous alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieALocationX] == 1 || gameBoard[i][zombieALocationX] == 4 || gameBoard[zombieALocationX][i] == 5) {
                    gameBoard[i][zombieALocationX] = 0;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieALocationY][i] == 1 || gameBoard[zombieALocationY][i] == 4 || gameBoard[zombieALocationY][i] == 5) {
                    gameBoard[zombieALocationY][i] = 0;
                }
            }
            
            //In case human moved to zombie's old location.
            if (gameBoard[zombieALocationY][zombieALocationX] == 1 || gameBoard[zombieALocationY][zombieALocationX] == 4 || gameBoard[zombieALocationY][zombieALocationX] == 5) {
                gameBoard[zombieALocationY][zombieALocationX] = 0;
            }
            
            //Test and make move decision
            if (humanLocationX == zombieALocationX) {
                //if human and zombie on the same vertical plane
                if (humanLocationY > zombieALocationY) {
                    if (moveValidatorAlerted(zombieALocationX, zombieALocationY+2) == true) {
                        zombieALocationY+=2;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieALocationX, zombieALocationY+1) == true) {
                        zombieALocationY+=1;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                }
                else if (humanLocationY < zombieALocationY) {
                    if (moveValidatorAlerted(zombieALocationX, zombieALocationY-2) == true) {
                        zombieALocationY-=2;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieALocationX, zombieALocationY-1) == true) {
                        zombieALocationY-=1;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                }
            }
            
            
            if (humanLocationY == zombieALocationY) {
                //if human and zombie on the same horizontal plane
                if (humanLocationX > zombieALocationX) {
                    if (moveValidatorAlerted(zombieALocationX+2, zombieALocationY) == true) {
                        zombieALocationX+=2;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieALocationX+1, zombieALocationY) == true) {
                        zombieALocationX+=1;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                }
                else if (humanLocationX < zombieALocationX) {
                    if (moveValidatorAlerted(zombieALocationX-2, zombieALocationY) == true) {
                        zombieALocationX-=2;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieALocationX-1, zombieALocationY) == true) {
                        zombieALocationX-=1;
                        gameBoard[zombieALocationY][zombieALocationX] = 1;
                    }
                }
            }
        }
        
        if (zombieBAlert == false) {
            
            //In case human moved to zombie's old location.
            if (gameBoard[zombieBLocationY][zombieBLocationX] == 1 || gameBoard[zombieBLocationY][zombieBLocationX] == 4 || gameBoard[zombieBLocationY][zombieBLocationX] == 5) {
                gameBoard[zombieBLocationY][zombieBLocationX] = 0;
            }
            
            //Remove previous alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieBLocationX] == 1 || gameBoard[i][zombieBLocationX] == 4 || gameBoard[i][zombieBLocationX] == 5) {
                    gameBoard[i][zombieBLocationX] = 0;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieBLocationY][i] == 1 || gameBoard[zombieBLocationY][i] == 4 || gameBoard[zombieBLocationY][i] == 5) {
                    gameBoard[zombieBLocationY][i] = 0;
                }
            }
            
            boolean[] moveArray = moveValidator(zombieBLocationX, zombieBLocationY);
            System.out.print("Original Y for B:");
            System.out.println(zombieBLocationY);
            System.out.print("Original X for B:");
            System.out.println(zombieBLocationX);
            System.out.print("MoveArray for A: ");
            System.out.print("MoveArray for B: ");
            System.out.println(Arrays.toString(moveArray));
            System.out.print("========Zombie Move Ends========");
            System.out.println();System.out.println();
            Random randomMoveInt = new Random();
            while ((zombieBMoved == false) & (!areAllFalse(moveArray))) {
                int zombieBMove = randomMoveInt.nextInt(3-0+1)+0;
                if (moveArray[zombieBMove] == true) {
                    if (zombieBMove == 0) {
                        zombieBLocationY -= 1;
                    }
                    else if (zombieBMove == 1){
                        zombieBLocationY += 1;
                    }
                    else if (zombieBMove == 2){
                        zombieBLocationX -= 1;
                    }
                    else if (zombieBMove == 3){
                        zombieBLocationX += 1;
                    }
                    zombieBMoved = true;
                }
            }
            gameBoard[zombieBLocationY][zombieBLocationX] = 1;
            //Add alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieBLocationX] == 0) {
                    gameBoard[i][zombieBLocationX] = 5;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieBLocationY][i] == 0) {
                    gameBoard[zombieBLocationY][i] = 5;
                }
            }
        }
        
        else {
            //Remove previous alert zones
            for (int i = 0; i<BOARD_HEIGHT; i++) {
                if (gameBoard[i][zombieBLocationX] == 1 || gameBoard[i][zombieBLocationX] == 4 || gameBoard[i][zombieBLocationX] == 5) {
                    gameBoard[i][zombieBLocationX] = 0;
                }
            }
            for (int i = 0; i<BOARD_WIDTH; i++) {
                if (gameBoard[zombieBLocationY][i] == 1 || gameBoard[zombieBLocationY][i] == 4 || gameBoard[zombieBLocationY][i] == 5) {
                    gameBoard[zombieBLocationY][i] = 0;
                }
            }
            
            //In case human moved to zombie's old location.
            if (gameBoard[zombieBLocationY][zombieBLocationX] == 1 || gameBoard[zombieBLocationY][zombieBLocationX] == 4 || gameBoard[zombieBLocationY][zombieBLocationX] == 5) {
                gameBoard[zombieBLocationY][zombieBLocationX] = 0;
            }
            
            //Test and make move decision
            if (humanLocationX == zombieBLocationX) {
                //if human and zombie on the same vertical plane
                if (humanLocationY > zombieBLocationY) {
                    if (moveValidatorAlerted(zombieBLocationX, zombieBLocationY+2) == true) {
                        zombieBLocationY+=2;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieBLocationX, zombieBLocationY+1) == true) {
                        zombieBLocationY+=1;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                }
                else if (humanLocationY < zombieBLocationY) {
                    if (moveValidatorAlerted(zombieBLocationX, zombieBLocationY-2) == true) {
                        zombieBLocationY-=2;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieBLocationX, zombieBLocationY-1) == true) {
                        zombieBLocationY-=1;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                }
            }
            
            
            if (humanLocationY == zombieBLocationY) {
                //if human and zombie on the same horizontal plane
                if (humanLocationX > zombieBLocationX) {
                    if (moveValidatorAlerted(zombieBLocationX+2, zombieBLocationY) == true) {
                        zombieBLocationX+=2;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieBLocationX+1, zombieBLocationY) == true) {
                        zombieBLocationX+=1;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                }
                else if (humanLocationX < zombieBLocationX) {
                    if (moveValidatorAlerted(zombieBLocationX-2, zombieBLocationY) == true) {
                        zombieBLocationX-=2;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                    else if (moveValidatorAlerted(zombieBLocationX-1, zombieBLocationY) == true) {
                        zombieBLocationX-=1;
                        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
                    }
                }
            }
        }
        
        //Safeguarding code, prevent zombies from disappearing.
        gameBoard[zombieALocationY][zombieALocationX] = 1;
        gameBoard[zombieBLocationY][zombieBLocationX] = 1;
    }
    
    public boolean[] moveValidator(int x, int y) {
        //Return boolean values determining whether moving to a location is valid.
        
        boolean[] validMoveArray = new boolean[4];
        
        if (y>0) {
            if ((gameBoard[y-1][x] != 1) & (gameBoard[y-1][x] != 3)) {
                boolean canMoveUp = true;
                validMoveArray[0] = canMoveUp;
            }
        }
        else {
            boolean canMoveUp = false;
            validMoveArray[0] = canMoveUp;
        }
        
        if (y<(BOARD_HEIGHT-1)) {
            if ((gameBoard[y+1][x] != 1) & (gameBoard[y+1][x] != 3)){
                boolean canMoveDown = true;
                validMoveArray[1] = canMoveDown;
            }
        }
        else {
            boolean canMoveDown = false;
            validMoveArray[1] = canMoveDown;
        }
        
        if (x>0) {
            if ((gameBoard[y][x-1] != 1) & (gameBoard[y][x-1] != 3)){
                boolean canMoveLeft = true;
                validMoveArray[2] = canMoveLeft;
            }
        }
        else {
            boolean canMoveLeft = false;
            validMoveArray[2] = canMoveLeft;
        }
        
        if (x<(BOARD_WIDTH-1)) {
            if ((gameBoard[y][x+1] != 1) & (gameBoard[y][x+1] != 3)) {
                boolean canMoveRight = true;
                validMoveArray[3] = canMoveRight;
            }
        }
        else {
            boolean canMoveRight = false;
            validMoveArray[3] = canMoveRight;
        }
        return validMoveArray;
    }
    
    public boolean moveValidatorAlerted(int x, int y) {
        //Check if a zombie can move to a proposed destination on alerted state.
        if (x>=0 & x<=BOARD_WIDTH & y>=0 & y<=BOARD_HEIGHT) {
            return true;
        }
        else {
            return false;
        }
        
    }
    
    public void winning(){
        //Display player winning message, end the game.
        playerWon = true;
        topPanelA.removeAll();
        JTextArea TopPanelMessage = new JTextArea("Congratulations, you have escaped the undead and reached the extraction point!\n\n\n");
        topPanelA.setBackground(Color.GREEN);
        TopPanelMessage.setBackground(Color.GREEN);
        topPanelA.add(TopPanelMessage);
        topPanelA.repaint();
        topPanelA.updateUI();
    }
    
    public void lost(){
        //Display player losing message, end the game.
        playerLost = true;
        topPanelA.removeAll();
        JTextArea TopPanelMessage = new JTextArea("Unfortunately zombies caught you! You are lost.\n\n\n");
        topPanelA.setBackground(Color.RED);
        TopPanelMessage.setBackground(Color.RED);
        topPanelA.add(TopPanelMessage);
        topPanelA.repaint();
        topPanelA.updateUI();
    }
    
    public void zombieAlertCheck(int x, int y) {
        //Check if zombies are alerted
        
        if (gameBoard[y][x] == 4) {
            zombieAAlert = true;
            topPanelA.removeAll();
            JTextArea TopPanelMessage = new JTextArea("You walked into one of the zombies' line of sight,\n"
                    + " the zombie becomes actived!\n It will now move faster.\n");
            topPanelA.setBackground(Color.RED);
            TopPanelMessage.setBackground(Color.RED);
            topPanelA.add(TopPanelMessage);
            topPanelA.repaint();
            topPanelA.updateUI();
        }
        else {
            zombieAAlert = false;
            if (zombieBAlert == false) {
                topPanelA.removeAll();
                JTextArea TopPanelMessage = new JTextArea("Zombies are calm.\n\n\n");
                topPanelA.setBackground(Color.YELLOW);
                TopPanelMessage.setBackground(Color.YELLOW);
                topPanelA.add(TopPanelMessage);
                topPanelA.repaint();
                topPanelA.updateUI();
            }
        }
        
        if (gameBoard[y][x] == 5) {
            zombieBAlert = true;
            topPanelA.removeAll();
            JTextArea TopPanelMessage = new JTextArea("You walked into one of the zombies' line of sight,\n"
                    + " the zombie becomes actived!\n It will now move faster.\n");
            topPanelA.setBackground(Color.RED);
            TopPanelMessage.setBackground(Color.RED);
            topPanelA.add(TopPanelMessage);
            topPanelA.repaint();
            topPanelA.updateUI();
        }
        else {
            zombieBAlert = false;
            if (zombieAAlert == false) {
                topPanelA.removeAll();
                JTextArea TopPanelMessage = new JTextArea("Zombies are calm.\n\n\n");
                topPanelA.setBackground(Color.YELLOW);
                TopPanelMessage.setBackground(Color.YELLOW);
                topPanelA.add(TopPanelMessage);
                topPanelA.repaint();
                topPanelA.updateUI();
            }
        }
    }
    
    //Method of Key and Action Listeners
    @Override
    public void keyPressed(KeyEvent e) {
        
        int keyCode = e.getKeyCode();
        
        if (keyCode == 38) {
            if (humanLocationY > 0 & playerWon == false & playerLost == false) {
                gameBoard[humanLocationY][humanLocationX] = 0;
                humanLocationY -= 1;
                zombieAlertCheck(humanLocationX, humanLocationY);
                gameBoard[humanLocationY][humanLocationX] = 2;
                updateBoard();
            }
        }
        else if (keyCode == 40) {
            if (humanLocationY < 9 & playerWon == false & playerLost == false) {
                gameBoard[humanLocationY][humanLocationX] = 0;
                humanLocationY += 1;
                zombieAlertCheck(humanLocationX, humanLocationY);
                gameBoard[humanLocationY][humanLocationX] = 2;
                updateBoard();
            }
        }
        else if (keyCode == 37) {
            if (humanLocationX > 0 & playerWon == false & playerLost == false) {
                gameBoard[humanLocationY][humanLocationX] = 0;
                humanLocationX -= 1;
                zombieAlertCheck(humanLocationX, humanLocationY);
                gameBoard[humanLocationY][humanLocationX] = 2;
                updateBoard();
            }
        }
        else if (keyCode == 39) {
            if (humanLocationX < 9 & playerWon == false & playerLost == false) {
                gameBoard[humanLocationY][humanLocationX] = 0;
                humanLocationX += 1;
                zombieAlertCheck(humanLocationX, humanLocationY);
                gameBoard[humanLocationY][humanLocationX] = 2;
                updateBoard();
            }
        }
        
    }
    
    public void actionPerformed(ActionEvent e)
    {
        JButton source = (JButton)e.getSource();
        if (source == NewGameButton) {
            System.out.println("Resetting the game...");
            reset();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        //pass
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        //pass
    }
    
    
    //utility functions
    public static boolean areAllFalse(boolean[] array){
        //shortcut to check if all elements in an array is false
        for(boolean b : array) if(b) return false;
        return true;
    }
    
    public void reset(){
        //reset game
        this.getContentPane().removeAll();
        topPanelA.removeAll();
        CenterPanel.removeAll();
        buttomPanel.removeAll();
        humanLocationX = 0;
        humanLocationY = 9;
        destinationX = 10;
        destinationY = 0;
        zombieALocationX = 0;
        zombieALocationY = 0;
        zombieBLocationX = 0;
        zombieBLocationY = 0;
        zombieAAlert = false;
        zombieBAlert = false;
        playerWon = false;
        playerLost = false;
        initialise();
        this.getContentPane().add(topPanelA, BorderLayout.NORTH);
        this.getContentPane().add(CenterPanel, BorderLayout.CENTER);
        this.getContentPane().add(buttomPanel, BorderLayout.SOUTH);
        topPanelA.repaint();
        topPanelA.updateUI();
        CenterPanel.repaint();
        CenterPanel.updateUI();
        buttomPanel.repaint();
        buttomPanel.updateUI();
    }
    
    //Main
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MainAppLayout mainWindow = new MainAppLayout();
        mainWindow.setVisible(true);
    }
    
}

