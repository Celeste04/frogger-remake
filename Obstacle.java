/*Obstacle.java
 *Jessica Chen
 *a class that makes Obstacle objects that the frog has to dodge or travel on
 *separated into 2 types: land and water
 *frog has to avoid the ones on land 
 *frog has to travel on the ones on water
 */
import java.awt.*;
import javax.swing.*;

public class Obstacle {
	private double x,y; //x and y coordinate of obstacle
	private int w,h;    //width and height
	private int direc;  //direction
	private double speed;  //speed
	private Image obPic;	//picture
	private String picName; //name of picture 
	private int crocW;     //width of crocodile picture

	
    public Obstacle(double xx, double yy, int ww, int hh, int dd, double ss,String pic) { //takes in an x,y, coordinate, width and height, direction, and picture file name
    	picName = pic; 
    	if (isCroc()){ //if the obstacle is a croc
    		w = ww-GamePanel.ROADLENGTH; //make the width smaller (width will be tail and body (no head)) 
    		//does not include head so when player jumps on head (jaws) it will be the same as jumping on water and they will die
    		//image size will use "normal" width not this one
    	}
    	else{
    		w = ww; //other obstacles have normal width
    	}
    	crocW = ww; //crocodile picture will have normal width
    	x = xx;
    	y = yy;
    	h = hh;
    	direc = dd;
    	speed = ss;
    	obPic = new ImageIcon(pic).getImage(); //loads image of obstacle
    }

    public void obDraw(Graphics g){ //draws the object at position x,y
    	g.drawImage(obPic, (int)x, (int)y, null);
 
    }
    
    public boolean isCroc(){
    	//determines if the obstacle is a crocodile by checking if the name of the picture file contains "croc"
    	if (picName.contains("croc")){
    		return true;
    	}
    	return false;
    }
    public void moveObstacle(){ 
    	//moves the obstacle
    	int width; //width of obstacle picture
    	if (isCroc()){
    		width = crocW; //width is crocW (picture length) if the obstacle is a crocodile
    	}
    	else{
    		width = w;     //width of everything else is normal width
    	}
    	if (direc == GamePanel.RIGHT){ //if the obstacle is moving right
	    	x+=speed;					//travels right at whatever the speed is
	    	if (x>GamePanel.WIDTH+width){ //if it goes out of bounds
	    		x=0-width;				//moves the end of the picture to the beginning of the screen
	    	}
    	}
    	else if (direc == GamePanel.LEFT){ //same as going right but moving left (picture will end up on opposite side of the screen)
    		x-=speed;
    		if(x<0-width){
    			x = GamePanel.WIDTH;
    		}
    	}
    }
    
    public Rectangle getObRect(){ //gets a Rectangle object of the Obstacle
    	return new Rectangle((int)x,(int)y,w,h);
    }
    
    public double getX(){ //gets x-coordinate of obstacle
    	return x;
    }
    
    public double getSpeed(){ //gets speed of obstacle
    	return speed;
    }
    
    public int getDirec(){ //gets direction of obstacle
    	return direc;
    }
}