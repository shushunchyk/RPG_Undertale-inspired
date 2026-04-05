import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents the boss/adversary in the game.
 * Demonstrates Inheritance, Encapsulation, and Overloading.
 */
public class Enemy extends GameObject {
    private int hp;

    /**
     * Default constructor for a basic enemy.
     * Demonstrates Compile-time Polymorphism (Overloading).
     * Initializes the enemy with a default of 50 HP.
     * @param x The starting X coordinate.
     * @param y The starting Y coordinate.
     */
    public Enemy(float x, float y) {
        this(x, y, 50); // Calls the overloaded constructor below
    }

    /**
     * Overloaded constructor for an enemy with custom health points.
     * @param x  The starting X coordinate.
     * @param y  The starting Y coordinate.
     * @param hp The custom starting health points.
     */
    public Enemy(float x, float y, int hp) {
        super(x, y, 60, 60); // 60x60 pixel boss
        this.hp = hp;
    }

    // --- Encapsulated Health Management ---
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

    @Override
    public void update() {
        // --- AI & Logic ---
        x += velX;
        y += velY;

        // Simple patrol bouncing between X coordinates
        if (x <= 0 || x >= 800 - width) {
            velX *= -1;
        }
    }

    @Override
    public void render(Graphics g) {
        // Draw the Enemy Body
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);

        // Draw Health Bar Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y - 15, width, 8);

        // Draw Actual Health (Green)
        g.setColor(Color.GREEN);
        int healthBarWidth = (int)((hp / 50.0) * width);

        if (healthBarWidth > width) healthBarWidth = width;
        if (healthBarWidth < 0) healthBarWidth = 0;

        g.fillRect((int)x, (int)y - 15, healthBarWidth, 8);

        // Draw Health Bar Outline
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y - 15, width, 8);
    }
}
