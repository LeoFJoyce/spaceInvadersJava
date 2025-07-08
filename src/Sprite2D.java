
import java.awt.*;

public class Sprite2D {

    //member data
    protected double x,y;//stores my x and y coordinate
    protected double xspeed=0;
    protected Image myImage;//stores image for object
    protected Image frame1;
    protected Image frame2;
    protected static int winWidth;

//constructor
public Sprite2D (Image i){

    myImage = i;
}

//public interface

public void setPosition (double xx,double yy){//method to set position for sprite2D objects
    x=xx;
    y=yy;


}


public void setXSpeed (double dx) {
    xspeed=dx;//sets if xspeed is 0, 5, or -5

}
public void setFrame (int frame){
    if (frame == 1){
        myImage = frame1;
    }
    if(frame == -1){
        myImage = frame2;
    }
}

public void paint (Graphics graphics){

    graphics.drawImage(myImage,(int)x,(int)y,null);//what paints myImage on my Jframe


}
    public static void setWinWidth(int w) {
        winWidth = w;
    }

}