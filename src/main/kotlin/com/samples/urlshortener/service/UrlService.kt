package com.samples.urlshortener.service

import com.samples.urlshortener.config.UrlShortenerProperties
import com.samples.urlshortener.exception.DuplicateEntityException
import com.samples.urlshortener.exception.ResourceNotFoundException
import com.samples.urlshortener.exception.UrlValidationException
import com.samples.urlshortener.model.entity.Url
import com.samples.urlshortener.repository.UrlRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.Base64


@Service
class UrlService(private val urlRepository : UrlRepository,private val urlShortenerProperties : UrlShortenerProperties) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * create a short url and save it
     */
    fun shortenUrl(originalUrl:String):String{
        if(!validateUrl(originalUrl)) {
            logger.error("Invalid URL provided: {}", originalUrl)
            throw UrlValidationException("Invalid URL: $originalUrl")
        }
        /* if the business needs not preventing having duplicated original urls,
        then we can remove this part.
        in some business like Amazon, as they wanted to track each shared urls separately
        to identify which one contributes to purchase,
        they created multiple short urls for the same product or the same original url */
        urlRepository.findByOriginalUrl(originalUrl)?.let {
           logger.info("This URL: {} already created at: {}",originalUrl,it.createdAt)
           return it.shortUrl
        }
        //generate short url
        val shortUrl = generateShortUrl(originalUrl)
        return saveIfUnique(shortUrl, originalUrl,urlShortenerProperties.maxRetries)
    }

    /*
    * Resolve the url
    * */
    fun resolveUrl(shortUrl:String):String{
        return urlRepository.findByShortUrl(shortUrl)?.originalUrl?:
            throw ResourceNotFoundException("Short URL not found: $shortUrl")
    }

    /*
    * you can change the hash algorithm and the slug length from properties file
    * */
    private fun generateShortUrl(originalUrl: String):String {
        try {
            val digest = MessageDigest.getInstance(urlShortenerProperties.hashAlgorithm)
            val hashBytes = digest.digest(originalUrl.toByteArray(Charsets.UTF_8))
            val base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes)
            return base64Hash.take(urlShortenerProperties.slugLength)
        }catch (e:Exception){
            logger.error("there is a problem while generating shortUrl {}", e.message)
            throw Exception("There is a problem while generating shortUrl")
        }
    }

    private fun validateUrl(originalUrl: String): Boolean {
        try {
            val url = java.net.URI( originalUrl).toURL()
            url.protocol in listOf("http", "https")
            return true

        }   catch (ex: Exception) {
            return false
        }
    }

    private fun saveIfUnique(shortUrl: String, originalUrl: String, retries : Int): String {
        //try to generate another short url for specific number in properties file
        if(retries <= 0) {
            logger.error("Max retries reached for short URL generation")
            throw DuplicateEntityException("Unable to generate unique short URL after $retries attempts")
        }
        //if generated shortUrl already exists try to generate another one
        if (urlRepository.existsById(shortUrl)) {
            logger.warn("Short URL collision detected: {}. Retrying...", shortUrl)
            val newShortUrl = generateShortUrl(originalUrl + System.nanoTime())
            saveIfUnique(newShortUrl, originalUrl, retries - 1)
        } else {
            urlRepository.save(Url(shortUrl, originalUrl))
        }
        return shortUrl
    }
}
