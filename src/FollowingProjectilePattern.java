public class FollowingProjectilePattern extends Projectile {
    private int trackingTimer = 120; // Tracks for 2 seconds (120 frames)
    private Player target;
    private float speed;

    public FollowingProjectilePattern(float x, float y, Player target, float speed) {
        super(x, y);
        this.target = target;
        this.speed = speed;
    }

    @Override
    public void update() {
        if (trackingTimer > 0) {
            // Calculate angle to player
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            this.velX = (float) (speed * Math.cos(angle));
            this.velY = (float) (speed * Math.sin(angle));
            trackingTimer--;
        }
        // Once timer hits 0, it just keeps its last velocity (flies straight)
        x += velX;
        y += velY;
    }
}