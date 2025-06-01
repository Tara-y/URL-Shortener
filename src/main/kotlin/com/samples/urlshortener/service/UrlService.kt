package com.samples.urlshortener.service

import com.samples.urlshortener.model.entity.Url
import com.samples.urlshortener.repository.UrlRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.Base64


@Service
class UrlService(private val urlRepository : UrlRepository) {
    private val logger: Logger = LoggerFactory.getLogger(UrlService::class.java)

    /**
     * create a short url and save it
     */
    fun shortenUrl(originalUrl:String):String{
        if(!validateUrl(originalUrl)) {
            logger.error("Invalid URL provided: {}", originalUrl)
            throw IllegalArgumentException("Invalid URL : $originalUrl")
        }
        /* if the business needs not preventing having duplicated original urls, then we can
        remove this part.
        in some business like Amazon, as they wanted to track each shared urls separately
        to identify which one contributes to purchase,
        they created multiple short urls for the same product or the same original url */
        urlRepository.findByOriginalUrl(originalUrl)?.let {
           logger.info("This URL: {} already created at: {}",originalUrl,it.createdAt)
           return it.shortUrl
        }
        //generate short url
        val shortUrl = generateShortUrl(originalUrl)
        //make sure generated shorturl doesn't already exist in db.
        if (!urlRepository.existsById(shortUrl)) {
            try {
                urlRepository.save<Url>(Url(shortUrl, originalUrl))
                return shortUrl
            } catch (e: Exception) {
                logger.error("There is a problem while saving URL: {} ", e.message)
                throw Exception("There is a problem while saving URL : $originalUrl")
            }
        } else {
            logger.error("This shorted URL already exists! ")
            throw Exception("This shorted URL already exists, Please try again!")
        }
    }

    /*
    * Resolve the url*/
    fun resolveUrl(shortUrl:String):String{
        return urlRepository.findByShortUrl(shortUrl)?.originalUrl?: "URL Not Found!"
    }

    /*
    * you can change the hash algorithm and the slug length from properties file
    * */
    private fun generateShortUrl(originalUrl: String):String {
        try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(originalUrl.toByteArray(Charsets.UTF_8))
            val base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
            return base64Hash.take(8)
        }catch (e:Exception){
            logger.error("there is a problem while generating shortUrl {}", e.message)
            throw Exception("There is a problem while generating shortUrl")
        }
    }

    private fun validateUrl(originalUrl: String): Boolean {
        try {
            java.net.URI( originalUrl).toURL()
            return true

        }   catch (ex: Exception) {
            logger.warn("The URL provided is not a valid URL")
            return false
        }

    }
}
