import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    // Screen settings
    final int screenWidth = 600;
    final int screenHeight = 800;

    // Game Thread & Loop
    Thread gameThread;
    boolean isRunning = false;

    // Game States
    final int STRIKE_PHASE = 0;
    final int EVASION_PHASE = 1;
    int currentState = STRIKE_PHASE;
    int evasionTimer = 0;

    // Entities
    Player player;
    Enemy enemy;
    ArrayList<Projectile> projectiles = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);

        // Instantiate entities
        player = new Player(300, 400);
        enemy = new Enemy(270, 50);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
        isRunning = true;
    }

    @Override
    public void run() {
        // Basic 60 FPS Game Loop
        double drawInterval = 1000000000 / 60;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (isRunning) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // convert to milliseconds
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (currentState == STRIKE_PHASE) {
            // Wait for player to press ENTER (handled in KeyListener)
            projectiles.clear(); // Clear bullets when not in evasion
        }
        else if (currentState == EVASION_PHASE) {
            player.update();

            // Primitive projectile spawning (1 in 20 chance per frame)
            if (Math.random() < 0.05) {
                int randomX = (int)(Math.random() * 380) + 100;
                projectiles.add(new Projectile(randomX, 200));
            }

            // Update projectiles and check collisions
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile p = projectiles.get(i);
                p.update();

                if (p.getBounds().intersects(player.getBounds())) {
                    player.hp -= 10;
                    projectiles.remove(i);
                }
            }

            // End Evasion Phase after roughly 5 seconds (300 frames)
            evasionTimer++;
            if (evasionTimer > 300) {
                currentState = STRIKE_PHASE;
                evasionTimer = 0;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw basic UI
        g.setColor(Color.WHITE);
        g.drawString("Player HP: " + player.hp, 50, 750);

        if (currentState == STRIKE_PHASE) {
            g.drawString("STRIKE PHASE: Press ENTER to Attack!", 200, 400);
            enemy.render(g);
        }
        else if (currentState == EVASION_PHASE) {
            enemy.render(g);

            // Draw Battle Box
            g.setColor(Color.DARK_GRAY);
            g.drawRect(100, 200, 400, 400);

            player.render(g);
            for (Projectile p : projectiles) {
                p.render(g);
            }
        }
        g.dispose();
    }

    // Input Handling
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (currentState == STRIKE_PHASE && code == KeyEvent.VK_ENTER) {
            enemy.hp -= 10;
            currentState = EVASION_PHASE;
            // Reset player to center of battle box
            player.x = 290;
            player.y = 390;
        }

        if (currentState == EVASION_PHASE) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.up = true;
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.down = true;
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.left = true;
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) player.up = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) player.down = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) player.left = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) player.right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}