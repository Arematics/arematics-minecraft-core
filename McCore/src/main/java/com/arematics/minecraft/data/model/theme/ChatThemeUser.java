package com.arematics.minecraft.data.model.theme;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class ChatThemeUser {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID playerId;
    @ManyToOne(fetch = FetchType.EAGER)
    private ChatTheme activeTheme;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatThemeUser)) return false;
        ChatThemeUser user = (ChatThemeUser) o;
        return getPlayerId() != null && Objects.equals(getPlayerId(), user.getPlayerId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
