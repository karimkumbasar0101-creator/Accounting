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
    private String type;
    private String model;
    private String serialNumber;
    private LocalDate data;
    private String status;
    @ManyToOne Employee currentEmployee;
    public Equipment(){

    }
}
