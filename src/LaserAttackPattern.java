import java.awt.*;

public class LaserAttackPattern extends Projectile {
    private int timer = 0;
    private int warningTime = 60; // 1 second warning
    private int activeTime = 20;  // 0.3 second blast
    private boolean isDeadly = false;

    public LaserAttackPattern(float x, float y, int width, int height) {
        super(x, y, width, height);
        this.velX = 0;
        this.velY = 0;
    }

    @Override
    public void update() {
        timer++;
        if (timer > warningTime) isDeadly = true;
        if (timer > warningTime + activeTime) {
            // Move it off-screen to be cleaned up by GamePanel
            this.y = -1000;
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isDeadly) {
            g.setColor(new Color(255, 0, 0, 100)); // Transparent red warning
            g.drawRect((int)x, (int)y, width, height);
        } else {
            g.setColor(Color.WHITE); // Intense white blast
            g.fillRect((int)x, (int)y, width, height);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (isDeadly) return super.getBounds();
        return new Rectangle(0,0,0,0); // No collision during warning
    }
}