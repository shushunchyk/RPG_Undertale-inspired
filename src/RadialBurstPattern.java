import java.util.ArrayList;

public class RadialBurstPattern implements AttackPattern {
    private int bulletCount;
    private double speed;

    public RadialBurstPattern(int bulletCount, double speed) {
        this.bulletCount = bulletCount;
        this.speed = speed;
    }

    @Override
    public ArrayList<Projectile> generateAttack(int enemyX, int enemyY, Player player) {
        ArrayList<Projectile> bullets = new ArrayList<>();

        // A full circle is 2 * PI radians
        double angleStep = (Math.PI * 2) / bulletCount;

        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;
            // Spawn bullets at the center of the enemy
            bullets.add(new Projectile(enemyX + 25, enemyY + 25, angle, speed));
        }

        return bullets;
    }
}
