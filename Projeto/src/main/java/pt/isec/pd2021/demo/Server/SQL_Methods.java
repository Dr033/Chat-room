package pt.isec.pd2021.demo.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SQL_Methods {
    static String url = "jdbc:mysql://localhost:3306/";
    static String urlInit = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    static String name = "root";
    static String pw = "123456";
    static String nomeBD = "pd_server_bd_";
    static Connection connect = null;
    static Statement statement = null;
    static ResultSet resultSet = null;
    static DatabaseMetaData metadata = null;
    static String LOG = null;

    public SQL_Methods() {
        gereBD();
    }

    static void gereBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(urlInit, name, pw);
            statement = connect.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        setTimeZone();
        criarBD();
        criarTabelaUsers();
        criarTabelaFicheiros();
        criarTabelaCanais();
    }

    public static void setTimeZone() {
        try {
            String sql = "SET GLOBAL time_zone = '+8:00';";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void apagaBD() {
        String sql = "DROP DATABASE " + nomeBD;
        try {
            statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void limpaBD() {
        try {
            String query = "DELETE FROM users";
            int deletedRows = statement.executeUpdate(query);
            if (deletedRows > 0) {
                //System.out.println("BD limpa");
            } else {
                //System.out.println("BD ja estava limpa");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void adicionaFicheiro(String nome, FileInputStream fis) {
        try {
            String sql = "insert into files (name,file) values(?,?)";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setString(1, nome);
            ps.setBinaryStream(2, fis);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean enviaMSGPrivada(String sender, String receiver, String msg) {
        try {
            String sql = "insert into msg (fk_sender,fk_receiver,msg) values(?,?,?)";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, msg);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean enviaMSGCanal(String id_user, String canal, String msg) {
        try {
            String sql = "insert into channels_msg (fk_channel,username,msg) values(?,?,?)";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setString(1, canal);
            ps.setString(2, id_user);
            ps.setString(3, msg);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            return false;
        }
        return true;
    }

    public String listaUsers() {
        StringBuilder sb = new StringBuilder();
        try {
            resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                String nome = resultSet.getString(1);
                //String pw = resultSet.getString(2);
                sb.append(nome + "\n");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return sb.toString();
    }

    public boolean adicionaUser(String nome, String pw, String ip, int porto) {
        try {
            try {
                String sql = "insert into users (name,password,ip,port) values(?,?,?,?)";
                PreparedStatement ps = connect.prepareStatement(sql);
                ps.setString(1, nome);
                ps.setString(2, pw);
                ps.setString(3, ip);
                ps.setInt(4, porto);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                LOG = "Nao foi possivel adicionar visto que ja existe um user com esse nome";
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean adicionaUserAoCanal(String canal, String user) {
        try {
            try {
                String sql = "insert into channels_users (fk_channel,fk_username) values(?,?)";
                PreparedStatement ps = connect.prepareStatement(sql);
                ps.setString(1, canal);
                ps.setString(2, user);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                return false;
            }
        } catch (SQLException throwables) {
            return false;
        }
        return true;
    }

    public boolean adicionaChannel(String nome, String owner, String pw) {
        try {
            try {
                statement.executeUpdate("insert into channels (name,owner,password) values('" + nome + "','" + owner + "','" + pw + "')");
            } catch (SQLIntegrityConstraintViolationException e) {
                LOG = "Nao foi possivel adicionar visto que ja existe um user com esse nome";
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean apagaCanal(String nome) {
        String sql = "delete from channels_users where fk_channel = ?";
        try {
            PreparedStatement preparedStmt = connect.prepareStatement(sql);
            preparedStmt.setString(1, nome);
            preparedStmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        sql = "delete from channels where name = ?";
        try {
            PreparedStatement preparedStmt = connect.prepareStatement(sql);
            preparedStmt.setString(1, nome);
            preparedStmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public String getCanal(String nome) {
        String sql = "SELECT * FROM channels WHERE name = ?";
        PreparedStatement st = null;
        String Name = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Name = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Name;
    }

    public String getPwCanal(String nome) {
        String sql = "SELECT * FROM channels WHERE name = ?";
        PreparedStatement st = null;
        String Name = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Name = rs.getString(3);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Name;
    }

    public String getOwnerCanal(String nome) {
        String sql = "SELECT * FROM channels WHERE name = ?";
        PreparedStatement st = null;
        String Name = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Name = rs.getString(2);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Name;
    }

    public String getUsersCanal(String canal) {
        String sql = "SELECT * FROM channels_users WHERE fk_channel = ?";
        PreparedStatement st = null;
        StringBuilder sb = new StringBuilder();
        String variavel;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, canal);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                variavel = rs.getString(3);
                sb.append(variavel + ".");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return sb.toString();
    }

    public String getUser(String nome) {
        String sql = "SELECT * FROM users WHERE name = ?";
        PreparedStatement st = null;
        String Name = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Name = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Name;
    }

    public String getPasswordUser(String nome) {
        String sql = "SELECT * FROM users WHERE name = ?";
        PreparedStatement st = null;
        String pw = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                pw = rs.getString(2);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pw;
    }

    public String getIp(String nome) {
        String sql = "SELECT * FROM users WHERE name = ?";
        PreparedStatement st = null;
        String pw = null;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                pw = rs.getString(3);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pw;
    }

    public int getPort(String nome) {
        String sql = "SELECT * FROM users WHERE name = ?";
        PreparedStatement st = null;
        int pw = 0;
        try {
            st = connect.prepareStatement(sql);
            st.setString(1, nome);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                pw = rs.getInt(4);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pw;
    }

    public String getLOG() {
        return LOG;
    }

    static void criarBD() {
        ArrayList listaBd = new ArrayList<String>();
        try {
            metadata = connect.getMetaData();
            resultSet = metadata.getCatalogs();
            while (resultSet.next()) {
                String aux = resultSet.getString(1);
                if (aux.contains(nomeBD))
                    listaBd.add(aux);
            }

            if (listaBd.size() == 0) {
                nomeBD = nomeBD + "0";
                url = url + nomeBD;
                String database = "CREATE DATABASE " + nomeBD;

                statement.executeUpdate(database);

                connect = DriverManager.getConnection(url, name, pw);
                statement = connect.createStatement();
            } else {
                Collections.sort(listaBd);
                ArrayList nrs = new ArrayList<Integer>();

                for (int i = 0; i < listaBd.size(); i++) {
                    String aux = listaBd.get(i).toString().replace(nomeBD, "");
                    int nr = Integer.parseInt(aux);
                    nrs.add(nr);
                }

                Collections.sort(nrs);

                int indexX = -1;
                for (int i = 0; i < nrs.size(); i++) {
                    if (!nrs.contains(i)) {
                        indexX = i;
                    }
                }

                if (indexX == -1) {
                    indexX = listaBd.size();
                }

                nomeBD = nomeBD + indexX;
                url = url + nomeBD;
                String database = "CREATE DATABASE " + nomeBD;

                statement.executeUpdate(database);

                connect = DriverManager.getConnection(url, name, pw);
                statement = connect.createStatement();
                return;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static void criarTabelaUsers() {
        String table = "CREATE TABLE users " +
                "(name VARCHAR(255) not NULL, " +
                " password VARCHAR(255)not NULL, " +
                " ip VARCHAR(255)not NULL, " +
                " port INT NOT NULL, " +
                " PRIMARY KEY ( name ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static void criarTabelaFicheiros() {
        String table = "CREATE TABLE files " +
                "(id INT NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(55)not NULL, " +
                " fk_sender VARCHAR(55)not NULL, " +
                " fk_receiver VARCHAR(55)not NULL, " +
                " file LONGBLOB, " +
                " FOREIGN KEY ( fk_sender ) REFERENCES users( name )," +
                " FOREIGN KEY ( fk_receiver ) REFERENCES users( name )," +
                " PRIMARY KEY ( id ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static void criarTabelaCanais() {
        String table = "CREATE TABLE channels " +
                "(name VARCHAR(55)not NULL, " +
                " owner VARCHAR(55)not NULL, " +
                " password VARCHAR(55)not NULL, " +
                " PRIMARY KEY ( name ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        table = "CREATE TABLE channels_msg " +
                "(id INT NOT NULL AUTO_INCREMENT, " +
                " fk_channel VARCHAR(55)not NULL, " +
                " username VARCHAR(55)not NULL, " +
                " msg VARCHAR(1000), " +
                " file VARCHAR(55), " +
                " FOREIGN KEY ( fk_channel ) REFERENCES channels( name )," +
                " PRIMARY KEY ( id ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        table = "CREATE TABLE channels_users " +
                "(id INT NOT NULL AUTO_INCREMENT, " +
                " fk_channel VARCHAR(55)not NULL, " +
                " fk_username VARCHAR(55)not NULL, " +
                " FOREIGN KEY ( fk_channel ) REFERENCES channels( name )," +
                " FOREIGN KEY ( fk_username ) REFERENCES users( name )," +
                " PRIMARY KEY ( id ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        table = "CREATE TABLE msg " +
                "(id INT NOT NULL AUTO_INCREMENT, " +
                " fk_sender VARCHAR(55)not NULL, " +
                " fk_receiver VARCHAR(55)not NULL, " +
                " msg VARCHAR(1000), " +
                " file VARCHAR(55), " +
                " FOREIGN KEY ( fk_sender ) REFERENCES users( name )," +
                " FOREIGN KEY ( fk_receiver ) REFERENCES users( name )," +
                " PRIMARY KEY ( id ))";
        try {
            statement.executeUpdate(table);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

