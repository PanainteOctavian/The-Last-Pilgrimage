package entities;

public interface HealthObserver {
    void onPlayerHealthLow();
    void onPlayerHealthRestored();
}

