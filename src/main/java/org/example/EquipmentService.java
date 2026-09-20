package org.example;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public Optional<Equipment> findBySerialNumber(String serialNumber) {
        return equipmentRepository.findBySerialNumber(serialNumber);
    }

    public List<Equipment> findByStatus(Status status) {
        return equipmentRepository.findByStatus(status);
    }

    public List<Equipment> findByEmployee(Long employeeId) {
        return equipmentRepository.findByCurrentEmployeeId(employeeId);
    }




    @Transactional
    public Equipment issueEquipment(Long equipmentId, Long employeeId) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new RuntimeException("Устройство "+equipmentId+" не найдено"));
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Сотрудник с id="+employeeId+" не найден"));

        if (equipment.getStatus() != Status.IN_STOCK) {
            throw new IllegalStateException("Устройство нельзя выдать. Статус: "+equipment.getStatus());
        }
        Status oldStatus = equipment.getStatus();
        equipment.setStatus(Status.ISSUED);
        equipment.setCurrentEmployee(employee);
        equipmentRepository.save(equipment);
        EquipmentHistory history = new EquipmentHistory();
        history.setEquipment(equipment);
        history.setEmployee(employee);
        history.setChangedAt(LocalDateTime.now());
        history.setOldStatus(oldStatus);
        history.setNewStatus(Status.ISSUED);
        history.setAction(Action.ISSUED);
        history.setComment("Выдано сотруднику");
        equipmentHistoryRepository.save(history);
        return equipment;
    }
    @Transactional
    public Equipment returnEquipment(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new RuntimeException("Устройство с id=" + equipmentId + " не найдено"));

        if (equipment.getStatus() != Status.ISSUED) {
            throw new IllegalStateException("Устройство нельзя вернуть. Текущий статус: " + equipment.getStatus());
        }
        Employee employee = equipment.getCurrentEmployee();
        Status oldStatus = equipment.getStatus();

        equipment.setStatus(Status.IN_STOCK);
        equipment.setCurrentEmployee(null);
        equipmentRepository.save(equipment);

        EquipmentHistory history = new EquipmentHistory();
        history.setEquipment(equipment);
        history.setEmployee(employee);
        history.setChangedAt(LocalDateTime.now());
        history.setOldStatus(oldStatus);
        history.setNewStatus(Status.IN_STOCK);
        history.setAction(Action.RETURNED);
        history.setComment("Возврат на склад");
        equipmentHistoryRepository.save(history);
        return equipment;
    }
    @Transactional
    public Equipment sendToRepair(Long equipmentId, String comment) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));

        if (equipment.getStatus() == Status.IN_REPAIR) {
            throw new IllegalStateException("Устройство уже в ремонте");
        }
        if (equipment.getStatus() == Status.WRITTEN_OFF) {
            throw new IllegalStateException("Устройство списано");
        }

        Employee employee = equipment.getCurrentEmployee();
        Status oldStatus = equipment.getStatus();
        equipment.setStatus(Status.IN_REPAIR);
        equipmentRepository.save(equipment);

        EquipmentHistory history = new EquipmentHistory();
        history.setEquipment(equipment);
        history.setEmployee(employee);
        history.setChangedAt(LocalDateTime.now());
        history.setOldStatus(oldStatus);
        history.setNewStatus(Status.IN_REPAIR);
        history.setAction(Action.REPAIR_START);
        history.setComment(comment == null || comment.isBlank() ? "Отправлено в ремонт" : comment);
        equipmentHistoryRepository.save(history);
        return equipment;
    }
    @Transactional
    public Equipment returnFromRepair(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));

        if (equipment.getStatus() != Status.IN_REPAIR) {
            throw new IllegalStateException("Устройство не в ремонте. Статус: " + equipment.getStatus());
        }
        Status oldStatus = equipment.getStatus();
        equipment.setStatus(Status.IN_STOCK);
        equipmentRepository.save(equipment);

        EquipmentHistory history = new EquipmentHistory();
        history.setEquipment(equipment);
        history.setChangedAt(LocalDateTime.now());
        history.setOldStatus(oldStatus);
        history.setNewStatus(Status.IN_STOCK);
        history.setAction(Action.REPAIR_END);
        history.setComment("Возвращено из ремонта");
        equipmentHistoryRepository.save(history);

        return equipment;
    }

    }

}
