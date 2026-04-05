import java.util.ArrayList;

public class AimedShotPattern implements AttackPattern {
    private double speed;

    public AimedShotPattern(double speed) {
        this.speed = speed;
    }

    @Override
    public ArrayList<Projectile> generateAttack(int enemyX, int enemyY, Player player) {
        ArrayList<Projectile> bullets = new ArrayList<>();

        // Use getX() and getY() to avoid "private access" errors
        double deltaX = player.getX() - (enemyX + 25);
        double deltaY = player.getY() - (enemyY + 25);

        // Calculate the exact angle to the player
        double angleToPlayer = Math.atan2(deltaY, deltaX);

        // Create the projectile using the "Angle/Speed" constructor
        // We pass: X, Y, Angle, Speed
        bullets.add(new Projectile(enemyX + 25, enemyY + 25, angleToPlayer, speed));

        return bullets;
    }
}