package model;

public class person
{
    String Id;
    String Firstname;
    String Lastname;
    int age;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setage(int age) {
        this.age = age;
    }

    public static void setput(String s, person p) {
    }

    public static void put(String s, person p) {
    }
}
