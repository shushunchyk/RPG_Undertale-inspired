import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class LaserAttackPattern implements AttackPattern {
    private Random rand = new Random();

    @Override
    public ArrayList<Projectile> generateAttack(int enemyX, int enemyY, Player player) {
        ArrayList<Projectile> bullets = new ArrayList<>();

        // Pick a random style: 0 = Vertical, 1 = Horizontal, 2 = BOTH (Cross)
        int choice = rand.nextInt(3);

        // 1. Vertical Laser (Targets Player's X)
        if (choice == 0 || choice == 2) {
            // Center the 40px laser on the player's current X position
            float targetX = player.getX() + (player.getWidth() / 2f) - 20;

            // x: targetX, y: 200 (box top), width: 40, height: 400 (box height)
            bullets.add(new LaserProjectile(targetX, 200, 40, 400, false));
        }

        // 2. Horizontal Laser (Targets Player's Y)
        if (choice == 1 || choice == 2) {
            // Center the 40px laser on the player's current Y position
            float targetY = player.getY() + (player.getHeight() / 2f) - 20;

            // x: 100 (box left), y: targetY, width: 400 (box width), height: 40
            bullets.add(new LaserProjectile(100, targetY, 400, 40, true));
        }

        return bullets;
    }

    // --- INNER CLASS ---
    public static class LaserProjectile extends Projectile {
        private int timer = 0;
        private final int warningTime = 45; // Slightly faster warning for targeted shots
        private final int activeTime = 15;
        private boolean isDeadly = false;
        private boolean isHorizontal;

        public LaserProjectile(float x, float y, int width, int height, boolean isHorizontal) {
            super(x, y, width, height);
            this.isHorizontal = isHorizontal;
            this.velX = 0;
            this.velY = 0;
        }

        @Override
        public void update() {
            timer++;
            if (timer >= warningTime && timer < warningTime + activeTime) {
                isDeadly = true;
            } else if (timer >= warningTime + activeTime) {
                isDeadly = false;
                this.y = -1000; // Trigger removal
            }
        }

        @Override
        public Rectangle getBounds() {
            if (isDeadly) return new Rectangle((int)x, (int)y, width, height);
            return new Rectangle(0, 0, 0, 0);
        }

        @Override
        public void render(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (timer < warningTime) {
                // WARNING: Thin red line at the target location
                g2.setColor(new Color(255, 0, 0, 150));
                if (isHorizontal) {
                    g2.fillRect((int)x, (int)y + (height / 2) - 2, width, 4);
                } else {
                    g2.fillRect((int)x + (width / 2) - 2, (int)y, 4, height);
                }
            } else if (isDeadly) {
                // BLAST: Full white beam covering the target area
                g2.setColor(Color.WHITE);
                g2.fillRect((int)x, (int)y, width, height);
            }
        }
    }
}
