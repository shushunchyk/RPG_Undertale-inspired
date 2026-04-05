import java.util.ArrayList;

public interface AttackPattern {
    // Generates a list of bullets based on the enemy's position and the player's position
    ArrayList<Projectile> generateAttack(int enemyX, int enemyY, Player player);
}