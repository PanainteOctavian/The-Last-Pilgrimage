package utilz;

public class Camera {
    private int xOffset, yOffset;
    private int xBound, yBound;
    private int screenWidth = 1024, screenHeight = 512;

    public Camera(int xOffset, int yOffset, int xBound, int yBound, int screenWidth, int screenHeight) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xBound = xBound;
        this.yBound = yBound;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void centerOnEntity(int entityX, int entityY, int entityWidth, int entityHeight) {

        xOffset = entityX - (screenWidth / 2) + (entityWidth / 2);
        yOffset = entityY - (screenHeight / 2) + (entityHeight / 2);
        clampToBounds();
    }

    private void clampToBounds() {
        xOffset = Math.max(0, Math.min(xOffset, xBound));
        yOffset = Math.max(0, Math.min(yOffset, yBound));
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }
}