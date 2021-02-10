package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_votes")
public class PlayerVotes implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    private int streak;
    private int totalVotes;
    private int currentVotePoints;
    private boolean freeVoteSkip;
    private Timestamp lastVote;
}
