package com.woojin.batchsample.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class AfterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    public void setUserName(String userName) {
        validateUserName(userName);
        this.userName = userName;
    }

    private void validateUserName(String userName) {
        if (userName.isBlank() || userName.isEmpty()) {
            throw new IllegalArgumentException("이름은 null이거나 비어있을 수 없습니다.");
        }
    }
}
