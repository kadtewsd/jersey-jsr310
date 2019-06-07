package com.example.kasakaid.controller

import com.fasterxml.jackson.annotation.JsonFormat
import java.io.Serializable
import java.time.*
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/jersey")
class JerseyController {

    @GET
    @Path("/get")
    @Consumes(value = [MediaType.APPLICATION_JSON])
    @Produces(value = [MediaType.APPLICATION_JSON])
    fun get(): Result {
        return Result(
                LocalDateTime.now(),
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

data class Result(
        val localDateTime: LocalDateTime? = null,
        val zonedDateTime: ZonedDateTime? = null,
        val offSetDateTime: OffsetDateTime? = null,
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss HH:mm:ss.SSSXXX")
        val customZonedDateTimeFormatUTC: ZonedDateTime? = null,
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss HH:mm:ss.SSSXXX") // spring boot の設定に勝つ
        val customLocalDateTimeFormatUTC: ZonedDateTime? = null,
        val atZoneZonedDateTimeUTC: ZonedDateTime? = null,
        val localDateTimeWithTimeZone: ZonedDateTime? = null,
        val offsetLocalDateTime: Instant? = null,
        val offsetLocalDateTimeInt: Instant? = null,
        val offsetDateTimeInt: OffsetDateTime? = null
) : Serializable
