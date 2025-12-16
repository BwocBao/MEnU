package com.MEnU.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(
                        name = "idx_chat_pair_created",
                        columnList = "sender_id, receiver_id, created_at DESC"
                ),
                @Index(
                        name = "idx_receiver_seen",
                        columnList = "receiver_id, seen"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class Message extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(length = 1000)
    private String text;

    @Column(name = "photo_id")
    private Long photoId;// lưu ID ảnh mà user comment vào

    private Boolean seen = false;

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }
}