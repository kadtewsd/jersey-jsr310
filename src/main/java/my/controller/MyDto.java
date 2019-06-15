package my.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MyDto {

    public final String word;
    public final LocalDateTime localDateTime;

    /**
     * Jackson が newInstance するための引数なしコンストラクタ
     */
    private MyDto() {
        this(null, null);
    }

    public MyDto(String word, LocalDateTime localDateTime) {
        this.word = word;
        this.localDateTime = localDateTime;
    }

    public String value() {
        return word + localDateTime.toString();
    }

}
