package lostark.todo.domainV2.board.recrutingBoard.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoard;
import lostark.todo.domainV2.board.recrutingBoard.entity.RecruitingBoardImages;
import lostark.todo.domainV2.board.recrutingBoard.repository.RecruitingBoardImagesRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RecruitingBoardImagesDao {

    private final RecruitingBoardImagesRepository repository;

    @Transactional
    public void uploadImage(ImageResponse imageResponse) {
        repository.save(RecruitingBoardImages.builder()
                .fileName(imageResponse.getFileName())
                .imageUrl(imageResponse.getImageUrl())
                .build());
    }

    @Transactional
    public void saveByfileNames(List<String> fileNameList, RecruitingBoard recruitingBoard) {
        for (RecruitingBoardImages boardImages : repository.findAllByFileNameIn(fileNameList)) {
            recruitingBoard.addImages(boardImages);
        }
    }
}
