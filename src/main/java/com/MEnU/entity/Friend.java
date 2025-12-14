package com.MEnU.entity;

import com.MEnU.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "friends",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "friend_user_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend extends AbstractEntity {

    // Người gửi lời mời
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Người nhận lời mời
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id", nullable = false)
    private User friendUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;//pending,accepted

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
