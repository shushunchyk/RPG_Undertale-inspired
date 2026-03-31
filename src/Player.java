import java.awt.Color;
import java.awt.Graphics;

public class Player extends GameObject {
    private int speed = 5;
    public int hp = 100;

    // Movement flags
    public boolean up, down, left, right;

    public Player(int x, int y) {
        super(x, y, 20, 20); // 20x20 pixel Spark
    }

    @Override
    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

        // Keep player in bounds (primitive 400x400 battle box)
        if (x < 100) x = 100;
        if (x > 480) x = 480;
        if (y < 200) y = 200;
        if (y > 580) y = 580;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}