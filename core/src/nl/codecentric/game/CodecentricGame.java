package nl.codecentric.game;

import java.util.Iterator;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class CodecentricGame extends ApplicationAdapter {
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture shipTexture;
    Texture enemyTexture;
    Texture bombTexture;
    Rectangle shipRectangle;
    Rectangle bombRectangle;
    Array<Rectangle> enemyArray;
    long lastEnemy;
    BitmapFont font;
    BitmapFont.TextBounds textBounds;
    String livesText = "Ships: ";
    int lives = 3;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        batch = new SpriteBatch();
        font = new BitmapFont(false);
        font.setColor(Color.GREEN);
        textBounds = font.getBounds(livesText + lives);

        shipTexture = new Texture("ship.png");
        enemyTexture = new Texture("enemy.png");
        bombTexture = new Texture("bomb.png");

        shipRectangle = new Rectangle();
        shipRectangle.x = 800 / 2 - 23 / 2;
        shipRectangle.y = 20;
        shipRectangle.width = 23;
        shipRectangle.height = 23;

        enemyArray = new Array<Rectangle>();
        spawnEnemies();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        redrawScreen();

        // Block moving ship after all lives lost
        if (lives > 0) {
            updateShipPositionAfterInput();
            fireBombAfterInput();
        }

        updateBombPosition();

        // Generate new enemies
        if (TimeUtils.nanoTime() - lastEnemy > 2500000000L)
            spawnEnemies();

        updateEnemyPositions();
    }

    private void updateEnemyPositions() {
        Iterator<Rectangle> iter = enemyArray.iterator();
        while (iter.hasNext()) {
            Rectangle enemy = iter.next();
            enemy.y -= 100 * Gdx.graphics.getDeltaTime();

            if (enemy.y + 20 < 0) {
                updateLivesWhenEnemyHitsBottom();
                iter.remove();
            } else if (bombRectangle != null && bombRectangle.overlaps(enemy)) {
                // Handle bomb hitting enemy
                bombRectangle = null;
                iter.remove();
            }
        }
    }

    private void updateLivesWhenEnemyHitsBottom() {
        if (lives > 0) {
            lives--;
        }
    }

    private void updateBombPosition() {
        if (bombRectangle != null) {
            bombRectangle.y += 200 * Gdx.graphics.getDeltaTime();
            if (bombRectangle.y + 10 > 480)
                bombRectangle = null;
        }
    }

    private void fireBombAfterInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            spawnBomb();
        }
    }

    private void updateShipPositionAfterInput() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            shipRectangle.x = touchPos.x - 23 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            shipRectangle.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            shipRectangle.x += 200 * Gdx.graphics.getDeltaTime();

        if (shipRectangle.x < 0)
            shipRectangle.x = 0;
        if (shipRectangle.x > 800 - 23)
            shipRectangle.x = 800 - 23;
    }

    private void redrawScreen() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        drawMovingTextures();

        font.draw(batch, livesText + lives, 780 - textBounds.width, 460 - textBounds.height);
        if (lives == 0)    {
            drawGameOver(batch);
        }

        batch.end();
    }

    private void drawMovingTextures() {
        batch.draw(shipTexture, shipRectangle.x, shipRectangle.y);
        for (Rectangle enemyRectangle : enemyArray) {
            batch.draw(enemyTexture, enemyRectangle.x, enemyRectangle.y);
        }
        if (bombRectangle != null) {
            batch.draw(bombTexture, bombRectangle.x, bombRectangle.y);
        }
    }

    private void drawGameOver(final SpriteBatch batch) {
        final String gameOver = "GAME OVER";
        BitmapFont gameOverFont = new BitmapFont(false);
        gameOverFont.setColor(Color.RED);
        gameOverFont.scale(5.0f);
        BitmapFont.TextBounds gameOverTextBounds = gameOverFont.getBounds(gameOver);
        gameOverFont.draw(batch, gameOver, 800 / 2 - gameOverTextBounds.width / 2, 300 - gameOverTextBounds.height / 2);
    }

    private void spawnBomb() {
        if (bombRectangle == null) {
            bombRectangle = new Rectangle();
            bombRectangle.x = shipRectangle.x + 6;
            bombRectangle.y = shipRectangle.height + shipRectangle.y;
            bombRectangle.width = 10;
            bombRectangle.height = 10;
        }
    }

    private void spawnEnemies() {
        Rectangle enemy = new Rectangle();
        enemy.x = MathUtils.random(0, 800 - 18);
        enemy.y = 480;
        enemy.width = 18;
        enemy.height = 18;
        enemyArray.add(enemy);
        lastEnemy = TimeUtils.nanoTime();
    }

    @Override
    public void dispose() {
        shipTexture.dispose();
        enemyTexture.dispose();
        bombTexture.dispose();
        batch.dispose();
    }
}
