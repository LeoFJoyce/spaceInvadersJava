import java.awt.*;


public class PlayerBullet  extends  Sprite2D {

    public PlayerBullet(Image image) {
        super(image);

    }

    public void trajectory() {//makes bullet move forwards
        y -= 5;
    }
}




