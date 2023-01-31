package com.rogers.accountmanager.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A AccountsInfo.
 */
@Entity
@Table(name = "accounts_info")
public class AccountsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @NotNull
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "country", nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    public enum Country {
        US,
        DE,
        ES,
        FR,
    }

    @NotNull
    //    @NotEmpty
    @Digits(integer = 5, fraction = 0)
    @Column(name = "postal_code", length = 5, nullable = false)
    private Integer postalCode;

    @Column(name = "age")
    private Integer age;

    @NotNull
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public enum Status {
        REQUESTED,
        ACTIVE,
        INACTIVE,
    }

    @Column(name = "place")
    private String place;

    @Size(min = 2, max = 2)
    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    //    @NotNull
    //    @NotEmpty
    //    @Digits(integer = 4, fraction = 0)
    @Column(name = "security_pin", length = 4)
    private Integer securityPin;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountsInfo id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public AccountsInfo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public AccountsInfo email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Country getCountry() {
        return this.country;
    }

    public AccountsInfo country(Country country) {
        this.country = country;
        return this;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Integer getPostalCode() {
        return this.postalCode;
    }

    public AccountsInfo postalCode(Integer postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getAge() {
        return this.age;
    }

    public AccountsInfo age(Integer age) {
        this.age = age;
        return this;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Status getStatus() {
        return this.status;
    }

    public AccountsInfo status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPlace() {
        return this.place;
    }

    public AccountsInfo place(String place) {
        this.place = place;
        return this;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getState() {
        return this.state;
    }

    public AccountsInfo state(String state) {
        this.state = state;
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public AccountsInfo longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public AccountsInfo latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getSecurityPin() {
        return this.securityPin;
    }

    public AccountsInfo securityPin(Integer securityPin) {
        this.securityPin = securityPin;
        return this;
    }

    public void setSecurityPin(Integer securityPin) {
        this.securityPin = securityPin;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountsInfo)) {
            return false;
        }
        return id != null && id.equals(((AccountsInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountsInfo{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", country='" + getCountry() + "'" +
            ", postalCode=" + getPostalCode() +
            ", age=" + getAge() +
            ", status='" + getStatus() + "'" +
            ", place='" + getPlace() + "'" +
            ", state='" + getState() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", securityPin=" + getSecurityPin() +
            "}";
    }
}
