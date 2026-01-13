package com.aimi.repository;

import com.aimi.entity.StepTwoProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<StepTwoProduct, String> {}
