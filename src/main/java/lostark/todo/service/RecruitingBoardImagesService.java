package lostark.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.controller.dtoV2.image.ImageResponse;
import lostark.todo.domain.recruitingBoard.RecruitingBoard;
import lostark.todo.domain.recruitingBoard.RecruitingBoardImages;
import lostark.todo.domain.recruitingBoard.RecruitingBoardImagesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecruitingBoardImagesService {


    private final RecruitingBoardImagesRepository repository;


    // 이미지 s3에 업로드
    public RecruitingBoardImages uploadImage(ImageResponse imageResponse) {
        return repository.save(RecruitingBoardImages.builder()
                .fileName(imageResponse.getFileName())
                .imageUrl(imageResponse.getImageUrl())
                .build());
    }

    // 공지사항 - 이미지 연결
    public void saveByfileNames(List<String> fileNameList, RecruitingBoard boards) {
        for (RecruitingBoardImages boardImages : repository.findAllByFileNameIn(fileNameList)) {
            boards.addImages(boardImages);
        }
    }
}
