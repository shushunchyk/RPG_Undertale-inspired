import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends GameObject {
    private int hp;
    private int maxHp;

    /**
     * Default constructor for a basic enemy.
     */
    public Enemy(float x, float y) {
        this(x, y, 50);
    }

    /**
     * Overloaded constructor for an enemy with custom health points.
     */
    public Enemy(float x, float y, int hp) {
        super(x, y, 60, 60);
        this.hp = hp;
        this.maxHp = hp; // Initialize maxHp to the starting value
    }

    // ---- Encapsulated Health Management ---
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; } // Added getter for maxHp

    public void setHp(int hp) {
        this.hp = hp;
        // Ensure maxHp is equal to current hp
        if (this.hp > maxHp) maxHp = this.hp;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp < 0) this.hp = 0;
    }

    @Override
    public void update() {
        x += velX;
        y += velY;

        if (x <= 0 || x >= 800 - width) {
            velX *= -1;
        }
    }

    @Override
    public void render(Graphics g) {
        // Draw Enemy Body
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y, width, height);

        // Health Bar Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y - 15, width, 8);

        // --- DYNAMIC HEALTH BAR ----
        g.setColor(Color.GREEN);

        // Use (double) to ensure decimal precision during division,
        // then multiply by the total width of the object.
        int healthBarWidth = (int)(((double)hp / maxHp) * width);

        // Safety clamps
        if (healthBarWidth > width) healthBarWidth = width;
        if (healthBarWidth < 0) healthBarWidth = 0;

        g.fillRect((int)x, (int)y - 15, healthBarWidth, 8);

        // Health Bar Outline
        g.setColor(Color.WHITE);
        g.drawRect((int)x, (int)y - 15, width, 8);
    }
}
