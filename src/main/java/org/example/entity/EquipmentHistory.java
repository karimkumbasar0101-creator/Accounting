package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.enumType.Action;
import org.example.enumType.Status;

import java.time.LocalDateTime;

@Entity
@Data
public class EquipmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    private LocalDateTime changedAt;
    @Enumerated(EnumType.STRING)
    private Status oldStatus;
    @Enumerated(EnumType.STRING)
    private Status newStatus;
    @Enumerated(EnumType.STRING)
    private Action action;
    private String comment;

    public EquipmentHistory(){}
}

