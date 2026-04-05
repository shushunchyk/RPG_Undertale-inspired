public class FollowingProjectilePattern extends Projectile {
    private int trackingTimer = 100; // How long it follows before flying straight
    private Player target;
    private float speed;

    public FollowingProjectilePattern(float x, float y, Player target, float speed) {
        super(x, y); // Defaults to 10x10 in Projectile constructor
        this.target = target;
        this.speed = speed;
    }

    @Override
    public void update() {
        if (trackingTimer > 0) {
            double angle = Math.atan2(target.getY() - y, target.getX() - x);
            this.velX = (float) (speed * Math.cos(angle));
            this.velY = (float) (speed * Math.sin(angle));
            trackingTimer--;
        }
        x += velX;
        y += velY;
    }
}
