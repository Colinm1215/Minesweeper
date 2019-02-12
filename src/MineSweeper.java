import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Colin Mettler
 * Minesweeper
 **/

class MineSweeper extends JPanel {
    private Square[][] board;
    private int mouseCurX, mouseCurY;
    private boolean gameOver = false;
    private boolean firstClick = false;
    private int totalMines, flagsLeft;
    private int time = 0;
    private final Random rand = new Random();
    private BufferedImage flag;
    private BufferedImage mine;
    private BufferedImage redX;
    private final JLabel flagsRemaining;
    private final JLabel timePassed;
    private static final JFrame window = new JFrame("Minesweeper");
    private Timer timer;

    static final int SIZE = 30;

    private MineSweeper(int width, int height) {
        JLabel bomb1 = new JLabel();
        JLabel bomb2 = new JLabel();
        flagsRemaining = new JLabel();
        timePassed = new JLabel();
        try {
            flag = ImageIO.read(getClass().getResource("/Resources/flag.png"));
            mine = ImageIO.read(getClass().getResource("/Resources/mine.png"));
            redX = ImageIO.read(getClass().getResource("/Resources/redX.png"));
            ImageIcon icon = new ImageIcon(mine);

            bomb1.setIcon(icon);
            bomb2.setIcon(icon);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IMAGE ERROR");
            System.exit(0);
        }
        setSize(width, height);

        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(width, SIZE * 2));

        JButton restart = new JButton("Restart");
        restart.addActionListener(e -> {
            int w = 600;
            int h = 600;
            if (timer != null) timer.stop();
            gameOver = true;
            Object[] options = {"Easy",
                    "Medium",
                    "Hard"};
            int n = JOptionPane.showOptionDialog(window,
                    "What difficulty would you like to play at?",
                    "Difficulty",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            switch (n) {
                case 0: // 18*18
                    break;
                case 1: // 21*21
                    w += SIZE * 3;
                    h += SIZE * 3;
                    break;
                case 2: // 24*24
                    w += SIZE * 6;
                    h += SIZE * 6;
                    break;
                default:
                    System.exit(0);
                    break;
            }
            window.setBounds(0, 0, width, height + 22); //(x, y, w, h) 22 due to title bar.
            setSize(h, w);
            reset(h, w);
            this.grabFocus();
            repaint();
        });

        menuPanel.add(flagsRemaining);
        menuPanel.add(bomb1);
        menuPanel.add(restart);
        menuPanel.add(bomb2);
        menuPanel.add(timePassed);
        this.add(menuPanel);
        reset(height, width);
    }

    private void reset(int height, int width) {
        window.setBounds(0, 0, width, height + 22);
        setSize(width, height);
        board = new Square[height / SIZE - 2][width / SIZE];
        firstClick = false;
        totalMines = ((height / SIZE - 2) * (width / SIZE)) / 6;
        flagsLeft = totalMines;
        time = 0;
        flagsRemaining.setText("Flags : " + flagsLeft);
        timePassed.setText("Time Passed : 0");
        timer = new Timer(1000, e -> {
            time++;
            timePassed.setText("Time Passed : " + time);
        });

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (board[r][c] == null) board[r][c] = new Square(false, r, c, board, flag, mine, redX);
            }
        }
        gameOver = false;
        setupListeners();
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);

        for (Square[] squares : board)
            for (int c = 0; c < board[0].length; c++) {
                squares[c].draw(g2);
            }
    }

    private void startGame(int clickedR, int clickedC) {
        ArrayList<int[]> coords = getRandomPlacements(totalMines, clickedR, clickedC);
        for (int[] coord : coords) {
            board[coord[0]][coord[1]] = new Square(true, coord[0], coord[1], board, flag, mine, redX);
        }

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (board[r][c] == null) board[r][c] = new Square(false, r, c, board, flag, mine, redX);
            }
        }

        for (Square[] row : board) {
            for (Square square : row) {
                square.calcNeighborMines();
            }
        }
        timer.start();
        firstClick = true;
    }

    private void setupListeners() {
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseCurX = e.getX();
                mouseCurY = e.getY();
                repaint();
            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    if (!firstClick) {
                        startGame(mouseCurY / SIZE - 2, mouseCurX / SIZE);
                    }
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        board[mouseCurY / SIZE - 2][mouseCurX / SIZE].click();
                    }
                    checkMines();
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!gameOver) {
                    if (!firstClick) {
                        startGame(mouseCurY / SIZE - 2, mouseCurX / SIZE);
                    }
                    int x = e.getX();
                    int y = e.getY();
                    System.out.println(x);
                    System.out.println(y);
                    System.out.println();

                    int r = (y / SIZE) - 2;
                    int c = x / SIZE;

                    if (r >= 0) board[r][c].click(e);

                    checkMines();
                }
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void lost() {
        gameOver = true;
        timer.stop();
        for (Square[] squares : board) {
            for (int c = 0; c < board[0].length; c++) {
                squares[c].setRevealedTrue();
            }
        }
        repaint();
        JOptionPane.showMessageDialog(window,
                "You Lose!");
    }

    private void win() {
        gameOver = true;
        timer.stop();
        for (Square[] squares : board) {
            for (int c = 0; c < board[0].length; c++) {
                squares[c].setRevealedTrue();
            }
        }
        repaint();
        JOptionPane.showMessageDialog(window,
                "You Win!");
    }

    private void checkMines() {
        int minesFound = 0;
        int nonMines = 0;
        flagsLeft = totalMines;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                Square square = board[r][c];
                if (!square.isRevealed()) {
                    if (square.isFlagged()) {
                        flagsLeft--;
                        if (square.isMine()) minesFound++;
                    }
                } else if (square.isMine()) {
                    board[r][c].lost();
                    this.lost();
                    r = board.length + 1;
                    break;
                } else {
                    nonMines++;
                }
            }
        }
        if (flagsLeft == 0) {
            if (minesFound == totalMines) win();
        } else if (board.length * board[0].length - totalMines == nonMines) { // if all non mines have been revealed
            win();
        }
    }

    private ArrayList<int[]> getRandomPlacements(int num, int row, int col) {
        ArrayList<int[]> coords = new ArrayList<>();
        while (num > 0) {
            int r = rand.nextInt(board.length);
            int c = rand.nextInt(board[0].length);
            if (r != row && c != col) {
                coords.add(new int[]{r, c});
                num--;
            }
        }
        return coords;
    }

    //sets ups the panel and frame.  Probably not much to modify here.
    public static void main(String[] args) {
        int width = 600;
        int height = 600;

        Object[] options = {"Easy",
                "Medium",
                "Hard"};
        int n = JOptionPane.showOptionDialog(window,
                "What difficulty would you like to play at?",
                "Difficulty",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]);

        switch (n) {
            case 0: // 18*18
                break;
            case 1: // 21*21
                width += SIZE * 3;
                height += SIZE * 3;
                break;
            case 2: // 24*24
                width += SIZE * 6;
                height += SIZE * 6;
                break;
            default:
                System.exit(0);
                break;
        }
        window.setBounds(0, 0, width, height + 22); //(x, y, w, h) 22 due to title bar.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MineSweeper panel = new MineSweeper(width, height);

        panel.setFocusable(true);
        panel.grabFocus();

        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }

}
