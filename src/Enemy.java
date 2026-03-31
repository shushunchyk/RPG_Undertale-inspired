import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends GameObject {
    public int hp = 50;

    public Enemy(int x, int y) {
        super(x, y, 60, 60);
    }

    @Override
    public void update() {
        // Basic enemy doesn't move in this skeleton
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);

        // Draw Enemy HP
        g.setColor(Color.GREEN);
        g.drawString("Boss HP: " + hp, x, y - 10);
    }
}