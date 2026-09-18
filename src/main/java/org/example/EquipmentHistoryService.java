package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentHistoryService {

    private final EquipmentHistoryRepository equipmentHistoryRepository;

    public EquipmentHistory AddRecord(EquipmentHistory  record){
        return equipmentHistoryRepository.save(record);
    }

    public List<EquipmentHistory> allEquipmentHistory(Long equipmentId){
        return equipmentHistoryRepository.findAll();
    }
    public List<EquipmentHistory> getAllHistory() {
        return equipmentHistoryRepository.findAll();
    }
}
