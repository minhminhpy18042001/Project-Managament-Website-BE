package com.hcmute.management.repository;

import com.hcmute.management.model.entity.LecturerEntity;
import com.hcmute.management.model.entity.StudentEntity;
import com.hcmute.management.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaRepositories
public interface StudentRepository extends JpaRepository<StudentEntity, String> {
    StudentEntity findByUser(UserEntity user);
    @Modifying
    @Query(value =  "Delete from students where id = ?", nativeQuery = true)
    void deleteById(String id);

    StudentEntity findByUserId(String userid);

    @Query(value = "select * from students",
            countQuery = "select count(*) from students",
            nativeQuery = true)
    Page<StudentEntity> findAllStudentPaging(Pageable pageable);
}
