import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {

    protected float x, y;
    protected float velX, velY;
    protected int width, height;

    public GameObject(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    // --- Getters and Setters ---
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getVelX() { return velX; }
    public void setVelX(float velX) { this.velX = velX; }

    public float getVelY() { return velY; }
    public void setVelY(float velY) { this.velY = velY; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}
