package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import utilz.LoadSave;
import static utilz.HelpMethods.*;

public class NPC extends Entity {

    private BufferedImage npcSprite;
    private String[] dialogues;
    private int currentDialogueIndex = 0;
    private boolean isInteracting = false;
    private boolean canInteract = false;
    private String name;
    private int interactionRange = 50; // pixels
    private int spriteType = 0; // Default sprite type

    // Sprite dimensions
    private static final int SPRITE_WIDTH = 18;
    private static final int SPRITE_HEIGHT = 44;
    private static final int DRAW_WIDTH = 32;
    private static final int DRAW_HEIGHT = 48;

    // Visual feedback
    private boolean showInteractionPrompt = false;
    private int promptBlinkTick = 0;
    private int promptBlinkSpeed = 30; // frames

    // Dialogue box properties
    private static final int DIALOGUE_BOX_WIDTH = 600;
    private static final int DIALOGUE_BOX_HEIGHT = 150;
    private static final int DIALOGUE_PADDING = 20;
    private static final Color DIALOGUE_BG_COLOR = new Color(0, 0, 0, 180);
    private static final Color DIALOGUE_BORDER_COLOR = Color.WHITE;
    private static final Font DIALOGUE_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font NAME_FONT = new Font("Arial", Font.BOLD, 18);

    public NPC(float x, float y, String name, String[] dialogues) {
        super(x, y, DRAW_WIDTH, DRAW_HEIGHT);
        this.name = name;
        this.dialogues = dialogues;
        this.health = 1;
        this.maxHealth = 1;

        // Inițializează hitbox-ul cu pozițiile corecte
        initHitbox(x, y, DRAW_WIDTH, DRAW_HEIGHT);
        loadNPCSprite();

        System.out.println("Created NPC: " + name + " at position (" + x + ", " + y + ")");
        System.out.println("Hitbox: " + hitbox.toString());
    }

    private void loadNPCSprite() {
        try {
            BufferedImage npcAtlas = LoadSave.GetSpriteAtlas(LoadSave.NPCMALESPRITE);

            if (npcAtlas != null) {
                System.out.println("NPC Atlas loaded successfully. Size: " +
                        npcAtlas.getWidth() + "x" + npcAtlas.getHeight());

                int row = spriteType / 5;
                int col = spriteType % 5;

                System.out.println("Extracting sprite at row: " + row + ", col: " + col);
                System.out.println("Coordinates: x=" + (col * SPRITE_WIDTH) +
                        ", y=" + (row * SPRITE_HEIGHT) +
                        ", w=" + SPRITE_WIDTH + ", h=" + SPRITE_HEIGHT);

                // Verifică dacă coordonatele sunt în limitele atlas-ului
                if ((col * SPRITE_WIDTH + SPRITE_WIDTH) <= npcAtlas.getWidth() &&
                        (row * SPRITE_HEIGHT + SPRITE_HEIGHT) <= npcAtlas.getHeight()) {

                    npcSprite = npcAtlas.getSubimage(
                            col * SPRITE_WIDTH,
                            row * SPRITE_HEIGHT,
                            SPRITE_WIDTH,
                            SPRITE_HEIGHT
                    );
                    System.out.println("Successfully loaded NPC sprite for " + name);
                } else {
                    System.err.println("Sprite coordinates out of bounds for " + name);
                    createFallbackSprite();
                }
            } else {
                System.err.println("Failed to load NPC sprite atlas. Using fallback sprite.");
                createFallbackSprite();
            }
        } catch (Exception e) {
            System.err.println("Exception loading NPC sprite: " + e.getMessage());
            e.printStackTrace();
            createFallbackSprite();
        }
    }

    private void createFallbackSprite() {
        // Creează un sprite de rezervă dacă încărcarea eșuează
        npcSprite = new BufferedImage(SPRITE_WIDTH, SPRITE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = npcSprite.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, SPRITE_WIDTH - 1, SPRITE_HEIGHT - 1);
        g2d.dispose();
        System.out.println("Created fallback sprite for " + name);
    }

    public void update(Player player) {
        if (isDead()) return;
        updateInteractionState(player);
        updateVisualEffects();
    }

    private void updateInteractionState(Player player) {
        float distance = getDistanceToPlayer(player);

        boolean wasCanInteract = canInteract;
        if (distance <= interactionRange && !player.isDead() && !player.isInDeathAnimation()) {
            canInteract = true;
            showInteractionPrompt = true;

            if (!wasCanInteract) {
                System.out.println("Player can now interact with " + name + " (distance: " + distance + ")");
            }
        } else {
            canInteract = false;
            showInteractionPrompt = false;
            if (isInteracting) {
                endInteraction();
            }
        }
    }

    private void updateVisualEffects() {
        promptBlinkTick++;
        if (promptBlinkTick >= promptBlinkSpeed) {
            promptBlinkTick = 0;
        }
    }

    private float getDistanceToPlayer(Player player) {
        // Calculează distanța de la centrul NPC-ului la centrul player-ului
        float npcCenterX = hitbox.x + hitbox.width / 2.0f;
        float npcCenterY = hitbox.y + hitbox.height / 2.0f;
        float playerCenterX = player.getWorldX() + player.getHitbox().width / 2.0f;
        float playerCenterY = player.getWorldY() + player.getHitbox().height / 2.0f;

        float dx = playerCenterX - npcCenterX;
        float dy = playerCenterY - npcCenterY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void interact() {
        if (!canInteract) return;

        if (!isInteracting) {
            isInteracting = true;
            currentDialogueIndex = 0;
            System.out.println("Started interaction with " + name);
        } else {
            currentDialogueIndex++;
            if (currentDialogueIndex >= dialogues.length) {
                endInteraction();
            }
        }
    }

    private void endInteraction() {
        isInteracting = false;
        currentDialogueIndex = 0;
        System.out.println("Ended interaction with " + name);
    }

    public void draw(Graphics g, int cameraOffsetX, int cameraOffsetY) {
        if (isDead()) return;

        // Calculează poziția pe ecran
        int drawX = (int) hitbox.x - cameraOffsetX;
        int drawY = (int) hitbox.y - cameraOffsetY;

        // Debug: afișează informații despre poziționare
        if (name.equals("Village Elder")) { // Debug doar pentru primul NPC
            System.out.println("Drawing " + name + " - World pos: (" + hitbox.x + ", " + hitbox.y +
                    ") Screen pos: (" + drawX + ", " + drawY + ") Camera: (" + cameraOffsetX + ", " + cameraOffsetY + ")");
        }

        // Verifică dacă NPC-ul este vizibil pe ecran (cu o marjă de siguranță)
        if (drawX < -DRAW_WIDTH || drawX > 1024 + DRAW_WIDTH ||
                drawY < -DRAW_HEIGHT || drawY > 512 + DRAW_HEIGHT) {
            return;
        }

        // Desenează sprite-ul NPC-ului
        if (npcSprite != null) {
            g.drawImage(npcSprite, drawX, drawY, DRAW_WIDTH, DRAW_HEIGHT, null);

            // Debug: desenează un contur roșu în jurul NPC-ului
            g.setColor(Color.RED);
            g.drawRect(drawX, drawY, DRAW_WIDTH, DRAW_HEIGHT);
        } else {
            // Fallback rectangle dacă nu există sprite
            g.setColor(Color.BLUE);
            g.fillRect(drawX, drawY, DRAW_WIDTH, DRAW_HEIGHT);
            g.setColor(Color.WHITE);
            g.drawRect(drawX, drawY, DRAW_WIDTH, DRAW_HEIGHT);
        }

        // Desenează numele NPC-ului deasupra (pentru debug)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int nameWidth = fm.stringWidth(name);
        g.fillRect(drawX + DRAW_WIDTH/2 - nameWidth/2 - 2, drawY - 20, nameWidth + 4, 15);
        g.setColor(Color.BLACK);
        g.drawString(name, drawX + DRAW_WIDTH/2 - nameWidth/2, drawY - 8);

        // Desenează prompt-ul de interacțiune dacă este aplicabil
        if (showInteractionPrompt && !isInteracting) {
            drawInteractionPrompt(g, drawX, drawY);
        }

        // Desenează dialogul dacă se interacționează
        if (isInteracting) {
            drawDialogue(g);
        }
    }

    private void drawInteractionPrompt(Graphics g, int npcX, int npcY) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        String prompt = "Press E to talk";
        FontMetrics fm = g.getFontMetrics();
        int promptWidth = fm.stringWidth(prompt);

        int promptX = npcX + DRAW_WIDTH / 2 - promptWidth / 2;
        int promptY = npcY - 25;

        // Background pentru vizibilitate mai bună
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(promptX - 5, promptY - fm.getHeight() + 5, promptWidth + 10, fm.getHeight());

        g.setColor(Color.YELLOW);
        g.drawString(prompt, promptX, promptY);
    }

    private void drawDialogue(Graphics g) {
        if (currentDialogueIndex >= dialogues.length) return;

        int screenWidth = 1024;
        int screenHeight = 512;

        int boxX = (screenWidth - DIALOGUE_BOX_WIDTH) / 2;
        int boxY = screenHeight - DIALOGUE_BOX_HEIGHT - 50;

        // Desenează fundalul dialogului
        g.setColor(DIALOGUE_BG_COLOR);
        g.fillRect(boxX, boxY, DIALOGUE_BOX_WIDTH, DIALOGUE_BOX_HEIGHT);

        // Desenează bordura
        g.setColor(DIALOGUE_BORDER_COLOR);
        g.drawRect(boxX, boxY, DIALOGUE_BOX_WIDTH, DIALOGUE_BOX_HEIGHT);

        // Desenează numele NPC-ului
        g.setFont(NAME_FONT);
        g.setColor(Color.YELLOW);
        g.drawString(name, boxX + DIALOGUE_PADDING, boxY + DIALOGUE_PADDING + 18);

        // Desenează textul dialogului
        g.setFont(DIALOGUE_FONT);
        g.setColor(Color.WHITE);

        String currentDialogue = dialogues[currentDialogueIndex];
        drawWrappedText(g, currentDialogue, boxX + DIALOGUE_PADDING,
                boxY + DIALOGUE_PADDING + 50, DIALOGUE_BOX_WIDTH - 2 * DIALOGUE_PADDING);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        String continueText = (currentDialogueIndex < dialogues.length - 1)
                ? "Press E to continue..."
                : "Press E to close";
        g.drawString(continueText, boxX + DIALOGUE_BOX_WIDTH - 150,
                boxY + DIALOGUE_BOX_HEIGHT - 10);
    }

    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String currentLine = "";
        int lineHeight = fm.getHeight();
        int currentY = y;

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;

            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine = testLine;
            } else {
                if (!currentLine.isEmpty()) {
                    g.drawString(currentLine, x, currentY);
                    currentY += lineHeight;
                    currentLine = word;
                } else {
                    g.drawString(word, x, currentY);
                    currentY += lineHeight;
                }
            }
        }
        if (!currentLine.isEmpty()) {
            g.drawString(currentLine, x, currentY);
        }
    }

    // Getters
    public boolean canInteract() { return canInteract; }
    public boolean isInteracting() { return isInteracting; }
    public String getName() { return name; }

    public void setDialogues(String[] dialogues) {
        this.dialogues = dialogues;
        currentDialogueIndex = 0;
    }

    public void setInteractionRange(int range) {
        this.interactionRange = range;
    }

    @Override
    public void takeDamage(int damage) {
        // NPCs don't take damage
    }
}