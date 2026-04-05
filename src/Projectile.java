import java.awt.Color;
import java.awt.Graphics;

public class Projectile extends GameObject {

    // --- Constructor 1 (Overloaded) ---
    // If only provided X and Y, it defaults to a 10x10 pixel size.
    public Projectile(float x, float y) {
        this(x, y, 10, 10);
    }

    // --- Constructor 2 (Main) ---
    public Projectile(float x, float y, int width, int height) {
        super(x, y, width, height);

        // Give the projectile a default downward speed
        this.velY = 4; // Moves down at 4 pixels per frame
        this.velX = 0;
    }

    // --- Trajectory Constructor ---
    // This allows patterns to spawn bullets with an angle and speed
    public Projectile(float x, float y, double angle, double speed) {
        // Call the main constructor to set X, Y and a default size (10x10)
        this(x, y, 10, 10);

        // calculate velocity based on the angle provided
        // cast to (float)
        this.velX = (float) (speed * Math.cos(angle));
        this.velY = (float) (speed * Math.sin(angle));
    }

    @Override
    public void update() {
        // Apply velocity to the position
        x += velX;
        y += velY;
    }

    @Override
    public void render(Graphics g) {
        // Draw the bullet
        g.setColor(Color.YELLOW);
        g.fillOval((int)x, (int)y, width, height);
    }
}
