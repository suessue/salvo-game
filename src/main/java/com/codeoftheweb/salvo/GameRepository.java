package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@RepositoryRestResource
public interface GameRepository extends JpaRepository<Game, Long> {

   List<Game> findByCreationDate (LocalDateTime creationDate);


}

