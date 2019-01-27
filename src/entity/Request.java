package entity;

import java.util.Date;

public class Request {

    // номер заявки
    private int id;
    // id клиента
    private int clientId;
    //время создания заявки
    private Date creationTime;
    // время выполнения заявки
    private Date leadTime;
    // статус заявки
    private Status status;
    // сотрудник, который взял заявку в обработку
    private int employeeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Date leadTime) {
        this.leadTime = leadTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployee(int employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "creationTime=" + creationTime +
                ", leadTime=" + leadTime +
                ", status=" + status +
                ", employeeId=" + employeeId +
                '}';
    }
}

