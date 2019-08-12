package za.co.moxomo.domain;

public enum Role {
    ROLE_ADMIN, ROLE_CLIENT;

    public String getAuthority() {
        return name();
    }

}