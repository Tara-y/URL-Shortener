package com.samples.urlshortener.service

import com.samples.urlshortener.config.UrlShortenerProperties
import com.samples.urlshortener.model.entity.Url
import com.samples.urlshortener.repository.UrlRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class UrlServiceTest {

    private lateinit var urlService: UrlService
    private val urlRepository: UrlRepository = mockk(relaxed = true)
    private val urlShortenerProperties: UrlShortenerProperties = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { urlShortenerProperties.hashAlgorithm } returns "SHA-256"
        every { urlShortenerProperties.slugLength } returns 8
        urlService = UrlService(urlRepository, urlShortenerProperties)
    }

    @Test
    fun `should shorten new URL and save it`() {
        val originalUrl = "https://example.com"
        val generatedShortUrl = urlService.javaClass
            .getDeclaredMethod("generateShortUrl", String::class.java)
            .apply { isAccessible = true }
            .invoke(urlService, originalUrl) as String

        val savedUrl = Url(shortUrl = generatedShortUrl, originalUrl = originalUrl, createdAt = LocalDateTime.now())

        every { urlRepository.findByOriginalUrl(originalUrl) } returns null
        every { urlRepository.existsById(generatedShortUrl) } returns false
        every { urlRepository.save(any()) } returns savedUrl

        val result = urlService.shortenUrl(originalUrl)

        assertNotNull(result)
        assertEquals(generatedShortUrl, result)
    }

    @Test
    fun `should resolve short URL`() {
        val shortUrl = "abc123"
        val originalUrl = "https://example.com"
        val url = Url(shortUrl = shortUrl, originalUrl = originalUrl, createdAt = LocalDateTime.now())

        every { urlRepository.findByShortUrl(shortUrl) } returns url

        val result = urlService.resolveUrl(shortUrl)

        assertNotNull(result)
        assertEquals(originalUrl, result)
    }

    @Test
    fun `should return empty string for non-existent short URL`() {
        val shortUrl = "5558785"

        every { urlRepository.findByShortUrl(shortUrl) } returns null

        val result = urlService.resolveUrl(shortUrl)

        assertEquals("", result)
    }
}