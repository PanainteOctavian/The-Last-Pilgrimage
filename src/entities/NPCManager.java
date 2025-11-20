package entities;

import gamestates.Playing;
import java.awt.Graphics;
import java.util.ArrayList;

public class NPCManager {

    private Playing playing;
    private ArrayList<NPC> npcs;

    public NPCManager(Playing playing) {
        this.playing = playing;
        this.npcs = new ArrayList<>();
    }

    public void loadNPCsForLevel(int level) {
        npcs.clear();
        switch (level) {
            case 2:
                loadLevel2NPCs();
                break;
            default:
                break;
        }
    }

    private void loadLevel2NPCs() {


        String[] elderDialogues = {
                "Welcome, traveler! You look tired from your journey.",
                "This village has been plagued by strange creatures lately.",
                "If you're brave enough, we could use your help clearing them out.",
                "Be careful out there, and may fortune favor you!"
        };
        NPC elder = new NPC(492, 218, "Village Elder", elderDialogues);
        npcs.add(elder);
        String[] merchantDialogues = {
                "Ah, a customer! Welcome to my humble shop.",
                "I've got the finest weapons and armor in the region.",
                "Unfortunately, my inventory is currently... limited.",
                "Come back later when I've restocked!"
        };
        NPC merchant = new NPC(800, 250, "Merchant", merchantDialogues);
        npcs.add(merchant);
        String[] guardDialogues = {
                "Halt! State your business in this village.",
                "Oh, you're here to help with the monster problem?",
                "That's good to hear. We've lost too many good people already.",
                "The creatures seem to come from the eastern caves."
        };
        NPC guard = new NPC(1200, 280, "Village Guard", guardDialogues);
        npcs.add(guard);
        String[] strangerDialogues = {
                "...",
                "You have the look of someone with... potential.",
                "There are secrets in this land, hidden from most eyes.",
                "Perhaps our paths will cross again..."
        };
        NPC stranger = new NPC(1500, 320, "Mysterious Stranger", strangerDialogues);
        stranger.setInteractionRange(40);
        npcs.add(stranger);
    }

    public void update() {
        Player player = playing.getPlayer();

        for (NPC npc : npcs) {
            npc.update(player);
        }
    }

    public void draw(Graphics g, int cameraOffsetX, int cameraOffsetY) {
        for (NPC npc : npcs) {
            npc.draw(g, cameraOffsetX, cameraOffsetY);
        }
    }

    public boolean isAnyNPCInteracting() {
        for (NPC npc : npcs) {
            if (npc.isInteracting()) {
                return true;
            }
        }
        return false;
    }


    public void handleInteraction() {
        // Don't allow new interactions if already interacting with someone
        if (isAnyNPCInteracting()) {
            for (NPC npc : npcs) {
                if (npc.isInteracting()) {
                    npc.interact();
                    break;
                }
            }
        } else {
            for (NPC npc : npcs) {
                if (npc.canInteract()) {
                    npc.interact();
                    break;
                }
            }
        }
    }
}