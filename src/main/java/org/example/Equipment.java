package org.example;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne Employee currentEmployee;
    public Equipment(){

    }

}
