package com.arematics.minecraft.core.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_rank_history")
public class UserRankHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", nullable = false)
    private String uuid;
    @Column(name = "new_rank", nullable = false)
    private Long newRank;
    @Column(name = "old_rank", nullable = false)
    private Long oldRank;
    @Column(name = "changer", nullable = false)
    private String changer;
    @Column(name = "change_time", nullable = false)
    private Timestamp changeTime;

}
