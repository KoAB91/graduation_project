package distrib;

import dao.DirectorDao;
import dao.ManagerDao;
import dao.OperatorDao;
import dao.RequestDao;
import entity.*;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Distributor {

    static ExecutorService executeIt = Executors.newFixedThreadPool(16);

    public static void distribute() {

        // получаем соединения с таблицами
        RequestDao requestDao = RequestDao.getInstance();
        requestDao.createTable();
        OperatorDao operatorDao = OperatorDao.getInstance();
        operatorDao.createTable();
        ManagerDao managerDao = ManagerDao.getInstance();
        managerDao.createTable();
        DirectorDao directorDao = DirectorDao.getInstance();
        directorDao.createTable();

        // получаем всех сотрудников
        BlockingQueue<Operator> operators = operatorDao.getNotWorking();
        BlockingQueue<Manager> managers = managerDao.getNotWorking();
        Director director = directorDao.getNotBusy();

        // в бесконечном цикле получаем постающие заявки
        while (true) {
            List<Request> requests = requestDao.getAllByStatus(RequestStatus.CREATED);
            for (Request request : requests) {
                System.out.println("Заявка " + request.getId() + " получена");
                requestDao.update(request.getId(), RequestStatus.IN_PROCESSING);
                System.out.println("Статус заявки " + request.getId() + " измененен на PROCESSING");
            }
            // передаем их в обработку
            while (!requests.isEmpty()) {
                Request request = requests.get(0);
                requests.remove(0);
                executeIt.execute(new DistribHelper(request, requestDao, operatorDao, managerDao, directorDao, operators, managers, director));
            }
        }
    }
}
