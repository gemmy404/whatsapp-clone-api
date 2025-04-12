package com.whatsappclone.entity;

import com.whatsappclone.enums.MessageState;
import com.whatsappclone.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@NamedQuery(name = "SET_MESSAGES_TO_SEEN_BY_CHAT_ID",
            query = "UPDATE MessageEntity SET state = :newState WHERE chat.id = :chatId")
public class MessageEntity extends BaseAuditingEntity {

    @Id
    @SequenceGenerator(name = "msg_seq", sequenceName = "msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String mediaFilePath;
    @Enumerated(EnumType.STRING)
    private MessageState state;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @Column(nullable = false)
    private Boolean senderDeleted = Boolean.FALSE;
    @Column(nullable = false)
    private Boolean receiverDeleted = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;
    @Column(name = "sender_id", nullable = false)
    private String senderId;
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

}
