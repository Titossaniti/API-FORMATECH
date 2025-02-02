package com.example.apiformatech.dto;

import java.util.List;

public class EstablishmentDTO {
    private final Long id;
    private final String name;
    private final String city;
    private final List<UserDTO> admins;

    public EstablishmentDTO(Long id, String name, String city, List<UserDTO> admins) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.admins = admins;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public List<UserDTO> getAdmins() { return admins; }
}
