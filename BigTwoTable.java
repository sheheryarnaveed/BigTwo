import java.awt.*;
import java.util.Collections;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.text.DefaultCaret;

/**
 * This is the interface for the Big Two card game table (GUI)
 * The avatars used in GUI are taken from the source (https://www.flaticon.com/packs/cartoon-avatars-3#)
 * The card backImage is taken from the source(https://www.pinterest.com/realitythyme/playing-cards-back-of-deck/)
 * Sounds in GUI are taken from the sources(www.findsounds.com and www.wavsource.com)
 * 
 * @author 3035493672
 */

public class BigTwoTable implements CardGameTable{
	
	//declaring the private variables
	private BigTwoClient game; //a card game associates with this table
	private boolean[] selected; //a boolean array indicating which cards are being selected.
	private int activePlayer; //an integer specifying the index of the active player
	private JFrame frame; //the main window of the application.
	private JPanel bigTwoPanel; //a panel for showing the cards of each player and the cards played on the table
	private JButton playButton; //a “Play” button for the active player to play the selected cards.
	private JButton passButton; //a “Pass” button for the active player to pass his/her turn to the next player
	private JTextArea msgArea; //a text area for showing the current game status as well as end of game messages
	private Image[][] cardImages; //a 2D array storing the images for the faces of the cards 
								  // first row: diamonds, second: clubs, third: hearts, fourth: spades

	private Image cardBackImage; //an image for the backs of the cards
	private Image[] avatars; //an array storing the images for the avatars
	private boolean setClickable;
	private JTextArea chatArea; //a text area for whowing the chat messages of the players in the game
	private JTextField chatTypingArea;
	Clip Vclip; //the victory sound clip

	
	
	/**
	 * Constructor for the BigTwoTable class. It sets the menu, images, avatars for setting up the GUI of the game
	 *  
	 * @param game - the card game for which the GUI needs to be created
	 */
	BigTwoTable(CardGame game){
		this.game = (BigTwoClient)game;
		selected = new boolean[13]; //selected cards
		
		loadCardImages();
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setResizable(false);
		
		MenuSetup();
		
		bigTwoPanel = new BigTwoPanel();
		bigTwoPanel.setPreferredSize(new Dimension(700,800));
	    frame.add(bigTwoPanel, BorderLayout.WEST);
	    
	    JPanel messages = new JPanel();
	    messages.setLayout(new BoxLayout(messages, BoxLayout.PAGE_AXIS));
	    
	    Font font = new Font("LucidaSans", Font.BOLD, 14);
	    
	    
	    msgArea = new JTextArea(41,23);
	    msgArea.setEnabled(false);
	    msgArea.setFont(font);
	    

	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setViewportView(msgArea);
	    messages.add(scrollPane);
	   
	    

	    chatArea = new JTextArea(21,24);
	    chatArea.setEnabled(false);;
	    DefaultCaret caretChat = (DefaultCaret) chatArea.getCaret();
	    caretChat.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JScrollPane scrollPaneChat = new JScrollPane();
	    scrollPaneChat.setViewportView(chatArea);
	    messages.add(scrollPaneChat);
	    
	    JPanel chat = new JPanel();
	    chat.setLayout(new FlowLayout());
	    chat.add(new JLabel("Message:"));
	    chatTypingArea = new JTextField();
	    chatTypingArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
	    chatTypingArea.addActionListener(new EnterListener());
	    chatTypingArea.setPreferredSize(new Dimension( 200, 24 ));
	    chat.add(chatTypingArea);
	    messages.add(chat);
	    
	    
	    frame.add(messages, BorderLayout.EAST);
	    
	    JPanel buttons = new JPanel();
	    playButton = new JButton("Play");
	    playButton.addActionListener(new PlayButtonListener());
	    passButton = new JButton("Pass");
	    passButton.addActionListener(new PassButtonListener());
	    buttons.add(playButton);
	    buttons.add(passButton);
	    frame.add(buttons, BorderLayout.SOUTH);
	    frame.setLocation(500, 100);
	    frame.setVisible(true);
	    setVictoryNoise();
	    
	    getClientDetails();
	    getServerDetails();
	    
	    
	    enable();
	    
	}
	
	
	private void getServerDetails(){
		JTextField ip = new JTextField(10);
		JTextField port = new JTextField(4);
		JCheckBox Default = new JCheckBox("Choose default values");
		Default.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1){
					ip.setText("127.0.0.1");
					ip.setEditable(false);
					port.setText("2396");
					port.setEditable(false);
				}else{
					ip.setText("");
					ip.setEditable(true);
					port.setText("");
					port.setEditable(true);
				}
			}
			
		});
		
		
		JPanel myPanel = new JPanel(new GridBagLayout());
		GridBagConstraints dial = new GridBagConstraints();
		dial.gridx = 0; 
		dial.gridy = 0; 
		dial.anchor = GridBagConstraints.WEST;
		myPanel.add(new JLabel("Server IP:"), dial);
		dial.gridy = 1;
		myPanel.add(new JLabel("Server Port:"), dial);
		dial.gridx = 1; 
		dial.gridy = 0;
		myPanel.add(ip, dial);
		dial.gridy = 1;
 		myPanel.add(port, dial);
 		dial.gridx = 0; 
 		dial.gridy = 2;
 		myPanel.add(Default, dial);
		
 		
 		
 		int result = JOptionPane.showConfirmDialog(null, myPanel, "Select server IP and port", JOptionPane.DEFAULT_OPTION);


		if(result == JOptionPane.OK_OPTION && ip.getText().equals(null) && port.getText().equals(null)){	
			System.out.println("Server IP: "+ip.getText());
			game.setServerIP(ip.getText());
			System.out.println("Server Port: "+port.getText());
			game.setServerPort(Integer.parseInt(port.getText()));
		 }else{
			 game.setServerIP("127.0.0.1");
			 game.setServerPort(2396);
		 }
	}
	
	
	private void getClientDetails(){ 
 		String name = JOptionPane.showInputDialog("Enter Your Name: ");
 		while(name == null) {
 			this.printMsg("Please enter your name!\n");
 			name = JOptionPane.showInputDialog("Enter Your Name: ");
 		}
		game.setPlayerName(name); 
	}
	
	/**
	 * a method for setting the index of the active player (i.e., the current player)
	 * It overrides the CardGameTable interface's setActivePlayer method
	 * 
	 * @param 
	 * 		activePlayer - an integer value representing the index of the current player
	 */
	@Override
	public void setActivePlayer(int activePlayer) {
		// TODO Auto-generated method stub
		this.activePlayer = activePlayer;
	}
	
	
	/**
	 * Returns an array of indices of the cards selected.
	 * Overrides the getSelected method of the CardGameTable interface.
	 * 
	 * @see CardGameTable#getSelected()
	 * @return an array of indices of the cards selected
	 */
	@Override
	public int[] getSelected() {
		// TODO Auto-generated method stub
		//finding the number of selected Cards
		ArrayList<Integer> selectedCards = new ArrayList<Integer>();
		for(int i=0; i<selected.length; i++) {
			if(selected[i]) {
				selectedCards.add(i);
			}
		}
		
		//creating an array and putting the indices of those selected Cards in that array
		if(selectedCards.size() >0) {
			int[] cardS = new int[selectedCards.size()];
			for(int i=0; i<selectedCards.size(); i++) {
				cardS[i] = selectedCards.get(i);
			}
			return cardS;
		}
		else {
			return null;
		}
	}
	
	
	
	/** 
	 * Resets the list of selected cards by making it equal to an empty list
	 * Overrides the resetSelected method of the CardGameTable interface
	 * 
	 * @see CardGameTable#resetSelected()
	 */
	@Override
	public void resetSelected() {
		// TODO Auto-generated method stub
		selected = new boolean[13];
		this.repaint();
	}
	
	
	/**
	 * Prints the cards in a pretty way into the message area
	 * @param cards - the CardList cards that needs to be printed
	 */
	public void print(CardList cards) {
		boolean printFront = true;
		boolean printIndex = false;
		if (cards.size() > 0) {
			for (int i = 0; i < cards.size(); i++) {
				String string = "";
				if (printIndex) {
					string = i + " ";
				}
				if (printFront) {
					string = string + "[" + cards.getCard(i) + "]";
				} else {
					string = string + "[  ]";
				}
				if (i % 13 != 0) {
					string = " " + string;
				}
				this.printMsg(string);
			}
		} else {
			this.printMsg("[Empty]");
		}
	}
	
	
	/** 
	 * It repaints the whole frame
	 * Overrides the repaint method of the CardGameTable interface. 
	 * @see CardGameTable#repaint()
	 */
	@Override
	public void repaint() {
		frame.repaint();
	}
	
	/**
	 * prints the given text to the specified message area of the BigTwo card game table
	 * 
	 * @see CardGameTable#printMsg(java.lang.String)
	 * @param text
	 * 		the message that needs to be printed to GUI's message area
	 */
	@Override
	public void printMsg(String text) {
		msgArea.append(text+"\n");
	}
	


	
	/**
	 * It erases/clears the message area of the BigTwo card game table
	 * Overrides the clearMsgArea method of the CardGameTable interface
	 * 
	 * @see CardGameTable#clearMsgArea()
	 */
	@Override
	public void clearMsgArea() {
		// TODO Auto-generated method stub
		msgArea.setText("");
	}
	
	
	/**
	 * Prints the message to the chat message text area
	 * 
	 * @param msg- the string to be printed to the chat message text area
	 */
	public void printChatMsg(String message) {
		chatArea.append(message + "\n");
	}
	
	/**
	 * Clears the chat message area
	 */
	public void clearChatMsgArea() {
		chatArea.setText("");
	}
	
	
	/**
	 * It resets BigTwo Game's GUI.
	 * Overrides the reset method of the CardGameTable interface.
	 * 
	 * @see CardGameTable#reset()
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
		resetSelected();
		clearMsgArea();
		enable();
	}
	
	/**
	 * It enables the user interactions with the GUI.
	 * Overrides the enable method of the CardGameTable interface. 
	 * @see CardGameTable#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		setClickable = true;
	}

	/**
	 * It disables user interactions with the GUI.
	 * Overrides disable method of the CardGameTable interface. 
	 * @see CardGameTable#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		setClickable = false;
	}
	
	/**
	 * It makes a noise invoking the next player to take the turn
	 */
	public void nextTurn() {
		// TODO Auto-generated method stub
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("./src/sounds/blip.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	/**
	 * It makes a sound for invalid hands played
	 */
	public void InvalidTurn() {
		// TODO Auto-generated method stub
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("./src/sounds/bloop.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	

	public void setVictoryNoise() {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("./src/sounds/fireworks.wav"));
			Vclip = AudioSystem.getClip();
			Vclip.open(ais);
			
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	/**
	 * It makes a victory noise and this function is called when the player wins a game
	 */
	public void VictoryNoise() {
		// TODO Auto-generated method stub
		try {
			Vclip.start();
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	/**
	 * It prints the end of game message and shows a popup for game end
	 */
	public void printEndGame() {
		VictoryNoise();
		printMsg("Game ends");
		String message = "";
		for(int i =0; i<game.getPlayerList().size(); i++) {
			CardGamePlayer temp = game.getPlayerList().get(i);
			if(temp.getNumOfCards()==0) {
				message += game.getPlayerList().get(i).getName() + " wins the game.\n";
			}
			else {
				message += game.getPlayerList().get(i).getName() + " has " + temp.getNumOfCards() + " cards in hand.\n";
			}
		}
		JOptionPane.showMessageDialog(null, message);
	}
	
	
	
	/**
	 * An inner class that is used go setup the panel for the BIgTwo Game's GUI
	 *  
	 * @author 3035493672
	 */
	
class BigTwoPanel extends JPanel implements MouseListener{
		
		/**
		 * BigTwoPanel default constructor which adds the Mouse Listener and sets background of the card table.
		 */
		BigTwoPanel(){
			this.addMouseListener(this);
	        setBackground(new Color(99,99,40)); 
		}

		/**
		 * Draws the avatars, text and cards on card table
		 * @param g Provided by system to allow drawing
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.WHITE);

			for (int i = 0; i < game.getNumOfPlayers(); i++) {
				if (i == game.getCurrentIdx()) {
					g.setColor(new Color(255,239,213));
					Font font = new Font("LucidaSans", Font.BOLD, 12);
					g.setFont(font);
					if(i == game.getPlayerID())
						g.drawString(game.getPlayerList().get(i).getName() + "(Your turn)", 15, 15+i*130);
					else 
						g.drawString(game.getPlayerList().get(i).getName(), 15, 15+i*130);
					
					font = new Font("LucidaSans", Font.PLAIN, 12);
					g.setFont(font);	
					g.setColor(Color.WHITE);
				}
				else {
					g.setColor(Color.WHITE);
					g.drawString(game.getPlayerList().get(i).getName(), 15, 15+i*130);
				}
				g.drawImage(avatars[i], 2, 22+i*130, this);
				
				for (int j = 0; j < game.getPlayerList().get(i).getNumOfCards(); j++) {
					if (i == game.getPlayerID()) {
						int s = game.getPlayerList().get(i).getCardsInHand().getCard(j).getSuit();
						int r = game.getPlayerList().get(i).getCardsInHand().getCard(j).getRank();
						
						if (selected[j] == true) {
							g.drawImage(cardImages[s][r], 110+j*cardImages[0][0].getWidth(this)/2, 25+i*130-10, this);
						} else {
							g.drawImage(cardImages[s][r], 110+j*cardImages[0][0].getWidth(this)/2, 25+i*130, this);
						}
					} else {
						g.drawImage(cardBackImage, 110+j*cardImages[0][0].getWidth(this)/2, 25+i*130, this);
					}
				}
			}
			if (game.getHandsOnTable().size()-1 > -1) {
				
				Hand lasthand = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
				g.drawString("Played by "+lasthand.getPlayer().getName(), 5, 25+4*130 - 10);
				for (int i = 0; i < lasthand.size(); i++) {
					int s = lasthand.getCard(i).getSuit();
					int r = lasthand.getCard(i).getRank();
					g.drawImage(cardImages[s][r], 5+i*20, 25+4*130, this);
				}
			}
			if(game.endOfGame() && game.isConnected() && game.getCurrentIdx() != -1) {
				
				g.setColor(new Color(120,160,200));
				g.setFont(new Font("TimesRoman", Font.BOLD, 70));
				g.drawString(game.getPlayerList().get(game.getCurrentIdx()).getName() + " Wins!", 202, 300);
				Font font = new Font("LucidaSans", Font.BOLD, 12);
				g.setFont(font);
				g.setColor(new Color(255,239,213));
			}
		}
		
		
		

		/**
		 * Defines what happens when mouse is clicked on the card table. 
		 * Only allows clicks on cards of active player.
		 * Once cards are selected, the JPanel is repainted to reflect changes.
		 * 
		 * @param e Mouse event created when Mouse Clicked
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (setClickable && activePlayer == ((BigTwoClient) game).getCurrentIdx()) {
				
				int width = cardImages[0][0].getWidth(this);
				int height = cardImages[0][0].getHeight(this);
				int num = game.getPlayerList().get(activePlayer).getNumOfCards();
				
				int minX = 110;
				int maxX = 110+(width/2)*num+width/2;
				int minY = 25+activePlayer*130-10; 
				int maxY = 25+activePlayer*130+height;
				if (e.getX() >= minX && e.getX() <= maxX && e.getY() >= minY && e.getY() <= maxY) {	
					int card = (int)Math.ceil((e.getX()-110)/(width/2));
					card = card / num > 0 ? num - 1 : card;
					if (selected[card]) {
						if (e.getY() > (maxY - 10) && e.getX() < (110+(width/2)*card + width/2) && selected[card-1] == false) {//for dealing with the part of the card just below the selected card
							if (card != 0) {
								card = card - 1;
							}
							selected[card] = true;
						} else if (e.getY() < (maxY - 10)){// to unselect the already selected card
							selected[card] = false;
						}
					} else if (e.getY() > (minY + 10)){ //selecting a card that is not selected
						selected[card] = true;
					} else if (selected[card - 1] && e.getX() < (110+(width/2)*card + width/2)) {//unselect an already selected card if the upper part(that comes on top of the right next unselected card i.e. selected[card]) of the selected card is pressed
						
						selected[card-1] = false;
					}
					this.repaint();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
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
	}
	

	
	
	/**
	 * An inner class which implements play button's ActionListener.
	 * 
	 * @author 3035493672
	 */
	class PlayButtonListener implements ActionListener{

		/**
		 * If the set of card(s) selected is/are valid and play button is pressed then it plays those cards. In  case none of the cards are selected then it prompts the user to select the cards
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(activePlayer == game.getPlayerID()){
				if(getSelected() != null) {
					game.makeMove(activePlayer, getSelected());
				}else {		
					printMsg("You must select cards that you need to play!!!"); 
				}	
			}
			else {
				printMsg("Wait for your turn!");
			}
		}
	}
	
	/**
	 * An inner class which implements pass button's ActionListener.
	 * 
	 * @author 3035493672
	 */
	class PassButtonListener implements ActionListener{
		/**
		 * It passes the player's turn to to the next player if passing is a valid move for that player
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(activePlayer == game.getPlayerID()){
				game.makeMove(activePlayer, null);
			}
			else {
				printMsg("Wait for your turn!");
			}
		}
		
	}
	
	
	/* 
	 * Load the images from the 'avatars' directory(which s a store of multiple avatars) and assigns those randomly to the four players
	 */
	private void loadAvatars(){
		avatars = new Image[game.getNumOfPlayers()]; // an array to hold the avatars of the four players
		
		File dir = new File("./src/avatars/");
		File [] images = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().toLowerCase().endsWith(".png");
			}
		});

//		ArrayList<Integer> randomImaging = new ArrayList<Integer>();
//		for(int i=0; i<images.length; i++) {
//			randomImaging.add(i);
//		} 
//		
//		Collections.shuffle(randomImaging);
		
		for(int i=0; i<game.getNumOfPlayers(); i++) {
			avatars[i] = new ImageIcon(images[i].toString()).getImage();

		}
		
	}
	
	/*
	 * Creates and sets the menu bar in the top left corner of the window
	 */
	private void MenuSetup(){
		JMenuBar Menu = new JMenuBar();
		Font innerfont = new Font("Verdana", Font.BOLD, 18);
		Font outerfont = new Font("Verdana", Font.LAYOUT_LEFT_TO_RIGHT, 18);
		JMenu StartMenu = new JMenu("Game");
		StartMenu.setFont(outerfont);
		
		JMenuItem quit = new JMenuItem(" Quit", new ImageIcon("./src/images/exit.png"));
		quit.setFont(innerfont);
		quit.setToolTipText("Quit The BigTwo Game");
		quit.addActionListener(new QuitMenuItemListener());
		
		JMenuItem connect = new JMenuItem(" Connect",new ImageIcon("./src/images/restart.png"));
		connect.setToolTipText("Reconnects to The BigTwo Game Server");
		connect.setFont(innerfont);
		connect.addActionListener(new ConnectMenuItemListener());
		
		StartMenu.add(connect);
		StartMenu.add(quit);
		Menu.add(StartMenu);
		frame.setJMenuBar(Menu);
	}
	

	
	/**
	 * An inner class which implements quit menu class's ActionListener.
	 * 
	 * @author 3035493672
	 */
	class QuitMenuItemListener implements ActionListener{

		/**
		 * When clicked, it terminates the application.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
		
	}
	
	
	/**
	 * An inner class that implements the ActionListener interface.
	 * Implements the actionPerformed() method from the ActionListener interface to handle click button event for "Send" button of Chat area 
	 * @author 3035493672
	 */
	class EnterListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			CardGameMessage message = new CardGameMessage(CardGameMessage.MSG, -1, chatTypingArea.getText());
			chatTypingArea.setText("");
			game.sendMessage(message);
		}
	}
	
	
	/**
	 * An inner class which implements restarting menu class's ActionListener.
	 * 
	 * @author 3035493672
	 */
	class ConnectMenuItemListener implements ActionListener{
		/**
		 * It restarts the game when the restart menu is clicked. It loads the whole game again!
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(Vclip.isRunning()) {
				Vclip.stop();
			}
			if(!game.isConnected()){
				getServerDetails();
				game.makeConnection();
				reset();
			}else{
				JOptionPane.showMessageDialog(null, "You are already connected to the game!");
			}
			
		}
		
	}
	
	
	/*
	 * It load the image files and puts the into the image array in the sequence. 
	 */
	private void loadCardImages(){
		
		loadAvatars();
		
		cardImages = new Image[4][13];
		File dir = new File("./src/cards");
		for(int i=0; i<4;i++){
			String s;
			if(i==0) s="d.gif";
			else if(i==1) s="c.gif";
			else if(i==2) s ="h.gif";
			else s="s.gif";
			
			File[] images = dir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.isFile() && file.getName().toLowerCase().endsWith(s);
				}
			});
			
			//putting cards into the image array in sequence i.e. 2,A,....K
			cardImages[i][0]= new ImageIcon(images[8].toString()).getImage();
			for(int j=1; j<9;j++){
				cardImages[i][j] = new ImageIcon(images[j-1].toString()).getImage();
			}
			cardImages[i][9]=new ImageIcon(images[12].toString()).getImage();
			cardImages[i][10]=new ImageIcon(images[9].toString()).getImage();
			cardImages[i][11]=new ImageIcon(images[11].toString()).getImage();
			cardImages[i][12]=new ImageIcon(images[10].toString()).getImage();
		}
		
		cardBackImage = new ImageIcon((new File("./src/cards/b.gif")).toString()).getImage();
		
	}
	
	
}
