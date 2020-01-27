//StartingFrameTest == ���� - ������
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

//�÷��̾� Ŭ����
class player {							
	int turn;	     							// turn = 1 �� �� �ڱ�����
	ArrayList<tile> myTile; 					// Ÿ�� �������� Ÿ�� �߰�(�ִ� 13)
	int myTileNumber;							// �� Ÿ�� ����(�ʱ� 0, �� �ڸ��� ��.)
	public player() {
		turn = 0;
		myTile = new ArrayList<tile>();			// player�Ҹ��� Ÿ�ϰ��� ����.
		myTileNumber = 0;	
	}
}

//�� Ÿ�� �ϳ��� Ŭ����
class tile extends JButton {			
	int color;									// black = 0, white = 1
	int show;									// 0 = �ȵ�������, 1 = ��������
	int centerShow;								// ���Ϳ��� �Ȱ����� = 0, ������ = 1
	int number;									// 0~12 : ����
	public tile(int color, int number) {
		//		super(""+number);

		this.color = color;
		this.show = 0; 							// ó������ �� �ȵ������� Ÿ����.
		this.centerShow = 0;					// ó������ �� �Ȱ����� ����
		this.number = number;
		if(color == 0){							// color == 0 (black)�϶�
			this.setBackground(Color.black);
			this.setForeground(Color.white);
		}
		else if(color == 1){					// color == 1 (white)�϶�
			this.setBackground(Color.white);
			this.setForeground(Color.black);
		}
	}

}

class playFrameSetting extends JFrame implements ActionListener {
	//BGM ����
	Clip clip;

	//����â
	player computer, player, nowPlayer, winner=null; 							// player 2��, nowPlayer = ���� ���� �÷��̾�, winner = �̱��� ǥ��
	JPanel computerPanel, computerOutterPanel, playerPanel, playerOutterPanel; 	// player 2�� �г�, �� �г��� �� �г� 2����
	JPanel centerPanel, centerOutterPanel; 										// �߰� �г�, �� �г��� �� �г� 2����
	JPanel wholePanel; 															// ��ü �г�

	//Ȯ��â
	JFrame confirm;					// Ȯ��â�� ������
	JPanel confirmWholePanel; 		// Ȯ��â�� ��ü�г�
	JPanel confirmLabelWholePanel; 	// ���̺� 1,2 �� �г�
	JPanel confirmLabel1Panel;		// ���̺�1 �� �г�
	JPanel confirmLabel2Panel;		// ���̺�2 �� �г�
	JPanel confirmButtonPanel; 		// ��ư �� �г�
	JButton confirmButton;			// Ȯ��â�� ��ư
	JLabel confirmLabel1;			// ���̺�1
	JLabel confirmLabel2;			// ���̺�2
	JButton cancleButton;			// ��ҹ�ư

	//��� Ÿ�� ���� ���� �г�
	JPanel TextPanel; 				// ���Ÿ�� ���� ���� �г�
	JTextField selectTileNumber; 	// ���Ÿ�� ���� ���� �ʵ�
	JButton selectTileNumberButton;	// ���Ÿ�� ���� ������ ��ư

	//�÷��� ���� ǥ��
	JPanel playStatePanel;			// ���� ���� ���� �����ִ� �г�
	JLabel playState;				// ���� ���� ���� �����ִ� �ʵ�

	//scrollPaneâ
	JFrame scrollFrame;				// ��ũ�� ������
	JScrollPane scroll;				// ��ũ�� ����
	JTextArea textArea;				// ��ũ�� ���ο� �� textArea

	//Layout����
	CardLayout cardLayout;			// ��ü â�� �Ѿ�� ���� layout
	CardLayout playerCardLayout;	// �� �÷��̾� �гΰ� �����гο� �� layout(��ü ũ�⿡ �������µ�?)

	//��ü ���α׷����� ���Ե� ������
	tile tile[];					// ��� �гο� �� Ÿ�� �迭
	int stage; 						// 0 = �غ�(����)�ܰ�, 1 = �÷��̴ܰ�
	tile recentTile;				// �ֱ� center���� �޾ƿ� Ÿ��(���� Ÿ��)
	tile selectTile;				// ����� ��Ÿ�� ���ϴ� ����(��� Ÿ��)
	int result;						// ��ǻ�Ͱ� ���� �� �ִ� ����
	BufferedImage BackgroundImg;
	BufferedImage PanelImg;

	public playFrameSetting() {

		try{
			AudioInputStream bgm = AudioSystem.getAudioInputStream(new File("image/game_bgm.wav"));
			clip = AudioSystem.getClip();
			clip.stop();
			clip.open(bgm);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}catch(Exception e){ }

		//�ʱ�ȭ
		computer = new player();								// �÷��̾� �ϳ��� ����.
		player = new player();
		nowPlayer = new player();
		stage = 0;												// 0 = �غ�(����), �ܰ� 1 = �÷��̴ܰ�
		recentTile = null;										// �ֱ� ���β� �� Ÿ�� = null��.
		selectTile = null;										// �ֱ� ��벨 �� Ÿ�� = null��.
		try {
			BackgroundImg = ImageIO.read(new File("image/BackgroundImage.jpg"));
			PanelImg = ImageIO.read(new File("image/PanelImage.jpg"));
		} catch (IOException e) {
		}

		//��ũ��â(������� ǥ��â)
		scrollFrame = new JFrame();
		textArea = new JTextArea(10,30);
		textArea.append("�÷��̾�� ��� ���̴� Ÿ���� 4���� �������ּ���");
		textArea.setCaretPosition(textArea.getDocument().getLength());			// ��ũ�� ������ ����.
		scroll = new JScrollPane(textArea);
		scrollFrame.add(scroll, BorderLayout.CENTER);
		scrollFrame.setVisible(true);											// ��ũ��â = ó������ ����
		scrollFrame.setBounds(950,0,380,250);									// ��ũ��â ũ��, ���ð� (350*250)
		scrollFrame.setTitle("�������");

		//Ȯ��â
		confirm = new JFrame();
		confirmWholePanel = new JPanel();						// Ȯ��â ��ü�г�
		confirmWholePanel.setLayout(new GridLayout(0,1));
		confirmLabelWholePanel = new JPanel();					// Ȯ��â�� �� ���̺� ��ü�г�
		confirmLabelWholePanel.setSize(350,100);		
		confirmLabelWholePanel.setLayout(new GridLayout(0,1));
		confirmLabel1Panel = new JPanel();						// ���̺� 1�г�
		confirmLabel2Panel = new JPanel();						// ���̺� 2�г�
		confirmButtonPanel = new JPanel();						// Ȯ��â ��ư�г�
		confirm.setBounds(700,500,350,200);						// Ȯ��â ũ��, ���ð� (350*200)
		confirm.setVisible(false);								// ó���� �Ⱥ���
		confirm.setTitle("Ȯ��â");	

		//��ü �������� ���̾ƿ� ����
		cardLayout = new CardLayout();
		playerCardLayout = new CardLayout();
		this.setLayout(cardLayout);

		//computer �г�
		computerOutterPanel = new JPanel();
		computerOutterPanel.setLayout(playerCardLayout);
		computerOutterPanel.setBackground(new Color(111,13,39));		
		computerPanel = new JPanel();
		computerPanel.setOpaque(false);
		computerPanel.setBackground(Color.red);
		computerOutterPanel.add(computerPanel);					// computerOutterPanel�� computerPanel �־���.

		//center �г�
		centerOutterPanel = new JPanel();
		centerOutterPanel.setLayout(playerCardLayout);
		centerOutterPanel.setBackground(new Color(111,13,39));
		centerOutterPanel.setOpaque(false);
		centerPanel = new JPanel();
		centerPanel.setLayout(null);							// Ÿ�� �������� ��ġ�� ��.
		centerPanel.setBackground(new Color(111,13,39));
		centerPanel.setOpaque(false);
		tile = new tile[26];									// center�гο� �� Ÿ�ϵ�
		setCenterTile();										// Ÿ�� ����, Ÿ�� ����(�Լ��� �θ�.)
		recentTile = tile[0];									// �ϴ� �ʱ�ȭ��Ŵ.
		centerOutterPanel.add(centerPanel);						// centerOutterPanel�� centerPanel �־���.

		//player �г�
		playerOutterPanel = new JPanel();
		playerOutterPanel.setLayout(playerCardLayout);
		playerOutterPanel.setBackground(new Color(111,13,39));
		playerPanel = new JPanel();
		playerPanel.setBackground(Color.blue);
		playerPanel.setOpaque(false);
		playerOutterPanel.add(playerPanel);						// playerOutterPanel�� playerPanel �־���.

		//playState �г�
		playStatePanel = new JPanel();
		playStatePanel.setBackground(new Color(0,0,128));
		playStatePanel.setBorder(new MatteBorder(5,5,5,5,new Color(111,13,39)));	//�׵θ� �������.
		playStatePanel.setOpaque(false);
		playStatePanel.setLayout(new BorderLayout());
		playState = new JLabel();
		playState.setHorizontalAlignment(JLabel.CENTER);		// ���̺� �����������.
		playState.setText("���� player = ��");					// ó�� �÷��̾�� �÷��̾���.
		playState.setForeground(Color.black);
		playStatePanel.add(playState,BorderLayout.CENTER);							// playStatePanel�� playState(���̺�) �־���.

		//Text �г�
		TextPanel = new JPanel();
		TextPanel.setBackground(new Color(111,13,39));
		TextPanel.setLayout(new FlowLayout());
		selectTileNumber = new JTextField(15);
		selectTileNumberButton = new JButton("�Է�");					// '�Է�' ��ư ����
		selectTileNumberButton.addActionListener(this);
		selectTileNumberButton.setPreferredSize(new Dimension(60,20));  // ��ư ������ ������.
		TextPanel.add(selectTileNumber);
		TextPanel.add(selectTileNumberButton);							// TextPanel�� �ؽ�Ʈ�ʵ�� ��ư �־���.

		//whole �г� (Outter�г� : ��ġ �����ϰ� �� ���� �гξ� ���� �����Ϸ���(��ġ �״�� ���뺯��))
		wholePanel = new JPanel() {
			public void paintComponent(Graphics g){
				//super.paintComponent(g);
				g.drawImage(BackgroundImg,0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		wholePanel.setBackground(Color.black);								// ��ü �г� ��
		wholePanel.setOpaque(false);
		wholePanel.setLayout(null); 										// JPanel�� ����Ʈ���� FlowLayout�̹Ƿ� null�������.
		wholePanel.add(computerOutterPanel);								// computerOutterPanel ����
		computerOutterPanel.setBounds(50,50,800,110); 					
		wholePanel.add(centerOutterPanel);									// centerOutterPanel ����
		centerOutterPanel.setBounds(80,220,720,220);
		wholePanel.add(playStatePanel);										// playStatePanel ����
		playStatePanel.setBounds(100,510,250,30);
		wholePanel.add(TextPanel);											// TextPanel ����
		TextPanel.setBounds(600,510,250,33);
		wholePanel.add(playerOutterPanel);									// playerOutterPanel ����
		playerOutterPanel.setBounds(50, 550, 800, 110);

		//��ü �����ӿ� whole �г� �߰�(layout = cardLayout)
		add("wholePanel", wholePanel);

		//���ӽ���
		player.turn = 1; 													// ó������ player�� �ڱ�����
		computer.turn = 0;
		nowPlayer = player;

		//��ü ������ ����
		setTitle("Da Vinci Code");
		setSize(900, 750);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);												// ������ ũ�⺯�� ���ϰ�
		setVisible(true);
	} 


	@Override 
	public void actionPerformed(ActionEvent e) { // �ܰ����ؼ� �ܰ躰�� �ؾ��� �� ������.
		System.out.println("======================================================");
		textArea.append("\n===============================================");

		// �ƹ� ��ư�̵� �������� winner�� ���� ���ٸ� -> stage = 100���� �ٲ���.
		if(winner != null){
			System.out.println("���ڰ���!");
			confirmLabel1.setText("���ڰ� �����Ǿ����ϴ�.");
			if(winner == computer)
				confirmLabel2.setText("���� : ��ǻ��");
			else
				confirmLabel2.setText("���� : �÷��̾�");
			confirmButtonPanel.remove(cancleButton);
			//confirmWholePanel.remove(confirmButtonPanel);
			JPanel p = new JPanel();
			p.add(new JLabel("�����Ͻðڽ��ϱ�?"));
			confirmWholePanel.add(p);
			//confirmWholePanel.add(confirmButtonPanel);
			stage = 100;							
			confirm.setVisible(true);
		}

		
		
		//�ʱ� ����
		if(player.turn == 1 && nowPlayer.myTileNumber < 5 && stage == 0){ 
			if(nowPlayer.myTileNumber != 4){
				System.out.println("now = player");
				nowPlayer = player;
			}
		}

		//tile[i](center Ÿ���� ��������)
		for(int i=0;i<26;i++){
			if(e.getSource() == tile[i] && tile[i].centerShow == 0){			// �������� tile[i]�̰�, center���� �Ȱ������ٸ�
				tile[i].centerShow = 1;
				nowPlayer.myTile.add(new tile(tile[i].color, tile[i].number)); 	// �÷��̾� Ÿ�� ���� �ø��� ���ڸ��� �־���.
				nowPlayer.myTileNumber++;
				recentTile = tile[i];											// �ֱ� Ÿ�Ͽ� �߰����ش�.

				//stage = 0,1�϶�
				if(stage==0 || stage==1){
					System.out.println("Ÿ�� ����: "+nowPlayer.myTileNumber+", Ÿ��Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

					//�ʱ⿡ 4�� �� ���� �� �ٲ���.
					if(nowPlayer.myTileNumber == 4 && stage == 0){ 				// stage==0�̰�, ���� �÷��̾��� Ÿ�ϰ����� 4����
						System.out.println("now = computer");
						nowPlayer = computer;									// ��ǻ�� ������ �Ѱ���.
						player.turn = 0;
						computer.turn = 1;	

						//�÷��̾� �Ͽ��� ��ǻ�������� �Ѿ�� Ȯ��â �������ϱ�
						confirmLabel1 = new JLabel("�÷��̾��� Ÿ���� �� ������ϴ�.");
						confirmLabel1Panel.add(confirmLabel1);
						confirmLabel2 = new JLabel("Ȯ���� �����ø� ��ǻ���� Ÿ���� ���ϴ�.");
						confirmLabel2Panel.add(confirmLabel2);
						textArea.append("\n�÷��̾ 4���� Ÿ���� ������ϴ�.");
						textArea.append("\n��ǻ�Ͱ� 4���� Ÿ���� ���ϴ�. ��ø� ��ٷ��ּ���.");

						confirmLabelWholePanel.add(confirmLabel1Panel);
						confirmLabelWholePanel.add(confirmLabel2Panel);

						confirmButton = new JButton("Ȯ��");
						confirmButton.addActionListener(this);
						confirmButtonPanel.add(confirmButton);
						confirmWholePanel.add(confirmLabelWholePanel);			// confirmWholePanel�� confirmLabelWholePanel �־���
						confirmWholePanel.add(confirmButtonPanel);				// confirmWholePanel�� confirmButtonPanel �־���
						confirm.add(confirmWholePanel);							// confirm ������â�� confirmWholePanel �־���.
						confirm.setVisible(true);
					}
					rePaint(tile[i]);
				}

				//stage = 2�϶� : stage = 3 ���� �Ѿ.
				if(stage==2){													
					System.out.println("\t Ÿ�� �ϳ� �̾Ҿ��");
					recentTile = tile[i];										// ���� Ÿ���� recentTile�� �߰����ش�.
					System.out.println("\t recentŸ�� = "+recentTile.number);
					stage = 3; //���� �ٲ��ְ�
					confirm.setVisible(true);
				}

				rePaint(tile[i]);
			}

		}

		//computerŸ���϶�
		for(int i=0;i<computer.myTileNumber;i++){
			if(stage == 4 && e.getSource() == computer.myTile.get(i)){ 			// �������� computer�� Ÿ���̶��
				computer.myTile.get(i).setPreferredSize(new Dimension(60,100));
				computer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
				selectTile = computer.myTile.get(i);							// ������ Ÿ���� selectTile�� �߰����ش�.
				System.out.println(" computer Ÿ�ϰ��� : "+ computer.myTileNumber+", Ÿ�Ϲ�ȣ :"+computer.myTile.get(i).number);	
				textArea.append("\n��ǻ���� Ÿ���� ���� Ÿ���� ��ġ�� �����߽��ϴ�.");
				textArea.append("\n���ڸ� �Է����� �� �Է� ��ư�� �����ּ���");
				confirm.setVisible(true);
				break;
			}
		}


		//Ȯ��â��ư �������� : �ܰ躰�� �ۿ���.
		if(e.getSource() == confirmButton){
			//�� �÷��̾��� �гο� Ÿ���� 13�� ������ �¸��� ã��(������ Ÿ���� ��������� �й�.)
			if(computer.myTileNumber == 13 || player.myTileNumber == 13){	
				int cN=0;
				int pN=0;
				confirm.setVisible(false);
				confirmLabel1.setText("���� Ÿ���� ���ų� �г��� ���� á���ϴ�.");
				for(int i=0;i<computer.myTileNumber;i++){									// cN = ��ǻ���� Ÿ���� ������ Ÿ�ϰ��� ��.
					if(computer.myTile.get(i).show == 1)
						cN++;
				}
				for(int i=0;i<player.myTileNumber;i++){										// pN = �÷��̾��� Ÿ���� ������ Ÿ�ϰ��� ��.
					if(player.myTile.get(i).show == 1)
						pN++;
				}
				if(cN < pN){																// cN < pN : ��ǻ�� �¸�
					confirmLabel2.setText("�÷��̾ ������ Ÿ���� �����Ƿ� ��ǻ���� �¸��Դϴ�.");
					textArea.append("��ǻ�� �¸�!!");
				}
				else{																		// cN > pN : �÷��̾� �¸�
					confirmLabel2.setText("��ǻ�Ͱ� ������ Ÿ���� �����Ƿ� �÷��̾��� �¸��Դϴ�.");
					textArea.append("�÷��̾� �¸�!!");
				}
			}

			if(stage == 0){																	// (stage == 0) : �������� ��ǻ�� Ÿ�� 4�� �����.
				confirm.setVisible(false);
				try {
					setComputerTile();														// setComputerTile() : ��ǻ�� Ÿ�� 4�� ���� �Լ�
					stage = 1;																// ��ǻ���� Ÿ�ϱ��� �� ���� ���� ������ (stage = 1)
					confirmLabel1.setText("��� �÷��̾ Ÿ���� ���������ϴ�.");
					textArea.append("\n��� �÷��̾ Ÿ���� ���������ϴ�.");
					confirmLabel2.setText("Ȯ���� �����ø� �� �÷��̾��� Ÿ���� �����մϴ�.");				
					textArea.append("\n�� �÷��̾��� Ÿ���� �����մϴ�.");
					confirm.setVisible(true);

				} catch (InterruptedException e1) {
				}
			}

			else if(stage == 1){ 															// (stage == 1) : �������� �� �÷��̾� Ÿ�� �����ϱ�
				confirm.setVisible(false);
				sort();																		// sort() : ��ǻ��, �÷��̾� Ÿ�� �����ϴ� �Լ�
				confirm.setVisible(true);

				confirmLabel1.setText("��� �÷��̾��� Ÿ���� ���ĵǾ����ϴ�.");
				textArea.append("\n��� �÷��̾��� Ÿ���� ���ĵǾ����ϴ�.");
				confirmLabel2.setText("Ȯ���� ������ ��� ���̴� Ÿ���� �ϳ��� ������.");
				textArea.append("\n�÷��̾�� ��� ���̴� Ÿ���� �ϳ��� ������.");
				confirm.setVisible(true);
				stage = 2; 																	// �� �÷��̾� Ÿ�� �����ϸ� ���� ������ (stage = 2)
			}

			else if(stage == 2) { 															// (stage == 2) : �������� �÷��̾ ���� �����ϰ�����.
				confirm.setVisible(false);	
				//���� �����ϴ°� ����.
				if(nowPlayer == computer){													
					nowPlayer = player;														// nowPlayer = player : �÷��̾ ���� �÷��̾�ǰ�.
					player.turn = 1;
					computer.turn = 0;
				}

				rePaint(null);																// rePaint() : ȭ�� �ٽ� �׷���										

				confirmLabel1.setText("�÷��̾ Ÿ���� �ϳ� �̾ҽ��ϴ�.");
				confirmLabel2.setText("���� Ÿ���� ��ġ�� ������ �ּ���");
			}

			else if(stage == 3){ 															// (stage == 3) : �������� �÷��̾ �ֱٿ� center���� ������ Ÿ��
				confirm.setVisible(false);													// 					�����ϰ�, ũ�� ��������.(������ Ÿ�� ǥ������.)
				textArea.append("\n�÷��̾ Ÿ���� �ϳ� �̾ҽ��ϴ�.");
				textArea.append("\nrecent = " + recentTile.number);
				textArea.append("\n��ǻ���� Ÿ���� ���� Ÿ���� ��ġ�� �������ּ���.");
				sort();																		// sort() : ��������
				for(int i=0;i<nowPlayer.myTileNumber;i++){									// ��ǻ�� Ÿ���� recentTile�� ��, ���ڰ� ���� Ÿ��  ã�Ƽ�
					if((nowPlayer.myTile.get(i).number == recentTile.number) && (nowPlayer.myTile.get(i).color == recentTile.color)){	
						System.out.print("\n\trecentTile = " + recentTile.number + ", nowTile = "+nowPlayer.myTile.get(i).number);																
						nowPlayer.myTile.get(i).setPreferredSize(new Dimension(60,100));	// ũ�⺯��
						//nowPlayer.myTile.get(i).setBackground(Color.yellow);
						nowPlayer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
						break;
					}
				}

				System.out.println("�÷��̾ Ÿ�� ����");
				confirmLabel1.setText("��ǻ�� Ÿ���� ��ġ�� �����߽��ϴ�.");
				confirmLabel2.setText("���ڸ� �Է����� �� �Է� ��ư�� �����ּ���.");		

				stage = 4; 																	// ũ�� ����Ǹ� �����ܰ�� (stage = 4)
			}
			// ũ�� ����� �� ��ǻ�� Ÿ�� ����(computer��ư�� ����������)
			// ���� �Է� �� �Է¹�ư ��������(�Է¹�ư�� ����������)
			//�� �ܰ� ���� �� ���ƿ�.

			else if(stage == 4) {															// (stage == 4) : �÷��̾ ���� ���� ������ ���� ����
				confirm.setVisible(false);

				confirmLabel1.setText("�÷��̾ �¾ҽ��ϴ�.");
				confirmLabel2.setText("�����ðڽ��ϱ�?");
				confirmButton.setText("��");												// '��' ��ư ������ (stage = 2)�� �ٽ� ��.

				cancleButton = new JButton("�ƴϿ�");										// '�ƴϿ�' ��ư ������ (stage = 5)�� ��.
				cancleButton.addActionListener(this);
				confirmButtonPanel.add(cancleButton);
				stage = 5; 																	// ���º��� �� �����ܰ�� (stage == 5)
			}

			else if(stage == 5) {															// (stage == 5) : ��ǻ�� ������ �ٲ��, ��ǻ�Ͱ� ������ Ÿ�� ǥ��
				confirm.setVisible(false);
				confirmButtonPanel.remove(cancleButton);
				nowPlayer = computer;
				computer.turn = 0;
				player.turn = 1;
				System.out.println("��ǻ���� ������ �ٲ�ϴ�.");
				try {
					setComputerTile();														// setComputerTile() : ��ǻ�Ͱ� ��� Ÿ���� �ϳ� �������� ��.
				} catch (InterruptedException e1) {
				}

				sort();																		// sort() : ���Ľ�Ű��

				for(int i=0;i<nowPlayer.myTileNumber;i++){
					System.out.print("\n\trecentTile = " + recentTile.number + ", nowTile = "+nowPlayer.myTile.get(i).number);
					if(nowPlayer.myTile.get(i).number == recentTile.number && nowPlayer.myTile.get(i).color == recentTile.color){	//�ֱ��� Ÿ���̸�
						//�÷��̾ Ÿ���ϳ� �����ϰ� ���ĵż� ũ�⺯���.(������ Ÿ�� ǥ���Ϸ�) -> ũ�� ũ��, ���� Yellow��
						nowPlayer.myTile.get(i).setPreferredSize(new Dimension(60,100));
						nowPlayer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
						break;
					}
				}
				stage=6;
				confirmLabel1.setText("��ǻ�Ͱ� Ÿ���� �ϳ� ��󰬽��ϴ�.");			
				confirmLabel2.setText("Ȯ���� ������ �� Ÿ���� �����մϴ�.");

				textArea.append("\n��ǻ�Ͱ� Ÿ���� �ϳ� ��󰬽��ϴ�.");
				textArea.append("\nȮ���� ������ �� Ÿ���� �����մϴ�.");
			}

			else if(stage == 6) {															// (stage == 6) : ��ǻ�Ͱ� �� Ÿ���� �ϳ� �����ϰ�, �� Ÿ�� ǥ����.
				confirm.setVisible(false);
				try {
					selectMyTile();															// selectMyTile() : ��ǻ�Ͱ� �� Ÿ���� �ϳ� �����ϰ� ��.
				} catch (InterruptedException e1) {
				}
				result = getResult();
				for(int j=0;j<player.myTileNumber;j++){										// �÷��̾��� Ÿ���߿��� selectTile�� ���� Ÿ�� ã��
					if(selectTile.number == player.myTile.get(j).number && selectTile.color == player.myTile.get(j).color){
						System.out.println("��ǻ�Ͱ� �� �÷��̾�Ÿ�� ="+ result);
						player.myTile.get(j).setPreferredSize(new Dimension(60,100));
						player.myTile.get(j).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
					}
				}

				stage = 7;
				confirmLabel1.setText("��ǻ�Ͱ� �÷��̾� Ÿ���� ��ġ�� �����߽��ϴ�."); 
				confirmLabel2.setText("��ǻ�Ͱ� ������ ã���ϴ�.");
			}

			else if(stage == 7) {															// (stage == 7) : ��ǻ�Ͱ� �÷��̾� Ÿ���� ����.
				confirm.setVisible(false);
				textArea.append("\n��ǻ�Ͱ� �÷��̾� Ÿ���� ��ġ�� �����߽��ϴ�.");
				textArea.append("\n��ǻ�Ͱ� ������ ã���ϴ�.");

				//������
				for(int i=0;i<computer.myTileNumber;i++){
					System.out.println("computer : "+computer.myTile.get(i).number+", recent : "+recentTile.number+", select : "+selectTile.number);
					textArea.append("\n��ǻ�Ͱ� ���� ���� : " + result+", ��¥ Ÿ�� ���� : "+selectTile.number);
					if( result == selectTile.number){
						textArea.append("\n��ǻ�Ͱ� �¾ҽ��ϴ�.");
						confirmLabel1.setText("��ǻ�Ͱ� �¾ҽ��ϴ�. ������� â�� Ȯ�����ּ���.");			// ������� â�� ���� Ȯ��
						confirmLabel2.setText("��ǻ�Ͱ� ��� ���� �������Դϴ�.");
						confirmButton.setText("Ȯ��");

						System.out.println("�¾ҽ��ϴ�.");
						int showNumber=0;																	// showNumber = �� �÷��̾��� 

						for(int j=0;j<player.myTileNumber;j++){
							if(player.myTile.get(j).number == selectTile.number && player.myTile.get(j).color == selectTile.color){ // ������ ��ȣ�� ��ġ�ϴ� ��ǻ���� ��ȣ�� ã��
								player.myTile.get(j).show = 1;																		// ��ǻ�Ͱ� ������ ��� �÷��̾� Ÿ���� ���̰���.
								//player.myTile.get(j).setBorder(null);
								//player.myTile.get(j).setEnabled(false);
							}	
							if(player.myTile.get(j).show == 1){
								showNumber++;																// �������°� �ϳ� �÷���
								System.out.println("player�� ������ Ÿ�� = "+showNumber);
							}
						}
						if(showNumber == player.myTileNumber)
							winner = computer;

						stage = 8;																			// ��ǻ�Ͱ� �÷��̾� Ÿ�� ���߸� (stage = 8)��.
					}

					//Ʋ����
					else if(result != selectTile.number){ 
						System.out.println("��ǻ�Ͱ� Ʋ�Ƚ��ϴ�.");

						for(int j=0;j<player.myTileNumber;j++){																			// recentTile�� ��ǻ�� ��ȣ ��
							if(computer.myTile.get(j).number == recentTile.number && computer.myTile.get(j).color == recentTile.color){ // ������ ��ȣ�� ��ġ�ϴ� ��ǻ���� ��ȣ�� ã��
								System.out.println("computer : "+computer.myTile.get(j).number+", recent : "+recentTile.number+", select : "+selectTile.number);
								computer.myTile.get(j).show = 1;																		// ��ǻ�Ͱ� Ʋ���� ��� ��ǻ���� ��ȣ�� ���̰���.
								//computer.myTile.get(j).setBorder(null);
								//computer.myTile.get(j).setEnabled(false);
								break;
							}	
						}
						confirmLabel1.setText("��ǻ�Ͱ� Ʋ�Ƚ��ϴ�.");
						confirmLabel2.setText("�÷��̾� ������ ���ư��ϴ�.");
						textArea.append("\n��ǻ�Ͱ� Ʋ�Ƚ��ϴ�.");
						textArea.append("\n�÷��̾� ������ ���ư��ϴ�.");
						textArea.append("\n�÷��̾�� ��� ���̴� Ÿ���� �Ѱ��� �������ּ���.");

						confirmButton.setText("Ȯ��");
						confirmButtonPanel.remove(cancleButton);

						stage = 2;																			// ��ǻ�Ͱ� �÷��̾� Ÿ�� Ʋ���� (stage = 2)��.

						confirm.setVisible(true);
						break;
					}
					confirm.setVisible(true);
					rePaint(null);
				}

				//Ÿ���� 13���� �Ǹ�
				if(computer.myTileNumber == 13 || player.myTileNumber == 13){								// Ÿ���� 13���� �Ǹ� �ִ� ������ �Ǿ����Ƿ� �����ؼ� ����.
					int cN=0;
					int pN=0;
					confirm.setVisible(false);
					confirmLabel1.setText("���� Ÿ���� ���ų� �г��� ���� á���ϴ�.");
					for(int i=0;i<computer.myTileNumber;i++){
						if(computer.myTile.get(i).show == 1)
							cN++;
					}
					for(int i=0;i<player.myTileNumber;i++){
						if(player.myTile.get(i).show == 1)
							pN++;
					}
					if(cN < pN){
						confirmLabel2.setText("�÷��̾ ������ Ÿ���� �����Ƿ� ��ǻ���� �¸��Դϴ�.");
						winner = computer;
						textArea.append("��ǻ�� �¸�!!");
					}
					else{
						confirmLabel2.setText("��ǻ�Ͱ� ������ Ÿ���� �����Ƿ� �÷��̾��� �¸��Դϴ�.");
						winner = player;
						textArea.append("�÷��̾� �¸�!!");
					}
					stage = 100;																			// �����Ҷ��� (stage=100)���� ��.
					confirm.setVisible(true);
				}
			}



			else if(stage == 8) {																			// (stage == 8) : ��ǻ�Ͱ� �÷��̾�Ÿ�� �������� ���°�
				confirm.setVisible(false);

				if(selectComputerReplay() == 1){															// selectComputerReplay() : �� ���� ���� ��, �������� 0,1������.
					stage = 5;																				// 1���Ͻ� ��ǻ�Ͱ� �ٽ� �����Ϸ��� (stage = 5)�� �ٲ���.
					textArea.append("\n��ǻ�Ͱ� �ٽ� �����մϴ�.");

					try {
						Thread.sleep(1000);
						confirm.setVisible(false);
						confirm.setVisible(true);
					} catch (InterruptedException e1) {
					}

					confirmLabel1.setText("��ǻ�Ͱ� �ٽ� �����մϴ�.");
					confirmLabel2.setText("��ǻ�Ͱ� ��� Ÿ���� �ϳ��� ���ϴ�.");
				}
				else{																						// 0���Ͻ� �����ϰ�, �÷��̾������� ���ư����� (stage = 2)�� �ٲ���.
					confirmLabel1.setText("��ǻ�Ͱ� ���� ��Ĩ�ϴ�.");
					confirmLabel2.setText("�÷��̾�� ��� Ÿ���� �ϳ��� ������.");
					textArea.append("\n��ǻ�Ͱ� ���� ��Ĩ�ϴ�.");
					textArea.append("\n�÷��̾� ���� �Ǿ����ϴ�.");
					stage = 2;																				// (stage = 2)�� ����							
				}

				confirm.setVisible(true);
			}

			//���� ������										
			else if(stage == 100){																			// (stage == 100) : ���ʰ� �����Ǿ����� ���� ��, �� ���������
				System.exit(0);
			}
		}// ������� Ȯ�ι�ư �������� �ܰ躰�� �۵��ϴ°�.

		// ĵ����ư(Ȯ��â�� ���°�.)
		if(e.getSource() == cancleButton){
			stage = 2;																						// (stage = 2)�� ����(�÷��̾�������)

			confirmButtonPanel.remove(cancleButton);														// ��ư �гο��� cancle��ư �����ְ�
			confirmLabel1.setText("�÷��̾ �ٽ� ���ϴ�.");												
			confirmLabel2.setText("�÷��̾�� ��� ���̴� Ÿ���� �ϳ��� ����ּ���");
			textArea.append("\n�÷��̾ �ٽ� ���ϴ�.");
			textArea.append("\n�÷��̾�� ��� ���̴� Ÿ���� �ϳ��� ����ּ���");

			confirmButton.setText("Ȯ��");
			confirm.setVisible(false);
			confirm.setVisible(true);
		}

		//Text �гο� �ִ� ��ư ��������("�Է�"������ ��)
		if(e.getSource() == selectTileNumberButton){
			textArea.append("\n�÷��̾ �� ���� : " + selectTileNumber.getText()+", ��¥ Ÿ�� ���� : " + selectTile.number);
			//������
			if( Integer.parseInt(selectTileNumber.getText()) == selectTile.number){													// ���� ������ �ѹ� ������ ����
				System.out.println("�÷��̾ �¾ҽ��ϴ�.");
				textArea.append("\n�÷��̾ �¾ҽ��ϴ�.");
				textArea.append("\n�� �÷��� ���� ����ּ���.(�� : �� ����, �ƴϿ� : �ѹ� �� �÷���)");
				int showNumber=0;
				if(nowPlayer == player){																							// ���� �÷��̾ �÷��̾���
					for(int i=0;i<computer.myTileNumber;i++){													
						if(computer.myTile.get(i).number == selectTile.number && computer.myTile.get(i).color == selectTile.color){ // select ��ȣ�� ��ġ�ϴ� ��ǻ���� ��ȣ�� ã��
							computer.myTile.get(i).show = 1;																		// ���� �¾����� ��ǻ��Ÿ���� ǥ���������.
							//computer.myTile.get(i).setBackground(Color.red);
						}	
						if(computer.myTile.get(i).show == 1){
							showNumber++;					//�������°� �ϳ� �÷���
							System.out.println("computer�� ������ Ÿ�� = "+showNumber);
						}
					}
					if(showNumber == computer.myTileNumber)
						winner = player;
				}
				else{																												// ���� �÷��̾ ��ǻ�Ͷ��
					for(int i=0;i<player.myTileNumber;i++){																			
						if(player.myTile.get(i).number == selectTile.number && player.myTile.get(i).color == selectTile.color){ 	// select ��ȣ�� ��ġ�ϴ� �÷��̾��� ��ȣ�� ã��
							player.myTile.get(i).show = 1;																			// ���� �¾����� �÷��̾�Ÿ���� ǥ���������.
						}	
						if(player.myTile.get(i).show == 1){
							showNumber++;					//�������°� �ϳ� �÷���
							System.out.println("player�� ������ Ÿ�� = "+showNumber);
						}
					}
					if(showNumber == player.myTileNumber)
						winner = computer;
				}
				//�� �������� ��.

			}
			//Ʋ����
			else{ 																													// �÷��̾ ���� ���� Ʋ������
				System.out.println("�÷��̾ Ʋ�Ƚ��ϴ�.");
				textArea.append("\n�÷��̾ Ʋ�Ƚ��ϴ�.");
				textArea.append("\n��ǻ���� ������ �Ѿ�ϴ�.");
				if(nowPlayer == player){																							// ���� �÷��̾ �÷��̾���
					for(int i=0;i<player.myTileNumber;i++){
						if(player.myTile.get(i).number == recentTile.number && player.myTile.get(i).color == recentTile.color){ 	// ������ ��ȣ�� ��ġ�ϴ��÷��̾��� ��ȣ�� ã��
							player.myTile.get(i).show = 1;																			// ���� Ʋ������ �÷��̾�Ÿ���� ǥ���������.
						}	
					}
				}
				else{																												// ���� �÷��̾ ��ǻ�Ͷ��
					for(int i=0;i<player.myTileNumber;i++){
						if(computer.myTile.get(i).number == recentTile.number && computer.myTile.get(i).color == recentTile.color){ // ������ ��ȣ�� ��ġ�ϴ� ��ǻ���� ��ȣ�� ã��
							computer.myTile.get(i).show = 1;																		// ���� Ʋ������ ��ǻ��Ÿ���� ǥ���������.
						}	
					}
				}
				confirmLabel1.setText("�÷��̾ Ʋ�Ƚ��ϴ�.");
				confirmLabel2.setText("��ǻ���� ������ �Ѿ�ϴ�.");
				confirmButton.setText("Ȯ��");
				confirmButtonPanel.remove(cancleButton);																			// �ƴϿ� ��ư ������.
			}

			//���ʰ� �����Ǹ�
			if(winner != null){
				System.out.println("���ڰ���!");
				confirmLabel1.setText("���ڰ� �����Ǿ����ϴ�.");
				if(winner == computer)
					confirmLabel2.setText("���� : ��ǻ��");
				else
					confirmLabel2.setText("���� : �÷��̾�");

				confirmButtonPanel.remove(cancleButton);
				confirmWholePanel.remove(confirmButtonPanel);
				JPanel p = new JPanel();
				p.add(new JLabel("�����Ͻðڽ��ϱ�?"));
				confirmWholePanel.add(p);
				confirmWholePanel.add(confirmButtonPanel);
				stage = 100;																										// winner������ (stage = 100)����.
			}

			//���� Ÿ���� 13���� �Ǹ�
			if(computer.myTileNumber == 13 || player.myTileNumber == 13){
				int cN=0;
				int pN=0;
				confirm.setVisible(false);
				confirmLabel1.setText("���� Ÿ���� ���ų� �г��� ���� á���ϴ�.");
				for(int i=0;i<computer.myTileNumber;i++){
					if(computer.myTile.get(i).show == 1)
						cN++;
				}
				for(int i=0;i<player.myTileNumber;i++){
					if(player.myTile.get(i).show == 1)
						pN++;
				}
				if(cN < pN){
					confirmLabel2.setText("�÷��̾ ������ Ÿ���� �����Ƿ� ��ǻ���� �¸��Դϴ�.");
					textArea.append("\n��ǻ�� �¸�!!");
				}
				else{
					confirmLabel2.setText("��ǻ�Ͱ� ������ Ÿ���� �����Ƿ� �÷��̾��� �¸��Դϴ�.");
					textArea.append("\n�÷��̾� �¸�!!");
				}
				stage = 100;																										// �гο� �������� winner������ (stage = 100)����.
				confirm.setVisible(true);
			}
			rePaint(null);
			confirm.setVisible(true);
		}
	}

	// ��ǻ�� Ÿ�� 4�� or 1�� ���� �Լ�
	void setComputerTile() throws InterruptedException {
		ArrayList<tile> computerTile = new ArrayList<tile>(); 		// Ÿ�� �迭����Ʈ ����

		// ��ǻ�Ͱ� ���� �ִ� Ÿ���� ���� �˾Ƴ�.(center�� �����ִ� Ÿ�� ���� �˾Ƴ�.)
		for(int i=0;i<26;i++){										
			if(tile[i].centerShow == 0) 							// centerShow == 1�̸� ���� ��������.
				computerTile.add(tile[i]);
		}
		System.out.print("��ǻ�Ͱ� �� �� �ִ� Ÿ�� : ");
		for(tile t : computerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//��ǻ�Ͱ� �� �� �ִ� Ÿ�� �������� �ٲ�.
		Collections.shuffle(computerTile);

		System.out.print("��ǻ�Ͱ� �� �� �ִ� Ÿ�� : ");
		for(tile t : computerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//��ǻ�Ͱ� �տ������� Ÿ�� 4�� ���� ��.
		if(stage != 5){
			int i;
			for(i=0;i<4;i++){
				tile t = computerTile.get(i);
				System.out.println("comTile : "+t.number+", �� : "+t.color);
				for(int j=0;j<26;j++){													// ��ü Ÿ���߿��� t�� ���� Ÿ�� ã��
					if(t== tile[j]){ 
						tile[j].centerShow = 1;
						nowPlayer.myTile.add(new tile(tile[j].color, tile[j].number));  
						nowPlayer.myTileNumber++;										// ��ǻ�� Ÿ�� ���� �ø��� ���ڸ��� �־���.
						System.out.println("��ǻ��Ÿ�� ����: "+nowPlayer.myTileNumber+", Ÿ��Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

						rePaint(tile[j]);												
						Thread.sleep(1000);
					}
				}
			}
			if(i == 4)
				textArea.append("\n��ǻ�Ͱ� 4���� Ÿ���� ������ϴ�.");
		}

		//��ǻ�Ͱ� 1�� ������
		else if(stage == 5) { 															// (stage == 5)���(��ǻ�Ͱ� �ϳ� �����ϴ� ��)
			tile t = computerTile.get(0);												// t = ��ǻ�Ͱ� �ϳ� ����.
			for(int j=0;j<26;j++){														// ��ü Ÿ���߿��� t�� ���� Ÿ�� ã��
				if(t== tile[j]){
					tile[j].centerShow = 1;
					tile[j].setSize(0,0);												// �� Ÿ���� �Ⱥ��̰�(center���� �����ְ�)

					nowPlayer.myTile.add(new tile(tile[j].color, tile[j].number)); 		// ��ǻ�� Ÿ�� ���� �ø��� ���ڸ��� �־���.
					recentTile = tile[j];
					nowPlayer.myTileNumber++;

					System.out.println("��ǻ��Ÿ�� ����: "+nowPlayer.myTileNumber+", Ÿ��Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

					if(computer.myTileNumber == 13 || player.myTileNumber == 13){		// �Ѱ� ������� �г��� ���� á�ٸ� �¸��� ����.
						int cN=0;
						int pN=0;
						confirm.setVisible(false);
						confirmLabel1.setText("���� Ÿ���� ���ų� �г��� ���� á���ϴ�.");
						for(int i=0;i<computer.myTileNumber;i++){
							if(computer.myTile.get(i).show == 1)
								cN++;
						}
						for(int i=0;i<player.myTileNumber;i++){
							if(player.myTile.get(i).show == 1)
								pN++;
						}
						if(cN < pN){
							confirmLabel2.setText("�÷��̾ ������ Ÿ���� �����Ƿ� ��ǻ���� �¸��Դϴ�.");
							textArea.append("��ǻ�� �¸�!!");
						}
						else{
							confirmLabel2.setText("��ǻ�Ͱ� ������ Ÿ���� �����Ƿ� �÷��̾��� �¸��Դϴ�.");
							textArea.append("�÷��̾� �¸�!!");
						}
						stage = 100;													// �¸��� ������� (stage = 100) ����.
					}
					rePaint(tile[j]);

					Thread.sleep(1000);
					confirm.setVisible(true);
				}
			}

		}
	}


	//wholeŸ�� �ٽ� �׸�
	void rePaint(tile t) {
		//computerPanel �ٽ� �׸�
		computerPanel.removeAll();															// computerPanel�� ���� ������.
		for(int j=computer.myTileNumber-1;j>=0;j--){										// ��ü ��ǻ�Ͱ� ���� Ÿ�Ͽ��� �˻�
			if(computer.myTile.get(j).show == 1){											// ��ǻ�� Ÿ���� �������Ŷ�� 
				//if(computer.myTile.get(j).show == 1 && recentTile.color == player.myTile.get(j).color)
				//				computer.myTile.get(j).setBackground(Color.red);
				computer.myTile.get(j).setText(""+computer.myTile.get(j).number);			// ��ư���� ���̰� ���ְ�,
				computer.myTile.get(j).setFont(new Font("Courier",Font.BOLD,13));			// �� ���� ��Ʈ �ٲ���.
				//computer.myTile.get(j).setEnabled(false);
				//computer.myTile.get(j).setBorder(null);
				computer.myTile.get(j).setForeground(Color.red);							// �� ���� �� �ٲ���(red��)
			}
			if(computer.myTile.get(j).getWidth() == 60){									// ��ǻ�Ͱ� ���� Ÿ���� (width == 60) select�� recentŸ���̿��� ǥ�õ�ä�̸�,
				computer.myTile.get(j).setBorder(tile[0].getBorder());						// �ʱ� border�� �ٲ���.(ǥ�õȰ� ������)
			}
			computer.myTile.get(j).setPreferredSize(new Dimension(50,90)); 					// ��ǻ�Ͱ� ���� ��üŸ�� ũ�⸦ ������� �ǵ���.
			computer.myTile.get(j).setBorder(tile[0].getBorder());

			computer.myTile.get(j).addActionListener(this);
			computerPanel.add(computer.myTile.get(j));
		}
		computerOutterPanel.add(computerPanel);
		wholePanel.add(computerOutterPanel);

		//centerPanel �ٽ� �׸�	
		if(t != null)																		// rePaintȣ�������� �Ѿ�� Ÿ���� �Ⱥ��̰�����.
			t.setSize(0,0);		

		centerPanel.removeAll();															// center�г��� ���� ������.
		for(int i=0;i<26;i++){
			centerPanel.add(tile[i]);
			if(tile[i].centerShow == 1){													// tile[](centerŸ��)�� �˻�, centerShow = 1�̸� ���� ��������.
				if(stage==3){
					tile[i].setPreferredSize(new Dimension(0,0));
				}
				else{
					tile[i].setPreferredSize(new Dimension(50,90));
				}
			}
		}
		centerOutterPanel.add(centerPanel);


		//playStatePanel �ٽ� �׸�
		if(nowPlayer == computer)															// ���� �÷��̾ ������ Ȯ���ϰ� ǥ������.
			playState.setText("���� player = ��ǻ��");
		else
			playState.setText("���� player = ��");


		//playerPanel �ٽ� �׸�
		playerPanel.removeAll();															// player �г��� ���� ������.
		for(int j=0;j<player.myTileNumber;j++){												// �÷��̾� �гο��� �˻���
			player.myTile.get(j).setText(""+player.myTile.get(j).number);					// �÷��̾� �г��� �� ����������(��������ȭ���̱⶧��)
			player.myTile.get(j).setFont(new Font("����",Font.BOLD,13));

			if(player.myTile.get(j).show == 1&& recentTile.color == player.myTile.get(j).color){	// (show == 1) �ǿ� �������� Ÿ���̸�
				//				player.myTile.get(j).setBackground(Color.red);
				//player.myTile.get(j).setBorder(null);
				//player.myTile.get(j).setEnabled(false);
				player.myTile.get(j).setForeground(Color.red);										// ���������� ǥ������
			}
			if(player.myTile.get(j).getWidth() == 60){												// ũ�� ����Ǿ����� border ������� ������
				player.myTile.get(j).setBorder(tile[0].getBorder());
			}

			player.myTile.get(j).setPreferredSize(new Dimension(50,90)); 
			playerPanel.add(player.myTile.get(j));

		}
		playerOutterPanel.add(playerPanel);


		cardLayout.show(getContentPane(), "wholePanel");
	}

	//centerŸ�� ����� �����ϴ� �Լ�(Ÿ�� ����, Ÿ�� ����, ������.)
	void setCenterTile() {
		//0~12=black, 13~25=white;

		//Ÿ���� �ϴ� �����.
		for(int i=0;i<26;i++){
			if(i<=12)									// blackŸ��(0~12)
				tile[i] = new tile(0,i);
			else if(i>12)								// whiteŸ�� (13~25)
				tile[i] = new tile(1,(i-13));
		}

		//Ÿ���� �����Ѵ�.
		shuffle(0,12);									// blackŸ�� ����
		shuffle(13,25);									// whiteŸ�� ����

		//Ÿ�Ͽ� ActionListener �߰��ϰ�, centerPanel�� Ÿ���� �߰��Ѵ�.
		for(int i=0;i<26;i++){
			tile[i].addActionListener(this);
			if(i<=12)
				tile[i].setBounds(5+i*55,15,50,90);
			else
				tile[i].setBounds(5+(i-13)*55,110,50,90);
			centerPanel.add(tile[i]);
		}
	}

	//centerŸ�� �����ϴ� �Լ�
	void shuffle(int start, int end) {								// 0~12���� ����, 13~25���� ����
		ArrayList<Integer> tileIndex = new ArrayList<Integer>();	// Collections.shuffle()����Ϸ��� �迭����Ʈ ���
		for(int i=start;i<=end;i++){								// tileIndex = white,blackŸ�� �ε�����ȣ ����.
			tileIndex.add(i);
		}

		Collections.shuffle(tileIndex);								// �ε�����ȣ ����

		// tileIndex (0,1), (2,3), ... , (11,12) ��ȯ.(i,i+1)�� ��ȯ
		// tileIndex (13,14), (15,16), ... , (24,25) ��ȯ
		for(int i=0;i<12;i = i+2){ 
			tile temp;
			temp = tile[(int) tileIndex.get(i)];
			tile[(int) tileIndex.get(i)] = new tile((int) tile[tileIndex.get(i+1)].color, tile[(int) tileIndex.get(i+1)].number);	// ��Ÿ�� ���� ��ȣ���� ��Ÿ�ϻ������� �޾ƿ�.
			tile[(int) tileIndex.get(i+1)] = new tile(temp.color, temp.number);
		}	
	}

	//��ǻ��, �÷��̾� Ÿ�� �����ϴ� �Լ�
	void sort() {
		ArrayList<Integer> computerTileIndex = new ArrayList<Integer>();	// Collections.shuffle()����Ϸ��� �迭����Ʈ ���
		ArrayList<tile> computerList = new ArrayList<tile>();

		ArrayList<Integer> playerTileIndex = new ArrayList<Integer>();		// Collections.shuffle()����Ϸ��� �迭����Ʈ ���
		ArrayList<tile> playerList = new ArrayList<tile>();

		System.out.print("��ǻ��Ÿ�� :");
		for(tile t : computer.myTile){
			System.out.print(t.number+" ");
			computerTileIndex.add(t.number);								// ��ǻ��Ÿ���� �����ִ� Ÿ���� �־���.
		}

		System.out.print(", �÷��̾�Ÿ�� :");
		for(tile t : player.myTile){
			System.out.print(t.number+" ");
			playerTileIndex.add(t.number);									// �÷��̾�Ÿ���� �����ִ� Ÿ���� �־���.
		}

		Collections.sort(computerTileIndex);								// �� ��ǻ��Ÿ���� �ε�����ȣ�� sort�ϰ�,
		Collections.sort(playerTileIndex);									// �� �÷��̾�Ÿ���� �ε�����ȣ�� sort�ϰ�

		System.out.print("\nsort ��ǻ��Ÿ�� :");
		for(int n : computerTileIndex){ 									// computerTileIndex�� ���ҿ� ���� i��° ��ȣ���(8,8,0,1)
			for(int i=0;i<computer.myTileNumber;i++){						// computer.myTile.get(i)�� 8�� ���ڷ� �����ִ� Ÿ�� ã��
				if(computer.myTile.get(i).number == n){						// computer�� ������ �ִ� Ÿ���� ��ȣ�� tileIndex�� i��° ��ȣ�� ������
					computerList.add(computer.myTile.get(i));				// list�� �� ��ȣ�� ���� Ÿ���� �߰�����.
					computer.myTile.remove(i);								// i��° ���� ������.
					break;
				}
			}
			System.out.print(n+" ");
		}

		System.out.print(", sort �÷��̾�Ÿ�� :");
		for(int n : playerTileIndex){ 										// tileIndex�� ���ҿ� ���� i��° ��ȣ���
			for(int i=0;i<player.myTileNumber;i++){	
				if(player.myTile.get(i).number == n){						// player�� ������ �ִ� Ÿ���� ��ȣ�� tileIndex�� j��° ��ȣ�� ������
					playerList.add(player.myTile.get(i));					// list�� �� ��ȣ�� ���� Ÿ���� �߰�����.
					player.myTile.remove(i);
					break;
				}
			}
			System.out.print(n+" ");
		}

		//������
		for(int i=0;i<computer.myTileNumber-1;i++){
			if(computerList.get(i).number == computerList.get(i+1).number)			// 0,1���� Ÿ�Ϲ�ȣ ������
				if(computerList.get(i+1).color == 0){ 								// 0 = black, 1��°�� �������̸�
					tile t = computerList.get(i);
					computerList.set(i,computerList.get(i+1)); 						// 0��° �ڸ��� 1�� ����
					computerList.set(i+1, t);										// 1��° �ڸ��� t(0�� ����) ����
					System.out.println("\n\t"+computerList.get(i).number+"�� Ÿ�ϰ� "+computerList.get(i+1).number+"�� Ÿ���� ��������");
				}

		}

		for(int i=0;i<player.myTileNumber-1;i++){
			if(playerList.get(i).number == playerList.get(i+1).number)				// 0,1���� Ÿ�Ϲ�ȣ ���� ���߿�
				if(playerList.get(i+1).color == 0){ 								// (color == 0 == black) 1��°�� �������̸�
					tile t = playerList.get(i);										// t = 0�� ������ Ÿ��
					playerList.set(i,playerList.get(i+1)); 							// 0��° �ڸ��� 1�� ����
					playerList.set(i+1, t);											// 1��° �ڸ��� t(0�� ����) ����
					System.out.println("\n\t"+playerList.get(i).number+"�� Ÿ�ϰ� "+playerList.get(i+1).number+"�� Ÿ���� ��������");
				}

		}

		computer.myTile = computerList;												// ��ǻ�� Ÿ���� ���ĵ� Ÿ�Ϸ� �ٲ��ش�.
		player.myTile = playerList;													// �÷��̾� Ÿ���� ���ĵ� Ÿ�Ϸ� �ٲ��ش�.

		System.out.print("\n��ǻ��Ÿ�� :");
		for(tile t : computer.myTile){
			System.out.print(t.number+" ");
			computerTileIndex.add(t.number);										// �� Ÿ���� ��ȣ�� �־���.(����Ÿ���� �����ִ� Ÿ���� �־���.)
		}

		System.out.print(", �÷��̾�Ÿ�� :");
		for(tile t : player.myTile){
			System.out.print(t.number+" ");
			playerTileIndex.add(t.number);											// �� Ÿ���� ��ȣ�� �־���.(����Ÿ���� �����ִ� Ÿ���� �־���.)
		}
		System.out.println("");
		rePaint(null);
	}

	// ��ǻ�Ͱ� ����� Ÿ�� ���� ����.(selectTile ���°�)
	void selectMyTile() throws InterruptedException {
		ArrayList<tile> playerTile = new ArrayList<tile>(); 						// Ÿ�� �迭����Ʈ ����

		//��ǻ�Ͱ� ���� �ִ� Ÿ���� ���� �˾Ƴ�.(center�� �����ִ� Ÿ�� ���� �˾Ƴ�.)
		for(int i=0;i<player.myTileNumber;i++){
			if(player.myTile.get(i).show == 0)										// show = ��������. ���������������߿� ������.
				playerTile.add(player.myTile.get(i));								// �÷��̾� Ÿ�� �ִ°� �� �޾ƿ�.
		}
		System.out.print("��ǻ�Ͱ� �� �� �ִ� Ÿ�� : ");
		for(tile t : playerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//��ǻ�Ͱ� �� �� �ִ� Ÿ�� �������� �ٲ�.
		Collections.shuffle(playerTile);

		System.out.print("��ǻ�Ͱ� �� �� �ִ� Ÿ�� : ");
		for(tile t : playerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");
		tile t = playerTile.get(0);													// ������ �����̶� �׳� �������� ���õȰ��� ���� ó���� �޾ƿ�

		for(int j=0;j<player.myTileNumber;j++){										// �÷��̾��� Ÿ���߿��� t�� ���� Ÿ�� ã��
			if(t.number == player.myTile.get(j).number && t.color == player.myTile.get(j).color){
				selectTile = player.myTile.get(j);									// selectTile = �� tŸ�ϰ� ������ �־���.
				System.out.println("��ǻ�� : �÷��̾�Ÿ�� �� ="+player.myTile.get(j).number);

				Thread.sleep(1000);

				player.myTile.get(j).setSize(60,100);								// ��ǻ�Ͱ� ��Ÿ�� ������� ǥ������.
				player.myTile.get(j).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
				confirm.setVisible(true);
			}
		}
	}

	//��ǻ�Ͱ� �÷��̾��� ���� ���� ���ߴ°�. : ��ü ���� �ִ� ������ ��ǻ�Ͱ� ������, �÷��̾ �������� �������� ����.
	int getResult() {
		ArrayList<tile> t = new ArrayList<tile>();				// t = ��ǻ�Ͱ� �� �÷��̾� Ÿ�ϰ� �������� Ÿ�� ���� ����.(�߾ӿ� �迭�Ǿ��� �������)
		ArrayList<tile> t2 = new ArrayList<tile>();				// t2 = ���� ��ǻ�Ͱ� �����Ͱ� �̹� �������� ����.
		ArrayList<tile> t3 = new ArrayList<tile>();				// t3 = �÷��̾� ��ü Ÿ�� �־���.
		int tN = 0; 											// tN = t�� �� Ÿ�ϰ������°�.
		int result = 0; 										// result = �� ��ȯ�Ҳ� ���
		int start = -1; 										// start = -1��.(0�̸� findAtoB�� �ѱ涧 ������)
		int end = player.myTileNumber-1;  						// end = �÷��̾ ���� ������ �ε���
		int selectTileIndex = 0; 								// selectTielIndex = selectTile�� �ε��� ��ȣ �޾ƿ�.
		//�ʱ�ȭ
		for(int i=0;i<26;i++){
			if(selectTile.color == tile[i].color){ 				// ��ǻ�Ͱ� �� �÷��̾� Ÿ�ϰ� �������� Ÿ����
				t.add(tile[i]);									// t�� t2�� �־���.
				t2.add(tile[i]);
				tN++;
			}
		}	
		for(int i=0;i<player.myTileNumber;i++){
			t3.add(player.myTile.get(i));						// t3 = �÷��̾� Ÿ�� ��ü �־���
		}

		//t2�� ��ǻ�Ͱ� ������, �÷��̾ ������ ��
		for(int i=0;i<t.size();i++){							// i = t�� ũ�⸸ŭ �ݺ�
			System.out.println("tN = "+tN+", computerŸ�� �� :"+computer.myTile.size());

			//��ǻ�Ͱ� �������ִ°� ��.
			for(int j=0;j<computer.myTileNumber;j++){ 			// j = ��ǻ�� ũ�⸸ŭ �ݺ�
				if(t.get(i).number == computer.myTile.get(j).number && t.get(i).color == computer.myTile.get(j).color){
					System.out.println("t ="+t.get(i).number+", computer="+computer.myTile.get(j).number);
					t2.set(i, null);							// t2�� i��°�ڸ��� ��ǻ�Ͱ� �������ִ°Ϳ� null �־���.
					tN--;
				}
				else
					System.out.println("363323t ="+t.get(i).number+", computer="+computer.myTile.get(j).number);
			}

			//�÷��̾ �������߿� �������� ��.
			for(int j=0;j<player.myTileNumber;j++){				// j = ��ǻ�� ũ�⸸ŭ �ݺ�
				if(player.myTile.get(j).show ==1 && t.get(i).number == player.myTile.get(j).number && t.get(i).color == player.myTile.get(j).color){
					System.out.println("t ="+t.get(i).number+", player="+player.myTile.get(j).number);
					t2.set(i, null);							// t2�� i��°�� �÷��̾ �������� ���������ΰ� null�־���.
					tN--;
				}
			}
		}

		for(int i=0;i<t.size();i++){
			if(t2.get(i) != null)
				System.out.print(" "+t2.get(i).number);
			else
				System.out.print(" 999");
		}
		System.out.println("");

		//�������� ����
		Collections.shuffle(t2);

		for(int i=0;i<t.size();i++){
			if(t2.get(i) != null){
				System.out.print(" "+t2.get(i).number);
				result = t2.get(i).number;
			}
			else
				System.out.print(" 999");
		}
		System.out.println("");

		//selectTile�� �ε��� ��ȣ �޾ƿ�.
		for(int i=0;i<player.myTileNumber;i++){
			if(selectTile.number == player.myTile.get(i).number && selectTile.color == player.myTile.get(i).color){
				selectTileIndex = i;																					// �÷��̾� Ÿ���� ��ǻ�Ͱ� ������ Ÿ���� �ε��� �޾ƿ�.
				System.out.println("selectTile ���� : "+selectTile.number + ", �� : "+selectTile.color +"�ε��� : "+selectTileIndex);
				break;
			}
		}

		//start, end ��ġ ã��.
		for(int i=selectTileIndex;i>=0;i--){		 			// 0 2 * * * 8
			if(t3.get(i).show == 1){ 							// ������ Ÿ���� ������������ �˻�
				start = i; 			 							// selectTile����մ°��� 3�̸� 1�� �� ��.
				break;	
			}
		}
		for(int i=selectTileIndex;i<player.myTileNumber;i++){
			if(t3.get(i).show == 1){ 							// ���̴µ����� �˻�
				end = i; 										// 5�� �� ��
				break;
			}
		}
		System.out.println("start = "+ start + ", end = "+ end);

		if(start == -1)											// start�� �ʱⰪ�̸� 0���� �ٲ���
			start = 0;
		else													// start�� 0�̰ԵǸ� t3�� 0��° '����' �� �ٲ���.
			start = t3.get(start).number;

		while(true){
			result = findAtoB(start, t3.get(end).number);											// A~B ���̰� ã�� �Լ�, result���� t2�� �ִ��� Ȯ��
			for(int i=0;i<t2.size();i++){															// t2 = ��ǻ�Ͱ� ���� �ִ� ��� ����
				if(t2.get(i) != null && t2.get(i).number == result){								// null���� �ƴ� �����߿� result�� ���� ���� �ִ��� Ȯ��					
					System.out.print("resultȮ�� : t2�� = "+t2.get(i).number+", result = "+result);	// ������ �ٽ� �Լ��θ�.(��ǻ�Ͱ� ���� �ִ� ���� ���ö�����)
					return result; 																	// �����߿� resultȮ���ϸ� �ѱ�
				}
			}
		}

	}

	//A�� B���� �� ��ȯ�ϴ� �Լ�.(A~B���� ���� ��)										// 3 * * 5 �̷������϶�
	int findAtoB(int start, int end){ 													// 3~5�� 3,4,5�� �ϳ� ��� ��ȯ
		int result;					  													// end-start + 1= 5-3 + 1 = 2 + 1 = 3(3���� �ϳ� ���;���.)
		Random ran = new Random();

		System.out.println("��ǻ�Ͱ� �� �� �ִ� �� = start : "+start+", end : "+end);
		result = ran.nextInt(end-start+1); 												// 0,1,2�� �ϳ� ����. = +start ���ָ� 3,4,5 ��!
		result += start;
		System.out.println("��ǻ�Ͱ� �� �� : "+result);
		return result;
	}

	//��ǻ�Ͱ� �ٽ����� �������� �����ϴ� �Լ�.
	int selectComputerReplay() { 														// 1 ���Ͻ� ��ǻ�Ͱ� �ٽ���, 0���Ͻ� �ٽþ���
		Random ran = new Random();
		int result = ran.nextInt(2); 													// 0, 1 �߿� �ϳ� ����.
		System.out.println("��ǻ�Ͱ� �ٽ����� : " + result);
		return result;
	}
}
/*
public class StartingFrameTest {
	public static void main(String[] args) {
		new playFrameSetting();
		//CardLayout cardlayout = new CardLayout();
	}
}*/