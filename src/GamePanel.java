import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 30;
    // Objects that can be fit onto screen:
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    // Arrays to hold coordinates of body parts of the snake
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts;
    int applesEaten;
    int bombAmount;
    int clearAmount;
    int doubleAmount;
    char direction = 'R';
    // Where the apple will appear
    int appleX;
    int appleY;
    boolean gridBoolean = false;
    int[] bombX = new int[50];
    int[] bombY = new int[50];
    int[] clearX = new int[50];
    int[] clearY = new int[50];
    int[] doubleX = new int[50];
    int[] doubleY = new int[50];

    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.pink);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        timer = new Timer(DELAY,this);
        startGame();
    }
    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        bombAmount = 0;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        generateApple();
        running = true;
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        if(running) {

            if(gridBoolean == true) {
                for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                    g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                    g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
            }

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            g.setColor(Color.black);
            for (int i = 0; i<bombAmount; i++) {
                g.fillOval(bombX[i], bombY[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            g.setColor(Color.yellow);
            for (int i = 0; i<doubleAmount; i++) {
                g.fillOval(doubleX[i], doubleY[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            g.setColor(Color.gray);
            for (int i = 0; i<clearAmount; i++) {
                g.fillOval(clearX[i], clearY[i], UNIT_SIZE, UNIT_SIZE);
            }

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(15, 15, 120));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(Color.blue);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.black);
            g.setFont(new Font("Ink Free",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }
    public void generateApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void generateBomb(int bombLoc) {
        bombX[bombLoc] = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        bombY[bombLoc] = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }
    
    public void generateClear(int clearLoc) {
    	clearX[clearLoc] = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        clearY[clearLoc] = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }
    
    public void generateDouble(int doubleLoc) {
    	doubleX[doubleLoc] = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
    	doubleY[doubleLoc] = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            generateApple();
            if ((applesEaten % 3 == 0) && (applesEaten % 5 == 0)) {
            	clearAmount = clearAmount+1;
            	generateClear(clearAmount-1);
            }
            if (applesEaten % 5 == 0) {
            	doubleAmount = doubleAmount+1;
            	generateDouble((applesEaten / 5)-1);
            }
            else if ((applesEaten % 3 == 0) && (bombAmount != applesEaten/3)) {
                bombAmount = bombAmount+1;
                generateBomb((applesEaten / 3)-1);
            }
        }
    }
    public void checkCollisions() {
        for(int i=bodyParts;i>0;i--) {
            if((x[0] == x[i])&&(y[0] == y[i])) {
                running = false;
            }
        }
        if(x[0] < 0) { running = false; }
        if(x[0] >= SCREEN_WIDTH) { running = false; }
        if(y[0] < 0) { running = false; }
        if(y[0] >= SCREEN_HEIGHT) { running = false; }
        for(int i = 0; i < bombAmount; i++) {
            if((x[0] == bombX[i]) && (y[0] == bombY[i])) { 
            	running = false; 
        	}
    	}
        for(int i = 0; i < doubleAmount; i++) {
            if((x[0] == doubleX[i]) && (y[0] == doubleY[i])) { 
            	applesEaten = applesEaten+2;
            	bodyParts++;
            	doubleAmount = doubleAmount-1;
            	if ((applesEaten % 3 == 0) && (applesEaten % 5 == 0)) {
                	clearAmount = clearAmount+1;
                	generateClear(clearAmount-1);
                }
                if (applesEaten % 5 == 0) {
                	doubleAmount = doubleAmount+1;
                	generateDouble((applesEaten / 5)-1);
                }
                else if ((applesEaten % 3 == 0) && (bombAmount != applesEaten/3)) {
                    bombAmount = bombAmount+1;
                    generateBomb((applesEaten / 3)-1);
                }
            }
        }
        for(int i = 0; i < clearAmount; i++) {
            if((x[0] == clearX[i]) && (y[0] == clearY[i])) { 
            	bombAmount = 0;
            	clearAmount--;
            }
        }

        if(!running) { timer.stop(); }
    }
    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());

        // Game over text
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    // Clears the screen of body parts
                    for(int i = 0; i < GAME_UNITS; i++) {
                        x[i] = 0;
                        y[i] = 0;
                    }
                    repaint();
                    startGame();
                    break;
                case KeyEvent.VK_G:
                    if (gridBoolean == false) {
                        gridBoolean = true;
                    }
                    else{
                        gridBoolean = false;
                    }
                    break;
            }
        }
    }
}