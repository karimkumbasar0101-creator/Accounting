package org.example;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository  equipmentRepository;

    public Equipment addEquipment(Equipment equipment){
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> getAllEquipment(){
        return equipmentRepository.findAll();
    }
    public Optional<Equipment> getEquipmentById(Long id){
        return equipmentRepository.findById(id);
    }

}
