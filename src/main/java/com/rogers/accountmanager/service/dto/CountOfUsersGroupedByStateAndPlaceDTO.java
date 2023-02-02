package com.rogers.accountmanager.service.dto;

public class CountOfUsersGroupedByStateAndPlaceDTO {

    private String state;
    private String place;
    private Long countOfUsers;

    public CountOfUsersGroupedByStateAndPlaceDTO(String state, String place, Long countOfUsers) {
        this.state = state;
        this.place = place;
        this.countOfUsers = countOfUsers;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getCountOfUsers() {
        return countOfUsers;
    }

    public void setCountOfUsers(Long countOfUsers) {
        this.countOfUsers = countOfUsers;
    }
}
