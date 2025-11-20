package entities;

public interface HealthSubject {
    void addHealthObserver(HealthObserver observer);
    void notifyHealthObservers(boolean isLowHealth);
}