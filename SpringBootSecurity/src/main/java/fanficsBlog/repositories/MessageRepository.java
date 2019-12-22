package fanficsBlog.repositories;

import fanficsBlog.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByTag(String tag);

    List<Message> findByAuthorId(Integer id);

}
