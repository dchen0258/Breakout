
/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	private static final double Y_VELOCITY = 0.3;

	/* Method: run() */
	/** Runs the Breakout program. */

	boolean restarting, computerTry, playing;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private double vx, vy;
	double lastX, startingPaddleY;
	int livesLeft, bricksLeft, level;
	GRect Brick, Paddle, tryAgainButton, computerTryButton, playButton, Rectangles;
	GOval Ball;
	GObject top, bottom, left, right;
	GLabel displayLivesLeft, displayBricksLeft, displayLevel, gameOverLose, gameOverWin, tryAgain, comTry, Loading,
			moveToNextLevel, clickToPlay;

	/*
	 * Sets booleans, one for restarting- which sets up the game again one of
	 * computer try if it is the computer playing or not, and one for the
	 * click to play feature, to see if they want to play
	 */
	public void run() {
		level = 1;
		computerTry = false;
		restarting = true;
		playing = false;

		printWelcomeSequence();
		while (restarting) {
			setUpGame();
			playGame();
			restartGame();
		}
	}

	public void setUpGame() {
		restarting = false;
		printBricks();
		createPaddle();
		createBall();
		createLabels();
	}

	public void playGame() {
		moveBall();
		gameOver();
	}

	public void restartGame() {

		while (!restarting) {
			pause(10);
		}
		disappearingAct();
	}

	public void printWelcomeSequence() {
		GLabel welcomeMessage = new GLabel("WELCOME TO BREAKOUT");
		welcomeMessage.setFont("Arial-25");
		add(welcomeMessage, WIDTH / 2 - welcomeMessage.getWidth() / 2, HEIGHT / 2);
		pause(1500);
		remove(welcomeMessage);

		GLabel description1 = new GLabel("Your goal is to break all the bricks");
		description1.setFont("Arial-20");
		add(description1, WIDTH / 2 - description1.getWidth() / 2, HEIGHT / 2);
		pause(1500);
		remove(description1);

		GLabel description2 = new GLabel("You have " + NTURNS + " lives");
		description2.setFont("Arial-20");
		add(description2, WIDTH / 2 - description2.getWidth() / 2, HEIGHT / 2);
		pause(1500);
		remove(description2);

		GLabel description3 = new GLabel("There are 3 levels in total");
		description3.setFont("Arial-20");
		add(description3, WIDTH / 2 - description3.getWidth() / 2, HEIGHT / 2);
		pause(1500);
		remove(description3);
	}

	/*
	 * Prints bricks in rectangle formation, changing the color every two rows
	 */
	public void printBricks() {

		double startingBrickX = WIDTH / 2 - ((double) NBRICKS_PER_ROW / 2 * BRICK_WIDTH)- (double) (NBRICKS_PER_ROW - 1) / 2 * BRICK_SEP;
		double startingBrickY = (BRICK_Y_OFFSET);

		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {

				double changeBrickX = (BRICK_WIDTH * j) + (BRICK_SEP * j);
				double changeBrickY = (BRICK_SEP * i) + (BRICK_HEIGHT * i);

				Brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				add(Brick, startingBrickX + changeBrickX, startingBrickY + changeBrickY);
				pause(7);

				if (i <= 1) {
					Brick.setFillColor(Color.CYAN);
					Brick.setFilled(true);
				} else if (i <= 3) {
					Brick.setFillColor(Color.GREEN);
					Brick.setFilled(true);
				} else if (i <= 5) {
					Brick.setFillColor(Color.YELLOW);
					Brick.setFilled(true);
				} else if (i <= 7) {
					Brick.setFillColor(Color.ORANGE);
					Brick.setFilled(true);
				} else if (i <= 9) {
					Brick.setFillColor(Color.RED);
					Brick.setFilled(true);
				}
			}
		}
	}

	public void createPaddle() {

		startingPaddleY = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		Paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		Paddle.setFillColor(Color.BLACK);
		Paddle.setFilled(true);
		add(Paddle, 0, startingPaddleY);
		addMouseListeners();
	}

	public void createBall() {
		Ball = new GOval(BALL_RADIUS, BALL_RADIUS);
		Ball.setFillColor(Color.BLACK);
		Ball.setFilled(true);
		add(Ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2);
	}

	/*
	 * if a certain button is pressed it will change boolean value This determines
	 * whether it tries again or the computer does it Also it determines whether the
	 * user has clicked to play yet
	 */
	public void mousePressed(MouseEvent e) {
		GObject gobj = getElementAt((double) e.getX(), (double) e.getY());

		if (gobj == tryAgainButton) {
			restarting = true;
			computerTry = false;
		} else if (gobj == computerTryButton) {
			restarting = true;
			computerTry = true;
		} else if (gobj == playButton) {
			playing = true;
		}
	}

	/*
	 * Creates all labels that will be displayed While the user has not clicked to
	 * play the game is paused
	 */
	public void createLabels() {

		livesLeft = NTURNS;
		bricksLeft = NBRICK_ROWS * NBRICKS_PER_ROW;

		displayLivesLeft = new GLabel("Lives Left:" + livesLeft);
		displayBricksLeft = new GLabel("Bricks Left:" + bricksLeft);
		displayLevel = new GLabel("Level:" + level);
		playButton = new GRect(WIDTH, HEIGHT);
		clickToPlay = new GLabel("Click Anywhere to Play!!");
		clickToPlay.setFont("Arial-20");
		GLabel Play = new GLabel("Play!");

		add(clickToPlay, WIDTH / 2 - clickToPlay.getWidth() / 2, HEIGHT / 2);
		add(playButton, 0, 0);

		while (playing == false) {
			pause(10);
		}
		remove(playButton);
		remove(clickToPlay);
		pause(500);

		add(displayLivesLeft, WIDTH / 2 - displayLivesLeft.getWidth() / 2, 20);
		add(displayBricksLeft, WIDTH / 2 - displayBricksLeft.getWidth() / 2, 30);
		add(displayLevel, WIDTH / 2 - displayLevel.getWidth() / 2, 40);

		add(Play, WIDTH / 2 - Play.getWidth() / 2, HEIGHT / 2);
		pause(500);
		remove(Play);
	}

	/*
	 * Sets paddle to the mouse location
	 */
	public void mouseMoved(MouseEvent e) {
		if (!computerTry) {
			if (e.getX() + PADDLE_WIDTH < WIDTH) {
				Paddle.setLocation(e.getX(), startingPaddleY);
			}
		}
	}

	/*
	 * Moves ball while you have lives and bricks left so it will stop when you
	 * don't and go to game over
	 */
	public void moveBall() {
		setInitialDirection();
		pause(1000);

		while (livesLeft > 0 && bricksLeft > 0) {
			computerMovePaddle();
			Ball.move(vx, vy);
			speedUpBall();
			ballBounce();
			loseLife();
		}
	}

	/*
	 * If the computer is doing the game, it will make the paddle aligned with the
	 * ball
	 */
	public void computerMovePaddle() {
		if (computerTry) {
			Paddle.setLocation(Ball.getX() - PADDLE_WIDTH / 2, startingPaddleY);
		}
	}

	public void setInitialDirection() {
		vx = rgen.nextDouble(0.1,0.3);
		if (rgen.nextBoolean(0.05))
			vx = -vx;
		vy = Y_VELOCITY;
	}

	/*
	 * This speeds up the ball depending on how many bricks are left
	 * Gets harder each level
	 */
	public void speedUpBall() {

		if (level == 1) {
			if (bricksLeft >= 60) {
				pause(1.3);
			} else if (bricksLeft >= 20) {
				pause(1.1);
			} else {
				pause(0.9);
			}
		}

		if (level == 2) {
			if (bricksLeft >= 80) {
				pause(1.0);
			} else if (bricksLeft >= 60) {
				pause(0.9);
			} else if (bricksLeft >= 40) {
				pause(0.8);
			} else if (bricksLeft >= 20) {
				pause(0.7);
			} else if (bricksLeft >= 10) {
				pause(0.6);
			} else {
				pause(0.5);
			}
		}

		if (level == 3) {

			if (bricksLeft >= 80) {
				pause(0.8);
			} else if (bricksLeft >= 60) {
				pause(0.7);
			} else if (bricksLeft >= 40) {
				pause(0.6);
			} else if (bricksLeft >= 20) {
				pause(0.5);
			} else if (bricksLeft >= 10) {
				pause(0.4);
			} else {
				pause(0.3);
			}
		}
	}

	/*
	 * If the ball goes past the bottom of the screen a life will be lost This
	 * changes the label and variable of lives left
	 */
	public void loseLife() {

		if (Ball.getY() >= HEIGHT - BALL_RADIUS) {
			livesLeft--;
			displayLivesLeft.setLabel("Lives Left: " + livesLeft);

			remove(Ball);
			changeBallDirectionAfterLoss();
			add(Ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2);
			pause(1000);
		}
	}

	/*
	 * After the user loses a life, the vx will change
	 */

	public void changeBallDirectionAfterLoss() {
		vx = rgen.nextDouble(0.1, 0.30);
		if (rgen.nextBoolean(0.05))
			vx = -vx;
	}

	public void ballBounce() {
		bounceOffWall();
		bounceOffBrick();
		bounceOffPaddle();
	}

	/*
	 * If the ball is about to go past the screen borders, it will bounce back off
	 * For level 3 it will randomly generate a vx and vy
	 */
	public void bounceOffWall() {
		if (Ball.getX() + 2 * BALL_RADIUS > APPLICATION_WIDTH || Ball.getX() < 0) {
			if (level == 1 || level == 2) {
				vx = -vx;
			} else if (level == 3) {
				vx = vx * -rgen.nextDouble(0.075, 0.15);
			}
		}
		if (Ball.getY() + 2 * BALL_RADIUS < 0) {
			if (level == 1 || level == 2) {
				vy = -vy;
			} else if (level == 3) {
				vy = vy * -rgen.nextDouble(0.075, 0.15);
			}
		}
	}

	/*
	 * Gets element at ball, if it is not null, paddle, or any of the GLabels it
	 * will remove the object and change some of the variable values
	 */
	public void bounceOffBrick() {

		AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

		top = getElementAt(Ball.getX() + BALL_RADIUS, Ball.getY());
		bottom = getElementAt(Ball.getX() + BALL_RADIUS, Ball.getY() + 2 * BALL_RADIUS);
		left = getElementAt(Ball.getX(), Ball.getY() + BALL_RADIUS);
		right = getElementAt(Ball.getX() + 2 * BALL_RADIUS, Ball.getY() + BALL_RADIUS);

		if (top != null && top != Paddle && top != displayBricksLeft && top != displayLivesLeft && top != displayLevel
				&& top != Rectangles) {
			if (level == 1 || level == 2) {
				vy = -vy;
			} else if (level == 3) {
				vy = vy * -rgen.nextDouble(0.075, 0.15);
			}
			bounceClip.play();
			remove(top);
			bricksLeft--;
			changeDisplayBricksLeft();
		} else if (bottom != null && bottom != Paddle && bottom != displayBricksLeft && bottom != displayLivesLeft
				&& bottom != displayLevel && bottom != Rectangles) {
			if (level == 1 || level == 2) {
				vy = -vy;
			} else if (level == 3) {
				vy = vy * -rgen.nextDouble(0.075, 0.15);
			}
			bounceClip.play();
			remove(bottom);
			bricksLeft--;
			changeDisplayBricksLeft();

		} else if (left != null && left != Paddle && left != displayBricksLeft && left != displayLivesLeft
				&& left != displayLevel && left != Rectangles) {

			if (level == 1 || level == 2) {
				vx = -vx;
			} else if (level == 3) {
				vx = vx * -rgen.nextDouble(0.075, 0.15);
			}
			bounceClip.play();
			remove(left);
			bricksLeft--;
			changeDisplayBricksLeft();

		} else if (right != null && right != Paddle && right != displayBricksLeft && right != displayLivesLeft
				&& right != displayLevel && right != Rectangles) {
			if (level == 1 || level == 2) {
				vx = -vx;
			} else if (level == 3) {
				vx = vx * -rgen.nextDouble(0.075, 0.15);
			}
			bounceClip.play();
			remove(right);
			bricksLeft--;
			changeDisplayBricksLeft();
		}
	}

	/*
	 * Gets element at ball, and if it is the paddle, it bounces off, based on where
	 * on the paddle it's hit
	 */
	public void bounceOffPaddle() {

		if (bottom != null && bottom == Paddle && bottom.getY() <= startingPaddleY) {
			if (vx > 0) {
				if (Ball.getX() - Paddle.getX() < PADDLE_WIDTH / 2) {
					vx = -vx;
				}
			}
			if (vx < 0) {
				if (Ball.getX() - Paddle.getX() > PADDLE_WIDTH / 2) {
					vx = -vx;
				}
			}
			if (level == 1 || level == 2) {
				vy = -vy;
			} else if (level == 3) {
				vy = vy * -rgen.nextDouble(0.075, 0.15);
			}
		}
	}

	public void changeDisplayBricksLeft() {
		displayBricksLeft.setLabel("Bricks Left: " + bricksLeft);
	}

	/*
	 * Depending on the circumstance of the loss, there are many different routes a
	 * game can take. The first is if you have no lives left, in which case you an
	 * try again or see the computer do it. If there are no bricks left because the
	 * computer did it, you can move to next level or try again If the user beats
	 * the level, they will move to the next level
	 * If you lose a level, you can ask the computer to do it
	 */
	public void gameOver() {

		if (livesLeft == 0) {
			gameOverLose = new GLabel("Level Failed. You had " + bricksLeft + " bricks remaining.");
			add(gameOverLose, WIDTH / 2 - gameOverLose.getWidth() / 2, HEIGHT / 2 - 10);

			tryAgainButton = new GRect(WIDTH, HEIGHT / 2);
			tryAgainButton.setFilled(false);
			tryAgainButton.setColor(Color.WHITE);

			computerTryButton = new GRect(WIDTH, HEIGHT / 2);
			computerTryButton.setFilled(false);
			computerTryButton.setColor(Color.WHITE);

			tryAgain = new GLabel("Click Above to Try Again");
			comTry = new GLabel("Click Below to See the Computer Do It");

			add(tryAgainButton, 0, 0);
			add(computerTryButton, 0, HEIGHT / 2);
			add(tryAgain, WIDTH / 2 - tryAgain.getWidth() / 2, HEIGHT / 2 - 150);
			add(comTry, WIDTH / 2 - comTry.getWidth() / 2, HEIGHT / 2 + 100);

		}

		if (bricksLeft == 0) {
			if (computerTry == true && level <= 3) {
				add(tryAgainButton, 0, 0);
				add(tryAgain, WIDTH / 2 - tryAgain.getWidth() / 2, HEIGHT / 2 - 100);

			} else if (level == 1) {
				gameOverWin = new GLabel("Level " + level + " Completed");
				add(gameOverWin, WIDTH / 2 - gameOverWin.getWidth() / 2, HEIGHT / 2 + 30);
				pause(500);
				Loading = new GLabel("Intiating Level 2...");
				add(Loading, WIDTH / 2 - Loading.getWidth() / 2, HEIGHT / 2);
				pause(500);
				restarting = true;
				level++;

			} else if (level == 2) {
				gameOverWin = new GLabel("Level " + level + " Completed");
				add(gameOverWin, WIDTH / 2 - gameOverWin.getWidth() / 2, HEIGHT / 2 + 30);
				pause(500);
				Loading = new GLabel("Intiating Level 3...");
				add(Loading, WIDTH / 2 - Loading.getWidth() / 2, HEIGHT / 2);
				pause(500);
				restarting = true;
				level++;

			} else if (level == 3) {
				GLabel godMode = new GLabel("YOU ARE A GOD AT BREAKOUT!!!!");
				add(godMode, WIDTH / 2 - godMode.getWidth() / 2, HEIGHT / 2 - 80);
				GImage godly = new GImage("Thanos.png");
				add(godly, WIDTH / 2 - godly.getWidth() / 2, HEIGHT / 2 - 70);
			}
		}
	}

	/*
	 * Prints many rectangles that slowly take up the screen so it looks like
	 * everything is disappearing
	 */
	public void disappearingAct() {
		Rectangles = new GRect(WIDTH, HEIGHT);
		Rectangles.setFillColor(Color.WHITE);
		Rectangles.setFilled(true);
		for (int i = 1; i <= HEIGHT; i++) {
			pause(5);
			add(Rectangles, 0, i - HEIGHT);
		}
	}
	
}
