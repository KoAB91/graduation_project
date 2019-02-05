package entity;

public class Employee {

    // id сотрудника
    private int id;

    // id заявки, которую он выполняет
    private int requestId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }


}
