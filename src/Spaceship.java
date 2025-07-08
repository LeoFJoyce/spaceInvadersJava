import java.awt.*;

public class Spaceship extends Sprite2D{
    public Spaceship(Image image) {
        super(image);

    }



    public void movePlayer () {
        x += xspeed;//adding 5 or minus 5 to x is how player moves
        {

// stop movement at screen edge?
            if (x<=0) {
                x=0;
                xspeed=0;
            }
            else if (x>=winWidth-myImage.getWidth(null)) {
                x=winWidth-myImage.getWidth(null);
                xspeed=0;
            }
        }




    }
}
