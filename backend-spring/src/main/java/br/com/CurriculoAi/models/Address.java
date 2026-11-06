package br.com.CurriculoAi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "zip_code", length = 9)
    private String zipCode;

    @Column(name = "street")
    private String street;

    @Column(name = "complement")
    private String complement;

    @Column(name = "unit")
    private String unit;

    @Column(name = "neighborhood")
    private String neighborhood;

    @Column(name = "city")
    private String city;

    @Column(name = "state_abbreviation", length = 2)
    private String stateAbbreviation;

    @Column(name = "state")
    private String state;

    @Column(name = "region")
    private String region;

    @Builder.Default
    @OneToMany(mappedBy = "address")
    private List<User> users = new ArrayList<>();
}