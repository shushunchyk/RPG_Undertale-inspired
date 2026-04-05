import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    final int screenWidth = 600;
    final int screenHeight = 800;

    Thread gameThread;
    boolean isRunning = false;

    // --- Game States ---
    final int STRIKE_PHASE = 0;
    final int DIALOGUE_PHASE = 1;
    final int EVASION_PHASE = 2;
    final int VICTORY_PHASE = 3;
    final int GAMEOVER_PHASE = 4;
    int currentState = STRIKE_PHASE;

    // Entities & Patterns
    Player player;
    Enemy enemy;
    ArrayList<Projectile> projectiles = new ArrayList<>();
    ArrayList<AttackPattern> currentPatterns = new ArrayList<>();

    // QTE Variables
    float qteBarX;
    float qteSpeed = 8.0f;
    int qteDirection = 1;
    int meterWidth = 400;
    int meterX = 100;
    int meterY = 550;

    // Dialogue Variables
    String currentDialogue = "";
    String[] bossQuotes = {
            "Is that all you've got? I've seen toddlers with more fighting spirit than you.",
            "A lucky hit... but luck eventually runs out, doesn't it?",
            "You'll have to try harder! My grandmother hits harder than that.",
            "Tch... annoying pest. I'll squash you like the bug you are.",
            "Your end is near! Can you feel the cold breath of the abyss?"
    };
    String defeatDialogue = "Impossible... how could I lose to a mere mortal like you?! This wasn't supposed to happen...";
    Random rand = new Random();

    // Progression
    int level = 1;
    int evasionTimer = 0;
    int patternCooldown = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(this);
        this.setFocusable(true);
        // CREATE THE PLAYER HERE (Once per game session)
        player = new Player(290, 600);
        setupLevel();
    }

    public void setupLevel() {
        player.resetPosition(290, 600);
        enemy = new Enemy(270, 50, 50 + (level * 20));
        qteBarX = meterX + 5; // Start slightly inside the box
        qteDirection = 1;    // Explicitly force it to move RIGHT at the start
        projectiles.clear();
        currentPatterns.clear();
        currentPatterns.add(new AimedShotPattern(3 + level));
        if (level >= 2) currentPatterns.add(new RadialBurstPattern(8 + level, 3.0));
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
        if (currentState == STRIKE_PHASE) {
            qteBarX += qteSpeed * qteDirection;
            if (qteBarX >= meterX + meterWidth || qteBarX <= meterX) qteDirection *= -1;
        }
        else if (currentState == EVASION_PHASE) {
            player.update();
            keepPlayerInBox();

            patternCooldown++;
            if (patternCooldown > 60) {
                for (AttackPattern pattern : currentPatterns) {
                    projectiles.addAll(pattern.generateAttack((int)enemy.getX(), (int)enemy.getY(), player));
                }
                patternCooldown = 0;
            }

            for (int i = 0; i < projectiles.size(); i++) {
                Projectile p = projectiles.get(i);
                p.update();
                if (p.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(5);
                    projectiles.remove(i);
                    if (player.getHp() <= 0) currentState = GAMEOVER_PHASE;
                }
            }

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

    // --- HELPER METHOD: Text Wrapping ---
    private void drawWrappedString(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineHeight = fm.getHeight();
        int currentY = y;

        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line.append(word).append(" ");
            } else {
                g2.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word + " ");
                currentY += lineHeight;
            }
        }
        g2.drawString(line.toString(), x, currentY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));

        // HUD
        g2.drawString("Level: " + level, 20, 30);
        g2.drawString("Player HP: " + player.getHp(), 20, 750);
        g2.drawString("Boss HP: " + enemy.getHp(), 450, 30);

        enemy.render(g2);

        if (currentState == STRIKE_PHASE) {
            g2.drawRect(meterX, meterY, meterWidth, 40);
            g2.setColor(Color.YELLOW);
            g2.fillRect(meterX + (meterWidth / 2) - 10, meterY, 20, 40);
            g2.setColor(Color.CYAN);
            g2.fillRect((int)qteBarX, meterY - 5, 10, 50);
            g2.setColor(Color.WHITE);
            g2.drawString("PRESS [ENTER] TO STRIKE!", 170, 630);
        }
        else if (currentState == DIALOGUE_PHASE || currentState == VICTORY_PHASE) {
            // Draw Wide Dialogue Box (4:3 aspect box)
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(100, 200, 400, 300);

            // Draw wrapped text inside the box boundaries
            drawWrappedString(g2, currentDialogue, 130, 260, 340);

            if (currentState == DIALOGUE_PHASE) {
                g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
                g2.drawString("[Press ENTER to Continue]", 200, 470);
            } else {
                g2.setColor(Color.GREEN);
                g2.drawString("[Victory! Press SPACE for Next Level]", 130, 470);
            }
        }
        else if (currentState == EVASION_PHASE) {
            g2.drawRect(100, 200, 400, 400);
            player.render(g2);
            for (Projectile p : projectiles) p.render(g2);
        }
        else if (currentState == GAMEOVER_PHASE) {
            g2.setColor(Color.RED);
            g2.drawString("GAME OVER. [SPACE] TO RESTART", 150, 400);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (currentState == STRIKE_PHASE && code == KeyEvent.VK_ENTER) {
            int center = meterX + (meterWidth / 2);
            float accuracy = 1.0f - (Math.abs(qteBarX - center) / (meterWidth / 2f));
            int damage = (int)(25 * accuracy);
            if (damage < 5) damage = 5;

            enemy.takeDamage(damage);

            if (enemy.getHp() <= 0) {
                currentDialogue = defeatDialogue;
                currentState = VICTORY_PHASE;
            } else {
                currentDialogue = bossQuotes[rand.nextInt(bossQuotes.length)];
                currentState = DIALOGUE_PHASE;
            }
        }
        else if (currentState == DIALOGUE_PHASE && code == KeyEvent.VK_ENTER) {
            currentState = EVASION_PHASE;
            player.setX(290); player.setY(390);
        }

        if (code == KeyEvent.VK_SPACE) {
            if (currentState == VICTORY_PHASE) { level++; setupLevel(); }
            if (currentState == GAMEOVER_PHASE && code == KeyEvent.VK_SPACE) {
                level = 1;
                player.setHp(100); // Manually heal only when restarting the whole game
                setupLevel();
            }
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
