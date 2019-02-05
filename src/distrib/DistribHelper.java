package distrib;

import dao.DirectorDao;
import dao.ManagerDao;
import dao.OperatorDao;
import dao.RequestDao;
import entity.*;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class DistribHelper implements Runnable {

    private Request request;
    private RequestDao requestDao;
    private OperatorDao operatorDao;
    private ManagerDao managerDao;
    private DirectorDao directorDao;
    private BlockingQueue<Operator> operators;
    private BlockingQueue<Manager> managers;
    private Director director;
    private static long timeToProccesingByOperator = 30;
    private static long timeToProccesingByManager = 60;

    public DistribHelper(Request request, RequestDao requestDao, OperatorDao operatorDao, ManagerDao managerDao, DirectorDao directorDao,
                         BlockingQueue<Operator> operators, BlockingQueue<Manager> managers, Director director) {
        this.request = request;
        this.requestDao = requestDao;
        this.operatorDao = operatorDao;
        this.managerDao = managerDao;
        this.directorDao = directorDao;
        this.operators = operators;
        this.managers = managers;
        this.director = director;
    }

    @Override
    public void run() {

        while (true) {
            // проверяем, есть ли свободные операторы
            if (!operators.isEmpty()) {
                // если есть, отдаем заявку
                DistribHelper.goToOperator(operatorDao, requestDao, request, operators);
                break;
            } else {
                // если нет, проверяем сколько времени заявка висит в принятых
                if (request.getCreationTime().plusSeconds(timeToProccesingByOperator).isAfter(LocalDateTime.now())) {
                    // если лимит времени не превышен, ждем пока освободится оператор
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // если превышен, идем к менеджерам
                    if (!managers.isEmpty()) {
                        // если есть свободный менеджер, отдаем ему заявку
                        DistribHelper.goToManager(managerDao, requestDao, request, managers);
                        break;
                    } else {
                        // если нет, проверяем сколько времени заявка висит в принятых
                        if (request.getCreationTime().plusSeconds(timeToProccesingByManager).isAfter(LocalDateTime.now())) {
                            // если лимит времени не превышен, возвращаемся и смотрим, не освободился ли к этому времени оператор
                            try {
                                Thread.sleep(15000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //если превышен - проверяем, не занят ли директор
                            if (director.getRequestId() == 0) {
                                // если нет - отдаем заявку
                                DistribHelper.goToDirector(requestDao, request, director);
                                break;
                            }
                            else {
                                // если занят, возвращаемся к операторам
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void goToOperator(OperatorDao operatorDao, RequestDao requestDao, Request request, BlockingQueue<Operator> operators) {
        Operator operator = null;
        try {
            operator = operators.take();
            operatorDao.update(operator.getId(), request.getId());
            request.setStartProcTime(LocalDateTime.now());
            requestDao.setEmployee(request.getId(), operator.getClass().getName(), operator.getId());
            System.out.println("Оператор " + operator.getId() + " взял заявку " + request.getId());
            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        operatorDao.update(operator.getId(), 0);
        operators.add(operator);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }

    private static void goToManager(ManagerDao managerDao, RequestDao requestDao, Request request, BlockingQueue<Manager> managers) {
        Manager manager = null;
        try {
            manager = managers.take();
            managerDao.update(manager.getId(), request.getId());
            request.setStartProcTime(LocalDateTime.now());
            requestDao.setEmployee(request.getId(), manager.getClass().getName(), manager.getId());
            System.out.println("Менеджер " + manager.getId() + " взял заявку");

            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        managerDao.update(manager.getId(), 0);
        managers.add(manager);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }

    private static void goToDirector(RequestDao requestDao, Request request, Director director) {
        DirectorDao directorDao = DirectorDao.getInstance();
        directorDao.createTable();
//        Director director = directorDao.getNotBusy();
        directorDao.update(director.getId(), request.getId());
        String directorName = "Director";
        requestDao.setEmployee(request.getId(), director.getClass().getName(), director.getId());
        System.out.println("Директор взял заявку");
        try {
            Thread.sleep(request.getLeadTime() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        directorDao.update(director.getId(), 0);
        requestDao.update(request.getId(), RequestStatus.DONE);
        System.out.println("Статус заявки " + request.getId() + " измененен на DONE");
    }

}

