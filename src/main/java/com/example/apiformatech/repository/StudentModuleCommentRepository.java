package com.example.apiformatech.repository;

import com.example.apiformatech.model.StudentModuleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentModuleCommentRepository extends JpaRepository<StudentModuleComment, Long> {
}