package entity;

public class Employee {
    private int id;
    Request request;

    public Employee (int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
