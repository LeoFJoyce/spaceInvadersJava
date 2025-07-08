import java.awt.*;

public class Alien extends Sprite2D {
    protected boolean isVisible=true;//keeps track if the alien is visible
    private static double xSpeed=0; // static: shared by all Alien instances
    public Alien (Image alienFrame1,Image alienFrame2) {
        super(alienFrame1);
        frame1=alienFrame1;
        frame2=alienFrame2;
    }

    public boolean moveEnemy () {//moves alien as well as returning a true or false if it hits the border of the window

       x+=xSpeed;
        if(x<=0 || x>=winWidth-myImage.getWidth(null)){
            return true;
        }
        else {
            return false;
        }


    }
    public void jumpDownwards() {
        y+=20;
    }
    public static void reverseDirection() {
        xSpeed=-xSpeed;

    }
    public static void setFleetXSpeed(double dx) {
        xSpeed=dx;
    }



}


