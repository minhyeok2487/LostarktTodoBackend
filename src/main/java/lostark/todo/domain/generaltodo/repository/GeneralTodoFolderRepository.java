package lostark.todo.domain.generaltodo.repository;

import lostark.todo.domain.generaltodo.entity.GeneralTodoFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralTodoFolderRepository extends JpaRepository<GeneralTodoFolder, Long>, GeneralTodoFolderRepositoryCustom {
}
