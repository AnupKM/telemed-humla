package com.telemed.backend.repository;

import com.telemed.backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<MedicalRecord, UUID> {

    List<MedicalRecord> findAllByPatientId(UUID patientId);

    List<MedicalRecord> findAllByCreatorId(UUID doctorId);

    List<MedicalRecord> findAllByPatientIdAndCreatorId(UUID patientId, UUID creatorId);

}
