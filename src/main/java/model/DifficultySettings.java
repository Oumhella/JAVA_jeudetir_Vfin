package model;

public class DifficultySettings {
    private final double enemyMoveSpeed;
    private final double enemyProjectileSpeed;
    private final long enemySpawnInterval;
    private final long enemyShootInterval;

    public static DifficultySettings getSettings(int difficultyLevel) {
        return switch (difficultyLevel) {
            case 1 -> new DifficultySettings(2.0, 5.0, 2_000_000_000L, 3_000_000_000L);
            case 2 -> new DifficultySettings(3.0, 7.0, 1_500_000_000L, 2_000_000_000L);
            case 3 -> new DifficultySettings(4.0, 10.0, 1_000_000_000L, 1_500_000_000L);
            default -> new DifficultySettings(2.0, 5.0, 2_000_000_000L, 3_000_000_000L);
        };
    }

    private DifficultySettings(double enemyMoveSpeed, double enemyProjectileSpeed,
                               long enemySpawnInterval, long enemyShootInterval) {
        this.enemyMoveSpeed = enemyMoveSpeed;
        this.enemyProjectileSpeed = enemyProjectileSpeed;
        this.enemySpawnInterval = enemySpawnInterval;
        this.enemyShootInterval = enemyShootInterval;
    }

    public double getEnemyMoveSpeed() {
        return enemyMoveSpeed;
    }

    public double getEnemyProjectileSpeed() {
        return enemyProjectileSpeed;
    }

    public long getEnemySpawnInterval() {
        return enemySpawnInterval;
    }

    public long getEnemyShootInterval() {
        return enemyShootInterval;
    }
} 