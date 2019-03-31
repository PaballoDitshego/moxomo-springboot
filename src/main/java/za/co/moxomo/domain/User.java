package za.co.moxomo.domain;

import org.springframework.data.annotation.Id;

import java.util.List;

public class User implements  Comparable<User> {

    @Id
    public String id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private List<Role> roles;
    private List<String> fcmTokens;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<String> getFcmTokens() {
        return fcmTokens;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User() {}

    @Override
    public int compareTo(User o) {
        return 0;
    }
}
