package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.boards.BoardImages;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.boards.BoardsImagesRepository;
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
