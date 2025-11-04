package pe.elections.microservices.core.newsarticle.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface NewsArticleRepository extends CrudRepository<NewsArticleEntity, Integer> {

    @Transactional(readOnly = true)
    List<NewsArticleEntity> findByCandidateId(int candidateId);
}
