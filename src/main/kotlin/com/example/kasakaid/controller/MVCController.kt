package com.example.kasakaid.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.*

@RestController
class MVCController {

    @GetMapping("/mvc")
    fun get(): DateTimeResult {
        return DateTimeResult(
                LocalDateTime.of(2019, 1, 2, 13, 3, 1),
                ZonedDateTime.now(),
                OffsetDateTime.now(),
                // フォーマットが UTC でありかつ時間も UTC になる
                ZonedDateTime.ofInstant(ZonedDateTime.now().toInstant(), ZoneId.of("UTC")),
                // フォーマットが UTC になるのみで時間はシステム現地時間。基本的に現地時間を表すものらしい
                ZonedDateTime.ofInstant(LocalDateTime.now().toInstant(ZoneOffset.UTC), ZoneId.of("UTC")),
                // フォーマットにタイムゾーンが含まれシステム時刻とタイムゾーンの表記が一致する
                ZonedDateTime.ofInstant(LocalDateTime.now().toInstant(ZoneOffset.of("+09:00")), ZoneId.of("Asia/Tokyo")),
                // これだと フォーマットが UTC で 時間は JST  で表示される
                LocalDateTime.now().atZone(ZoneId.of("UTC")),
                // これだと フォーマットも UTC だし時間も UTC で表示される
                LocalDateTime.now().toInstant(ZoneOffset.of("+09:00")),
                // これだと フォーマットも UTC だし時間も UTC で表示される
                LocalDateTime.now().toInstant(ZoneOffset.ofHours(9)),
                // これだと フォーマットが +9:00 で時間も JST で表示される。OffsetDateTime は標準時からの時差なので単純に 9 時間のプラスになる。
                OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.ofHours(9))

        )
    }
}

