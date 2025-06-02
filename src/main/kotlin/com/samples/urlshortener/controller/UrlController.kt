package com.samples.urlshortener.controller

import com.samples.urlshortener.model.ShortenRequest
import com.samples.urlshortener.service.UrlService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
@RequestMapping(value = ["/api"])
class UrlController(private val urlService: UrlService) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/shorten")
    fun shortenUrl(@RequestBody requestedUrl: ShortenRequest): ResponseEntity<String>{
        try {
            val shortUrl = urlService.shortenUrl(requestedUrl.originalUrl)
            return ResponseEntity.ok(shortUrl)

        }catch (e: IllegalArgumentException) {
                logger.error("URL: {} Is not a valid URL", requestedUrl.originalUrl, e)
                return ResponseEntity.badRequest().build()
        }catch (e2:Exception){
            logger.error(e2.message, e2)
            return ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/resolve/{shortUrl}")
    fun resolveUrl(@PathVariable shortUrl:String):ResponseEntity<String>{
        val originalUrl = urlService.resolveUrl(shortUrl)
        if(originalUrl.isNotEmpty()){
            val location: URI = URI.create(originalUrl)
            return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(location)
                .build()
        }else{
            logger.error("{} is not Found",shortUrl)
            return ResponseEntity.notFound().build()
        }
    }

}