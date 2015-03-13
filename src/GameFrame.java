import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class GameFrame extends JFrame implements ActionListener{
	
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem newGame;
	private JMenuItem exit;
	private JMenuItem records;
	private JPanel game;
	private JButton start;
	
	public GameFrame(String title){
		super(title);
		game = new GUI();
		
		start = new JButton("Start Game");
		add(start);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(350,350,500,500);
		setVisible(true);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Files");
		newGame = new JMenuItem("New Game!");
		records = new JMenuItem("Records");
		exit = new JMenuItem("Exit");
		
		start.addActionListener(this);
		newGame.addActionListener(this);
		records.addActionListener(this);
		exit.addActionListener(this);
		add(menuBar, BorderLayout.NORTH);
		menuBar.add(menu);
		menu.add(newGame);
		menu.add(records);
		menuBar.add(exit);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newGame)){
			remove(game);
			game = new GUI();
			add(game);
		}
		if (e.getSource().equals(records)){
			JOptionPane frame = new JOptionPane();
			JOptionPane.showMessageDialog(frame, getRecords());
		}
		
		if (e.getSource().equals(exit)){
			this.dispose();
		}
		
		if (e.getSource().equals(start)){
			remove(start);
			game = new GUI();
			add(game);
		}
	}
	private String getRecords(){
		String output = "Records: \n";
		try {
			BufferedReader br = new BufferedReader(new FileReader("records.dat"));
			String line;
			for (int i = 0; i < 3; i++){
				line  = br.readLine();
				output += line + "\n";
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return output;
	}
	
}
