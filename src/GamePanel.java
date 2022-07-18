import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/*
 * IDEAS TO ADD
 * -- Snake has decreasing color opacity from head to end of body
 * -- At random times the border collisions are activated (like the edges turn red)
 * -- Random fading collision blocks in the middle of the screen
 * -- Super apples that give like 5 points
 * -- AI snake?
 * -- Portals?
 *
 *COULD BE BETTER
 * ---- Speed boost looks laggy and weird. Probably need to rethink the game timer
 * ---- i.e the movement to frame ratio shouldnt be 1 frame = 1 moved square
 * ---- maybe like 5 frames = 1 moved square or something.
 * DONE
 * -- If input direction is the same as the current direction, it should give a speed boost
 * -- Hit space to quickly restart game
 * -- Pausing functionality
 * */

/*
 * ISSUES
 * --Food spawns underneath the snake
 * --(0,0) square is filled if food gives snake more than 1 length
 * ---- i.e if the food is worth 10 the next 10 frames the 0,0 square is filled
 * ---- set the bodyParts increment to > 1 to see this.
 * ---- not a problem if food is only worth 1 but for new features this should be fixed
 *
 * CODE ISSUES
 * -- make functions for reused code
 */
public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1200;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20; //Should be perfectly divisible by screenwidth*screenheight
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;

    //Should the gridlines be drawn
    boolean drawGridLines = true;

    //Snake body coordinates. Cannot be larger than units on screen
    int x[] = new int[GAME_UNITS];
    int y[] = new int[GAME_UNITS];

    //Starting body parts
    int bodyParts = 6;

    //Score
    int applesEaten = 0;
    //Position of the apple.
    int appleX;
    int appleY;
    //Direction of snake
    char direction = 'R'; //right in the beginning
    //Game is running?
    boolean running = false;
    //Game is over?
    boolean isGameOver = false;

    //Random features
    boolean bordersOpen = false;
    //Instance of the timer class
    Timer timer;
    //Instance of the random class
    Random random;


    //Window constructor
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        //how fast the game runs is set here
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (!isGameOver && running) {
            //Draw grid (for debugging)
            if (drawGridLines) {
                for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
                    g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                    g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
            }

            //Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            //Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    //Head of the snake
                    g.setColor(Color.BLUE);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    //Rest of the snake
                    g.setColor(Color.orange);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            //Draw various texts
            g.setColor(Color.RED);
            g.setFont(new Font("Comic Sans", Font.BOLD, 25));

            //Get the string in the middle of the screen;
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else if (!running && !isGameOver) {
            //Display pause menu
            pauseMenu(g);
        } else {
            //Display game over menu
            gameOver(g);
        }
    }

    //Move the snake. Interate through bodyParts;
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            //Shift all coordinates over by 1 spot;
            x[i] = x[i - 1];
            y[i] = y[i - 1];
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
        default:
            throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    //Display a string of info to the user
    public void displayInfoString() {
        ;
    }

    //Check collisions with apple;
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            newApple();
            newApple();
        }
    }

    //Check collision with self and borders;
    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[i] == y[0])) {
                //check if head collides with body
                running = false; //game over method here;
                isGameOver = true;

            }
        }
        //Check if head touches left border
        if (x[0] < 0) {
            // if borderOpen?
//            x[0] = SCREEN_WIDTH;
            running = false;
            isGameOver = true;
        }
        //Check if head touches right border
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
//            x[0] = 0;
            running = false;
            isGameOver = true;

        }
        //Check if head touches top border
        if (y[0] < 0) {
            running = false;
            isGameOver = true;
        }
        //Check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            running = false;
            isGameOver = true;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Graphics g on pmst canvas context
        g.setColor(Color.RED);
        g.setFont(new Font("Comic Sans", Font.BOLD, 75));

        //Get the string in the middle of the screen;
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        //Draw the restart message
        String s = "Press space to restart";
        g.setFont(new Font("Comic Sans", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        g.setColor(Color.WHITE);
        g.drawString(s, (SCREEN_WIDTH - metrics.stringWidth(s)) / 2, SCREEN_HEIGHT - SCREEN_HEIGHT / 8);

        //Draw the score message
        s = "Score: " + applesEaten;
        g.setFont(new Font("Comic Sans", Font.BOLD, 25));
        metrics = getFontMetrics(g.getFont());
        g.setColor(Color.WHITE);
        g.drawString(s, (SCREEN_WIDTH - metrics.stringWidth(s)) / 2, SCREEN_HEIGHT - 2 * SCREEN_HEIGHT / 8);
    }

    //Pause menu
    public void pauseMenu(Graphics g) {
        //This code is the same as in the draw function except w gray
//        if(drawGridLines){
//            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
//                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
//            }
//        }
        g.setColor(Color.lightGray);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                //Head of the snake
                g.setColor(Color.darkGray);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                //Rest of the snake
                g.setColor(Color.gray);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans", Font.BOLD, 25));

        //Get the string in the middle of the screen;
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    //Starts a new game
    public void init() {
        //Snake body coordinates. Cannot be larger than units on screen
        x = new int[GAME_UNITS];
        y = new int[GAME_UNITS];

        //Starting body parts
        bodyParts = 6;

        //Score
        applesEaten = 0;
        //Position of the apple.
        appleX = 0;
        appleY = 0;
        //Direction of snake
        direction = 'R'; //right in the beginning
        //Game is running?
        running = false;
        isGameOver = false;
        startGame();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {
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
                //If paused do not change direction
                if (!running) break;
                //If already moving in this direction, give a speed boost
                if (direction == 'L') {
                    //speed boost;
                    move();
                }
                //No 180 degree turns should be allowed
                if (direction != 'R') {
                    direction = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                //If paused do not change direction
                if (!running) break;
                //If already moving in this direction, give a speed boost
                if (direction == 'R') {
                    //speed boost;
                    move();
                }
                //No 180 degree turns should be allowed
                if (direction != 'L') {
                    direction = 'R';
                }
                break;
            case KeyEvent.VK_DOWN:
                //If paused do not change direction
                if (!running) break;
                //If already moving in this direction, give a speed boost
                if (direction == 'D') {
                    //speed boost;
                    move();
                }
                //No 180 degree turns should be allowed
                if (direction != 'U') {
                    direction = 'D';
                }
                break;
            case KeyEvent.VK_UP:
                //If paused do not change direction
                if (!running) break;
                //If already moving in this direction, give a speed boost
                if (direction == 'U') {
                    //speed boost;
                    move();
                }
                //No 180 degree turns should be allowed
                if (direction != 'D') {
                    direction = 'U';
                }
                break;
            case KeyEvent.VK_SPACE:
                //Start a quick new game
                if (!running && isGameOver) {
                    init();
                }
                //This is for the pause menu. If paused you can press space to unpause.
                if (!running && !isGameOver) {
                    running = true;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                //Pause the game
                if (running) {
                    running = false;
                } else {
                    running = true;
                }
                break;
            }
        }
    }
}


