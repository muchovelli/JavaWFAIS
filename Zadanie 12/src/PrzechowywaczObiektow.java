import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Optional;

public class PrzechowywaczObiektow implements PrzechowywaczI {
    Connection _connnection = null;
    static HashMap<Integer, String> katalog = new HashMap<>();
    static HashMap<HashMap<Integer, Integer>, Object> pliki = new HashMap<>();

    public void setConnection(Connection connection) {
        this._connnection = connection;
    }

    public static void getPliki(Connection conn)
            throws SQLException {
        ResultSet resultSet;
        try {
            Statement stat = conn.createStatement();
            resultSet = stat.executeQuery("SELECT * FROM pliki");
            while (resultSet.next()) {
                HashMap<Integer, Integer> temp = new HashMap<>();
                temp.put(resultSet.getInt("idPliku"), resultSet.getInt("idKatalogu"));
                pliki.put(temp, resultSet.getObject("plik"));
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void getKatalogi(Connection conn) {
        ResultSet resultSet;
        try {
            Statement stat = conn.createStatement();
            resultSet = stat.executeQuery("SELECT * FROM katalogi");
            while (resultSet.next()) {
                katalog.put(resultSet.getInt("idKatalogu"), resultSet.getString("katalog"));
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Optional<Object> read(int obiektDoOdczytu) {
        String name = "";
        try {
            getPliki(_connnection);
            getKatalogi(_connnection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Object> optional = Optional.empty();
        for (HashMap<Integer, Integer> key : pliki.keySet()) {
            if (key.keySet().contains(obiektDoOdczytu)) {
                name = String.valueOf(pliki.get(key));
            }
        }
        String path;
        int catId = 1;
        HashMap<Integer, Integer> temp = new HashMap<>();
        if (name == null) {
            return optional;
        }
        FileInputStream file;
        ObjectInputStream in;
        try {
            try {
                for (HashMap<Integer, Integer> key : pliki.keySet()) {
                    if (pliki.get(key).equals(name)) {
                        for (int key1 : key.keySet()) {
                            if (key1 == obiektDoOdczytu) {
                                catId = key.get(key1);
                            }
                        }
                        path = katalog.get(catId);
                        file = new FileInputStream( path + File.separator + name);
                        in = new ObjectInputStream(file);
                        optional = Optional.ofNullable(in.readObject());
                        file.close();
                        in.close();
                        return optional;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return optional;
    }

    public int save(int path, Object obiektDoZapisu) throws IllegalArgumentException {
        try {
            getPliki(_connnection);
            getKatalogi(_connnection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int iD = 0;
        ResultSet rs;
        String pathname;
        String filename;
        StringBuilder sb = new StringBuilder();
        try {
            Statement stat = _connnection.createStatement();
            rs = stat.executeQuery("SELECT * FROM Pliki");
            while (rs.next()) {
                if (rs.getInt("idPliku") >= iD)
                    iD = rs.getInt("idPliku") + 1;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sb.append("plik");
            sb.append(iD);
            ResultSet resultSet = _connnection.createStatement().executeQuery("SELECT * FROM Katalogi");
            try {
                while (resultSet.next()) {
                    if (resultSet.getInt("idKatalogu") == path) {
                        pathname = resultSet.getString("katalog");
                        filename = sb.toString();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(pathname + File.separator + filename);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                            objectOutputStream.writeObject(obiektDoZapisu);
                            PreparedStatement preparedStatement = _connnection.prepareStatement("INSERT INTO Pliki VALUES ( ? , ? , ? )");
                            preparedStatement.setInt(1, iD);
                            preparedStatement.setInt(2, path);
                            preparedStatement.setString(3, filename);
                            preparedStatement.executeUpdate();
                            objectOutputStream.close();
                            fileOutputStream.close();
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                        return iD;
                    }
                }
                throw new IllegalArgumentException();
            } finally {
                resultSet.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iD;
    }
}