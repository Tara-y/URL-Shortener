package com.samples.urlshortener.service

import com.samples.urlshortener.config.UrlShortenerProperties
import com.samples.urlshortener.exception.ResourceNotFoundException
import com.samples.urlshortener.exception.UrlValidationException
import com.samples.urlshortener.model.entity.Url
import com.samples.urlshortener.repository.UrlRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        every { urlShortenerProperties.maxRetries } returns 2
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
        every { urlRepository.save(any<Url>()) } returns savedUrl

        val result = urlService.shortenUrl(originalUrl)

        assertNotNull(result)
        assertEquals(generatedShortUrl, result)
    }

    @Test
    fun `should resolve short URL`() {
        val shortUrl = "abc12345"
        val originalUrl = "https://example.com"
        val url = Url(shortUrl = shortUrl, originalUrl = originalUrl, createdAt = LocalDateTime.now())

        every { urlRepository.findByShortUrl(shortUrl) } returns url

        val result = urlService.resolveUrl(shortUrl)

        assertNotNull(result)
        assertEquals(originalUrl, result)
    }

    @Test
    fun `should return existing short URL if original URL already exists`() {
        val originalUrl = "https://example.com"
        val shortUrl = "abc12345"
        val existingUrl = Url(shortUrl = shortUrl, originalUrl = originalUrl, createdAt = LocalDateTime.now())

        every { urlRepository.findByOriginalUrl(originalUrl) } returns existingUrl

        val result = urlService.shortenUrl(originalUrl)

        assertEquals(shortUrl, result)
    }

    @Test
    fun `should throw UrlValidationException for invalid URL`() {
        val invalidUrl = "invalid-url"

        assertThrows<UrlValidationException> {
            urlService.shortenUrl(invalidUrl)
        }.also {
            assertEquals("Invalid URL: $invalidUrl", it.message)
        }
    }

    @Test
    fun `should throw ResourceNotFoundException for non-existent short URL`() {
        val shortUrl = "noneExistent"

        every { urlRepository.findByShortUrl(shortUrl) } returns null

        assertThrows<ResourceNotFoundException> {
            urlService.resolveUrl(shortUrl)
        }.also {
            assertEquals("Short URL not found: $shortUrl", it.message)
        }
    }
}