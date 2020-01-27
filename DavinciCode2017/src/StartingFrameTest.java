//StartingFrameTest == 연습 - 빨간색
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

//플레이어 클래스
class player {							
	int turn;	     							// turn = 1 일 때 자기차례
	ArrayList<tile> myTile; 					// 타일 고를때마다 타일 추가(최대 13)
	int myTileNumber;							// 내 타일 개수(초기 0, 들어갈 자리가 됨.)
	public player() {
		turn = 0;
		myTile = new ArrayList<tile>();			// player불릴때 타일개수 생김.
		myTileNumber = 0;	
	}
}

//각 타일 하나당 클래스
class tile extends JButton {			
	int color;									// black = 0, white = 1
	int show;									// 0 = 안뒤집혀짐, 1 = 뒤집혀짐
	int centerShow;								// 센터에서 안가져감 = 0, 가져감 = 1
	int number;									// 0~12 : 숫자
	public tile(int color, int number) {
		//		super(""+number);

		this.color = color;
		this.show = 0; 							// 처음에는 다 안뒤집혀진 타일임.
		this.centerShow = 0;					// 처음에는 다 안가져간 상태
		this.number = number;
		if(color == 0){							// color == 0 (black)일때
			this.setBackground(Color.black);
			this.setForeground(Color.white);
		}
		else if(color == 1){					// color == 1 (white)일때
			this.setBackground(Color.white);
			this.setForeground(Color.black);
		}
	}

}

class playFrameSetting extends JFrame implements ActionListener {
	//BGM 삽입
	Clip clip;

	//메인창
	player computer, player, nowPlayer, winner=null; 							// player 2명, nowPlayer = 지금 턴의 플레이어, winner = 이긴사람 표시
	JPanel computerPanel, computerOutterPanel, playerPanel, playerOutterPanel; 	// player 2명 패널, 그 패널의 겉 패널 2개씩
	JPanel centerPanel, centerOutterPanel; 										// 중간 패널, 그 패널의 겉 패널 2개씩
	JPanel wholePanel; 															// 전체 패널

	//확인창
	JFrame confirm;					// 확인창의 프레임
	JPanel confirmWholePanel; 		// 확인창의 전체패널
	JPanel confirmLabelWholePanel; 	// 레이블 1,2 들어갈 패널
	JPanel confirmLabel1Panel;		// 레이블1 들어갈 패널
	JPanel confirmLabel2Panel;		// 레이블2 들어갈 패널
	JPanel confirmButtonPanel; 		// 버튼 들어갈 패널
	JButton confirmButton;			// 확인창의 버튼
	JLabel confirmLabel1;			// 레이블1
	JLabel confirmLabel2;			// 레이블2
	JButton cancleButton;			// 취소버튼

	//상대 타일 숫자 적는 패널
	JPanel TextPanel; 				// 상대타일 숫자 적는 패널
	JTextField selectTileNumber; 	// 상대타일 숫자 적는 필드
	JButton selectTileNumberButton;	// 상대타일 숫자 보내는 버튼

	//플레이 상태 표시
	JPanel playStatePanel;			// 게임 진행 상태 보여주는 패널
	JLabel playState;				// 게임 진행 상태 보여주는 필드

	//scrollPane창
	JFrame scrollFrame;				// 스크롤 프레임
	JScrollPane scroll;				// 스크롤 페인
	JTextArea textArea;				// 스크롤 페인에 들어갈 textArea

	//Layout선언
	CardLayout cardLayout;			// 전체 창이 넘어갈때 쓰는 layout
	CardLayout playerCardLayout;	// 각 플레이어 패널과 센터패널에 쓸 layout(전체 크기에 맞춰지는듯?)

	//전체 프로그램에서 쓰게될 변수들
	tile tile[];					// 가운데 패널에 들어갈 타일 배열
	int stage; 						// 0 = 준비(세팅)단계, 1 = 플레이단계
	tile recentTile;				// 최근 center에서 받아온 타일(본인 타일)
	tile selectTile;				// 상대의 고른타일 정하는 변수(상대 타일)
	int result;						// 컴퓨터가 말한 답 넣는 변수
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

		//초기화
		computer = new player();								// 플레이어 하나씩 만듦.
		player = new player();
		nowPlayer = new player();
		stage = 0;												// 0 = 준비(세팅), 단계 1 = 플레이단계
		recentTile = null;										// 최근 본인꺼 고른 타일 = null임.
		selectTile = null;										// 최근 상대꺼 고른 타일 = null임.
		try {
			BackgroundImg = ImageIO.read(new File("image/BackgroundImage.jpg"));
			PanelImg = ImageIO.read(new File("image/PanelImage.jpg"));
		} catch (IOException e) {
		}

		//스크롤창(진행상태 표시창)
		scrollFrame = new JFrame();
		textArea = new JTextArea(10,30);
		textArea.append("플레이어는 가운데 보이는 타일중 4개를 선택해주세요");
		textArea.setCaretPosition(textArea.getDocument().getLength());			// 스크롤 밑으로 내림.
		scroll = new JScrollPane(textArea);
		scrollFrame.add(scroll, BorderLayout.CENTER);
		scrollFrame.setVisible(true);											// 스크롤창 = 처음부터 보임
		scrollFrame.setBounds(950,0,380,250);									// 스크롤창 크기, 나올곳 (350*250)
		scrollFrame.setTitle("진행상태");

		//확인창
		confirm = new JFrame();
		confirmWholePanel = new JPanel();						// 확인창 전체패널
		confirmWholePanel.setLayout(new GridLayout(0,1));
		confirmLabelWholePanel = new JPanel();					// 확인창에 들어갈 레이블 전체패널
		confirmLabelWholePanel.setSize(350,100);		
		confirmLabelWholePanel.setLayout(new GridLayout(0,1));
		confirmLabel1Panel = new JPanel();						// 레이블 1패널
		confirmLabel2Panel = new JPanel();						// 레이블 2패널
		confirmButtonPanel = new JPanel();						// 확인창 버튼패널
		confirm.setBounds(700,500,350,200);						// 확인창 크기, 나올곳 (350*200)
		confirm.setVisible(false);								// 처음엔 안보임
		confirm.setTitle("확인창");	

		//전체 프레임의 레이아웃 설정
		cardLayout = new CardLayout();
		playerCardLayout = new CardLayout();
		this.setLayout(cardLayout);

		//computer 패널
		computerOutterPanel = new JPanel();
		computerOutterPanel.setLayout(playerCardLayout);
		computerOutterPanel.setBackground(new Color(111,13,39));		
		computerPanel = new JPanel();
		computerPanel.setOpaque(false);
		computerPanel.setBackground(Color.red);
		computerOutterPanel.add(computerPanel);					// computerOutterPanel에 computerPanel 넣어줌.

		//center 패널
		centerOutterPanel = new JPanel();
		centerOutterPanel.setLayout(playerCardLayout);
		centerOutterPanel.setBackground(new Color(111,13,39));
		centerOutterPanel.setOpaque(false);
		centerPanel = new JPanel();
		centerPanel.setLayout(null);							// 타일 절댓값으로 배치할 것.
		centerPanel.setBackground(new Color(111,13,39));
		centerPanel.setOpaque(false);
		tile = new tile[26];									// center패널에 들어갈 타일들
		setCenterTile();										// 타일 생성, 타일 셔플(함수로 부름.)
		recentTile = tile[0];									// 일단 초기화시킴.
		centerOutterPanel.add(centerPanel);						// centerOutterPanel에 centerPanel 넣어줌.

		//player 패널
		playerOutterPanel = new JPanel();
		playerOutterPanel.setLayout(playerCardLayout);
		playerOutterPanel.setBackground(new Color(111,13,39));
		playerPanel = new JPanel();
		playerPanel.setBackground(Color.blue);
		playerPanel.setOpaque(false);
		playerOutterPanel.add(playerPanel);						// playerOutterPanel에 playerPanel 넣어줌.

		//playState 패널
		playStatePanel = new JPanel();
		playStatePanel.setBackground(new Color(0,0,128));
		playStatePanel.setBorder(new MatteBorder(5,5,5,5,new Color(111,13,39)));	//테두리 만들어줌.
		playStatePanel.setOpaque(false);
		playStatePanel.setLayout(new BorderLayout());
		playState = new JLabel();
		playState.setHorizontalAlignment(JLabel.CENTER);		// 레이블 가운데정렬해줌.
		playState.setText("현재 player = 나");					// 처음 플레이어는 플레이어임.
		playState.setForeground(Color.black);
		playStatePanel.add(playState,BorderLayout.CENTER);							// playStatePanel에 playState(레이블) 넣어줌.

		//Text 패널
		TextPanel = new JPanel();
		TextPanel.setBackground(new Color(111,13,39));
		TextPanel.setLayout(new FlowLayout());
		selectTileNumber = new JTextField(15);
		selectTileNumberButton = new JButton("입력");					// '입력' 버튼 생성
		selectTileNumberButton.addActionListener(this);
		selectTileNumberButton.setPreferredSize(new Dimension(60,20));  // 버튼 사이즈 정해줌.
		TextPanel.add(selectTileNumber);
		TextPanel.add(selectTileNumberButton);							// TextPanel에 텍스트필드와 버튼 넣어줌.

		//whole 패널 (Outter패널 : 위치 지정하고 그 안의 패널안 값을 변경하려고(위치 그대로 내용변경))
		wholePanel = new JPanel() {
			public void paintComponent(Graphics g){
				//super.paintComponent(g);
				g.drawImage(BackgroundImg,0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		wholePanel.setBackground(Color.black);								// 전체 패널 색
		wholePanel.setOpaque(false);
		wholePanel.setLayout(null); 										// JPanel의 디폴트값은 FlowLayout이므로 null해줘야함.
		wholePanel.add(computerOutterPanel);								// computerOutterPanel 넣음
		computerOutterPanel.setBounds(50,50,800,110); 					
		wholePanel.add(centerOutterPanel);									// centerOutterPanel 넣음
		centerOutterPanel.setBounds(80,220,720,220);
		wholePanel.add(playStatePanel);										// playStatePanel 넣음
		playStatePanel.setBounds(100,510,250,30);
		wholePanel.add(TextPanel);											// TextPanel 넣음
		TextPanel.setBounds(600,510,250,33);
		wholePanel.add(playerOutterPanel);									// playerOutterPanel 넣음
		playerOutterPanel.setBounds(50, 550, 800, 110);

		//전체 프레임에 whole 패널 추가(layout = cardLayout)
		add("wholePanel", wholePanel);

		//게임실행
		player.turn = 1; 													// 처음에는 player가 자기차례
		computer.turn = 0;
		nowPlayer = player;

		//전체 프레임 설정
		setTitle("Da Vinci Code");
		setSize(900, 750);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);												// 프레임 크기변경 못하게
		setVisible(true);
	} 


	@Override 
	public void actionPerformed(ActionEvent e) { // 단계정해서 단계별로 해야할 것 정해줌.
		System.out.println("======================================================");
		textArea.append("\n===============================================");

		// 아무 버튼이든 눌렸을때 winner에 값이 담긴다면 -> stage = 100으로 바꿔줌.
		if(winner != null){
			System.out.println("승자결정!");
			confirmLabel1.setText("승자가 결정되었습니다.");
			if(winner == computer)
				confirmLabel2.setText("승자 : 컴퓨터");
			else
				confirmLabel2.setText("승자 : 플레이어");
			confirmButtonPanel.remove(cancleButton);
			//confirmWholePanel.remove(confirmButtonPanel);
			JPanel p = new JPanel();
			p.add(new JLabel("종료하시겠습니까?"));
			confirmWholePanel.add(p);
			//confirmWholePanel.add(confirmButtonPanel);
			stage = 100;							
			confirm.setVisible(true);
		}

		
		
		//초기 세팅
		if(player.turn == 1 && nowPlayer.myTileNumber < 5 && stage == 0){ 
			if(nowPlayer.myTileNumber != 4){
				System.out.println("now = player");
				nowPlayer = player;
			}
		}

		//tile[i](center 타일이 눌렸을때)
		for(int i=0;i<26;i++){
			if(e.getSource() == tile[i] && tile[i].centerShow == 0){			// 눌러진게 tile[i]이고, center에서 안가져갔다면
				tile[i].centerShow = 1;
				nowPlayer.myTile.add(new tile(tile[i].color, tile[i].number)); 	// 플레이어 타일 갯수 늘리고 빈자리에 넣어줌.
				nowPlayer.myTileNumber++;
				recentTile = tile[i];											// 최근 타일에 추가해준다.

				//stage = 0,1일때
				if(stage==0 || stage==1){
					System.out.println("타일 갯수: "+nowPlayer.myTileNumber+", 타일Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

					//초기에 4개 다 고르면 턴 바꿔줌.
					if(nowPlayer.myTileNumber == 4 && stage == 0){ 				// stage==0이고, 현재 플레이어의 타일갯수가 4개면
						System.out.println("now = computer");
						nowPlayer = computer;									// 컴퓨터 턴으로 넘겨줌.
						player.turn = 0;
						computer.turn = 1;	

						//플레이어 턴에서 컴퓨터턴으로 넘어가면 확인창 열리게하기
						confirmLabel1 = new JLabel("플레이어의 타일을 다 골랐습니다.");
						confirmLabel1Panel.add(confirmLabel1);
						confirmLabel2 = new JLabel("확인을 누르시면 컴퓨터의 타일을 고릅니다.");
						confirmLabel2Panel.add(confirmLabel2);
						textArea.append("\n플레이어가 4개의 타일을 골랐습니다.");
						textArea.append("\n컴퓨터가 4개의 타일을 고릅니다. 잠시만 기다려주세요.");

						confirmLabelWholePanel.add(confirmLabel1Panel);
						confirmLabelWholePanel.add(confirmLabel2Panel);

						confirmButton = new JButton("확인");
						confirmButton.addActionListener(this);
						confirmButtonPanel.add(confirmButton);
						confirmWholePanel.add(confirmLabelWholePanel);			// confirmWholePanel에 confirmLabelWholePanel 넣어줌
						confirmWholePanel.add(confirmButtonPanel);				// confirmWholePanel에 confirmButtonPanel 넣어줌
						confirm.add(confirmWholePanel);							// confirm 프레임창에 confirmWholePanel 넣어줌.
						confirm.setVisible(true);
					}
					rePaint(tile[i]);
				}

				//stage = 2일때 : stage = 3 으로 넘어감.
				if(stage==2){													
					System.out.println("\t 타일 하나 뽑았어요");
					recentTile = tile[i];										// 뽑은 타일을 recentTile에 추가해준다.
					System.out.println("\t recent타일 = "+recentTile.number);
					stage = 3; //상태 바꿔주고
					confirm.setVisible(true);
				}

				rePaint(tile[i]);
			}

		}

		//computer타일일때
		for(int i=0;i<computer.myTileNumber;i++){
			if(stage == 4 && e.getSource() == computer.myTile.get(i)){ 			// 눌러진게 computer의 타일이라면
				computer.myTile.get(i).setPreferredSize(new Dimension(60,100));
				computer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
				selectTile = computer.myTile.get(i);							// 선택한 타일을 selectTile에 추가해준다.
				System.out.println(" computer 타일갯수 : "+ computer.myTileNumber+", 타일번호 :"+computer.myTile.get(i).number);	
				textArea.append("\n컴퓨터의 타일중 맞출 타일의 위치를 선택했습니다.");
				textArea.append("\n숫자를 입력해준 후 입력 버튼을 눌러주세요");
				confirm.setVisible(true);
				break;
			}
		}


		//확인창버튼 눌렀을때 : 단계별로 작용함.
		if(e.getSource() == confirmButton){
			//각 플레이어의 패널에 타일이 13개 들어갔을때 승리자 찾음(보여진 타일이 많은사람이 패배.)
			if(computer.myTileNumber == 13 || player.myTileNumber == 13){	
				int cN=0;
				int pN=0;
				confirm.setVisible(false);
				confirmLabel1.setText("남은 타일이 없거나 패널이 가득 찼습니다.");
				for(int i=0;i<computer.myTileNumber;i++){									// cN = 컴퓨터의 타일중 보여진 타일갯수 셈.
					if(computer.myTile.get(i).show == 1)
						cN++;
				}
				for(int i=0;i<player.myTileNumber;i++){										// pN = 플레이어의 타일중 보여진 타일갯수 셈.
					if(player.myTile.get(i).show == 1)
						pN++;
				}
				if(cN < pN){																// cN < pN : 컴퓨터 승리
					confirmLabel2.setText("플레이어가 보여진 타일이 많으므로 컴퓨터의 승리입니다.");
					textArea.append("컴퓨터 승리!!");
				}
				else{																		// cN > pN : 플레이어 승리
					confirmLabel2.setText("컴퓨터가 보여진 타일이 많으므로 플레이어의 승리입니다.");
					textArea.append("플레이어 승리!!");
				}
			}

			if(stage == 0){																	// (stage == 0) : 눌렸을때 컴퓨터 타일 4개 골라줌.
				confirm.setVisible(false);
				try {
					setComputerTile();														// setComputerTile() : 컴퓨터 타일 4개 고르는 함수
					stage = 1;																// 컴퓨터의 타일까지 다 고르면 다음 턴으로 (stage = 1)
					confirmLabel1.setText("모든 플레이어가 타일을 가져갔습니다.");
					textArea.append("\n모든 플레이어가 타일을 가져갔습니다.");
					confirmLabel2.setText("확인을 누르시면 두 플레이어의 타일을 정렬합니다.");				
					textArea.append("\n두 플레이어의 타일을 정렬합니다.");
					confirm.setVisible(true);

				} catch (InterruptedException e1) {
				}
			}

			else if(stage == 1){ 															// (stage == 1) : 눌렸을때 두 플레이어 타일 정렬하기
				confirm.setVisible(false);
				sort();																		// sort() : 컴퓨터, 플레이어 타일 정렬하는 함수
				confirm.setVisible(true);

				confirmLabel1.setText("모든 플레이어의 타일이 정렬되었습니다.");
				textArea.append("\n모든 플레이어의 타일이 정렬되었습니다.");
				confirmLabel2.setText("확인을 누르고 가운데 보이는 타일중 하나를 고르세요.");
				textArea.append("\n플레이어는 가운데 보이는 타일중 하나를 고르세요.");
				confirm.setVisible(true);
				stage = 2; 																	// 두 플레이어 타일 정렬하면 다음 턴으로 (stage = 2)
			}

			else if(stage == 2) { 															// (stage == 2) : 눌렸을때 플레이어가 먼저 시작하게해줌.
				confirm.setVisible(false);	
				//게임 시작하는것 구현.
				if(nowPlayer == computer){													
					nowPlayer = player;														// nowPlayer = player : 플레이어가 현재 플레이어되게.
					player.turn = 1;
					computer.turn = 0;
				}

				rePaint(null);																// rePaint() : 화면 다시 그려줌										

				confirmLabel1.setText("플레이어가 타일을 하나 뽑았습니다.");
				confirmLabel2.setText("맞출 타일의 위치를 선택해 주세요");
			}

			else if(stage == 3){ 															// (stage == 3) : 눌렸을때 플레이어가 최근에 center에서 선택한 타일
				confirm.setVisible(false);													// 					정렬하고, 크기 변경해줌.(가져온 타일 표시해줌.)
				textArea.append("\n플레이어가 타일을 하나 뽑았습니다.");
				textArea.append("\nrecent = " + recentTile.number);
				textArea.append("\n컴퓨터의 타일중 맞출 타일의 위치를 선택해주세요.");
				sort();																		// sort() : 정렬해줌
				for(int i=0;i<nowPlayer.myTileNumber;i++){									// 컴퓨터 타일중 recentTile과 색, 숫자가 같은 타일  찾아서
					if((nowPlayer.myTile.get(i).number == recentTile.number) && (nowPlayer.myTile.get(i).color == recentTile.color)){	
						System.out.print("\n\trecentTile = " + recentTile.number + ", nowTile = "+nowPlayer.myTile.get(i).number);																
						nowPlayer.myTile.get(i).setPreferredSize(new Dimension(60,100));	// 크기변경
						//nowPlayer.myTile.get(i).setBackground(Color.yellow);
						nowPlayer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
						break;
					}
				}

				System.out.println("플레이어가 타일 뽑음");
				confirmLabel1.setText("컴퓨터 타일의 위치를 선택했습니다.");
				confirmLabel2.setText("숫자를 입력해준 후 입력 버튼을 눌러주세요.");		

				stage = 4; 																	// 크기 변경되면 다음단계로 (stage = 4)
			}
			// 크기 변경된 후 컴퓨터 타일 고르고(computer버튼이 눌렸을때로)
			// 숫자 입력 후 입력버튼 눌러야함(입력버튼이 눌렸을때로)
			//두 단계 실행 후 돌아옴.

			else if(stage == 4) {															// (stage == 4) : 플레이어가 말한 답이 맞으면 여기 실행
				confirm.setVisible(false);

				confirmLabel1.setText("플레이어가 맞았습니다.");
				confirmLabel2.setText("끝내시겠습니까?");
				confirmButton.setText("예");												// '예' 버튼 누르면 (stage = 2)로 다시 감.

				cancleButton = new JButton("아니오");										// '아니오' 버튼 누르면 (stage = 5)로 감.
				cancleButton.addActionListener(this);
				confirmButtonPanel.add(cancleButton);
				stage = 5; 																	// 상태변경 후 다음단계로 (stage == 5)
			}

			else if(stage == 5) {															// (stage == 5) : 컴퓨터 턴으로 바뀌고, 컴퓨터가 가져온 타일 표시
				confirm.setVisible(false);
				confirmButtonPanel.remove(cancleButton);
				nowPlayer = computer;
				computer.turn = 0;
				player.turn = 1;
				System.out.println("컴퓨터의 턴으로 바뀝니다.");
				try {
					setComputerTile();														// setComputerTile() : 컴퓨터가 가운데 타일중 하나 가져오게 함.
				} catch (InterruptedException e1) {
				}

				sort();																		// sort() : 정렬시키고

				for(int i=0;i<nowPlayer.myTileNumber;i++){
					System.out.print("\n\trecentTile = " + recentTile.number + ", nowTile = "+nowPlayer.myTile.get(i).number);
					if(nowPlayer.myTile.get(i).number == recentTile.number && nowPlayer.myTile.get(i).color == recentTile.color){	//최근의 타일이면
						//플레이어가 타일하나 선택하고 정렬돼서 크기변경됨.(가져온 타일 표시하려) -> 크기 크게, 선을 Yellow로
						nowPlayer.myTile.get(i).setPreferredSize(new Dimension(60,100));
						nowPlayer.myTile.get(i).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
						break;
					}
				}
				stage=6;
				confirmLabel1.setText("컴퓨터가 타일을 하나 골라갔습니다.");			
				confirmLabel2.setText("확인을 누르면 내 타일을 선택합니다.");

				textArea.append("\n컴퓨터가 타일을 하나 골라갔습니다.");
				textArea.append("\n확인을 누르면 내 타일을 선택합니다.");
			}

			else if(stage == 6) {															// (stage == 6) : 컴퓨터가 내 타일중 하나 선택하고, 그 타일 표시함.
				confirm.setVisible(false);
				try {
					selectMyTile();															// selectMyTile() : 컴퓨터가 내 타일중 하나 선택하게 함.
				} catch (InterruptedException e1) {
				}
				result = getResult();
				for(int j=0;j<player.myTileNumber;j++){										// 플레이어의 타일중에서 selectTile과 같은 타일 찾음
					if(selectTile.number == player.myTile.get(j).number && selectTile.color == player.myTile.get(j).color){
						System.out.println("컴퓨터가 고른 플레이어타일 ="+ result);
						player.myTile.get(j).setPreferredSize(new Dimension(60,100));
						player.myTile.get(j).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
					}
				}

				stage = 7;
				confirmLabel1.setText("컴퓨터가 플레이어 타일의 위치를 선택했습니다."); 
				confirmLabel2.setText("컴퓨터가 정답을 찾습니다.");
			}

			else if(stage == 7) {															// (stage == 7) : 컴퓨터가 플레이어 타일을 맞춤.
				confirm.setVisible(false);
				textArea.append("\n컴퓨터가 플레이어 타일의 위치를 선택했습니다.");
				textArea.append("\n컴퓨터가 정답을 찾습니다.");

				//맞으면
				for(int i=0;i<computer.myTileNumber;i++){
					System.out.println("computer : "+computer.myTile.get(i).number+", recent : "+recentTile.number+", select : "+selectTile.number);
					textArea.append("\n컴퓨터가 말한 숫자 : " + result+", 진짜 타일 숫자 : "+selectTile.number);
					if( result == selectTile.number){
						textArea.append("\n컴퓨터가 맞았습니다.");
						confirmLabel1.setText("컴퓨터가 맞았습니다. 진행상태 창을 확인해주세요.");			// 진행상태 창에 내용 확인
						confirmLabel2.setText("컴퓨터가 계속 고를지 선택중입니다.");
						confirmButton.setText("확인");

						System.out.println("맞았습니다.");
						int showNumber=0;																	// showNumber = 각 플레이어의 

						for(int j=0;j<player.myTileNumber;j++){
							if(player.myTile.get(j).number == selectTile.number && player.myTile.get(j).color == selectTile.color){ // 선택한 번호와 일치하는 컴퓨터의 번호를 찾음
								player.myTile.get(j).show = 1;																		// 컴퓨터가 맞췄을 경우 플레이어 타일을 보이게함.
								//player.myTile.get(j).setBorder(null);
								//player.myTile.get(j).setEnabled(false);
							}	
							if(player.myTile.get(j).show == 1){
								showNumber++;																// 보여지는것 하나 늘려줌
								System.out.println("player의 보여진 타일 = "+showNumber);
							}
						}
						if(showNumber == player.myTileNumber)
							winner = computer;

						stage = 8;																			// 컴퓨터가 플레이어 타일 맞추면 (stage = 8)로.
					}

					//틀리면
					else if(result != selectTile.number){ 
						System.out.println("컴퓨터가 틀렸습니다.");

						for(int j=0;j<player.myTileNumber;j++){																			// recentTile과 컴퓨터 번호 비교
							if(computer.myTile.get(j).number == recentTile.number && computer.myTile.get(j).color == recentTile.color){ // 선택한 번호와 일치하는 컴퓨터의 번호를 찾음
								System.out.println("computer : "+computer.myTile.get(j).number+", recent : "+recentTile.number+", select : "+selectTile.number);
								computer.myTile.get(j).show = 1;																		// 컴퓨터가 틀렸을 경우 컴퓨터의 번호를 보이게함.
								//computer.myTile.get(j).setBorder(null);
								//computer.myTile.get(j).setEnabled(false);
								break;
							}	
						}
						confirmLabel1.setText("컴퓨터가 틀렸습니다.");
						confirmLabel2.setText("플레이어 턴으로 돌아갑니다.");
						textArea.append("\n컴퓨터가 틀렸습니다.");
						textArea.append("\n플레이어 턴으로 돌아갑니다.");
						textArea.append("\n플레이어는 가운데 보이는 타일중 한개를 선택해주세요.");

						confirmButton.setText("확인");
						confirmButtonPanel.remove(cancleButton);

						stage = 2;																			// 컴퓨터가 플레이어 타일 틀리면 (stage = 2)로.

						confirm.setVisible(true);
						break;
					}
					confirm.setVisible(true);
					rePaint(null);
				}

				//타일이 13개가 되면
				if(computer.myTileNumber == 13 || player.myTileNumber == 13){								// 타일이 13개가 되면 최대 갯수가 되었으므로 정산해서 종료.
					int cN=0;
					int pN=0;
					confirm.setVisible(false);
					confirmLabel1.setText("남은 타일이 없거나 패널이 가득 찼습니다.");
					for(int i=0;i<computer.myTileNumber;i++){
						if(computer.myTile.get(i).show == 1)
							cN++;
					}
					for(int i=0;i<player.myTileNumber;i++){
						if(player.myTile.get(i).show == 1)
							pN++;
					}
					if(cN < pN){
						confirmLabel2.setText("플레이어가 보여진 타일이 많으므로 컴퓨터의 승리입니다.");
						winner = computer;
						textArea.append("컴퓨터 승리!!");
					}
					else{
						confirmLabel2.setText("컴퓨터가 보여진 타일이 많으므로 플레이어의 승리입니다.");
						winner = player;
						textArea.append("플레이어 승리!!");
					}
					stage = 100;																			// 종료할때는 (stage=100)으로 감.
					confirm.setVisible(true);
				}
			}



			else if(stage == 8) {																			// (stage == 8) : 컴퓨터가 플레이어타일 맞췄을때 오는곳
				confirm.setVisible(false);

				if(selectComputerReplay() == 1){															// selectComputerReplay() : 더 할지 말지 고름, 랜덤으로 0,1값받음.
					stage = 5;																				// 1리턴시 컴퓨터가 다시 진행하려고 (stage = 5)로 바꿔줌.
					textArea.append("\n컴퓨터가 다시 진행합니다.");

					try {
						Thread.sleep(1000);
						confirm.setVisible(false);
						confirm.setVisible(true);
					} catch (InterruptedException e1) {
					}

					confirmLabel1.setText("컴퓨터가 다시 진행합니다.");
					confirmLabel2.setText("컴퓨터가 가운데 타일중 하나를 고릅니다.");
				}
				else{																						// 0리턴시 종료하고, 플레이어턴으로 돌아가려고 (stage = 2)로 바꿔줌.
					confirmLabel1.setText("컴퓨터가 턴을 마칩니다.");
					confirmLabel2.setText("플레이어는 가운데 타일중 하나를 고르세요.");
					textArea.append("\n컴퓨터가 턴을 마칩니다.");
					textArea.append("\n플레이어 턴이 되었습니다.");
					stage = 2;																				// (stage = 2)로 변경							
				}

				confirm.setVisible(true);
			}

			//위너 결정시										
			else if(stage == 100){																			// (stage == 100) : 위너가 결정되었을때 오는 곳, 다 종료시켜줌
				System.exit(0);
			}
		}// 여기까지 확인버튼 눌렀을때 단계별로 작동하는것.

		// 캔슬버튼(확인창에 들어가는것.)
		if(e.getSource() == cancleButton){
			stage = 2;																						// (stage = 2)로 변경(플레이어턴으로)

			confirmButtonPanel.remove(cancleButton);														// 버튼 패널에서 cancle버튼 없애주고
			confirmLabel1.setText("플레이어가 다시 고릅니다.");												
			confirmLabel2.setText("플레이어는 가운데 보이는 타일중 하나를 골라주세요");
			textArea.append("\n플레이어가 다시 고릅니다.");
			textArea.append("\n플레이어는 가운데 보이는 타일중 하나를 골라주세요");

			confirmButton.setText("확인");
			confirm.setVisible(false);
			confirm.setVisible(true);
		}

		//Text 패널에 있는 버튼 눌렀을때("입력"눌렀을 때)
		if(e.getSource() == selectTileNumberButton){
			textArea.append("\n플레이어가 고른 숫자 : " + selectTileNumber.getText()+", 진짜 타일 숫자 : " + selectTile.number);
			//맞으면
			if( Integer.parseInt(selectTileNumber.getText()) == selectTile.number){													// 답이 맞으면 한번 더할지 고르기
				System.out.println("플레이어가 맞았습니다.");
				textArea.append("\n플레이어가 맞았습니다.");
				textArea.append("\n더 플레이 할지 골라주세요.(예 : 턴 종료, 아니오 : 한번 더 플레이)");
				int showNumber=0;
				if(nowPlayer == player){																							// 현재 플레이어가 플레이어라면
					for(int i=0;i<computer.myTileNumber;i++){													
						if(computer.myTile.get(i).number == selectTile.number && computer.myTile.get(i).color == selectTile.color){ // select 번호와 일치하는 컴퓨터의 번호를 찾음
							computer.myTile.get(i).show = 1;																		// 답이 맞았으니 컴퓨터타일을 표시해줘야함.
							//computer.myTile.get(i).setBackground(Color.red);
						}	
						if(computer.myTile.get(i).show == 1){
							showNumber++;					//보여지는것 하나 늘려줌
							System.out.println("computer의 보여진 타일 = "+showNumber);
						}
					}
					if(showNumber == computer.myTileNumber)
						winner = player;
				}
				else{																												// 현재 플레이어가 컴퓨터라면
					for(int i=0;i<player.myTileNumber;i++){																			
						if(player.myTile.get(i).number == selectTile.number && player.myTile.get(i).color == selectTile.color){ 	// select 번호와 일치하는 플레이어의 번호를 찾음
							player.myTile.get(i).show = 1;																			// 답이 맞았으니 플레이어타일을 표시해줘야함.
						}	
						if(player.myTile.get(i).show == 1){
							showNumber++;					//보여지는것 하나 늘려줌
							System.out.println("player의 보여진 타일 = "+showNumber);
						}
					}
					if(showNumber == player.myTileNumber)
						winner = computer;
				}
				//더 할지말지 고름.

			}
			//틀리면
			else{ 																													// 플레이어가 말한 답이 틀렸을때
				System.out.println("플레이어가 틀렸습니다.");
				textArea.append("\n플레이어가 틀렸습니다.");
				textArea.append("\n컴퓨터의 턴으로 넘어갑니다.");
				if(nowPlayer == player){																							// 현재 플레이어가 플레이어라면
					for(int i=0;i<player.myTileNumber;i++){
						if(player.myTile.get(i).number == recentTile.number && player.myTile.get(i).color == recentTile.color){ 	// 선택한 번호와 일치하는플레이어의 번호를 찾음
							player.myTile.get(i).show = 1;																			// 답이 틀렸으니 플레이어타일을 표시해줘야함.
						}	
					}
				}
				else{																												// 현재 플레이어가 컴퓨터라면
					for(int i=0;i<player.myTileNumber;i++){
						if(computer.myTile.get(i).number == recentTile.number && computer.myTile.get(i).color == recentTile.color){ // 선택한 번호와 일치하는 컴퓨터의 번호를 찾음
							computer.myTile.get(i).show = 1;																		// 답이 틀렸으니 컴퓨터타일을 표시해줘야함.
						}	
					}
				}
				confirmLabel1.setText("플레이어가 틀렸습니다.");
				confirmLabel2.setText("컴퓨터의 턴으로 넘어갑니다.");
				confirmButton.setText("확인");
				confirmButtonPanel.remove(cancleButton);																			// 아니오 버튼 삭제함.
			}

			//위너가 결정되면
			if(winner != null){
				System.out.println("승자결정!");
				confirmLabel1.setText("승자가 결정되었습니다.");
				if(winner == computer)
					confirmLabel2.setText("승자 : 컴퓨터");
				else
					confirmLabel2.setText("승자 : 플레이어");

				confirmButtonPanel.remove(cancleButton);
				confirmWholePanel.remove(confirmButtonPanel);
				JPanel p = new JPanel();
				p.add(new JLabel("종료하시겠습니까?"));
				confirmWholePanel.add(p);
				confirmWholePanel.add(confirmButtonPanel);
				stage = 100;																										// winner결정시 (stage = 100)으로.
			}

			//뽑은 타일이 13개가 되면
			if(computer.myTileNumber == 13 || player.myTileNumber == 13){
				int cN=0;
				int pN=0;
				confirm.setVisible(false);
				confirmLabel1.setText("남은 타일이 없거나 패널이 가득 찼습니다.");
				for(int i=0;i<computer.myTileNumber;i++){
					if(computer.myTile.get(i).show == 1)
						cN++;
				}
				for(int i=0;i<player.myTileNumber;i++){
					if(player.myTile.get(i).show == 1)
						pN++;
				}
				if(cN < pN){
					confirmLabel2.setText("플레이어가 보여진 타일이 많으므로 컴퓨터의 승리입니다.");
					textArea.append("\n컴퓨터 승리!!");
				}
				else{
					confirmLabel2.setText("컴퓨터가 보여진 타일이 많으므로 플레이어의 승리입니다.");
					textArea.append("\n플레이어 승리!!");
				}
				stage = 100;																										// 패널에 가득차서 winner결정시 (stage = 100)으로.
				confirm.setVisible(true);
			}
			rePaint(null);
			confirm.setVisible(true);
		}
	}

	// 컴퓨터 타일 4개 or 1개 고르는 함수
	void setComputerTile() throws InterruptedException {
		ArrayList<tile> computerTile = new ArrayList<tile>(); 		// 타일 배열리스트 선언

		// 컴퓨터가 고를수 있는 타일이 뭔지 알아냄.(center에 남아있는 타일 뭔지 알아냄.)
		for(int i=0;i<26;i++){										
			if(tile[i].centerShow == 0) 							// centerShow == 1이면 누가 가져간거.
				computerTile.add(tile[i]);
		}
		System.out.print("컴퓨터가 고를 수 있는 타일 : ");
		for(tile t : computerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//컴퓨터가 고를 수 있는 타일 랜덤으로 바꿈.
		Collections.shuffle(computerTile);

		System.out.print("컴퓨터가 고를 수 있는 타일 : ");
		for(tile t : computerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//컴퓨터가 앞에서부터 타일 4개 고르게 함.
		if(stage != 5){
			int i;
			for(i=0;i<4;i++){
				tile t = computerTile.get(i);
				System.out.println("comTile : "+t.number+", 색 : "+t.color);
				for(int j=0;j<26;j++){													// 전체 타일중에서 t와 같은 타일 찾음
					if(t== tile[j]){ 
						tile[j].centerShow = 1;
						nowPlayer.myTile.add(new tile(tile[j].color, tile[j].number));  
						nowPlayer.myTileNumber++;										// 컴퓨터 타일 갯수 늘리고 빈자리에 넣어줌.
						System.out.println("컴퓨터타일 갯수: "+nowPlayer.myTileNumber+", 타일Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

						rePaint(tile[j]);												
						Thread.sleep(1000);
					}
				}
			}
			if(i == 4)
				textArea.append("\n컴퓨터가 4개의 타일을 골랐습니다.");
		}

		//컴퓨터가 1개 고르게함
		else if(stage == 5) { 															// (stage == 5)라면(컴퓨터가 하나 골라야하는 턴)
			tile t = computerTile.get(0);												// t = 컴퓨터가 하나 고른것.
			for(int j=0;j<26;j++){														// 전체 타일중에서 t와 같은 타일 찾음
				if(t== tile[j]){
					tile[j].centerShow = 1;
					tile[j].setSize(0,0);												// 그 타일을 안보이게(center에서 없애주고)

					nowPlayer.myTile.add(new tile(tile[j].color, tile[j].number)); 		// 컴퓨터 타일 갯수 늘리고 빈자리에 넣어줌.
					recentTile = tile[j];
					nowPlayer.myTileNumber++;

					System.out.println("컴퓨터타일 갯수: "+nowPlayer.myTileNumber+", 타일Number = "+nowPlayer.myTile.get(nowPlayer.myTileNumber-1).number);

					if(computer.myTileNumber == 13 || player.myTileNumber == 13){		// 한개 골랐을때 패널이 가득 찼다면 승리자 정함.
						int cN=0;
						int pN=0;
						confirm.setVisible(false);
						confirmLabel1.setText("남은 타일이 없거나 패널이 가득 찼습니다.");
						for(int i=0;i<computer.myTileNumber;i++){
							if(computer.myTile.get(i).show == 1)
								cN++;
						}
						for(int i=0;i<player.myTileNumber;i++){
							if(player.myTile.get(i).show == 1)
								pN++;
						}
						if(cN < pN){
							confirmLabel2.setText("플레이어가 보여진 타일이 많으므로 컴퓨터의 승리입니다.");
							textArea.append("컴퓨터 승리!!");
						}
						else{
							confirmLabel2.setText("컴퓨터가 보여진 타일이 많으므로 플레이어의 승리입니다.");
							textArea.append("플레이어 승리!!");
						}
						stage = 100;													// 승리자 골라지면 (stage = 100) 으로.
					}
					rePaint(tile[j]);

					Thread.sleep(1000);
					confirm.setVisible(true);
				}
			}

		}
	}


	//whole타일 다시 그림
	void rePaint(tile t) {
		//computerPanel 다시 그림
		computerPanel.removeAll();															// computerPanel의 모든것 지워줌.
		for(int j=computer.myTileNumber-1;j>=0;j--){										// 전체 컴퓨터가 가진 타일에서 검색
			if(computer.myTile.get(j).show == 1){											// 컴퓨터 타일중 보여진거라면 
				//if(computer.myTile.get(j).show == 1 && recentTile.color == player.myTile.get(j).color)
				//				computer.myTile.get(j).setBackground(Color.red);
				computer.myTile.get(j).setText(""+computer.myTile.get(j).number);			// 버튼숫자 보이게 해주고,
				computer.myTile.get(j).setFont(new Font("Courier",Font.BOLD,13));			// 그 숫자 폰트 바꿔줌.
				//computer.myTile.get(j).setEnabled(false);
				//computer.myTile.get(j).setBorder(null);
				computer.myTile.get(j).setForeground(Color.red);							// 그 숫자 색 바꿔줌(red로)
			}
			if(computer.myTile.get(j).getWidth() == 60){									// 컴퓨터가 가진 타일중 (width == 60) select나 recent타일이여서 표시된채이면,
				computer.myTile.get(j).setBorder(tile[0].getBorder());						// 초기 border로 바꿔줌.(표시된것 없애줌)
			}
			computer.myTile.get(j).setPreferredSize(new Dimension(50,90)); 					// 컴퓨터가 가진 전체타일 크기를 원래대로 되돌림.
			computer.myTile.get(j).setBorder(tile[0].getBorder());

			computer.myTile.get(j).addActionListener(this);
			computerPanel.add(computer.myTile.get(j));
		}
		computerOutterPanel.add(computerPanel);
		wholePanel.add(computerOutterPanel);

		//centerPanel 다시 그림	
		if(t != null)																		// rePaint호출했을때 넘어온 타일을 안보이게해줌.
			t.setSize(0,0);		

		centerPanel.removeAll();															// center패널의 모든것 지워줌.
		for(int i=0;i<26;i++){
			centerPanel.add(tile[i]);
			if(tile[i].centerShow == 1){													// tile[](center타일)로 검색, centerShow = 1이면 누가 가져간것.
				if(stage==3){
					tile[i].setPreferredSize(new Dimension(0,0));
				}
				else{
					tile[i].setPreferredSize(new Dimension(50,90));
				}
			}
		}
		centerOutterPanel.add(centerPanel);


		//playStatePanel 다시 그림
		if(nowPlayer == computer)															// 현재 플레이어가 누군지 확인하고 표시해줌.
			playState.setText("현재 player = 컴퓨터");
		else
			playState.setText("현재 player = 나");


		//playerPanel 다시 그림
		playerPanel.removeAll();															// player 패널의 모든것 지워줌.
		for(int j=0;j<player.myTileNumber;j++){												// 플레이어 패널에서 검색해
			player.myTile.get(j).setText(""+player.myTile.get(j).number);					// 플레이어 패널은 다 보여져야함(내가보는화면이기때문)
			player.myTile.get(j).setFont(new Font("굴림",Font.BOLD,13));

			if(player.myTile.get(j).show == 1&& recentTile.color == player.myTile.get(j).color){	// (show == 1) 판에 내려놓은 타일이면
				//				player.myTile.get(j).setBackground(Color.red);
				//player.myTile.get(j).setBorder(null);
				//player.myTile.get(j).setEnabled(false);
				player.myTile.get(j).setForeground(Color.red);										// 빨간색으로 표시해줌
			}
			if(player.myTile.get(j).getWidth() == 60){												// 크기 변경되었던것 border 원래대로 돌려줌
				player.myTile.get(j).setBorder(tile[0].getBorder());
			}

			player.myTile.get(j).setPreferredSize(new Dimension(50,90)); 
			playerPanel.add(player.myTile.get(j));

		}
		playerOutterPanel.add(playerPanel);


		cardLayout.show(getContentPane(), "wholePanel");
	}

	//center타일 만들고 세팅하는 함수(타일 생성, 타일 셔플, 보여줌.)
	void setCenterTile() {
		//0~12=black, 13~25=white;

		//타일을 일단 만든다.
		for(int i=0;i<26;i++){
			if(i<=12)									// black타일(0~12)
				tile[i] = new tile(0,i);
			else if(i>12)								// white타일 (13~25)
				tile[i] = new tile(1,(i-13));
		}

		//타일을 셔플한다.
		shuffle(0,12);									// black타일 셔플
		shuffle(13,25);									// white타일 셔플

		//타일에 ActionListener 추가하고, centerPanel에 타일을 추가한다.
		for(int i=0;i<26;i++){
			tile[i].addActionListener(this);
			if(i<=12)
				tile[i].setBounds(5+i*55,15,50,90);
			else
				tile[i].setBounds(5+(i-13)*55,110,50,90);
			centerPanel.add(tile[i]);
		}
	}

	//center타일 셔플하는 함수
	void shuffle(int start, int end) {								// 0~12까지 셔플, 13~25까지 셔플
		ArrayList<Integer> tileIndex = new ArrayList<Integer>();	// Collections.shuffle()사용하려고 배열리스트 사용
		for(int i=start;i<=end;i++){								// tileIndex = white,black타일 인덱스번호 들어옴.
			tileIndex.add(i);
		}

		Collections.shuffle(tileIndex);								// 인덱스번호 셔플

		// tileIndex (0,1), (2,3), ... , (11,12) 변환.(i,i+1)을 변환
		// tileIndex (13,14), (15,16), ... , (24,25) 변환
		for(int i=0;i<12;i = i+2){ 
			tile temp;
			temp = tile[(int) tileIndex.get(i)];
			tile[(int) tileIndex.get(i)] = new tile((int) tile[tileIndex.get(i+1)].color, tile[(int) tileIndex.get(i+1)].number);	// 그타일 색과 번호가진 새타일생성으로 받아옴.
			tile[(int) tileIndex.get(i+1)] = new tile(temp.color, temp.number);
		}	
	}

	//컴퓨터, 플레이어 타일 솔팅하는 함수
	void sort() {
		ArrayList<Integer> computerTileIndex = new ArrayList<Integer>();	// Collections.shuffle()사용하려고 배열리스트 사용
		ArrayList<tile> computerList = new ArrayList<tile>();

		ArrayList<Integer> playerTileIndex = new ArrayList<Integer>();		// Collections.shuffle()사용하려고 배열리스트 사용
		ArrayList<tile> playerList = new ArrayList<tile>();

		System.out.print("컴퓨터타일 :");
		for(tile t : computer.myTile){
			System.out.print(t.number+" ");
			computerTileIndex.add(t.number);								// 컴퓨터타일이 갖고있는 타일을 넣어줌.
		}

		System.out.print(", 플레이어타일 :");
		for(tile t : player.myTile){
			System.out.print(t.number+" ");
			playerTileIndex.add(t.number);									// 플레이어타일이 갖고있는 타일을 넣어줌.
		}

		Collections.sort(computerTileIndex);								// 그 컴퓨터타일의 인덱스번호를 sort하고,
		Collections.sort(playerTileIndex);									// 그 플레이어타일의 인덱스번호를 sort하고

		System.out.print("\nsort 컴퓨터타일 :");
		for(int n : computerTileIndex){ 									// computerTileIndex의 원소에 대해 i번째 번호라면(8,8,0,1)
			for(int i=0;i<computer.myTileNumber;i++){						// computer.myTile.get(i)로 8을 숫자로 갖고있는 타일 찾음
				if(computer.myTile.get(i).number == n){						// computer가 가지고 있는 타일의 번호가 tileIndex의 i번째 번호와 같으면
					computerList.add(computer.myTile.get(i));				// list에 그 번호를 가진 타일을 추가해줌.
					computer.myTile.remove(i);								// i번째 숫자 없애줌.
					break;
				}
			}
			System.out.print(n+" ");
		}

		System.out.print(", sort 플레이어타일 :");
		for(int n : playerTileIndex){ 										// tileIndex의 원소에 대해 i번째 번호라면
			for(int i=0;i<player.myTileNumber;i++){	
				if(player.myTile.get(i).number == n){						// player가 가지고 있는 타일의 번호가 tileIndex의 j번째 번호와 같으면
					playerList.add(player.myTile.get(i));					// list에 그 번호를 가진 타일을 추가해줌.
					player.myTile.remove(i);
					break;
				}
			}
			System.out.print(n+" ");
		}

		//색정렬
		for(int i=0;i<computer.myTileNumber-1;i++){
			if(computerList.get(i).number == computerList.get(i+1).number)			// 0,1비교해 타일번호 같으면
				if(computerList.get(i+1).color == 0){ 								// 0 = black, 1번째가 검은색이면
					tile t = computerList.get(i);
					computerList.set(i,computerList.get(i+1)); 						// 0번째 자리에 1번 넣음
					computerList.set(i+1, t);										// 1번째 자리에 t(0번 복사) 넣음
					System.out.println("\n\t"+computerList.get(i).number+"번 타일과 "+computerList.get(i+1).number+"번 타일을 변경했음");
				}

		}

		for(int i=0;i<player.myTileNumber-1;i++){
			if(playerList.get(i).number == playerList.get(i+1).number)				// 0,1비교해 타일번호 같은 것중에
				if(playerList.get(i+1).color == 0){ 								// (color == 0 == black) 1번째가 검은색이면
					tile t = playerList.get(i);										// t = 0번 복사할 타일
					playerList.set(i,playerList.get(i+1)); 							// 0번째 자리에 1번 넣음
					playerList.set(i+1, t);											// 1번째 자리에 t(0번 복사) 넣음
					System.out.println("\n\t"+playerList.get(i).number+"번 타일과 "+playerList.get(i+1).number+"번 타일을 변경했음");
				}

		}

		computer.myTile = computerList;												// 컴퓨터 타일을 정렬된 타일로 바꿔준다.
		player.myTile = playerList;													// 플레이어 타일을 정렬된 타일로 바꿔준다.

		System.out.print("\n컴퓨터타일 :");
		for(tile t : computer.myTile){
			System.out.print(t.number+" ");
			computerTileIndex.add(t.number);										// 그 타일의 번호를 넣어줌.(컴터타일이 갖고있는 타일을 넣어줌.)
		}

		System.out.print(", 플레이어타일 :");
		for(tile t : player.myTile){
			System.out.print(t.number+" ");
			playerTileIndex.add(t.number);											// 그 타일의 번호를 넣어줌.(컴터타일이 갖고있는 타일을 넣어줌.)
		}
		System.out.println("");
		rePaint(null);
	}

	// 컴퓨터가 상대편 타일 고르게 해줌.(selectTile 고르는것)
	void selectMyTile() throws InterruptedException {
		ArrayList<tile> playerTile = new ArrayList<tile>(); 						// 타일 배열리스트 선언

		//컴퓨터가 고를수 있는 타일이 뭔지 알아냄.(center에 남아있는 타일 뭔지 알아냄.)
		for(int i=0;i<player.myTileNumber;i++){
			if(player.myTile.get(i).show == 0)										// show = 보여진것. 보여지지않은것중에 골라야함.
				playerTile.add(player.myTile.get(i));								// 플레이어 타일 있는거 다 받아옴.
		}
		System.out.print("컴퓨터가 고를 수 있는 타일 : ");
		for(tile t : playerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");

		//컴퓨터가 고를 수 있는 타일 랜덤으로 바꿈.
		Collections.shuffle(playerTile);

		System.out.print("컴퓨터가 고를 수 있는 타일 : ");
		for(tile t : playerTile){
			System.out.print(t.number + " ");
		}
		System.out.println("");
		tile t = playerTile.get(0);													// 어차피 랜덤이라서 그냥 랜덤으로 셔플된것중 가장 처음꺼 받아옴

		for(int j=0;j<player.myTileNumber;j++){										// 플레이어의 타일중에서 t와 같은 타일 찾음
			if(t.number == player.myTile.get(j).number && t.color == player.myTile.get(j).color){
				selectTile = player.myTile.get(j);									// selectTile = 그 t타일과 같은것 넣어줌.
				System.out.println("컴퓨터 : 플레이어타일 고름 ="+player.myTile.get(j).number);

				Thread.sleep(1000);

				player.myTile.get(j).setSize(60,100);								// 컴퓨터가 내타일 골랐으니 표시해줌.
				player.myTile.get(j).setBorder(new MatteBorder(5,5,5,5,Color.yellow));
				confirm.setVisible(true);
			}
		}
	}

	//컴퓨터가 플레이어의 답이 뭔지 맞추는것. : 전체 고를수 있는 숫자중 컴퓨터가 가진것, 플레이어가 가진것중 보여진것 빼줌.
	int getResult() {
		ArrayList<tile> t = new ArrayList<tile>();				// t = 컴퓨터가 고른 플레이어 타일과 같은색인 타일 전부 받음.(중앙에 배열되었던 순서대로)
		ArrayList<tile> t2 = new ArrayList<tile>();				// t2 = 그중 컴퓨터가 가진것과 이미 보여진것 없앰.
		ArrayList<tile> t3 = new ArrayList<tile>();				// t3 = 플레이어 전체 타일 넣어줌.
		int tN = 0; 											// tN = t에 들어간 타일갯수세는것.
		int result = 0; 										// result = 값 반환할꺼 담김
		int start = -1; 										// start = -1함.(0이면 findAtoB에 넘길때 오류남)
		int end = player.myTileNumber-1;  						// end = 플레이어가 가진 마지막 인덱스
		int selectTileIndex = 0; 								// selectTielIndex = selectTile의 인덱스 번호 받아옴.
		//초기화
		for(int i=0;i<26;i++){
			if(selectTile.color == tile[i].color){ 				// 컴퓨터가 고른 플레이어 타일과 같은색인 타일을
				t.add(tile[i]);									// t와 t2에 넣어줌.
				t2.add(tile[i]);
				tN++;
			}
		}	
		for(int i=0;i<player.myTileNumber;i++){
			t3.add(player.myTile.get(i));						// t3 = 플레이어 타일 전체 넣어줌
		}

		//t2에 컴퓨터가 가진것, 플레이어가 가진것 뺌
		for(int i=0;i<t.size();i++){							// i = t의 크기만큼 반복
			System.out.println("tN = "+tN+", computer타일 수 :"+computer.myTile.size());

			//컴퓨터가 가지고있는건 뺌.
			for(int j=0;j<computer.myTileNumber;j++){ 			// j = 컴퓨터 크기만큼 반복
				if(t.get(i).number == computer.myTile.get(j).number && t.get(i).color == computer.myTile.get(j).color){
					System.out.println("t ="+t.get(i).number+", computer="+computer.myTile.get(j).number);
					t2.set(i, null);							// t2의 i번째자리중 컴퓨터가 가지고있는것에 null 넣어줌.
					tN--;
				}
				else
					System.out.println("363323t ="+t.get(i).number+", computer="+computer.myTile.get(j).number);
			}

			//플레이어가 가진것중에 보여진것 뺌.
			for(int j=0;j<player.myTileNumber;j++){				// j = 컴퓨터 크기만큼 반복
				if(player.myTile.get(j).show ==1 && t.get(i).number == player.myTile.get(j).number && t.get(i).color == player.myTile.get(j).color){
					System.out.println("t ="+t.get(i).number+", player="+player.myTile.get(j).number);
					t2.set(i, null);							// t2의 i번째중 플레이어가 가진것중 보여진것인곳 null넣어줌.
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

		//랜덤으로 섞음
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

		//selectTile의 인덱스 번호 받아옴.
		for(int i=0;i<player.myTileNumber;i++){
			if(selectTile.number == player.myTile.get(i).number && selectTile.color == player.myTile.get(i).color){
				selectTileIndex = i;																					// 플레이어 타일중 컴퓨터가 선택한 타일의 인덱스 받아옴.
				System.out.println("selectTile 숫자 : "+selectTile.number + ", 색 : "+selectTile.color +"인덱스 : "+selectTileIndex);
				break;
			}
		}

		//start, end 위치 찾음.
		for(int i=selectTileIndex;i>=0;i--){		 			// 0 2 * * * 8
			if(t3.get(i).show == 1){ 							// 선택한 타일이 보여진데까지 검색
				start = i; 			 							// selectTile들어잇는곳이 3이면 1이 될 것.
				break;	
			}
		}
		for(int i=selectTileIndex;i<player.myTileNumber;i++){
			if(t3.get(i).show == 1){ 							// 보이는데까지 검색
				end = i; 										// 5가 될 것
				break;
			}
		}
		System.out.println("start = "+ start + ", end = "+ end);

		if(start == -1)											// start가 초기값이면 0으로 바꿔줌
			start = 0;
		else													// start가 0이게되면 t3의 0번째 '숫자' 로 바꿔줌.
			start = t3.get(start).number;

		while(true){
			result = findAtoB(start, t3.get(end).number);											// A~B 사이값 찾는 함수, result값이 t2에 있는지 확인
			for(int i=0;i<t2.size();i++){															// t2 = 컴퓨터가 고를수 있는 모든 숫자
				if(t2.get(i) != null && t2.get(i).number == result){								// null값이 아닌 원소중에 result와 같은 원소 있는지 확인					
					System.out.print("result확인 : t2값 = "+t2.get(i).number+", result = "+result);	// 없으면 다시 함수부름.(컴퓨터가 고를수 있는 숫자 나올때까지)
					return result; 																	// 랜덤중에 result확인하면 넘김
				}
			}
		}

	}

	//A와 B사이 값 반환하는 함수.(A~B사이 나올 값)										// 3 * * 5 이런상태일때
	int findAtoB(int start, int end){ 													// 3~5면 3,4,5중 하나 골라서 반환
		int result;					  													// end-start + 1= 5-3 + 1 = 2 + 1 = 3(3개중 하나 나와야함.)
		Random ran = new Random();

		System.out.println("컴퓨터가 고를 수 있는 수 = start : "+start+", end : "+end);
		result = ran.nextInt(end-start+1); 												// 0,1,2중 하나 나옴. = +start 해주면 3,4,5 됨!
		result += start;
		System.out.println("컴퓨터가 고른 수 : "+result);
		return result;
	}

	//컴퓨터가 다시할지 랜덤으로 선택하는 함수.
	int selectComputerReplay() { 														// 1 리턴시 컴퓨터가 다시함, 0리턴시 다시안함
		Random ran = new Random();
		int result = ran.nextInt(2); 													// 0, 1 중에 하나 나옴.
		System.out.println("컴퓨터가 다시할지 : " + result);
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