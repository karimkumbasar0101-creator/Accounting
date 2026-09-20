package org.example;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository  equipmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EquipmentHistoryRepository equipmentHistoryRepository;
    public Equipment addEquipment(Equipment equipment){
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipment(){
        return equipmentRepository.findAll();
    }
    public Optional<Equipment> getEquipmentById(Long id){
        return equipmentRepository.findById(id);
    }



    @Transactional
    public void issueEquipment(Long equipmentId, Long employeeId){
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new RuntimeException("устройство не найдено"));
        if (equipment.getStatus() != Status.IN_STOCK){
            throw new IllegalStateException("Устройство нельзя выдать так как" + equipment.getStatus());
        }
    }

}
