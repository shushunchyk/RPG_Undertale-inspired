import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    // Screen settings
    final int screenWidth = 600;
    final int screenHeight = 800;

    // Game Thread
    Thread gameThread;
    boolean isRunning = false;

    // Game States
    final int STRIKE_PHASE = 0;
    final int EVASION_PHASE = 1;
    final int VICTORY_PHASE = 2;
    final int GAMEOVER_PHASE = 3;
    int currentState = STRIKE_PHASE;

    // Progression
    int level = 1;
    int evasionTimer = 0;
    int patternCooldown = 0; // Controls how fast patterns trigger

    // Entities & Patterns
    Player player;
    Enemy enemy;
    ArrayList<Projectile> projectiles = new ArrayList<>();
    ArrayList<AttackPattern> currentPatterns = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        setupLevel();
    }

    // Sets up the boss and patterns based on current level
    public void setupLevel() {
        player = new Player(290, 600); // Reset player position
        enemy = new Enemy(270, 50, 50 + (level * 20)); // Boss gets +20 HP per level

        projectiles.clear();
        currentPatterns.clear();

        // Add patterns based on level difficulty
        currentPatterns.add(new AimedShotPattern(3 + level)); // Faster shots per level
        if (level >= 2) {
            currentPatterns.add(new RadialBurstPattern(8 + level, 3.0));
        }

        currentState = STRIKE_PHASE;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        isRunning = true;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (isRunning) {
            update();
            repaint();
            try {
                double remainingTime = (nextDrawTime - System.nanoTime()) / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void update() {
        if (currentState == EVASION_PHASE) {
            player.update();
            keepPlayerInBox();

            // Handle Attack Patterns
            patternCooldown++;
            if (patternCooldown > 60) { // Every 1 second
                for (AttackPattern pattern : currentPatterns) {
                    projectiles.addAll(pattern.generateAttack((int)enemy.getX(), (int)enemy.getY(), player));
                }
                patternCooldown = 0;
            }

            // Update Projectiles
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile p = projectiles.get(i);
                p.update();

                if (p.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(5);
                    projectiles.remove(i);
                    if (player.getHp() <= 0) currentState = GAMEOVER_PHASE;
                }
            }

            // Phase Timer
            evasionTimer++;
            if (evasionTimer > 300) {
                currentState = STRIKE_PHASE;
                evasionTimer = 0;
                projectiles.clear();
            }
        }
    }

    private void keepPlayerInBox() {
        if (player.getX() < 100) player.setX(100);
        if (player.getX() > 470) player.setX(470);
        if (player.getY() < 200) player.setY(200);
        if (player.getY() > 570) player.setY(570);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        // HUD
        g.drawString("Level: " + level, 20, 30);
        g.drawString("Player HP: " + player.getHp(), 20, 750);
        g.drawString("Boss HP: " + enemy.getHp(), 450, 30);

        if (currentState == STRIKE_PHASE) {
            enemy.render(g);
            g.drawString("STRIKE PHASE: Press [ENTER] to Attack!", 140, 400);
        }
        else if (currentState == EVASION_PHASE) {
            enemy.render(g);
            g.drawRect(100, 200, 400, 400); // Battle Box
            player.render(g);
            for (Projectile p : projectiles) p.render(g);
        }
        else if (currentState == VICTORY_PHASE) {
            g.setColor(Color.GREEN);
            g.drawString("VICTORY! Press [SPACE] for Next Level", 140, 400);
        }
        else if (currentState == GAMEOVER_PHASE) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER. Press [SPACE] to Restart", 140, 400);
        }
        g.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (currentState == STRIKE_PHASE && code == KeyEvent.VK_ENTER) {
            enemy.takeDamage(15);
            if (enemy.getHp() <= 0) {
                currentState = VICTORY_PHASE;
            } else {
                currentState = EVASION_PHASE;
                player.setX(290); player.setY(390); // Center player
            }
        }

        // Progression Handling
        if (code == KeyEvent.VK_SPACE) {
            if (currentState == VICTORY_PHASE) { level++; setupLevel(); }
            if (currentState == GAMEOVER_PHASE) { level = 1; setupLevel(); }
        }

        if (currentState == EVASION_PHASE) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.setUp(true);
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.setDown(true);
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.setLeft(true);
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.setRight(true);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.setUp(false);
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.setDown(false);
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.setLeft(false);
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.setRight(false);
    }
    @Override public void keyTyped(KeyEvent e) {}
}
