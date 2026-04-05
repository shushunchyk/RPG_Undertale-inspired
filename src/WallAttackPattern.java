import java.util.ArrayList;

public class WallAttackPattern implements AttackPattern {
    @Override
    public ArrayList<Projectile> generateAttack(int enemyX, int enemyY, Player player) {
        ArrayList<Projectile> bullets = new ArrayList<>();
        int gapIndex = (int)(Math.random() * 8); // Random gap position

        // Spawn a horizontal wall moving down
        for (int i = 0; i < 10; i++) {
            if (i == gapIndex || i == gapIndex + 1) continue; // The Gap

            // Space bullets out across the 400px wide box (starting at x=100)
            bullets.add(new Projectile(100 + (i * 40), 200, 40, 20));
            bullets.get(bullets.size()-1).setVelY(3);
        }
        return bullets;
    }
}