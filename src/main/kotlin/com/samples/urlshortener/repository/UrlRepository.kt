package com.samples.urlshortener.repository

import com.samples.urlshortener.model.entity.Url
import org.springframework.data.jpa.repository.JpaRepository

interface UrlRepository: JpaRepository<Url, String>{
    fun findByShortUrl(shortUrl: String): Url?
}