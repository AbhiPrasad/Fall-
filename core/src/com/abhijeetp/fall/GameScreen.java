package com.abhijeetp.fall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.javafx.scene.traversal.Direction;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Fall game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Rectangle caveman;
    private float elapsedTime;
    private Texture rockImg;
    private Array<Rectangle> rockdrops;
    private long lastDropTime;
    private int Infinity;
    private boolean LeftRight;

    private Texture cavemanStillImgLeft;
    private Texture cavemanStillImgRight;
    private Animation animationLeft;
    private Animation animationRight;
    private TextureRegion[] animationFramesLeft;
    private TextureRegion[] animationFramesRight;
    private Texture cavemanImgLeft;
    private Texture cavemanImgRight;
    int dropsGathered;

    public GameScreen(final Fall gam) {
        this.game = gam;
        LeftRight = true;

        //load images for caveman
        cavemanImgLeft = new Texture(Gdx.files.internal("cavemansheet_left.png"));
        cavemanImgRight = new Texture(Gdx.files.internal("cavemansheet_right.png"));
        rockImg = new Texture(Gdx.files.internal("rock.png"));
        cavemanStillImgLeft = new Texture(Gdx.files.internal("caveman_left.png"));
        cavemanStillImgRight = new Texture(Gdx.files.internal("caveman_right.png"));

        //create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        //Create Animation Array - LEFT
        TextureRegion[][] tmpFramesLeft = TextureRegion.split(cavemanImgLeft,64,64);
        animationFramesLeft = new TextureRegion[16];
        int indexLeft = 0;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                animationFramesLeft[indexLeft++] = tmpFramesLeft[j][i];
            }
        }
        animationLeft = new Animation(1f/8f, animationFramesLeft);

        //Create Animation Array - RIGHT
        TextureRegion[][] tmpFramesRight = TextureRegion.split(cavemanImgRight,64,64);
        animationFramesRight = new TextureRegion[16];
        int indexRight = 0;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                animationFramesRight[indexRight++] = tmpFramesRight[j][i];
            }
        }
        animationRight = new Animation(1f/8f, animationFramesRight);

        //Draw caveman hitbox
        caveman = new Rectangle();
        caveman.x = 800 / 2 - 64 / 2;
        caveman.y = 0;
        caveman.width = 64;
        caveman.height = 64;

        //Draw falling rocks
        rockdrops = new Array<Rectangle>();
    }

    private void spawnRock(){
        Rectangle rockdrop = new Rectangle();
        rockdrop.x = MathUtils.random(0, 800-64);
        rockdrop.y = 480;
        rockdrop.width = 32;
        rockdrop.height = 32;
        rockdrops.add(rockdrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        elapsedTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        //game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

        //begins drawing batch for caveman, follows touch movement
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        game.font.draw(batch, "Score: " + dropsGathered, 0, 480);
        //if screen is touched, animation moves to left or to right
        if (Gdx.input.isTouched()||Gdx.input.isKeyPressed(Input.Keys.LEFT)||Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
           if(LeftRight){
               //moving left
               batch.draw(animationLeft.getKeyFrame(elapsedTime, true), caveman.x, caveman.y);
           }else{
               //moving right
               batch.draw(animationRight.getKeyFrame(elapsedTime, true), caveman.x, caveman.y);
           }
            //if nothing is touched animation is in default
        } else {
            if (LeftRight){
                batch.draw(cavemanStillImgLeft, caveman.x, caveman.y);
            } else {
                batch.draw(cavemanStillImgRight, caveman.x, caveman.y);
            }
        }

        //drawing batch for rocks falling down
        for(Rectangle drop: rockdrops) {
            batch.draw(rockImg, drop.x, drop.y);
        }
        batch.end();

        // process user input
        //movement controls
        if(Gdx.input.isTouched())  {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
                //moving to the left
            if (touchPos.x <= caveman.x){
                caveman.x -= 600 * Gdx.graphics.getDeltaTime();
                LeftRight = true;
                //moving to the right
            } if (touchPos.x > caveman.x){
                caveman.x += 600 * Gdx.graphics.getDeltaTime();
                LeftRight = false;
            }
        }
        //keyboard controls - testing
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            caveman.x -= 600 * Gdx.graphics.getDeltaTime();
            LeftRight = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            caveman.x += 600 * Gdx.graphics.getDeltaTime();
            LeftRight = false;
        }

        // make sure the bucket stays within the screen bounds
        if(caveman.x < 0) caveman.x = 0;
        if(caveman.x > 800 - 64) caveman.x = 800 - 64;

        // check if we need to create a new rock
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRock();
        }
        // move the rock, remove any that are beneath the bottom edge of
        // the screen or that hit the caveman.
        Iterator<Rectangle> iter = rockdrops.iterator();
        while(iter.hasNext()) {
            Rectangle drop = iter.next();
            drop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (drop.y + 64 < 0) {
                iter.remove();
                dropsGathered = dropsGathered + 1;
            }
            Infinity = Infinity - 1;
            if (drop.overlaps(caveman)) {
                iter.remove();
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        //dispose everything when code is cleaned up
    }

}