
/*Frog.java
 *Jessica Chen
 *makes a Frog object that includes features such as movement, animation, and interaction with other objects (other features listed below)
 *the Frog object is what the player will control
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*; 
import java.applet.*;
import java.util.ArrayList; 


public class Frog {
    private double x,y;					//x and y coordinate of frog   
    private int frame,delay,dir;        //used for animation (current frame frog is on, delay in animation, direction frog is moving)
    private boolean moving;				//checks if frog is moving
    private Image lifePic,noLifePic, happyFrogPic;	//picture of heart, picture of empty heart, picure of happy frog (when frog has made it to other side)
    public static int lives = 3;					//lives of frog
	private Rectangle []goals = new Rectangle[5];   //array of Rectangles(represents home)
	private Rectangle []walls = new Rectangle[6];   //array of walls (borders between the homes)
	private boolean []finished = new boolean[5];	//boolean array that keeps track of which homes are taken up
	private Rectangle goal,wall;					//goal and wall variable that will be put into goals and walls arrays respectively
	private boolean isDead = true;					//assumes that the frog is dead
	private boolean drown;							//determines if the frog is drowning
	private int safeCount;							//number of safe frogs
	
	private Image []pics1;							//Left frog hops				
	private Image []pics2;							//Right
	private Image []pics3;							//Up
	private Image []pics4;							//Down		
	private int timeRemaining;
	
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3,WAIT = 5; //constants (WAIT determines how long before next frame is displayed)


    public Frog(double xx, double yy) { //takes in an x and y val
    	x = xx;
    	y = yy;
    	dir = UP;  					//default direction
    	frame = 0;					//frame starts at 0
    	delay = 0;					//delay starts at 0
    	moving = false;				//frog is not moving (default)
    	setSafeCount(0);			//0 frogs saved
    	setFinished(false);			//0 homes taken
    	//loading images
    	happyFrogPic = new ImageIcon("frog/happyFrog.png").getImage(); 
    	lifePic = new ImageIcon("lives/heart.png").getImage();
    	noLifePic = new ImageIcon("lives/noHeart.png").getImage();
 		
 		//loading LEFT RIGHT UP DOWN frog images
    	pics1 = new Image[2];
    	pics2 = new Image[2];
    	pics3 = new Image[2];
    	pics4 = new Image[2];
		for(int i = 0; i<2; i++){
			pics1[i] = new ImageIcon("frog"+"/"+"frog"+i+".png").getImage();
		}
		for(int i = 2; i<4; i++){
			pics2[i-2] = new ImageIcon("frog"+"/"+"frog"+i+".png").getImage();
		}
		for(int i = 4; i<6; i++){
			pics3[i-4] = new ImageIcon("frog"+"/"+"frog"+i+".png").getImage();
		}
		for(int i = 6; i<8; i++){
			pics4[i-6] = new ImageIcon("frog"+"/"+"frog"+i+".png").getImage();
		}
		//makes the goals (homes)
		for (int i=0; i<goals.length; i++){ //makes the home rectangle a little bigger than the space shown so that if the frog is a little off it can still go home
			goal =  new Rectangle(53+i*(126+GamePanel.ROADLENGTH),GamePanel.ROADLENGTH,GamePanel.ROADLENGTH+20,GamePanel.ROADLENGTH);
			goals[i] = goal;
		}
		//makes the borders between the homes
		for (int i=0; i<walls.length; i++){
			wall = new Rectangle(-63+i*(126+GamePanel.ROADLENGTH),GamePanel.ROADLENGTH,126,GamePanel.ROADLENGTH);
			walls[i] = wall;
		}
    }
	public void setFinished(boolean done){ //sets the array finished (represents which frogs are home)
		for (int i=0; i<finished.length;i++){
			finished[i] = done;
		}
	}
	
    public void draw(Graphics g){ //draws 
    	for (int i=0; i<3; i++){
    		g.drawImage(noLifePic, (int) GamePanel.ROADLENGTH*(11+i),0, null); //draws 3 empty hearts
    	}
    	for (int i=0; i<lives; i++){
    		g.drawImage(lifePic, (int) GamePanel.ROADLENGTH*(11+i),0, null); //draws filled hearts based on number of lives
    	}
		if(dir == LEFT){ 
			g.drawImage(pics1[frame], (int)x, (int)y, null);				//when frog hops left animates it based on the picture that's the current frame
		}
		else if (dir == RIGHT){												//similar to going LEFT
			g.drawImage(pics2[frame], (int)x, (int)y, null);
		}
		else if (dir == UP){
			g.drawImage(pics3[frame], (int)x, (int)y, null);
		}
		else if (dir == DOWN){
			g.drawImage(pics4[frame], (int)x, (int)y,null);
		}
		
 		for (int i=0; i<goals.length; i++){		//draws the happy frogs									
 			if (finished[i] == true){			//checks if the home at position i is empty or not
 				g.drawImage(happyFrogPic,(int) goals[i].getX()+10, (int) goals[i].getY(), null); //if it is not empty (draw a happy frog where the goal (home is)
 				//goals was shifted so we have to shift the X position back
 			}
 		}
    }
    public void setDrown(boolean unsafe){ //sets drown
    	drown = unsafe;
    }
    
    public boolean getDrown(){           //gets drown
    	return drown;
    }

	public void toGoal(){
		//checks if frog has reached goal
		for (int i = 0; i<goals.length; i++){ //goes through each goal
			if (goals[i].contains(getRect())){//checks if the goal contains the frog
				if (finished[i] == true){	  //if there already is a happy frog there the frog isDead
					isDead = true;
				}
				else{
					finished[i] = true;      //fills in that position as true
					safeCount+=1;			 //increases safeCount
					isDead = false;          //frog is not dead
					reset();				//reset frog
				}
			}
		}
	}
    
    public void hitTopWall(){			//checks if frog hits the top wall
		for (int i = 0; i<walls.length; i++){//goes through an array of wall Rectangles
			if (walls[i].contains(getRect())){ //if the wall contains the frog the frog will die
				reset();						//reset frog
			}
		}
    }
    public void startMove(int key){		//starts frog movement
		if(!moving){					//if frog is not moving
			if(key == KeyEvent.VK_RIGHT){	//if the RIGHT key is pressed set the direction to RIGHT

				dir = RIGHT;
			}
			else if(key == KeyEvent.VK_LEFT){ //similar to previous one

				dir = LEFT;
			}
			else if(key == KeyEvent.VK_UP){

				dir = UP;
			}
			else if(key == KeyEvent.VK_DOWN){

				dir = DOWN;
			}
			else{						//if another key is hit return
				return;
			}
			moving = true;				//frog is now moving
		}
    }
	public void move(){
		//moves frog (first condition checks direction...second condition checks boundary)
		if(moving){						//if frog is currently moving
			if (dir == LEFT && x>=0){  //if frog is moving left and not going to move out of bounds
				x -= 7;					//move 7 pixels left 
			}
			if (dir == RIGHT && x<= GamePanel.WIDTH-GamePanel.ROADLENGTH){ //similar to previous
				x += 7;
			}
			if (dir == UP && y > GamePanel.ROADLENGTH){
				y -= 7;
			}
			if (dir == DOWN && y < GamePanel.HEIGHT-GamePanel.ROADLENGTH*2){
				y += 7;
			}
			
			//paces the animation so it isn't too fast
			delay += 1; //increase delay
			if(delay % WAIT == 0){ //when delay is divisible by 5 increase the frame 
				frame++;
				if(frame == pics1.length){ //when frame is the same length as the pics array (frog has made movement animation)
					frame = 0;				//reset to 0
					moving = false;         //set moving to false
				}
			}

		}
	}

    public void getHit(Obstacle []cars){
    	//takes in an array of obstacles and checks if frog hits any of them
		for (int i=0; i<cars.length; i++){ //goes through each item in the array
			if (getRect().intersects(cars[i].getObRect()) && !moving){ //if the frog intersects with the car and the frog is at its rest position
				reset(); 												//reset frog
			}
		}
    }
    
    // isNotOnObject
    public boolean isNotOnObject(Rectangle ob){
    	//checks if frog is not on an object
		if (getRect().intersects(ob)){  //if frog is on an object
			return false;
		}
		return true;
    }
    
    public void rideObstacle(Obstacle ob, Rectangle sameOb){
    	//takes in an Obstacle and a Rectangle object with same coordinates,width, and height of obstacle
    	if (isNotOnObject(sameOb) == false){ //if frog is on an object
    		drown = false;				//frog is not drowning
			if(ob.getDirec() == GamePanel.LEFT && !moving){ //if frog is moving left
				x-=ob.getSpeed();				//frog will move left at the speed of the object and frog is still
			}
			else if(ob.getDirec() == GamePanel.RIGHT && !moving){ //similar to left but moving right
				x+=ob.getSpeed();
			}
    	}
		if (x<0||x>GamePanel.WIDTH-GamePanel.ROADLENGTH){			//if frog moves out of bounds reset its position
			reset();
		}

    }
    public void noTime(){  //if there is no time left reset frog
    	if (GamePanel.timerLength == 0){ 
    		reset();
    	}
    }
    public int getTimerLength(){ //gets the length of the time remaining when the frog resets (used for the calculating score)
    	return timeRemaining;
    }
    
    public double getX(){			//gets x-coordinate of frog
    	return x;
    }
    
    public double getY(){			//gets y-coordinate of frog
    	return y;
    }

    public void setSafeCount(int num){ //sets the safeCount to num 
    	safeCount = num;
    }
    public int getSafeCount(){        //gets safeCount
    	return safeCount;
    }
    
    public Rectangle getRect(){			//gets a Rectangle object of the frog
    	return new Rectangle((int) x, (int) y, GamePanel.ROADLENGTH, GamePanel.ROADLENGTH);
    }
    
    public int getLives(){				//gets the amount of lives remaining
    	return lives;
    }
    public void reset(){				//resets (when frog dies or goes home or level is done)
    	if (isDead == true){			//if the frog is dead take a life
    		lives-=1;
    	}
    	isDead = true;					//if frog is not dead assume it is dead again
    	
    	//resets x and y coordinates
    	x = GamePanel.WIDTH/2;			
		y = GamePanel.HEIGHT-GamePanel.ROADLENGTH*2;
		dir = UP;						//resets default direction
		timeRemaining = GamePanel.timerLength;		//resets timeRemaining
		GamePanel.timerLength = 800;				//resets timerLength
		frame = 0;	//resets frame and delay
    	delay = 0;					
    }

    
}