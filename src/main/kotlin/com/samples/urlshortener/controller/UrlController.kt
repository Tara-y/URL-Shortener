package com.samples.urlshortener.controller

import com.samples.urlshortener.model.ShortenRequest
import com.samples.urlshortener.service.UrlService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
@RequestMapping(value = ["/api"])
class UrlController(private val urlService: UrlService) {

    @PostMapping("/shorten")
    fun shortenUrl(@RequestBody requestedUrl: ShortenRequest): ResponseEntity<String>{
            val shortUrl = urlService.shortenUrl(requestedUrl.originalUrl)
            return ResponseEntity.ok(shortUrl)
    }

    @GetMapping("/resolve/{shortUrl}")
    fun resolveUrl(@PathVariable shortUrl:String):ResponseEntity<String>{
        val originalUrl = urlService.resolveUrl(shortUrl)
        val location: URI = URI.create(originalUrl)
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(location)
                .build()
    }

}