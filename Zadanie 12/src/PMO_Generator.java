import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PMO_Generator {
    public static void main(String[] args)
            throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        loadDriver();
        String a = "zadanie12";
        String b = "/tmp/A1";
        String c = "/tmp/A2";
        Connection con = openConnection(a);
        Statement st = con.createStatement();
        createTables(st);
        insertDirsToDB(st, b, c);
        PrzechowywaczI p = (PrzechowywaczI) Class.forName("PrzechowywaczObiektow").newInstance();

        try {
            p.setConnection(con);
            saveObject(p, st, 1, 0);
            saveObject(p, st, 1, 1);
            saveObject(p, st, 1, 2);
            saveObject(p, st, 1, 3);
            SerializableClass cos1 = (SerializableClass) p.read(0).get();
            SerializableClass cos2 = (SerializableClass) p.read(1).get();
            SerializableClass cos3 = (SerializableClass) p.read(2).get();
            SerializableClass cos4 = (SerializableClass) p.read(3).get();
            System.out.println("HI" + p.read(4).isPresent());
            System.out.println(cos1.getCode());
            System.out.println(cos2.getCode());
            System.out.println(cos3.getCode());
            System.out.println(cos4.getCode());
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        st.close();
        con.close();
    }

    private static void saveObject(PrzechowywaczI p, Statement st, int idKatalogu, int code) {
        Object obiektDoZapisu = createObject(code);
        int oid = p.save(idKatalogu, obiektDoZapisu);
        System.out.println( "Otrzymano kod: " + oid );
    }

    private static Object createObject(int code) {
        return new SerializableClass(code);
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection openConnection(String dbFile) {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException e) {
            System.err.println("Nie moĹźna otworzyÄ poĹÄczenia do " + dbFile);
            System.exit(0);
        }
        return null;
    }

    private static void exec(Statement st, String SQL) throws SQLException {
        System.out.print(SQL + " -> " );
        System.out.flush();
        System.out.println(st.executeUpdate(SQL));
    }

    private static void createTables(Statement st) throws SQLException {
        exec(st, "drop table if exists Katalogi");
        exec(st, "drop table if exists Pliki");
        exec(st, "create table Katalogi( idKatalogu integer, katalog string)");
        exec(st, "create table Pliki( idPliku integer, idKatalogu integer, plik string )");
    }

    private static void insertDirsToDB(Statement st, String d1, String d2) {
        try {
            exec(st, "INSERT INTO Katalogi VALUES(1, '" + d1 + "')");
            exec(st, "INSERT INTO Katalogi VALUES(2, '" + d2 + "')");
        } catch (SQLException e) {
            System.err.println("Insert nie zadziaĹaĹ");
            System.exit(0);
        }
    }
}
