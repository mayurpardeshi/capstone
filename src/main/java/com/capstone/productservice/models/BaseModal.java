package com.capstone.productservice.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Date createdAt;
    private Date updatedAt;
}
