package BazaDeDate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static final String URL = "jdbc:sqlite:src\\BazaDeDate\\game.db";
    private static final int SINGLE_PLAYER_ID = 1;

    public DataBase() {
        connect();
        createTable();
        createEnemyTable();
        createChestTable();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Entity (" +
                "id INTEGER NOT NULL PRIMARY KEY, " +
                "Health NUMERIC NOT NULL, " +
                "coordonate_x NUMERIC NOT NULL, " +
                "coordonate_y NUMERIC NOT NULL, " +
                "level INTEGER NOT NULL," +
                "Score INTEGER," +
                "Stamina INTEGER)";  // New column for stamina
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.executeUpdate(sql);
                System.out.println("Tabela Entity a fost creată/verificată.");
            } else {
                System.out.println("Nu s-a putut crea tabela: conexiunea este null");
            }
        } catch (SQLException e) {
            System.out.println("Eroare la crearea tabelei: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createEnemyTable() {
        String sql = "CREATE TABLE IF NOT EXISTS EnemyState (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_id INTEGER NOT NULL, " +
                "level INTEGER NOT NULL, " +
                "enemy_type INTEGER NOT NULL, " +
                "spawn_x REAL NOT NULL, " +
                "spawn_y REAL NOT NULL, " +
                "is_dead INTEGER NOT NULL DEFAULT 0, " +
                "UNIQUE(player_id, level, enemy_type, spawn_x, spawn_y))";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.executeUpdate(sql);
                System.out.println("Tabela EnemyState a fost creată/verificată.");
            } else {
                System.out.println("Nu s-a putut crea tabela EnemyState: conexiunea este null");
            }
        } catch (SQLException e) {
            System.out.println("Eroare la crearea tabelei EnemyState: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createChestTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ChestState (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_id INTEGER NOT NULL, " +
                "level INTEGER NOT NULL, " +
                "chest_x REAL NOT NULL, " +
                "chest_y REAL NOT NULL, " +
                "is_opened INTEGER NOT NULL DEFAULT 0, " +
                "UNIQUE(player_id, level, chest_x, chest_y))";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.executeUpdate(sql);
                System.out.println("Tabela ChestState a fost creată/verificată.");
            } else {
                System.out.println("Nu s-a putut crea tabela ChestState: conexiunea este null");
            }
        } catch (SQLException e) {
            System.out.println("Eroare la crearea tabelei ChestState: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveGame(int health, float x, float y, int level, int Score, int stamina) {
        String selectSql = "SELECT COUNT(*) AS count FROM Entity WHERE id = ?";
        String insertSql = "INSERT INTO Entity (id, Health, coordonate_x, coordonate_y, level, Score, Stamina) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE Entity SET Health = ?, coordonate_x = ?, coordonate_y = ?, level = ?, Score = ?, Stamina = ? WHERE id = ?";

        System.out.println("Attempting to save game: Level=" + level + ", X=" + x + ", Y=" + y +
                ", Health=" + health + ", Score=" + Score + ", Stamina=" + stamina);

        try (Connection conn = connect()) {

            // Check if record exists
            PreparedStatement selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, SINGLE_PLAYER_ID);
            ResultSet rs = selectStmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");

            if (count == 0) {
                // Insert new record
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, SINGLE_PLAYER_ID);
                insertStmt.setInt(2, health);
                insertStmt.setFloat(3, x);
                insertStmt.setFloat(4, y);
                insertStmt.setInt(5, level);
                insertStmt.setInt(6, Score);
                insertStmt.setInt(7, stamina);
                int rowsInserted = insertStmt.executeUpdate();
                System.out.println("Salvare inițială a jocului. Rânduri inserate: " + rowsInserted);
            } else {
                // Update existing record
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, health);
                updateStmt.setFloat(2, x);
                updateStmt.setFloat(3, y);
                updateStmt.setInt(4, level);
                updateStmt.setInt(5, Score);
                updateStmt.setInt(6, stamina);
                updateStmt.setInt(7, SINGLE_PLAYER_ID);
                int rowsUpdated = updateStmt.executeUpdate();
                System.out.println("Salvare joc actualizată. Rânduri actualizate: " + rowsUpdated +
                        " Nivel: " + level + ", X: " + x + ", Y: " + y + ", Score: " + Score + ", Stamina: " + stamina);
            }
        } catch (SQLException e) {
            System.out.println("Eroare la salvarea jocului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveEnemyState(int level, int enemyType, float spawnX, float spawnY, boolean isDead) {
        String sql = "INSERT OR REPLACE INTO EnemyState (player_id, level, enemy_type, spawn_x, spawn_y, is_dead) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Nu s-a putut salva starea inamicului: conexiunea este null");
                return;
            }

            pstmt.setInt(1, SINGLE_PLAYER_ID);
            pstmt.setInt(2, level);
            pstmt.setInt(3, enemyType);
            pstmt.setFloat(4, spawnX);
            pstmt.setFloat(5, spawnY);
            pstmt.setInt(6, isDead ? 1 : 0);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Starea inamicului salvată: Level=" + level + ", Type=" + enemyType +
                    ", Spawn=(" + spawnX + "," + spawnY + "), Dead=" + isDead +
                    ", Rows affected=" + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Eroare la salvarea stării inamicului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<EnemyData> loadEnemyStates(int level) {
        String sql = "SELECT enemy_type, spawn_x, spawn_y, is_dead FROM EnemyState WHERE player_id = ? AND level = ?";
        List<EnemyData> enemyStates = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SINGLE_PLAYER_ID);
            pstmt.setInt(2, level);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int enemyType = rs.getInt("enemy_type");
                float spawnX = rs.getFloat("spawn_x");
                float spawnY = rs.getFloat("spawn_y");
                boolean isDead = rs.getInt("is_dead") == 1;
                enemyStates.add(new EnemyData(enemyType, spawnX, spawnY, isDead));
            }

            System.out.println("Încărcate " + enemyStates.size() + " stări de inamici pentru nivelul " + level);

        } catch (SQLException e) {
            System.out.println("Eroare la încărcarea stării inamicilor: " + e.getMessage());
            e.printStackTrace();
        }

        return enemyStates;
    }

    public List<ChestData> loadChestStates(int level) {
        String sql = "SELECT chest_x, chest_y, is_opened FROM ChestState WHERE player_id = ? AND level = ?";
        List<ChestData> chestStates = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SINGLE_PLAYER_ID);
            pstmt.setInt(2, level);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                float chestX = rs.getFloat("chest_x");
                float chestY = rs.getFloat("chest_y");
                boolean isOpened = rs.getInt("is_opened") == 1;
                chestStates.add(new ChestData(chestX, chestY, isOpened));
            }

            System.out.println("Încărcate " + chestStates.size() + " stări de cufere pentru nivelul " + level);

        } catch (SQLException e) {
            System.out.println("Eroare la încărcarea stării cuferelor: " + e.getMessage());
            e.printStackTrace();
        }

        return chestStates;
    }

    public void saveChestState(int level, float chestX, float chestY, boolean isOpened) {
        String sql = "INSERT OR REPLACE INTO ChestState (player_id, level, chest_x, chest_y, is_opened) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Nu s-a putut salva starea cufărului: conexiunea este null");
                return;
            }

            pstmt.setInt(1, SINGLE_PLAYER_ID);
            pstmt.setInt(2, level);
            pstmt.setFloat(3, chestX);
            pstmt.setFloat(4, chestY);
            pstmt.setInt(5, isOpened ? 1 : 0);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Starea cufărului salvată: Level=" + level +
                    ", Position=(" + chestX + "," + chestY + "), Opened=" + isOpened +
                    ", Rows affected=" + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Eroare la salvarea stării cufărului: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public PlayerData loadGame() {
        String sql = "SELECT Health, coordonate_x, coordonate_y, level, Score, Stamina FROM Entity WHERE id = ?";

        try (Connection conn = connect()) {
            if (conn == null) {
                System.out.println("Nu s-a putut încărca jocul: conexiunea la baza de date este null");
                return null;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SINGLE_PLAYER_ID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int health = rs.getInt("Health");
                float x = rs.getFloat("coordonate_x");
                float y = rs.getFloat("coordonate_y");
                int level = rs.getInt("level");
                int score = rs.getInt("Score");

                int stamina = 100;
                try {
                    stamina = rs.getInt("Stamina");
                } catch (SQLException e) {
                    System.out.println("Stamina column not found, using default value.");
                }

                System.out.println("Joc încărcat cu succes. Nivel: " + level + ", X: " + x + ", Y: " + y +
                        ", Health: " + health + ", Score: " + score + ", Stamina: " + stamina);
                return new PlayerData(health, x, y, level, score, stamina);
            } else {
                System.out.println("Nicio salvare găsită pentru id=" + SINGLE_PLAYER_ID);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Eroare la încărcarea jocului: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public PlayerData startNewGame() {
        int initialHealth = 100;
        float initialX = 200f;
        float initialY = 200f;
        int initialLevel = 1;
        int score = 0;
        int stamina = 100;

        clearAllEnemyStates();

        saveGame(initialHealth, initialX, initialY, initialLevel, score, stamina);
        System.out.println("Partidă nouă începută. X: " + initialX + ", Y: " + initialY +
                ", Nivel: " + initialLevel + ", Stamina: " + stamina);
        return new PlayerData(initialHealth, initialX, initialY, initialLevel, score, stamina);
    }

    private void clearAllEnemyStates() {
        String sql = "DELETE FROM EnemyState WHERE player_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("Nu s-a putut șterge toate stările inamicilor: conexiunea este null");
                return;
            }

            pstmt.setInt(1, SINGLE_PLAYER_ID);
            int rowsDeleted = pstmt.executeUpdate();

            System.out.println("Șterse toate stările inamicilor: " + rowsDeleted + " rânduri");

        } catch (SQLException e) {
            System.out.println("Eroare la ștergerea tuturor stărilor inamicilor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class PlayerData {
        public int score;
        public int health;
        public float x;
        public float y;
        public int level;
        public int stamina;

        public PlayerData(int health, float x, float y, int level, int score, int stamina) {
            this.health = health;
            this.x = x;
            this.y = y;
            this.level = level;
            this.score = score;
            this.stamina = stamina;
        }
    }

    public static class EnemyData {
        public int enemyType;
        public float spawnX;
        public float spawnY;
        public boolean isDead;

        public EnemyData(int enemyType, float spawnX, float spawnY, boolean isDead) {
            this.enemyType = enemyType;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.isDead = isDead;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EnemyData that = (EnemyData) obj;
            return enemyType == that.enemyType &&
                    Float.compare(that.spawnX, spawnX) == 0 &&
                    Float.compare(that.spawnY, spawnY) == 0;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(enemyType, spawnX, spawnY);
        }
    }

    public static class ChestData {
        public float x;
        public float y;
        public boolean isOpened;

        public ChestData(float x, float y, boolean isOpened) {
            this.x = x;
            this.y = y;
            this.isOpened = isOpened;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ChestData that = (ChestData) obj;
            return Float.compare(that.x, x) == 0 &&
                    Float.compare(that.y, y) == 0;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }
}