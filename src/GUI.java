import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class GUI extends JPanel	implements MouseListener{ 
	
	private JPanel gamePanel;
	private JButton[][] buttons;
	private JPanel topPanel, empty1, empty2, empty3;
	private JLabel time;
	private JLabel flagLabel;
	private Integer[][] bombs;
	private Integer[][] bombsTemp;
	private Integer[][] flags;
	private Image bombImg;
	private Image flagImg;
	private Image explosionImg;
	private Image noFlagImg;
	private Integer flagNumb;
	private Double timeCount;
	private boolean gameOn;
	private boolean win;
	private boolean lost;
	private Border border;
	
	public GUI(){
		super();

		//peldanyositas
		gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(10,10));
		time = new JLabel("0");
		flagLabel = new JLabel("       10");
		topPanel = new JPanel();
		empty1 = new JPanel();
		empty2 = new JPanel();
		empty3 = new JPanel();		
		border = BorderFactory.createLineBorder(Color.BLACK);
		
	
		gameOn = true;
		win = false;
		lost = false;
		bombs = new Integer[10][10];
		flags = new Integer[10][10];
		buttons = new JButton[10][10];
		timeCount = new Double(0);
		flagNumb = new Integer(10);
		bombsTemp = new Integer[12][12];
		
		for (int i=0; i<10; i++){
			for (int j=0; j<10; j++){
				buttons[i][j] = new JButton();
				gamePanel.add(buttons[i][j]);
				buttons[i][j].addMouseListener(this);
				bombs[i][j] = new Integer(0); 
				flags[i][j] = new Integer(0);
				buttons[i][j].setBackground(Color.YELLOW);
				buttons[i][j].setForeground(Color.YELLOW);
			}
		}	
		
		//kepek
		try {
			bombImg = ImageIO.read(new File("mine.png"));
			flagImg = ImageIO.read(new File("flag.png"));
			explosionImg = ImageIO.read(new File("explosion.png"));
			noFlagImg = ImageIO.read(new File("noflag.png"));
		} catch (IOException e){
			e.printStackTrace();
		}
	
		//GUI felepitese
		
		setLayout(new BorderLayout());
		
		add(gamePanel, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		add(empty1, BorderLayout.EAST);
		add(empty2, BorderLayout.WEST);
		add(empty3, BorderLayout.SOUTH);
		empty1.setBorder(new EmptyBorder(10,10,10,10));
		empty2.setBorder(new EmptyBorder(10,10,10,10));
		empty3.setBorder(new EmptyBorder(10,10,10,10));
		topPanel.setBorder(new EmptyBorder(10,10,10,10));

		topPanel.setLayout(new BorderLayout());
		//topPanel.setBackground(Color.BLACK);
		topPanel.add(time, BorderLayout.EAST);
		topPanel.add(flagLabel, BorderLayout.WEST);
		
		//valtozok kezodertek adasa
		for (int i=0; i<12; i++){
			for (int j=0; j<12; j++){
				bombsTemp[i][j] = new Integer(0);
			}
		}
		
		//feltoltom a mezot random 10 bombaval
		int bombNumb = 10;
		Random rand = new Random();
		int x, y;
		while (bombNumb != 0){
			x = rand.nextInt(9)+1;
			y = rand.nextInt(9)+1;
			if (bombsTemp[x][y] == 0){
				bombsTemp[x][y] = 9;
				bombNumb--;
			}
		}		
		//feltoltom a mezoket a megfelelo erteku szamokkal, azaz hogy hany bomba van korulottuk
		for (int i=1; i<11; i++){
			for (int j=1; j<11; j++){
				if (bombsTemp[i][j] != 9){
					bombsTemp[i][j] = countNeighbourBombs(bombsTemp, i, j);
				}
				bombs[i-1][j-1] = bombsTemp[i][j];
			}
		}	
		//keretnek feltoltom -1el a szelso sort
		for (int i=0; i<12; i++){
			bombsTemp[i][0]  = -1;
			bombsTemp[i][11] = -1;
			bombsTemp[0][i]  = -1;
			bombsTemp[11][i]  = -1;
		}
		
		//Idoszamitas
		new Thread(){
			public void run(){
				try {
					while(gameOn){
						Thread.sleep(10);
						timeCount +=0.01;
						String out = String.format("%.2f", timeCount);
						time.setText(out + "       ");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		/*for (int i= 0; i<12; i++){
			for (int j= 0; j<12; j++){
				System.out.print(bombsTemp[i][j]);
			}
			System.out.println();
		}*/
	}
	
	private void revealButtons(int i, int j){		
		if (bombs[i][j] == 0 && flags[i][j] == 0){
			setVisible(i, j);
			bombs[i][j] = 10;
			if (i-1 >= 0 && i < 10 && j >= 0 && j < 10){
				revealButtons(i-1, j);
			}
			if (i+1 >= 0 && i+1 < 10 && j >= 0 && j < 10){
				revealButtons(i+1, j);
			}
			if (i >= 0 && i < 10 && j-1 >= 0 && j-1 < 10){
				revealButtons(i, j-1);
			}
			if (i >= 0 && i-1 < 10 && j+1 >= 0 && j+1 < 10){
				revealButtons(i, j+1);
			}		
		}
	}
	
	private void setVisible(int i, int j){
		setNumb(i, j);
		if (i-1 >= 0 && i-1 < 10 && j >= 0 && j < 10){
			setNumb(i-1, j);	//fel
		}
		if (i+1 >= 0 && i+1 < 10 && j >= 0 && j < 10){
			setNumb(i+1, j);	//le
		}
		if (i >= 0 && i < 10 && j-1 >= 0 && j-1 < 10){
			setNumb(i, j-1);	//balra
		}
		if (i >= 0 && i < 10 && j+1 >= 0 && j+1 < 10){
			setNumb(i, j+1);	//jobbra
		}
		
		if (i-1 >= 0 && i-1 < 10 && j-1 >= 0 && j-1 < 10){
			setNumb(i-1, j-1);	//fel balra
		}
		if (i-1 >= 0 && i-1 < 10 && j+1 >= 0 && j+1 < 10){
			setNumb(i-1, j+1);	//fel jobbra
		}
		if (i+1 >= 0 && i+1 < 10 && j-1 >= 0 && j-1 < 10){
			setNumb(i+1, j-1);	//le balra
		}
		if (i+1 >= 0 && i+1 < 10 && j+1 >= 0 && j+1 < 10){
			setNumb(i+1, j+1);	//le jobbra
		}
	}
	
	private void setNumb(int i, int j){
		if (flags[i][j] == 0){
			buttons[i][j].setEnabled(false);
			if (bombs[i][j] != 0 && bombs[i][j] != 10){
				buttons[i][j].setText(bombs[i][j].toString());
			}
			else{
				buttons[i][j].setText(" ");
			}
		}
	}
	
	private void showExplosions(){
		for (int i=0; i<10; i++){
			for (int j=0; j<10; j++){
				if (bombs[i][j] == 9 && buttons[i][j].getIcon() == null){
					buttons[i][j].setIcon(new ImageIcon(explosionImg));
				}
				if (flags[i][j] == 1 && bombs[i][j] != 9){
					buttons[i][j].setIcon(new ImageIcon(noFlagImg));
				}
			}
		}
	}
	
	private void showBombs(){
		for (int i=0; i<10; i++){
			for (int j=0; j<10; j++){
				if (bombs[i][j] == 9){
					buttons[i][j].setIcon(new ImageIcon(bombImg));
				}
			}
		}
	}
	
	private void gameEnd(){
		gameOn = false;
		Object[] opt = {"OK"};
		JOptionPane frame = new JOptionPane();
		if (win){
			String out = String.format("%.2f", timeCount);
			JOptionPane.showOptionDialog(frame, "You solved the field! Your time is: "+ out, "Game Over", 
														JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, opt, opt[0]);
			setRecord();
		}
		else{
			JOptionPane.showOptionDialog(frame, "You failed!", "Game Over", 
					JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, opt, opt[0]);
		}
	}
	
	private void setRecord(){
		Double rec[] = new Double[3];
		String name[] = new String[3];
		String split[] = new String[2];
		try {
			BufferedReader br = new BufferedReader(new FileReader("records.dat"));
			String line;
			for (int i = 0; i < 3; i++){
				line  = br.readLine();
				split = line.split(" ");
				name[i] = split[0];
				rec[i] = Double.valueOf(split[1]);
			}
			br.close();
			String newName = new String("unknown");
			if (timeCount < rec[2]){
				JOptionPane frame = new JOptionPane();
				String n = JOptionPane.showInputDialog(frame, "You set a new record. Enter Your Name!");
				if (!n.matches("\\S")){
					newName = n;
				}
				if (timeCount < rec[0]){
					rec[2] = rec[1];
					rec[1] = rec[0];
					rec[0] = Double.valueOf(timeCount);
					name[2] = name[1];
					name[1] = name[0];
					name[0] = newName;
				}else if (timeCount < rec[1]){
					rec[2] = rec[1];
					rec[1] = Double.valueOf(timeCount);
					name[2] = name[1];
					name[1] = newName;
				}else if (timeCount < rec[2]){
					rec[2] = Double.valueOf(timeCount);
					name[2] = newName;
				}
			}		
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("records.dat"));
			String out = new String();
			for (int i = 0; i < 3; i++){
				String d = String.format("%.2f", rec[i].floatValue());
				out = name[i] + " " + d + "\n";
				bw.write(out);
			}
			bw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}	 
	}
	
	private boolean checkWin(){
		int count = 0;
		for (int i=0; i<10; i++){
			for (int j=0; j<10; j++){
				if (buttons[i][j].isEnabled()){
					count++;
					if (count > 10){
						return false;
					}
				}
			}
		}
		win = true;
		return true;
	}
	
	private void putFlag(int i, int j){
		if (flags[i][j] == 0){
			if (flagNumb > 0){
				buttons[i][j].setIcon(new ImageIcon(flagImg));
				flags[i][j] = 1;
				flagNumb--;
				flagLabel.setText("       " + flagNumb.toString());
			}
		}
		else{
			buttons[i][j].setIcon(null);
			flags[i][j] = 0;
			flagNumb++;
			flagLabel.setText("       " + flagNumb.toString());
		}
	}
	
	public boolean getGameOn(){
		return gameOn;
	}
	
	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		if (!lost){
			for (int i=0; i<10; i++){
				for (int j=0; j<10; j++){
					if (e.getSource().equals(buttons[i][j]) && buttons[i][j].isEnabled()){
						if (e.getButton() == 1){			//ha bal klikk
							if (flags[i][j] == 0)			//ha nem flag
							switch (bombs[i][j]){
								case 0:						//0 mezok felfedese
									revealButtons(i, j);
									if (checkWin()){
										showBombs();
										gameEnd();
									}
									break;
								case 9:						//bomba
									showExplosions();
									lost = true;
									gameEnd();
									break;
								default:					//mas
									buttons[i][j].setEnabled(false);
									setNumb(i, j);
									if (checkWin()){
										showBombs();
										gameEnd();
									}
									break;
							}
						}
						else{								//ha jobb klikk
							putFlag(i, j);
						}
					}
				}
			}
		}
	}
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		
	}
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	private Integer countNeighbourBombs(Integer[][] bombs, int i, int j){
		Integer count = 0;
		
		if (bombs[i-1][j-1] == 9){
			count++;
		}
		if (bombs[i-1][j] == 9){
			count++;
		}
		if (bombs[i-1][j+1] == 9){
			count++;
		}
		if (bombs[i][j-1] == 9){
			count++;
		}
		if (bombs[i][j+1] == 9){
			count++;
		}
		if (bombs[i+1][j-1] == 9){
			count++;
		}
		if (bombs[i+1][j] == 9){
			count++;
		}
		if (bombs[i+1][j+1] == 9){
			count++;
		}
		return count;
	}
}