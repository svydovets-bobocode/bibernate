package com.bobocode.svydovets.bibernate.testdata.entity;

import com.bobocode.svydovets.bibernate.annotation.Column;
import com.bobocode.svydovets.bibernate.annotation.Entity;
import com.bobocode.svydovets.bibernate.annotation.Id;
import com.bobocode.svydovets.bibernate.annotation.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table("users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
    @Id private int id;
    private String name;

    @Column(insertable = false, updatable = false)
    private LocalDateTime creationTime;

    @Column(name = "phone_number")
    private String phone;
}
