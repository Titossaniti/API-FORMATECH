package com.example.apiformatech.repository;

import com.example.apiformatech.model.SessionModule;
import com.example.apiformatech.model.StudentModuleComment;
import com.example.apiformatech.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentModuleCommentRepository extends JpaRepository<StudentModuleComment, Long> {
    boolean existsByStudentAndTrainerAndSessionModule(User student, User trainer, SessionModule sessionModule);
}