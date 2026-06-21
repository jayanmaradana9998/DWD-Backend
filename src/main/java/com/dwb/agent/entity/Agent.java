package com.dwb.agent.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.retailer.entity.RetailerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "agents",
       uniqueConstraints = {
               @UniqueConstraint(columnNames = {"retailer_profile_id", "email"}),
               @UniqueConstraint(columnNames = {"retailer_profile_id", "phone_number"})
       })
public class Agent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_profile_id", nullable = false)
    private RetailerProfile retailerProfile;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentAccessLevel accessLevel;

    @Column(nullable = false)
    private Boolean active = true;
}
