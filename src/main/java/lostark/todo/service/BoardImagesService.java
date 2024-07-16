package lostark.todo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lostark.todo.domain.boards.BoardImages;
import lostark.todo.domain.boards.Boards;
import lostark.todo.domain.boards.BoardsImagesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardImagesService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    private final BoardsImagesRepository repository;

    public BoardImages upload(MultipartFile image) {
        //입력받은 이미지 파일이 빈 파일인지 검증
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new IllegalArgumentException("빈 파일 입니다.");
        }
        //uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환한다.
        return this.uploadImage(image);
    }

    //1. validateImageFileExtention()을 호출하여 확장자 명이 올바른지 확인한다.
    //2. uploadImageToS3()를 호출하여 이미지를 S3에 업로드하고, S3에 저장된 이미지의 public url을 받아서 서비스 로직에 반환한다.
    private BoardImages uploadImage(MultipartFile image) {
        this.validateImageFile(Objects.requireNonNull(image.getOriginalFilename()));
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new IllegalStateException("이미지 업로드를 실패했습니다.");
        }
    }

    // filename을 받아서 파일 확장자가 jpg, jpeg, png, gif 중에 속하는지 검증한다.
    private void validateImageFile(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다.");
        }
    }

    // 이미지 s3에 업로드
    private BoardImages uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명

        String folderName = "boards/";
        String s3FileName = folderName + UUID.randomUUID().toString().substring(0, 10) + originalFilename;


        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType("image/" + extention);
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            //실제로 S3에 이미지 데이터를 넣는 부분이다.
            amazonS3.putObject(putObjectRequest); // put image to S3
        }catch (Exception e){
            log.info(e.getMessage());
            throw new IllegalStateException("이미지 S3저장 실패");
        }finally {
            byteArrayInputStream.close();
            is.close();
        }

        String url = amazonS3.getUrl(bucket, s3FileName).toString();

        BoardImages boardImages = BoardImages.builder()
                .fileName(s3FileName)
                .imageUrl(url)
                .build();
        repository.save(boardImages);

        return boardImages;
    }

    public void saveByfileNames(List<String> fileNameList, Boards boards) {
        for (BoardImages boardImages : repository.findAllByFileNameIn(fileNameList)) {
            boards.addImages(boardImages);
        }
    }

    // s3 이미지 삭제
    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
        }catch (Exception e){
            throw new IllegalStateException("이미지 삭제 실패");
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new IllegalStateException("이미지 삭제 실패");
        }
    }
}
