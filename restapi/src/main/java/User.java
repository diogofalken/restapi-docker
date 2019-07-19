package main.java;

public class User {
    private Integer id;
    private String name;
    private String birthDate;
    private String city;

    public User(Integer id, String name, String birthDate, String city) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.city = city;
    }

    public User(String name, String birthDate, String city) {
        this.name = name;
        this.birthDate = birthDate;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
