package com.example.BackEndProjetOCSpringBoot.Models;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RENTALS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Double surface;
    private Double price;
    private String picture;
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at")
    private Timestamp created_at;

    @Column(name = "updated_at")
    private Timestamp updated_at;

    @OneToMany(mappedBy = "rental")
    @JsonManagedReference
    private List<Message> messages;
}
