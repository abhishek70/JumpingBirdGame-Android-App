package com.abhishekdesai.jumpingbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class JumpingBird extends ApplicationAdapter {

	// Integration gdx for the textures
	SpriteBatch batch;

	// Defining background image texture
	Texture background;

	// Defining top tube image texture
	Texture topTube;

	// Defining bottom tube image texture
	Texture bottomTube;

	// Defining array for birds (w/o flaps)
	Texture[] birds;

	// Defining game over image texture
	Texture gameOver;

	// Setting bird state (0 = without wings, 1 = with wings)
	int birdState 		= 0;

	// Setting gaps between top and bottom tube
	int gap 		   	= 400;

	// Defining the set of tubes shown on the screen and rotate.
	// Todo : Make it Screen Dependent
	int noOfTubes 		= 4;

	// Defining game score for keeping track of score and display it to user
	int gameScore 		= 0;

	// By default its the first tube so if the user pass this tube then increment the score and scoring tube
	int scoringTube 	= 0;

	// Defining the gravity for the bird to fell on  the ground
	// Change the gravity more than 1 for harder levels
	float gravity 		= 1;

	// Defining the tube speed to show up
	float tubeVelocity 	= 4;

	// Defining bird Y position (Goes up and down on Y Axis)
	float birdYPos 		= 0;

	// Defining the speed of bird to float whenever user touch the screen. Initially set to 0
	float velocity 		= 0;

	// Setting game state (0 = Game Not started, 1 = Game Started, 2 = Game Over)
	int gameState 		= 0;

	// Defining the max tube offset between top and bottom so that they not overlap
	//float maxTubeOffset;

	// Defining Top Tube which keeps on changing height wise
	float[] tubeX;

	// Defining Tube Offset
	float[] tubeOffset;

	// Defining distance between tubes
	float distBetTubes;

	// Creating bird clone for detecting the collision with the tubes
	Circle birdClone;

	// Creating array for handling the top and bottom tube clone
	Rectangle[] topTubeClone;
	Rectangle[] bottomTubeClone;

	// Defining shape renderer used for creating tube and bird clone
	ShapeRenderer shapeRenderer;

	// Defining custom fonts
	BitmapFont font;

	// Defining Random Number generator
	Random randomNumber;

	@Override
	public void create () {
		batch           = new SpriteBatch();

		// Setting up all the layout design
		background      = new Texture("bg.png");
		birds           = new Texture[2];
		birds[0]        = new Texture("bird.png");
		birds[1]        = new Texture("bird2.png");
		topTube         = new Texture("toptube.png");
		bottomTube      = new Texture("bottomtube.png");
		gameOver        = new Texture("gameover.png");


		//maxTubeOffset   = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

		// Calculating the space between tubes (screenWidth / 2 + 200)
		// Todo : Needs to verify on the tablet
		distBetTubes    = Gdx.graphics.getWidth() / 2 + 200;


		tubeX           = new float[noOfTubes];
		tubeOffset      = new float[noOfTubes];

		// Cloning the Bird and Tubes for Collision Detection
		shapeRenderer   = new ShapeRenderer();
		birdClone       = new Circle();
		topTubeClone    = new Rectangle[noOfTubes];
		bottomTubeClone = new Rectangle[noOfTubes];

		// Setting up the font style
		font            = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);


		randomNumber    = new Random();

		// Start the Game
		beginGame();

	}

	public void beginGame(){


		// Calculating the initial position of the bird (screenHeight / 2 - birdHeight / 2)
		birdYPos = Gdx.graphics.getHeight()/2 - birds[0].getHeight() / 2;

		// Loop through no of Tubes
		for(int i = 0; i < noOfTubes; i++) {
			tubeOffset[i]       = (randomNumber.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i]            = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distBetTubes;
			topTubeClone[i]     = new Rectangle();
			bottomTubeClone[i]  = new Rectangle();
		}
	}

	/*
     * Render method is called repeatedly
     */
	@Override
	public void render () {


		batch.begin();

		// background texture, xOrigin, yOrigin, width, height
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		// Check for the gameState
		if(gameState == 1) {

			// Keeping track of the scoring tube if its less than 3 reinitialized again.
			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2) {

				gameScore++;

				if(scoringTube < noOfTubes - 1) {

					scoringTube++;

				} else {

					scoringTube = 0;
				}
			}

			// Called whenever user touch anywhere on the screen
			if(Gdx.input.isTouched()) {

				velocity = -10; // Speed of the bird

			}

			for(int i = 0; i < noOfTubes; i++) {

				if(tubeX[i] < -topTube.getWidth()) {

					tubeX[i] += noOfTubes * distBetTubes;
					tubeOffset[i] = (randomNumber.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;

				}

				// Drawing the Top and Bottom Tubes
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				// Creating the Top and Bottom Tubes Clone
				topTubeClone[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
						topTube.getWidth(), topTube.getHeight());
				bottomTubeClone[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],
						bottomTube.getWidth(), bottomTube.getHeight());

			}


			if(birdYPos > 0) {

				velocity = velocity + gravity;
				birdYPos -= velocity;

			} else {

				gameState = 2;
			}


		} else if(gameState == 0) {

			if(Gdx.input.isTouched())
				gameState = 1;


		} else {

			// show game over image
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if(Gdx.input.isTouched()) {
				// restating the params for starting the new game
				gameState   = 1;
				gameScore   = 0;
				scoringTube = 0;
				velocity    = 0;
				beginGame();

			}
		}



            /*if(birdState == 0){
                birdState = 1;
            } else {
                birdState = 0;
            }*/



		// Set it in the center of the screen
		batch.draw(birds[birdState], Gdx.graphics.getWidth()/2 - birds[birdState].getWidth() / 2,
				birdYPos);

		// Setting the Score to show up to the user
		font.draw(batch, String.valueOf(gameScore), 100, 200);

		// Cloning Bird
		birdClone.set(Gdx.graphics.getWidth() / 2, birdYPos + birds[birdState].getHeight() / 2,
				birds[birdState].getWidth() / 2);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.BLUE);

		// Center of Circle, Y Coordinate, Radius of Circle
		//shapeRenderer.circle(birdClone.x, birdClone.y, birdClone.radius);

		for(int i = 0; i < noOfTubes; i++) {

                /* shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
                    toptube.getWidth(), toptube.getHeight());*/
                /* shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOffset[i],
                    bottomtube.getWidth(), bottomtube.getHeight()); */

			// Collision Detection between the bird and tubes
			if(Intersector.overlaps(birdClone, topTubeClone[i]) || Intersector.overlaps(birdClone, bottomTubeClone[i])) {
				//Gdx.app.log("Collision", "yes");
				gameState = 2;
			}
		}


		shapeRenderer.end();
		batch.end();

	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		birds[0].dispose();
		birds[1].dispose();
		topTube.dispose();
		bottomTube.dispose();
		gameOver.dispose();
		shapeRenderer.dispose();
	}
}
