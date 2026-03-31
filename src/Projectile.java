import java.awt.Color;
import java.awt.Graphics;

public class Projectile extends GameObject {
    private int speed = 4;

    public Projectile(int x, int y) {
        super(x, y, 10, 10);
    }

    @Override
    public void update() {
        y += speed; // Moves down the screen
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, width, height);
    }
}