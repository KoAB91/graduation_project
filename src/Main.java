import dao.RequestDao;
import entity.Request;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();

//        RequestDao requestDao = RequestDao.getInstance();
//        requestDao.createTable();
    }
}
