package lostark.todo.global.dto;

import lombok.Data;

@Data
public class ImageResponse {

    private String fileName;

    private String imageUrl;

    public ImageResponse(String s3FileName, String url) {
        this.fileName = s3FileName;
        this.imageUrl = url;
    }
}
