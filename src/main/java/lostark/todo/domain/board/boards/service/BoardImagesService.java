package lostark.todo.domain.board.boards.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.board.boards.entity.BoardImages;
import lostark.todo.domain.board.boards.entity.Boards;
import lostark.todo.domain.board.boards.repository.BoardsImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardImagesService {


    private final BoardsImagesRepository repository;


    // 이미지 s3에 업로드
    public void uploadImage(ImageResponse imageResponse) {
        BoardImages boardImages = BoardImages.builder()
                .fileName(imageResponse.getFileName())
                .imageUrl(imageResponse.getImageUrl())
                .build();
        repository.save(boardImages);
    }

    // 공지사항 - 이미지 연결
    public void saveByfileNames(List<String> fileNameList, Boards boards) {
        for (BoardImages boardImages : repository.findAllByFileNameIn(fileNameList)) {
            boards.addImages(boardImages);
        }
    }
}
