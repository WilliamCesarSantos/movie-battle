package br.com.santos.william.moviebattle.battle;

import br.com.santos.william.moviebattle.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BattleRepository extends JpaRepository<Battle, Long> {

    Page<Battle> findByPlayer(User player, Pageable pageable);

    Optional<Battle> findByPlayerAndId(User player, Long id);

    List<Battle> findByPlayer(User player);

}
