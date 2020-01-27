import java.applet.AudioClip;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;




public class Main extends JFrame implements MouseListener, ActionListener {

	BufferedImage img;
	CardLayout cardLayout;
	Clip clip;
	JButton bt[] = new JButton[3]; //JButton bt[] = new JButton[4];

	
	public Main(){

		JPanel Main;
		cardLayout = new CardLayout();

		setTitle("Da Vinci Code");
		setSize(900,780);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//메인화면
		Main = new JPanel() {
			public void paintComponent(Graphics g){
				g.drawImage(img,0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}

		}; 
		Main.setBackground(Color.BLACK );
		Main.setLayout(null);


		try{
			img = ImageIO.read(new File("image/main08.jpg"));
		}catch(IOException e){

		}

		//BGM 삽입 
		try{
			AudioInputStream bgm = AudioSystem.getAudioInputStream(new File("image/main_bgm.wav"));
			clip = AudioSystem.getClip();
			clip.open(bgm);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){}

		//메인 버튼
		bt[0] = new JButton("Play");
		bt[1] = new JButton("Tutorial");
		bt[2] = new JButton("Exit");

		for(int i=0; i<3; i++){ //버튼설정
			bt[i].setBackground(null);
			bt[i].setBorderPainted(false);
			bt[i].setForeground(Color.white);
			bt[i].setContentAreaFilled(false);
			bt[i].setFont(new Font("Viner Hand ITC",Font.BOLD,30));
			bt[i].addMouseListener(this);
			bt[i].addActionListener(this);
			Main.add(bt[i]);
		}

		bt[0].setBounds(670, 390, 150, 50); //버튼위치조절
		bt[1].setBounds(675, 460, 200, 50); 
		bt[2].setBounds(670, 530, 150, 50); 
		Main.setOpaque(false);
		add(Main);
		setVisible(true);

	}
	public static void main(String[] args) {
		Main Main = new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == bt[0]){
			this.setVisible(false);
			clip.stop();
			playFrameSetting playFrameSetting = new playFrameSetting();
			//StartingFrameTest.playFrameSetting.setVisible(true);
		}
		if(e.getSource() == bt[1]){
			this.setVisible(false);
			clip.stop();
			Tuto1 Tuto1 = new Tuto1();
			
			//System.exit(0);
		}
		if(e.getSource() == bt[2]){
			System.exit(0); //창 꺼짐
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JButton b=(JButton)e.getSource();
		b.setForeground(Color.gray);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JButton b=(JButton)e.getSource();
		b.setForeground(Color.white);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JButton b=(JButton)e.getSource();
		b.setForeground(Color.gray);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
}