package org.ciaochat.repository;

import org.ciaochat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM chat_user
        WHERE chat_id = :id   
    """)
    void deleteChatUserByChatId(@Param("id") Long id);

    @Query(nativeQuery = true, value = """
        SELECT user_id FROM chat_user
        WHERE chat_id = :id
    """)
    List<Long> getChatParticipantsByChatId(@Param("id") Long id);
}
