package server.model;


import server.dto.AuthRequest;

public class PersonDetails {
    private long id;
    private String username;
    private String password;
    private String status = "online";
    private String host;
    private int port;


    public PersonDetails(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    private PersonDetails(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.status = builder.status;
        this.host = builder.host;
        this.port = builder.port;
    }

    public String username() {
        return username;
    }

    public String status() {
        return status;
    }

    public String password() {
        return password;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static PersonDetails fromAuthRequest(AuthRequest authRequest) {
        return new PersonDetails(
                authRequest.username(), authRequest.password(), authRequest.host(), authRequest.port());
    }

    public static class Builder {
        private long id;
        private String username;
        private String password;
        private String status;
        private String host;
        private int port;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public PersonDetails build() {
            return new PersonDetails(this);
        }
    }
}
