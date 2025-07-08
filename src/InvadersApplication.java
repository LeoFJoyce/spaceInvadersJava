import javax.swing.*;//for jframe and ImageIcon
import java.awt.*;//for paint method
import java.awt.event.*;//for keyListener
import java.awt.image.*;//for double buffering
import java.util.ArrayList;
import java.util.Iterator;



public class InvadersApplication extends JFrame implements Runnable, KeyListener{

    private static final Dimension WindowSize=new Dimension(900,900);//sets width and heights dimensions for the window
    private static final int NumAliens =30;//number of aliens
    private Alien[] AliensArray=new Alien[NumAliens];//array stores aliens
    private Spaceship PlayerShip;//variable of type Sprite2D
    private ArrayList <PlayerBullet> BulletArray=new ArrayList<>();//stores my bullets
    private BufferStrategy strategy;
    private  boolean shotFired=false;//makes sure trajectory only changes when a shot has been fired
    private Image BulletImage;
    private ImageIcon BulletIcon;
    private  int score=0;//stores score current  game
    private  int highScore=0;//stores best score out of all games played
    private boolean gameOver=true;
    int waveNum=1;//keeps track of waveNum
    long lastShotTime;
    int shotDelay=180;
    //constructor
    public InvadersApplication() {
        this.setTitle("Space Invaders");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
        int x= ScreenSize.width/2-WindowSize.width/2; // is middle of screen horizontally
        int y= ScreenSize.height/2-WindowSize.height/2; // is middle of screen vertically
        setBounds (x,y,WindowSize.width,WindowSize.height);//1st to arguments align my screen horizontally and vertically and set the size of window using WindowSize reference


// tell all sprites the window width
        Sprite2D.setWinWidth(WindowSize.width);


        addKeyListener(this);//need this for keyboard inputs
        //create and start our animation thread

        setVisible(true);//makes my jframe viewable

        createBufferStrategy(2);
        strategy=getBufferStrategy();


    }
    public boolean hit () {//this method calculates if a bullet has hit an alien

        for (Alien alien : AliensArray) {

            Iterator<PlayerBullet> iterator = BulletArray.iterator();//Use an iterator so I can delete bullets
            while (iterator.hasNext()) {
                PlayerBullet playerBullet = (PlayerBullet) iterator.next();
                    if (!alien.isVisible){
                        continue;//go on to the next alien if it's not visible because non-visible aliens are considered dead
                    }


                    boolean xOverlap=(alien.x < playerBullet.x && (alien.x + (double) alien.myImage.getWidth(null)) > playerBullet.x) ||
                            (playerBullet.x < alien.x && (playerBullet.x + (double) playerBullet.myImage.getWidth(null)) > alien.x);

                    boolean yOverlap=(alien.y < playerBullet.y && (alien.y + (double) alien.myImage.getHeight(null)) > playerBullet.y) ||
                            (playerBullet.y < alien.y && (playerBullet.y + (double) playerBullet.myImage.getHeight(null) > alien.y));

                       if (xOverlap && yOverlap) {
                           iterator.remove();//remove bullet

                           alien.isVisible = false;//this means alien wouldnt be painted on

                           return true;
                       }



                    }

            }





        return false;//if no bullets hits an alien
    }
public void AliensWin() {
        for (Alien alien : AliensArray) {
            if (!alien.isVisible) {
                continue;

            }
            if (alien.y>850-PlayerShip.myImage.getHeight(null)) {// aliens win when they get below spaceShip as space ship cant shoot they any more
                gameOver=true;//ends game
            }
        }
}
    //thread entry's point
    public void run () {

        int numFrame =0;//keeps track of frame
        int frame=1;//for swapping between the two frames
        while(true) {//infinity loop for animation



            PlayerShip.movePlayer();//this makes it so player only moves every frame
            boolean alienDirectionReversalNeeded = false;
            for (int i=0;i<NumAliens; i++) {
                if (AliensArray[i].moveEnemy()&&AliensArray[i].isVisible) {//only visible aliens that hit the borders of the screen will reverse direction
                    alienDirectionReversalNeeded = true;
                }
            }
            if (alienDirectionReversalNeeded) {//when a reversal is needed
                Alien.reverseDirection();//change direction
                for (int i=0;i<NumAliens; i++)
                    AliensArray[i].jumpDownwards();//moves all aliens downwards
            }
                if(numFrame==50){//every 50 frames
                    for (Alien alien : AliensArray) {
                        alien.setFrame(frame);//change alien image to the other frame

                    }
                    frame *=-1;//change frame
                    numFrame=0;//reset frame counter
                }
                if (shotFired) { //once a shot has been fired
                    for(PlayerBullet playerBullet:BulletArray){

                        playerBullet.trajectory();//move each bullet in the array upwards towards the aliens
                    }
                }
             Iterator <PlayerBullet> iterator = BulletArray.iterator();//use an iterator so I can delete bullets
            while (iterator.hasNext()) {//while the iterator still has elements
                PlayerBullet aBullet= (PlayerBullet) iterator.next();//set the iterator element to a PlayerBullet variable
                if (aBullet.y<0){//if aBullet y coordinate is less than zero then it's of the screen

                    iterator.remove();//remove that element from the BulletArray ArrayList
                }
            }


        if(hit()){//if hit return true
            score+=10;//add 10 to current game score
        }
            boolean newWave=true;

            for (Alien alien : AliensArray) {//checks for if a new wave is need by checking if any aliens are visible
                if (alien.isVisible) {
                    newWave=false;
                }
            }
            if (newWave) {//if no aliens are visible
                waveNum++;//increment waveNum
                startNewWave(waveNum);//starts a new wave

            }
            AliensWin();//checking if aliens have won
            //The reason the sprites only move each frame is so they move at the same time as repaint
        this.repaint();
            numFrame++;
        try {
            Thread.sleep(20);
        }
        catch (InterruptedException e) {}


        }

    }

    //Three Keyboard Event-Handler functions
    public void keyPressed (KeyEvent e) {

        if (gameOver) {//if any key is pressed and gameOver is true then a new game is started
            gameOver=startNewGame();
            score();//score is reset and if it is higher or equal to previous score then highscore is set equal to score

        }
        else {//Now inputs will control game if gameOver is false
        int input=e.getKeyCode();

    if (KeyEvent.VK_LEFT==input){
        PlayerShip.setXSpeed(-5);//makes player go left

    }
    if (KeyEvent.VK_RIGHT==input){
        PlayerShip.setXSpeed(5);//makes player go right

    }
    if (KeyEvent.VK_SPACE==input){
        shotFired=true;
        long currentTime=System.currentTimeMillis();
        if (currentTime- lastShotTime >shotDelay) {
            PlayerBullet currentShot=new PlayerBullet(BulletImage);//creates new bullet object
            double currentX =PlayerShip.x+((double) PlayerShip.myImage.getWidth(null) /2);//starting x coordinate is that middle of Playerships image
            double currentY=PlayerShip.y;//sets y coordiate to be the same as playerShip y coordinate
            currentShot.setPosition(currentX,currentY);
            BulletArray.add(currentShot);//add shot to BulletArray
            lastShotTime =currentTime;
        }



    }
        }


    }

    public void keyReleased (KeyEvent e) {
        PlayerShip.setXSpeed(0);//makes player stop

    }


    public void keyTyped (KeyEvent e) {//method need for keylistener class to work

    }


    public void paint (Graphics g) {
        g = strategy.getDrawGraphics();//redirects our drawing calls to our buffer
        g.setColor(Color.black);//sets background colour to black
        g.fillRect(0, 0, getWidth(), getHeight());//makes background match size of window
        if(gameOver){//Display text before game starts or when it ends
            g.setColor(Color.white);


         Font bigFont=(new Font("Arial", Font.BOLD, 50));//creates a font
            g.setFont(bigFont);//sets font
            FontMetrics fmBig=g.getFontMetrics(bigFont);
            String GameOver="GAME OVER";
            int widthGameOver=g.getFontMetrics().stringWidth(GameOver);//gets width of GAME OVER
            g.drawString(GameOver,((getWidth()-widthGameOver)/2),((getHeight()/2)-100));//GAME OVER is centred horizontally and is offset from the center by -100 pixel vertically


          Font mediumFont=(new Font("Arial", Font.BOLD, 25));
          g.setFont(mediumFont);
          String keys="Press any key to play ";
          int widthKeys=g.getFontMetrics().stringWidth(keys);

            g.drawString(keys, ((getWidth()-widthKeys)/2),((getHeight()/2)));//Press any key to play is centred horizontally and  vertically

            String instructions="[Arrow keys to move,space to fire]";
            int widthInstructions=g.getFontMetrics().stringWidth(instructions);//gets width of [Arrow keys to move,space to fire]
            g.drawString(instructions,((getWidth()-widthInstructions)/2) ,((getHeight()/2)+100));//[Arrow keys to move,space to fire] is centred horizontally and is offset from the center by 100 pixel vertically

        }
        else {
        PlayerShip.paint(g);//paints PlayerShip

        for (Alien aliens : AliensArray) {//paints all Aliens using for loop
            if(aliens.isVisible) {//Only aliens that are supposed to be visible are painted
                aliens.paint(g);
            }
        }
        Iterator <PlayerBullet> iterator = BulletArray.iterator();
        while (iterator.hasNext()) {//paints all bullets
            PlayerBullet aBullet= (PlayerBullet) iterator.next();
            aBullet.paint(g);
                  }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Wave: "+waveNum,getWidth()/2-200,55);//paints current wave
        g.drawString("Score : "+score,((getWidth()/2)),55);//paints current score
        g.drawString("High Score: "+highScore,((getWidth()/2)+200),55);//paints current high score
        }

        strategy.show();//flip the buffers

    }
public  boolean startNewGame() {



    // load image from disk
    BulletIcon =new ImageIcon("bullet.png");
    BulletImage=BulletIcon.getImage();


    ImageIcon PlayerShipIcon= new ImageIcon("player_ship.png");
    Image PlayerShipImage=PlayerShipIcon.getImage();
    PlayerShip=new Spaceship(PlayerShipImage);
    PlayerShip.setPosition(450,850);

    ImageIcon AlienIcon1= new ImageIcon("alien_ship_1.png");
    Image AlienImage1=AlienIcon1.getImage();

    ImageIcon AlienIcon2= new ImageIcon("alien_ship_2.png");
    Image AlienImage2=AlienIcon2.getImage();

    for (int i = 0; i < NumAliens; i++) { //for loop  initialise all aliens
        AliensArray[i]=new Alien(AlienImage1,AlienImage2);
    }

    BulletArray.clear();//makes bulletArray is empty
    waveNum=1;//resets waveNumber
    startNewWave(waveNum);//creates 1st fleet

    Thread thread=new Thread(this);//initialize my thread object
    thread.start();//start my thread


        return false;
}
public void score () {//reset score and sets high score
    if (score>=highScore) {
        highScore=score;
        score=0;
    }
}
public void startNewWave (int waveNum) {// creates new wave

    for (int i = 0; i < NumAliens; i++) { //for loop  initialise all aliens
        double   xpos=(i%5)*80+70;
        double    ypos=(i/5)*40+50;
        AliensArray[i].setPosition(xpos,ypos);//resets all aliens position to the start again
        AliensArray[i].isVisible=true;//makes all aliens are visible again

    }

   Alien.setFleetXSpeed(waveNum);
    BulletArray.clear();//clears bullet array after every wave

}
    public static void main(String[] args) {



        InvadersApplication app = new InvadersApplication();



    }


}
