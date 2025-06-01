package com.samples.urlshortener.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "url.shortener")
class UrlShortenerProperties {
    var slugLength : Int = 8
    var hashAlgorithm : String = "SHA-256"
}
