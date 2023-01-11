/*Frogger.java
 *Jessica Chen
 *recreates a simple Frogger game with two levels
 *Level 1: cars in the first half, logs and turtles in the second half
 *Level 2: speed is increased, logs, turtles, and crocodiles in the second half
 *Game objective: have the frog go to each of its 5 homes safely and as fast as possible while avoiding obstacles on land and avoiding water on the lake
 *different several methods are used to that will be explained throughout the code to produce features such as a timer and a score keeper
 *****SCORING******
 *10 points for every NEW step taken (resets each level)
 *50 points for every frog home
 *10 points for every half second remaining when a frog arrives home
 *1000 points for all 5 frogs arriving home*/
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.applet.*;
import java.io.*;

//Frogger class
public class Frogger extends JFrame{
	GamePanel game;
	
		
    public Frogger() {
		super("Basic Graphics");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new GamePanel();
		add(game);
		pack();
		setVisible(true);
    }
	
    public static void main(String[] arguments) {
		Frogger frame = new Frogger();	//new Frogger object	
    }
}

// All of my my game logic goes here      // interface
class GamePanel extends JPanel implements MouseListener, ActionListener, KeyListener{
	//Fields
	private boolean []keys; 
	private Frog frog; 								//frog object
	private Obstacle []cars = new Obstacle[13];     //array of land obstacles
	private Obstacle []water = new Obstacle[15];	//array of water obstacles
	private Obstacle obstacle;						//Obstacle object
	
	private String screen = "intro";				//keeps track of what screen the player is on (default is intro screen)
	
	private Image introPic;							//intro picture
	private Image rulesPic;							//rules picture
	private Image overPic;							//game over picture
	private Image completePic;						//road picture (purple horizontal road across the screen one at the beginning, one in the middle, one at the top);
	private Image roadPic;							//road pieces (the homes for the frog are in between these);
	private Image roadPiecePic;
	
	private AudioClip hopSound;						//frog hopping sound

	private Font comicFnt;							//variable for font
	
	private int timePassed;                      //tracks time passed
	private int pointTracker = 0;					//variable to make sure points are not recounted
	
	private boolean keyReady = true;				//false when key is pressed, true when key is released
	
	private String []obPic1 = {"croc/crocOpen0.png","log/log1.png","log/log1.png"}; //pictures of water obstacles in level 2
	private String []obPic2 = {"croc/crocOpen1.png","log/log2.png","log/log2.png"}; //pictures of water obstacles in level 2;
	
	private MP3 froggerSong;						//theme song
	
	
	public static int timerLength = 800;			//length of timer (40 seconds (each second being 20 pixels long on the visible timer))
	
	private int level = 1;							//keeps track of what level it is
	private int finalScore;							//final score of player when game ends
	private int total = 0;							//keeps track of score
	
	
	public static final int WIDTH = 980, HEIGHT = 1050,ROADLENGTH = 70; //constants (width of screen, height of screen, length of one section of the road)
	
	public static int cY = HEIGHT-ROADLENGTH*2; //cY is the current highest Y position the frog has reached in one level of the game (used later when keeping track of score)
	public static final int LEFT = 1;			//LEFT direction (used for obstacle direction)
	public static final int RIGHT = 2;			//RIGHT direction (used for obstacle direction)
	
	Timer myTimer;								//timer
	
	public GamePanel(){
		setPreferredSize(new Dimension(WIDTH, HEIGHT)); //set screen size
		addMouseListener(this);
		addKeyListener(this);
		
		comicFnt  = new Font("Comic Sans MS",  Font.BOLD, 30);  //Font used for the score
			
		//LOADING IMAGES
		introPic = new ImageIcon("screen/intro.png").getImage(); 
		rulesPic = new ImageIcon("screen/rules.png").getImage();
		overPic = new ImageIcon("screen/gameover.png").getImage();
		completePic = new ImageIcon("screen/gamecomplete.png").getImage();
		roadPiecePic = new ImageIcon("roads/roadPiece.png").getImage();
		roadPic = new ImageIcon("roads/road.png").getImage();
		
		//MUSIC
        froggerSong = new MP3("sound/froggersong.mp3");
		froggerSong.play(); //plays song
		
		//HOPPING SOUND
		hopSound = loadSound("sound/froghop.wav");
		
		keys = new boolean[KeyEvent.KEY_LAST+1]; //array of booleans that has the length of the last number in the range of ids used for key events
		
		frog = new Frog(WIDTH/2,HEIGHT-ROADLENGTH*2); //creates a Frog object

		if (level == 1){ //calls level1 method
			level1();
		}
		if (level == 2){ //calls level2 method
			level1();
		}

		myTimer = new Timer(20, this); //every 20 miliseconds
		setFocusable(true);
		requestFocus();	
 	}

 	public void level1(){
 		//creates the land and water obstacles for level 1
 		makeObstacle(cars,HEIGHT-ROADLENGTH*3,ROADLENGTH,RIGHT,0.5,"land",obPic1,obPic2);
		makeObstacle(water,ROADLENGTH*6,ROADLENGTH,LEFT,0.5,"water",obPic1,obPic2);
 	}
 	
 	public void level2(){
 		//creates the land and water obstacles for level 2
 		makeObstacle(cars,HEIGHT-ROADLENGTH*3,ROADLENGTH,RIGHT,1,"land",obPic1,obPic2);
		makeObstacle(water,ROADLENGTH*6,ROADLENGTH,LEFT,1,"water",obPic1,obPic2);
 	}
 	
 	public void makeObstacle(Obstacle []c,int yDist, int w, int direc,double speed,String place,String []obPic1, String []obPic2){
 		//takes in an empty array and fills it with the obstacles for that level
 		//yDist is the the y-coordinate of the object, w is width of the object, speed (self explanatory), place (land or water),obPic1 and obPic 2 (pictures file names for level 2)
 		double delay1 = Math.random()*(800-500+1)+500; //making 2 (random) delays so that not all of the obstacles start at the same place
 		double delay2 = Math.random()*(800-500+1)+500;
 		if (place.equals("land")){ //makes land obstacles and puts it in the array
			for (int i=1; i<3; i++){	//x-coordinate of each obstacle the 450 helps space out the obstacles                          
				obstacle = new Obstacle(delay1-(ROADLENGTH*(i-1))-((450)*(i-1)),yDist,w,ROADLENGTH,direc,speed+2,"car/car0.png");
											
				c[i-1] = obstacle;
			}
			for (int i=3; i<6; i++){		
				obstacle = new Obstacle((WIDTH+(ROADLENGTH*(i-3)))+(300*(i-3)),yDist-ROADLENGTH,w,ROADLENGTH,direc-1,speed,"car/car1.png");
				c[i-1] = obstacle;
			}
			for (int i=6; i<9; i++){
				obstacle = new Obstacle(delay2-(ROADLENGTH*(i-6))-(300*(i-6)),yDist-ROADLENGTH*2,w,ROADLENGTH,direc,speed+1.5,"car/car2.png");
				c[i-1] = obstacle;
			}
			for (int i=9; i<11; i++){ //making trucks (double the width of a car)
				obstacle = new Obstacle(((WIDTH-delay2)*(i-9))+(300+(200)*(i-9)),yDist-ROADLENGTH*3,w*2,ROADLENGTH,direc-1,speed+0.5,"car/car3.png");
				c[i-1] = obstacle;
			}
			for (int i=11; i<14; i++){
				obstacle = new Obstacle(0-(ROADLENGTH*(i-12))-(300*(i-12)),yDist-ROADLENGTH*4,w,ROADLENGTH,direc,speed+1,"car/car4.png");
				c[i-1] = obstacle;
			}
			
 		}
 		else if (place.equals("water") && level == 1){ //makes water obstacles for level 1
 			for (int i=1; i<4; i++){
				obstacle = new Obstacle(delay1-(ROADLENGTH*(i-1))-((330)*(i-1)),yDist,w*3,ROADLENGTH,direc,speed+1.5,"turtle/turtle0.png");
				c[i-1] = obstacle;
			}
			for (int i=4; i<7; i++){
				obstacle = new Obstacle((0+(ROADLENGTH*(i-4)))+(350*(i-4)),yDist-ROADLENGTH,w*2,ROADLENGTH,direc+1,speed+2,"log/log0.png");
				c[i-1] = obstacle;
			}
			for (int i=7; i<10; i++){
				obstacle = new Obstacle(delay2-(ROADLENGTH*(i-7))-(300*(i-7)),yDist-ROADLENGTH*2,w*4,ROADLENGTH,direc+1,speed+3,"log/log1.png");
				c[i-1] = obstacle;
			}
			for (int i=10; i<13; i++){
				obstacle = new Obstacle(0+((ROADLENGTH)*(i-10))+(300*(i-10)),yDist-ROADLENGTH*3,w*2,ROADLENGTH,direc,speed+2.7,"turtle/turtle1.png");
				c[i-1] = obstacle;
			}
			for (int i=13; i<16; i++){
				obstacle = new Obstacle(0-(ROADLENGTH*(i-13))-(400*(i-13)),yDist-ROADLENGTH*4,w*3,ROADLENGTH,direc+1,speed+2.5,"log/log2.png");
				c[i-1] = obstacle;

			}
 		}
  		else if (place.equals("water") && level == 2){ //water obstacles for level 2
 			for (int i=1; i<4; i++){
				obstacle = new Obstacle(delay1-(ROADLENGTH*(i-1))-((330)*(i-1)),yDist,w*3,ROADLENGTH,direc,speed+1.5,"turtle/turtle0.png");
				c[i-1] = obstacle;
			}
			for (int i=4; i<7; i++){
				obstacle = new Obstacle((0+(ROADLENGTH*(i-4)))+(350*(i-4)),yDist-ROADLENGTH,w*2,ROADLENGTH,direc+1,speed+2,"log/log0.png");
				c[i-1] = obstacle;
			}
			for (int i=7; i<10; i++){
				obstacle = new Obstacle(delay2+(ROADLENGTH*(i-7))-(400*(i-7)),yDist-ROADLENGTH*2,w*4,ROADLENGTH,direc+1,speed+3,obPic1[i-7]);
				c[i-1] = obstacle;
			}
			for (int i=10; i<13; i++){
				obstacle = new Obstacle(0+((ROADLENGTH)*(i-10))+(300*(i-10)),yDist-ROADLENGTH*3,w*2,ROADLENGTH,direc,speed+2.7,"turtle/turtle1.png");
				c[i-1] = obstacle;
			}
			for (int i=13; i<16; i++){
				obstacle = new Obstacle(0-(ROADLENGTH*(i-13))-(400*(i-13)),yDist-ROADLENGTH*4,w*3,ROADLENGTH,direc+1,speed+2.5,obPic2[i-13]);
				c[i-1] = obstacle;

			}
  		}
 	
 	}
 	
	@Override
	public void actionPerformed(ActionEvent e){
		frog.move(); 						//moves frog
		if (frog.getLives()==0){            //game over if no lives are left 
			screen = "game over";
			resetGame();					//resets game
		}

		timePassed+=1;					//counts the time passed (adds one every 20 miliseconds)
		if (timePassed%25 == 0){		//every half of a second (500 miliseconds)
			timerLength-=10;			//subtract 10 pixels from timer bar
		}

		frog.noTime();					//checks if the frog has run out of time or not
		frog.hitTopWall();				//checks if the frog has hit the top wall (kills it if it does)
		frog.toGoal();					//checks if a frog makes it home
		
		frog.getHit(cars);				//checks if frog has been hit by a car (kills frog if yes)
		
		frog.setDrown(true);			//sets the frog as drowning (frog is assumed to be drowning unless proven safe for the water half)
		if (frog.getY()<ROADLENGTH*7){  //if frog is in the water half
			for (int i=0; i<water.length; i++){ //loops through and calls rideObstacle() if the frog is on an obstacle it will ride on it
				frog.rideObstacle(water[i], water[i].getObRect());
			}
		}
		
		if(frog.getDrown() && frog.getY()<ROADLENGTH*7){  //if the frog is drowning and in water reset the frog
			frog.reset();
		}
		if (frog.getY()%ROADLENGTH == 0 && frog.getY()!=HEIGHT-ROADLENGTH){ //checks score when the y-coordinate of the frog is divisible by the length of a road segment 
		//does not check score when frog is at start position
			score();
		}
		move(cars); //moves the land obstacles
		move(water); //moves the water obstacles
		repaint();

	}

	public void move(Obstacle []c){ //takes in an array of obstacles and moves them
		for (int i=0; i<c.length; i++){
			c[i].moveObstacle();

		}
	}

	@Override
    public void paint(Graphics g){ 	//draws everything
    	g.setFont(comicFnt); 				//sets the font (this case: Comic Sans MS)
		g.setColor(Color.white); 			//sets the current colour to white
    	if(screen == "intro"){				//if the player is on the intro screen draw it
    		g.drawImage(introPic, 0,0,null);
    	}
    	if(screen == "rules"){             //if the player is on the rules screen draw it
    		g.drawImage(rulesPic, 0,0,null);
    	}
    	if (screen == "game over"){       //if the game is over draw game over screen
    		g.drawImage(overPic,0,0,null);
    		g.drawString("FINAL SCORE: "+finalScore, 20, 50);
    		
    	}  
    	if (screen == "finished"){       //draws this when game is complete
    		g.drawImage(completePic,0,0,null);
    		g.drawString("FINAL SCORE: "+finalScore, 20, 50);
    	}
    	//GAME SECTION
   		if(screen == "game"){			
			g.setColor(new Color(24,48,171,255)); //sets colour (background colour of water part)
			g.fillRect(0,0,WIDTH,ROADLENGTH*7);	  //draws the water
			g.setColor(Color.black);			  //sets colour to black (background colour of land part)
			g.fillRect(0,HEIGHT-ROADLENGTH*7,WIDTH,ROADLENGTH*7); //draws the land

			for (int i=0; i<6; i++){								//draws the things separating the frog homes
				g.drawImage(roadPiecePic,-63+i*(126+ROADLENGTH),ROADLENGTH,null);
			}
			//drawing the long purple roads
			g.drawImage(roadPic,0,0,null);       					
			g.drawImage(roadPic,0,HEIGHT-ROADLENGTH*2, null);
			g.drawImage(roadPic,0,HEIGHT-ROADLENGTH*8, null);
			
			//drawing the obstacles
			paintObstacle(cars,g);
			paintObstacle(water,g);
			
			//drawing the frog
			frog.draw(g);
			
			g.setColor(Color.white);				//sets colour to white
			g.drawString("SCORE: "+total, 20, 50);	//draws the score
			g.drawString("TIMER",20,HEIGHT-25);		//draws the word timer
			g.fillRect(130, HEIGHT-ROADLENGTH+20,timerLength,ROADLENGTH-40); //draws the timer
   		}
    }
    
    public void paintObstacle(Obstacle []c,Graphics g){ //goes through an array of obstacles and draws them
		for (int i=0; i<c.length; i++){
			c[i].obDraw(g);
		}
    }

	public void score(){
		//Scoring system
		if (frog.getY()<cY && frog.getY()>ROADLENGTH){ //if current Y position of frog is greater than the highest Y position the frog has been to and Y position 
			total+=10;   								//adds 10 points
			cY = (int)frog.getY();						//sets new highest y-value achieved to current y-value
		}
		if (frog.getSafeCount() == 1 && pointTracker == 0){ //if there is 1 frog safe and pointTracker (makes sure the program doesn't constantly add points) is 0
			pointTracker+=1;								//adds 1 to pointTracker
			total+=50+frog.getTimerLength();				//adds 50 points plus remaining time (which is the number of half seconds remaining *10)
		}
		if (frog.getSafeCount() == 2 && pointTracker == 1){ //similar to one above
			pointTracker+=1;
			total+=50+frog.getTimerLength();
		}
		if (frog.getSafeCount() == 3 && pointTracker == 2){
			pointTracker+=1;
			total+=50+frog.getTimerLength();
		}
		if (frog.getSafeCount() == 4 && pointTracker == 3){
			pointTracker+=1;
			total+=50+frog.getTimerLength();
		}
		if(frog.getSafeCount() == 5 && pointTracker == 4){ //if all 5 frogs make it home
			total = total+1050+frog.getTimerLength();      //add 1000 points and 50 for the frog and calculate points for remaining time
			if (level == 1){							   //if currently on level 1: start next level
				nextLevel();
			}
			else{
				resetGame();								//if on level 2: reset game
			}	
		}
	}
  	public static AudioClip loadSound(String fn){
  		//loads sound files (takes in a file name)
	  	File wavFile = new File(fn); //creates new file
  		AudioClip sound=null;  		 
    	try{
    		sound = Applet.newAudioClip(wavFile.toURL());
    	}
    		catch(Exception e){
    		e.printStackTrace();
    	}
    	return sound; //returns loaded sound as an AudioClip
  	}
	
	public void nextLevel(){ //increases level and resets variables needed for next game
		level = 2; 				//level increases to 2
		level2(); 				//calls level2() which makes obstacles for level 2
		frog.reset();			//resets frog
		frog.lives = 3;			//resets lives of the frog
		frog.setFinished(false);	//sets elements in finished array as false (explained in Frog.java)
		frog.setSafeCount(0);		//resets the number of saved frogs
		cY = HEIGHT-ROADLENGTH*2;  //resets cY
		pointTracker = 0;	       //resets pointTracker;
	}
	public void resetGame(){       //resets game
		finalScore = total;		   //sets final score to total
		frog.reset();			   //resets frog
		if (frog.lives <= 0){      //if frog has no more lives (display game over screen)
			screen = "game over";  
		}
		
		if (frog.lives>0){         //if frog still has lives (means player has won)
			screen = "finished";
		}
		frog.lives = 3;				//resets lives to 3
		level = 1;					//resets level to 1
		level1();					//makes obstacles for level1
		frog.setFinished(false);    //sets elements in finished array as false (explained in Frog.java)
		frog.setSafeCount(0);		//resets the number of saved frogs
		cY = HEIGHT-ROADLENGTH*2;   //resets cY
		pointTracker = 0;			//resets pointTracker
		total = 0;					//resets point total
		timePassed = 0;				//resets timePassed
	}


	// Invoked when the mouse button has been clicked (pressed and released) on a component.  
	@Override
	public void	mousePressed(MouseEvent e){
	}

	public void	mouseClicked(MouseEvent e){}
	public void	mouseEntered(MouseEvent e){}
	public void	mouseExited(MouseEvent e){}
	public void	mouseReleased(MouseEvent e){}
	
	public void	keyPressed(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_SPACE && !keys[KeyEvent.VK_SPACE]){ //when space is pressed the game starts
			screen = "game";		
		}
 
		if(e.getKeyCode()==KeyEvent.VK_R && !keys[KeyEvent.VK_R]){       //when "R" is pressed during intro page on keyboard rules page shows up
			screen = "rules";
		}
		
		myTimer.start(); //start timer
		frog.startMove(e.getKeyCode()); //calls frog.startMove

		keyReady = false;              //sets keyReady to false
		if(e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_DOWN||e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_RIGHT){ //plays hopping sounds 
			hopSound.play();
		}		
	}
	public void	keyReleased(KeyEvent e){
		keyReady = true; //sets keyReady to true
	}	
	public void	keyTyped(KeyEvent e){}
}