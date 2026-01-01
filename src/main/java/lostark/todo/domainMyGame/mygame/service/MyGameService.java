package lostark.todo.domainMyGame.mygame.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domainMyGame.mygame.dto.MyGameResponse;
import lostark.todo.domainMyGame.mygame.entity.MyGame;
import lostark.todo.domainMyGame.mygame.repository.GameRepository;
import lostark.todo.global.dto.ImageResponse;
import lostark.todo.global.exhandler.exceptions.ConditionNotMetException;
import lostark.todo.global.service.ImagesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyGameService {

    private static final String GAME_IMAGES_FOLDER = "game-images/";

    private final GameRepository gameRepository;
    private final ImagesService imagesService;

    public MyGame get(Long id) {
        MyGame game = gameRepository.get(id);
        if (game == null) {
            throw new ConditionNotMetException("존재하지 않는 게임입니다.");
        }
        return game;
    }

    public MyGameResponse getGameById(Long id) {
        MyGame game = get(id);
        return MyGameResponse.from(game);
    }

    public Page<MyGameResponse> searchGames(String search, int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<MyGame> gamesPage = gameRepository.searchGames(search, pageRequest);
        return gamesPage.map(MyGameResponse::from);
    }

    public List<MyGameResponse> getAllGames() {
        return gameRepository.findAll().stream()
                .map(MyGameResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MyGameResponse createGame(MyGame game) {
        MyGame savedGame = gameRepository.save(game);
        return MyGameResponse.from(savedGame);
    }

    public ImageResponse uploadImage(MultipartFile image) {
        return imagesService.upload(image, GAME_IMAGES_FOLDER);
    }
}
