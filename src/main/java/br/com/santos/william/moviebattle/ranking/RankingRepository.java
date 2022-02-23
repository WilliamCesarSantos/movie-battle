package br.com.santos.william.moviebattle.ranking;

import br.com.santos.william.moviebattle.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByUser(User user);

}
