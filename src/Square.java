import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class Square {

    private final boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighborMines;
    private final int r;
    private final int c;
    private final Square[][] board;
    private boolean lostMine = false;
    private final int[][] relativeLoc = {{1, -1}, {1, 0}, {1, 1},
            {0, -1}, {0, 0}, {0, 1},
            {-1, -1}, {-1, 0}, {-1, 1}};
    private final BufferedImage flag;
    private final BufferedImage mine;
    private final BufferedImage redX;

    Square(boolean isMine, int r, int c, Square[][] board,
           BufferedImage flag,
           BufferedImage mine,
           BufferedImage redX) {
        this.isMine = isMine;
        this.r = r;
        this.c = c;
        this.isRevealed = false;
        this.board = board;
        this.isFlagged = false;
        this.flag = flag;
        this.mine = mine;
        this.redX = redX;
    }

    private boolean surroundingFlagged() {
        int count = 0;
        if (!isFlagged && isRevealed) {
            for (int[] coords : relativeLoc) {
                int newR = r + coords[0];
                int newC = c + coords[1];
                if (newR >= 0 && newR <= board.length - 1) {
                    if (newC >= 0 && newC <= board[0].length - 1) {
                        if (board[newR][newC].isFlagged) count++;
                    }
                }
            }
        }
        return (count >= neighborMines);
    }

    void lost() {
        lostMine = true;
    }

    void calcNeighborMines() {
        neighborMines = 0;
        if (!isMine) {
            for (int[] coords : relativeLoc) {
                int newR = r + coords[0];
                int newC = c + coords[1];
                if (newR >= 0 && newR <= board.length - 1) {
                    if (newC >= 0 && newC <= board[0].length - 1) {
                        if (board[newR][newC].isMine) neighborMines++;
                    }
                }
            }
        }
    }

    void draw(Graphics2D g2) {
        int size = MineSweeper.SIZE;

        if (!isRevealed) {
            g2.setColor(Color.gray);
            g2.fillRect(c * size, r * size + (size * 2), size, size);
            if (isFlagged) g2.drawImage(flag, c * size, r * size + (size * 2), null);
        } else {
            if (isMine) {
                if (lostMine) {
                    g2.setColor(Color.red);
                    g2.fillRect(c * size, r * size + (size * 2), size, size);
                } else if (isFlagged) {
                    g2.setColor(Color.green);
                    g2.fillRect(c * size, r * size + (size * 2), size, size);
                }
                g2.drawImage(mine, c * size, r * size + (size * 2), null);
            } else {
                if (isFlagged) {
                    g2.drawImage(mine, c * size, r * size + (size * 2), null);
                    g2.drawImage(redX, c * size, r * size + (size * 2), null);
                }
            }

            if (neighborMines > 0) {
                g2.setColor(Color.BLACK);
                g2.drawString(Integer.toString(neighborMines),
                        c * size + (size / 2) - (size / 6),
                        r * size + (size * 3) - (size / 6));
            }
        }

        g2.setColor(Color.BLACK);
        g2.drawRect(c * size, r * size + (size * 2), size, size);
    }

    private void altRevealCell() {
        if (surroundingFlagged()) {
            if (isRevealed) {
                for (int[] coords : relativeLoc) {
                    int newR = r + coords[0];
                    int newC = c + coords[1];
                    if (newR >= 0 && newR <= board.length - 1) {
                        if (newC >= 0 && newC <= board[0].length - 1) {
                            if (!board[newR][newC].isRevealed &&
                                    !board[newR][newC].isFlagged) {
                                board[newR][newC].revealCell();
                            }
                        }
                    }
                }
            }
        }
    }

    private void revealCell() {
        if (!isFlagged) {
            isRevealed = true;
            if (neighborMines == 0) {
                for (int[] coords : relativeLoc) {
                    int newR = r + coords[0];
                    int newC = c + coords[1];
                    if (newR >= 0 && newR <= board.length - 1) {
                        if (newC >= 0 && newC <= board[0].length - 1) {
                            if (!board[newR][newC].isRevealed &&
                                    !board[newR][newC].isMine) board[newR][newC].revealCell();
                        }
                    }
                }
            }
        }
    }

    void click(MouseEvent e) {
        if (!isRevealed) {
            if (e.getButton() == MouseEvent.BUTTON1) revealCell();
            else isFlagged = !isFlagged;
        }
    }

    void click() {
        if (!isRevealed) {
            isFlagged = !isFlagged;
        } else {
            altRevealCell();
        }
    }

    boolean isMine() {
        return isMine;
    }

    boolean isRevealed() {
        return isRevealed;
    }

    boolean isFlagged() {
        return isFlagged;
    }

    void setRevealedTrue() {
        isRevealed = true;
    }


//    public void setFlagged(boolean flagged) {
//        isFlagged = flagged;
//    }
}
