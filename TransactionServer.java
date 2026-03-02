package backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;

public class TransactionServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/add", new AddHandler());
        server.createContext("/transactions", new GetHandler());
        server.createContext("/delete", new DeleteHandler());

        server.setExecutor(null);
        System.out.println("🚀 Backend Server started on port 8080...");
        server.start();
    }

    // ================= ADD TRANSACTION =================
    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), "utf-8"));

                String data = br.readLine(); // description=Salary&amount=5000&type=income

                boolean success = saveToDB(data);

                String response = success ? "Saved Successfully" : "Insert Failed";

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private boolean saveToDB(String data) {
            try (Connection conn = DBConnection.getConnection()) {

                String[] parts = data.split("&");

                String desc = parts[0].split("=")[1];
                double amount = Double.parseDouble(parts[1].split("=")[1]);
                String type = parts[2].split("=")[1];

                String sql = "INSERT INTO transactions (description, amount, type) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, desc);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, type);

                return pstmt.executeUpdate() > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // ================= GET ALL TRANSACTIONS =================
    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            String json = getTransactions();

            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        }

        private String getTransactions() {

            StringBuilder json = new StringBuilder("[");
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM transactions");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"description\":\"").append(rs.getString("description")).append("\",")
                            .append("\"amount\":").append(rs.getDouble("amount")).append(",")
                            .append("\"type\":\"").append(rs.getString("type")).append("\"")
                            .append("},");
                }

                if (json.charAt(json.length() - 1) == ',') {
                    json.deleteCharAt(json.length() - 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            json.append("]");
            return json.toString();
        }
    }

    // ================= DELETE TRANSACTION =================
    static class DeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String query = exchange.getRequestURI().getQuery(); // id=1
            int id = Integer.parseInt(query.split("=")[1]);

            boolean success = deleteFromDB(id);

            String response = success ? "Deleted Successfully" : "Delete Failed";

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean deleteFromDB(int id) {
            try (Connection conn = DBConnection.getConnection()) {

                String sql = "DELETE FROM transactions WHERE id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);

                return pstmt.executeUpdate() > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}