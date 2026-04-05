import java.awt.Color;
import java.awt.Graphics;

public class Player extends GameObject {

    private int hp = 100; // Safely encapsulated health

    // --- Movement Flags ---
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;

    // --- Constructor 1 (Overloaded) ---
    // Defaults to a 32x32 size if only X and Y are provided
    public Player(float x, float y) {
        this(x, y, 32, 32);
    }

    // --- Constructor 2 (Main) ---
    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
    }

    // --- Safe HP Management ---
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp < 0) this.hp = 0;
    }

    // --- Movement Flag Getters and Setters ---
    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }

    public boolean isDown() { return down; }
    public void setDown(boolean down) { this.down = down; }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    // --- Core Game Loop Methods ---
    @Override
    public void update() {
        // 1. Check the flags and set velocity
        if (up) {
            velY = -5;
        } else if (down) {
            velY = 5;
        } else {
            velY = 0;
        }

        if (right) {
            velX = 5;
        } else if (left) {
            velX = -5;
        } else {
            velX = 0;
        }

        // 2. Apply the velocity to the actual position
        x += velX;
        y += velY;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        // Cast the float coordinates to ints just for drawing
        g.fillRect((int)x, (int)y, width, height);
    }
}
