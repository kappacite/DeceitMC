package fr.kappacite.deceit;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.*;

public class Database {

    private Connection connection;
    private String urlbase,host,database,user,pass;

    public Database(String urlbase, String host, String database, String user, String pass) {
        this.urlbase = urlbase;
        this.host = host;
        this.database = database;
        this.user = user;
        this.pass = pass;
    }

    public void connection(){
        if(!isConnected()){
            try {
                connection = DriverManager.getConnection(urlbase + host + "/" + database, user, pass);
                System.out.println("connected ok");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(){
        if(isConnected()){
            try {
                connection.close();
                System.out.println("connected off");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected(){
        return connection != null;
    }

    public void addServer(){

        if(hasServer()) return;

        try{

            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("0.0.0.0", 80));
            } catch (IOException e) {
                e.printStackTrace();
            }

            PreparedStatement rs = connection.prepareStatement("INSERT INTO deceit_games(host, port, server_status, game_status) " +
                    "VALUES (?,?,?,?)");
            rs.setString(1, socket.getLocalAddress().getHostAddress());
            rs.setInt(2, Bukkit.getPort());
            rs.setString(3, "lobby");
            rs.setString(4, "online");
            rs.execute();
            rs.close();

        }catch(SQLException e){

            e.printStackTrace();

        }

    }

    public void createTable(){

        if(checkTable()) return;

        try {

            String serverQuery = "CREATE TABLE IF NOT EXISTS deceit_games(host VARCHAR(255), port INT(11)," +
                    "game_status VARCHAR(255), server_status VARCHAR(255));";

            PreparedStatement rs = connection.prepareStatement(serverQuery);
            rs.executeUpdate();

            rs = connection.prepareStatement(serverQuery);
            rs.executeUpdate();
            rs.close();

            System.out.println("[Deceit] Server tablecreated.");
        } catch (SQLException throwables) {
            System.out.println("[Deceit] Can't create table.");
            throwables.printStackTrace();
        }

    }

    public void updateStatus(String game_status, String server_status){

        addServer();

        try {

            Integer port = Bukkit.getPort();

            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("0.0.0.0", 80));
            } catch (IOException e) {
                e.printStackTrace();
            }


            String ipAdress = "\""+ socket.getLocalAddress().getHostAddress() + "\"";

            String updateQuery = "UPDATE deceit_games SET server_status = " + server_status  + ", game_status = "
                    + game_status + " WHERE port = \"" +  port.toString() + "\" AND host = " + ipAdress;

            PreparedStatement rs = connection.prepareStatement(updateQuery);
            rs.executeUpdate();
            rs.close();

            System.out.println("[Deceit] Status updated");
        } catch (SQLException throwables) {
            System.out.println("[Deceit] Can't update status");
            throwables.printStackTrace();
        }

    }

    public boolean hasServer(){
        //SELECT

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("0.0.0.0", 80));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement q = connection.prepareStatement("SELECT host FROM deceit_games WHERE host = \""
                    + socket.getLocalAddress().getHostAddress() + "\" AND port = \"" + Bukkit.getPort() + "\"");
            ResultSet resultat = q.executeQuery();
            boolean hasServer = resultat.next();
            q.close();
            return hasServer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkTable(){
        //SELECT

        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet resultat = dbm.getTables(null, null, "deceit_games", null);
            System.out.println(resultat.next());
            return resultat.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
