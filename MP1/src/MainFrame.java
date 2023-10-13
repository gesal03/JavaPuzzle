import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class MainFrame extends JFrame{
	private MyPanel mp = new MyPanel();
	public MainFrame() {
		super("Puzzle");
		setContentPane(mp);
		setSize(800,800);
		setVisible(true);
	}
	
	class PuzzleButton extends JButton{
		private BufferedImage backgroundImage;
		private int correctX; // 알맞은 위치
		private int correctY; // 알맞은 위치
		private int currentX;
		private int currentY;
		
		public PuzzleButton(BufferedImage img, int correctX, int correctY) {
			backgroundImage = img;
			this.correctX = correctX;
			this.correctY = correctY;
			
			setSize(200,200);
		}
		public void setCurrentPosition(int currentX, int currentY) {
			this.currentX = currentX;
			this.currentY = currentY;
		}
		public int getCurrentX() {return currentX; }
		public int getCurrentY() {return currentY; }
		public boolean isCorrect() {
			if(correctX == currentX)
				if(correctY == currentY)
					return true;
				
			return false;
		}
	
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
	
	class MyPanel extends JPanel{
		private Vector<Point> posVector = new Vector<Point>(); // 사진 위치 
		private boolean [] isChecked = new boolean[16]; // 초기값 false
		private PuzzleButton [] puzzleBtn = new PuzzleButton[16];
		private BufferedImage img;
		private BufferedImage puzzleImg; // maxX 6000, maxY 4000
		private ArrayList<BufferedImage> bfimgArray = new ArrayList<BufferedImage>(); // 잘린 사진 저장
		
		private CheckComplete cth;
		
		private PuzzleButton firstClickBtn;
		private boolean isSecond = false;
		private boolean isComplete = false;
		
		public MyPanel(){
			try {
				img = ImageIO.read(new File("image4.jpg"));
			}catch(IOException e) {}
			divideImage(); // 사진 자르기
			setPosition(); // 퍼즐 위치
			init();
			
			cth = new CheckComplete();
			cth.start();
			
			setLayout(null);
			for(int i=0; i<puzzleBtn.length; i++) {
				while(true) {
					int index = (int)(Math.random()*puzzleBtn.length);
					if(isChecked[index] ==false) {
						int x = posVector.get(index).x;
						int y = posVector.get(index).y;
						puzzleBtn[i].setCurrentPosition(x,y);
						puzzleBtn[i].setSize(200,200);
						puzzleBtn[i].setLocation(x,y);
						puzzleBtn[i].addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if(isSecond) {
									PuzzleButton secondClickBtn = (PuzzleButton)e.getSource();
									int firstX = firstClickBtn.getCurrentX();
									int firstY = firstClickBtn.getCurrentY();
									int secondX = secondClickBtn.getCurrentX();
									int secondY = secondClickBtn.getCurrentY();
									firstClickBtn.setCurrentPosition(secondX, secondY);
									secondClickBtn.setCurrentPosition(firstX, firstY);
									firstClickBtn.setLocation(secondX, secondY);
									secondClickBtn.setLocation(firstX, firstY);
									isSecond = false;
									repaint();
								}
								else {
									firstClickBtn = (PuzzleButton)e.getSource();
									isSecond = true;
								}
							}
						});
						add(puzzleBtn[i]);
						isChecked[index] = true;
						break;
					}
				}
			}
		}
		
		private void divideImage() {
			int maxX = 6000;
			int maxY = 4000;
			int intervalX = 1500;
			int intervalY = 1000;
			for(int x=0; x<maxX; x+=intervalX) {
				for(int y=0; y<maxY; y+=intervalY) {
					puzzleImg = img.getSubimage(x, y, intervalX, intervalY); // 4등
					bfimgArray.add(puzzleImg);
				}	
			}
		}
		private void setPosition() {
			for(int x=0; x<800; x+=200) {
				for(int y=0; y<800; y+=200)
					posVector.add(new Point(x,y));
			}
		}
		private void init() {
			for(int i=0; i<bfimgArray.size();i++) {
				BufferedImage image = bfimgArray.get(i);
				int x = posVector.get(i).x;
				int y = posVector.get(i).y;
				puzzleBtn[i] = new PuzzleButton(image,x,y);
			}
		}
		private class CheckComplete extends Thread{
			@Override 
			public void run() {
				while(true) {
					try {
						sleep(300);
						for(int i=0; i<puzzleBtn.length; i++) {
							if(puzzleBtn[i].isCorrect() == false)
								break;
						}
						isComplete = true;
						repaint();
					}catch(InterruptedException e) {}
					
				}
			}
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(isComplete) {
				Font f = new Font("Arial", Font.BOLD, 30);
				g.setFont(f);
				g.setColor(Color.BLACK);
				g.drawString("Complete!!", 30, 30);
			}
		}
	}
}
