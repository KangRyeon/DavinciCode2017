import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Tuto1 extends JFrame implements ActionListener {
	private JButton bt[] = new JButton[2]; //버튼설정
	private JPanel  tutorial;
	private int count =0; //패널이미지변환
	BufferedImage img;
	CardLayout cardLayout; //메인패널로 전환
	Clip clip;

	public Tuto1(){
		setTitle("Da Vinci Code");
		setSize(900,780);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel tutorial;
		cardLayout = new CardLayout(); //마지막페이지에서 next누르면 다시 메인으로

		tutorial = new JPanel(){
			public void paintComponent(Graphics g){
				g.drawImage(img,0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		tutorial.setLayout(null);

		//BGM삽입
		try{
			AudioInputStream bgm = AudioSystem.getAudioInputStream(new File("image/tuto_bgm.wav"));
			clip = AudioSystem.getClip();
			clip.stop();
			clip.open(bgm);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		}catch(Exception e){}


		bt[0] = new JButton("Back");
		bt[1] = new JButton("Next");


		for(int i=0; i<2; i++){     //버튼설정
			tutorial.add(bt[i]);
			bt[i].addActionListener(this);
			bt[i].setBorderPainted(false);
			bt[i].setBackground(null);
			bt[i].setContentAreaFilled(false);
			bt[i].setBorderPainted(false);
		}

		bt[0].setBounds(67, 679, 100, 40);
		bt[1].setBounds(735, 679, 100, 40);


		try{
			img = ImageIO.read(new File("image/tuto0.jpg"));
		}catch(IOException e){
		}


		add(tutorial);

		setVisible(true);
	}




	@Override
	public void actionPerformed(ActionEvent event) {

		//if(count > 13)
		//	count = 13;//마지막 페이지고정
		if(count <0)
			count =0; //첫페이지고정
		if(count < 14){
			
			if(event.getSource() == bt[1]){
				count ++;
				try{
					if(count != 14)
						img = ImageIO.read(new File("image/tuto"+count+".jpg"));

					repaint();
				}catch(IOException e){
					e.printStackTrace();}	
			}
			else if(event.getSource() == bt[0]){
				count--;
				try{
					img = ImageIO.read(new File("image/tuto"+count+".jpg"));
					repaint();
				}catch(IOException e){
					e.printStackTrace();}


			}
			System.out.println("count="+count);
		}
		if(count == 14)
		{
			if(event.getSource()== bt[1]) //마지막페이지에서next누르면메인으로
			{
				System.out.println("asdfcount="+count);
				//cardLayout.show(getContentPane(),"panel");
				this.setVisible(false);
				clip.stop();
				new Main();
			}

		}
	}/*
	public static void main(String[] args) {

		new Tuto1();
	}
	 */
}