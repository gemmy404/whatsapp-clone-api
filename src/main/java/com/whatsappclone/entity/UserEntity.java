package com.whatsappclone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseAuditingEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5;
    @Id
    private String id; // ID will not be auto generated, but it comes from keycloak (UUID format)
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private LocalDateTime lastSeen; // helps to check if the user is online or not

    @OneToMany(mappedBy = "sender")
    @Column
    private List<ChatEntity> chatsAsSender;
    @OneToMany(mappedBy = "recipient")
    private List<ChatEntity> chatsAsRecipient;

    @Transient
    public boolean isUserOnline() {
        // lastSeen = 10:05
        // now = 10:09, interval = 10:04 -> online
        // now = 10:12, interval = 10:07 -> offline
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }

}
